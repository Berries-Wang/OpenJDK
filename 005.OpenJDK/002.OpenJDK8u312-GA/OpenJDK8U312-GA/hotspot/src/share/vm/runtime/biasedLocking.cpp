/*
 * Copyright (c) 2005, 2014, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 *
 */

#include "precompiled.hpp"
#include "oops/klass.inline.hpp"
#include "oops/markOop.hpp"
#include "runtime/basicLock.hpp"
#include "runtime/biasedLocking.hpp"
#include "runtime/task.hpp"
#include "runtime/vframe.hpp"
#include "runtime/vmThread.hpp"
#include "runtime/vm_operations.hpp"
#include "jfr/support/jfrThreadId.hpp"
#include "jfr/jfrEvents.hpp"

static bool _biased_locking_enabled = false;
BiasedLockingCounters BiasedLocking::_counters;

static GrowableArray<Handle>*  _preserved_oop_stack  = NULL;
static GrowableArray<markOop>* _preserved_mark_stack = NULL;

static void enable_biased_locking(Klass* k) {
  k->set_prototype_header(markOopDesc::biased_locking_prototype());
}

class VM_EnableBiasedLocking: public VM_Operation {
 private:
  bool _is_cheap_allocated;
 public:
  VM_EnableBiasedLocking(bool is_cheap_allocated) { _is_cheap_allocated = is_cheap_allocated; }
  VMOp_Type type() const          { return VMOp_EnableBiasedLocking; }
  Mode evaluation_mode() const    { return _is_cheap_allocated ? _async_safepoint : _safepoint; }
  bool is_cheap_allocated() const { return _is_cheap_allocated; }

  void doit() {
    // Iterate the system dictionary enabling biased locking for all
    // currently loaded classes
    SystemDictionary::classes_do(enable_biased_locking);
    // Indicate that future instances should enable it as well
    _biased_locking_enabled = true;

    if (TraceBiasedLocking) {
      tty->print_cr("Biased locking enabled");
    }
  }

  bool allow_nested_vm_operations() const        { return false; }
};


// One-shot PeriodicTask subclass for enabling biased locking
class EnableBiasedLockingTask : public PeriodicTask {
 public:
  EnableBiasedLockingTask(size_t interval_time) : PeriodicTask(interval_time) {}

  virtual void task() {
    // Use async VM operation to avoid blocking the Watcher thread.
    // VM Thread will free C heap storage.
    VM_EnableBiasedLocking *op = new VM_EnableBiasedLocking(true);
    VMThread::execute(op);

    // Reclaim our storage and disenroll ourself
    delete this;
  }
};


void BiasedLocking::init() {
  // If biased locking is enabled, schedule a task to fire a few
  // seconds into the run which turns on biased locking for all
  // currently loaded classes as well as future ones. This is a
  // workaround for startup time regressions due to a large number of
  // safepoints being taken during VM startup for bias revocation.
  // Ideally we would have a lower cost for individual bias revocation
  // and not need a mechanism like this.
  if (UseBiasedLocking) {
    if (BiasedLockingStartupDelay > 0) {
      EnableBiasedLockingTask* task = new EnableBiasedLockingTask(BiasedLockingStartupDelay);
      task->enroll();
    } else {
      VM_EnableBiasedLocking op(false);
      VMThread::execute(&op);
    }
  }
}


bool BiasedLocking::enabled() {
  return _biased_locking_enabled;
}

/**
 * Returns MonitorInfos for all objects locked on this thread in youngest to
 * oldest order
 *
 * 将lock record 转换为MonitorInfo (不区分锁类型)
 * 
 * @param thread 需要处理的java线程
 *
 * @return  GrowableArray<MonitorInfo*>* 
 */
static GrowableArray<MonitorInfo *>* get_or_compute_monitor_info(JavaThread *thread) {
  GrowableArray<MonitorInfo *> *info = thread->cached_monitor_info();
  if (info != NULL) {
    return info;
  }

  info = new GrowableArray<MonitorInfo *>();

  // It's possible for the thread to not have any Java frames on it,
  // i.e., if it's the main thread and it's already returned from main()
  if (thread->has_last_Java_frame()) {
    RegisterMap rm(thread);
    for (javaVFrame *vf = thread->last_java_vframe(&rm); vf != NULL; vf = vf->java_sender()) {
      GrowableArray<MonitorInfo *> *monitors = vf->monitors();
      if (monitors != NULL) {
        int len = monitors->length();
        // Walk monitors youngest to oldest
        for (int i = len - 1; i >= 0; i--) {
          MonitorInfo *mon_info = monitors->at(i);
          if (mon_info->eliminated())
            continue;
          oop owner = mon_info->owner(); 
          if (owner != NULL) {
            info->append(mon_info);
          }
        }
      }
    }
  }

  thread->set_cached_monitor_info(info);
  return info;
}

/**
 *  After the call, *biased_locker will be set to obj->mark()->biased_locker()
 * if biased_locker != NULL,AND it is a living thread. Otherwise it will not be
 * updated, (i.e. the caller is responsible for initialization:
 * 调用者负责初始化).
 *
 * 调用之后，如果biased_locker !=
 * NULL并且是一个存活的线程，那么biased_locker被设置为obj->mark()->biased_locker()。
 * 否则他将不会更新.
 * 
 * 
 * 重偏向操作
 * 
 * @param obj 锁对象
 * @param allow_rebias
 * @param is_bulk
 * @param requesting_thread 当前判断的线程，即偏向锁偏向的线程是否是该线程
 * @param biased_locker 
 * 
 * @return 
 */
static BiasedLocking::Condition revoke_bias(oop obj, bool allow_rebias, bool is_bulk, JavaThread* requesting_thread, JavaThread** biased_locker) {
  
  // 获取锁对象的对象头
  markOop mark = obj->mark();
  
  // 当前并不是处于偏向锁模式
  if (!mark->has_bias_pattern()) {
    if (TraceBiasedLocking) {
      ResourceMark rm;
      tty->print_cr("  (Skipping revocation of object of type %s because it's no longer biased)",
                    obj->klass()->external_name());
    }
    return BiasedLocking::NOT_BIASED;
  }

  // 当前锁对象处于偏向模式

  // 获取锁对象年龄
  uint age = mark->age();

  // 处于偏向模式的markword,但是偏向线程不存在
  markOop   biased_prototype = markOopDesc::biased_locking_prototype()->set_age(age);

  // 不处于偏向模式的markword
  markOop unbiased_prototype = markOopDesc::prototype()->set_age(age);

  if (TraceBiasedLocking && (Verbose || !is_bulk)) {
    ResourceMark rm;
    tty->print_cr("Revoking bias of object " INTPTR_FORMAT " , mark " INTPTR_FORMAT " , type %s , prototype header " INTPTR_FORMAT " , allow rebias %d , requesting thread " INTPTR_FORMAT,
                  p2i((void *)obj), (intptr_t) mark, obj->klass()->external_name(), (intptr_t) obj->klass()->prototype_header(), (allow_rebias ? 1 : 0), (intptr_t) requesting_thread);
  }

  JavaThread* biased_thread = mark->biased_locker();

  // 当偏向的线程不存在(匿名偏向)
  if (biased_thread == NULL) {
    /**
     * Object is anonymously biased. We can get here if, for
     * example, we revoke the bias due to an identity hash code
     * being computed for an object.
     * 匿名偏向情况: 
     * 对象处于匿名偏向，代码能够执行到这里，例如: 
     * 1. 由于正在为对象计算身份哈希码，撤销了偏向锁.
     *    -> 故，hash code 被计算会导致无法使用偏向锁
     * 2. 如上面的“biased_prototype”
     */
    if (!allow_rebias) { // 不允许重偏向
      obj->set_mark(unbiased_prototype); // 撤销偏向
    }
    if (TraceBiasedLocking && (Verbose || !is_bulk)) {
      tty->print_cr("  Revoked bias of anonymously-biased object");
    }
    return BiasedLocking::BIAS_REVOKED;
  }

  // Handle case where the thread toward which the object was biased has exited
  bool thread_is_alive = false;
  
  // 判断偏向锁偏向的线程是否还存活
  if (requesting_thread == biased_thread) {
    thread_is_alive = true;
  } else {
    // 遍历Java线程的方式
    for (JavaThread* cur_thread = Threads::first(); cur_thread != NULL; cur_thread = cur_thread->next()) {
      if (cur_thread == biased_thread) {
        thread_is_alive = true;
        break;
      }
    }
  }

  // 线程不存活
  if (!thread_is_alive) {
    if (allow_rebias) {
      obj->set_mark(biased_prototype); // 这样会处于一个匿名偏向的模式
    } else {
      obj->set_mark(unbiased_prototype);
    }
    if (TraceBiasedLocking && (Verbose || !is_bulk)) {
      tty->print_cr("  Revoked bias of object biased toward dead thread");
    }
    return BiasedLocking::BIAS_REVOKED;
  }

  /**
   * Thread owning bias is alive.
   * Check to see whether it currently owns the lock and, if so,
   * write down the needed displaced headers to the thread's stack.
   * Otherwise, restore the object's header either to the unlocked
   * or unbiased state.
   *
   * 拥有偏向锁的线程是存活的，校验是否是当前拥有锁的线程.如果是，则需要将替换的
   * 对象头写入到线程的堆栈。否则，将对象的头部重置为未锁住或者未偏向的状态
   **/
  // 将当前线程中的lock record转换为MonitorInfo 
  GrowableArray<MonitorInfo*>* cached_monitor_info = get_or_compute_monitor_info(biased_thread);
  BasicLock* highest_lock = NULL;
  for (int i = 0; i < cached_monitor_info->length(); i++) {
    MonitorInfo* mon_info = cached_monitor_info->at(i);
    
    if (mon_info->owner() == obj) {
      if (TraceBiasedLocking && Verbose) {
        tty->print_cr("   mon_info->owner (" PTR_FORMAT ") == obj (" PTR_FORMAT ")",
                      p2i((void *) mon_info->owner()),
                      p2i((void *) obj));
      }
      /**
       * Assume recursive case and fix up highest lock later
       * 
       */ 
      markOop mark = markOopDesc::encode((BasicLock*) NULL);
      highest_lock = mon_info->lock();
      highest_lock->set_displaced_header(mark);
    } else {  
      if (TraceBiasedLocking && Verbose) {
        tty->print_cr("   mon_info->owner (" PTR_FORMAT ") != obj (" PTR_FORMAT ")",
                      p2i((void *) mon_info->owner()),
                      p2i((void *) obj));
      }
    }
  }

  // 锁存在
  if (highest_lock != NULL) {
    /**
     * Fix up highest lock to contain displaced header and point  object at it
     *
     * --> 修正锁中存储的markword字段
     */
    highest_lock->set_displaced_header(unbiased_prototype);
    // Reset object header to point to displaced mark.
    // Must release storing the lock address for platforms without TSO
    // ordering (e.g. ppc).
    obj->release_set_mark(markOopDesc::encode(highest_lock));
    assert(!obj->mark()->has_bias_pattern(),
           "illegal mark state: stack lock used bias bit");
    if (TraceBiasedLocking && (Verbose || !is_bulk)) {
      tty->print_cr("  Revoked bias of currently-locked object");
    }
  } else { 
    if (TraceBiasedLocking && (Verbose || !is_bulk)) {
      tty->print_cr("  Revoked bias of currently-unlocked object");
    }
    if (allow_rebias) {
      obj->set_mark(biased_prototype);
    } else {
      // Store the unlocked value into the object's header.
      obj->set_mark(unbiased_prototype);
    }
  }

#if INCLUDE_JFR
  // If requested, return information on which thread held the bias
  if (biased_locker != NULL) {
    *biased_locker = biased_thread;
  }
#endif // INCLUDE_JFR

  return BiasedLocking::BIAS_REVOKED;
}


enum HeuristicsResult {
  HR_NOT_BIASED    = 1,
  HR_SINGLE_REVOKE = 2,
  HR_BULK_REBIAS   = 3,
  HR_BULK_REVOKE   = 4
};


/**
 * 启发式更新?
 * 
 * @param  o  锁对象
 * @param allow_rebias 是否允许重偏向
 * 
 * 
 */ 
static HeuristicsResult update_heuristics(oop o, bool allow_rebias) {
  markOop mark = o->mark();
  // 锁对象不处于偏向模式
  if (!mark->has_bias_pattern()) {
    return HR_NOT_BIASED;
  }

  // Heuristics to attempt to throttle(阻挡;压制;) the number of revocations.
  // Stages:
  // 1. Revoke(撤销) the biases of all objects in the heap of this type,
  //    but allow rebiasing of those objects if unlocked.
  //    -> 对堆中所有该类型的对象进行撤销偏向锁操作，但是允许在这些对象没有被锁住的情况下进行重偏向操作。
  // 2. Revoke the biases of all objects in the heap of this type
  //    and don't allow rebiasing of these objects. Disable
  //    allocation of objects of that type with the bias bit set.
  //    -> 对堆中所有该类型的对象进行撤销偏向锁操作，但是不允许对这些对象进行重偏向操作。禁止bias位被设置
  //       的该类型的对象的分配.
  Klass* k = o->klass();
  jlong cur_time = os::javaTimeMillis();
  // 获取上一次批量重偏向的执行时间
  jlong last_bulk_revocation_time = k->last_biased_lock_bulk_revocation_time();
  // 获取重偏向执行的次数
  int revocation_count = k->biased_lock_revocation_count();

  /**
   * BiasedLockingBulkRebiasThreshold: 20
   * BiasedLockingBulkRevokeThreshold: 40
   * BiasedLockingDecayTime: 25000
   * 
   * 即在一定时间内，如果某一个类型的实例(锁对象)偏向锁撤销
   * 超过一定次数，则会执行批量撤销/重偏向操作.
   */ 
  if ((revocation_count >= BiasedLockingBulkRebiasThreshold) &&
      (revocation_count <  BiasedLockingBulkRevokeThreshold) &&
      (last_bulk_revocation_time != 0) &&
      (cur_time - last_bulk_revocation_time >= BiasedLockingDecayTime)) {
    /**
     * This is the first revocation(取消;撤回) we've seen in a while of an object of this
     * type since the last time we performed a bulk rebiasing operation. The
     * application is allocating objects in bulk which are biased toward a
     * thread and then handing them off to another thread. We can cope with this
     * allocation pattern via the bulk rebiasing mechanism so we reset the
     * klass's revocation count rather than allow it to increase monotonically.
     * If we see the need to perform another bulk rebias operation later, we
     * will, and if subsequently we see many more revocation operations in a
     * short period of time we will completely disable biasing for this type.
     *
     */
    k->set_biased_lock_revocation_count(0);
    revocation_count = 0;
  }

  // Make revocation count saturate just beyond BiasedLockingBulkRevokeThreshold
  if (revocation_count <= BiasedLockingBulkRevokeThreshold) {
    revocation_count = k->atomic_incr_biased_lock_revocation_count();
  }

  // 批量撤销
  if (revocation_count == BiasedLockingBulkRevokeThreshold) {
    return HR_BULK_REVOKE;
  }

  // 批量重偏向
  if (revocation_count == BiasedLockingBulkRebiasThreshold) {
    return HR_BULK_REBIAS;
  }

  return HR_SINGLE_REVOKE;
}

/**
 * 批量撤销/重偏向
 *
 * @param o 线程信息和锁对象信息
 * @param requesting_thread 当前执行线程
 * @param bulk_rebias true:批量重偏向;false:批量撤销
 * @param attempt_rebias_of_object 是否允许重偏向
 *
 * @return
 * 
 * 通过下面的代码，需要了解 epoch是什么? klass中的epoch 和 实例中的epoch 有什么关系?
 * > epoch 是偏向时间戳，代表了偏向锁的有效性；
 * > 将klass中的epoch 与 实例对象中的epoch 相比对，相等，则说明偏向锁还在使用；
 *                                            若不相等，则说明偏向锁已经失效了，此时竞争线程可以尝试对此对象重新进行偏向操作
 * > 通过设置prototype_header,可以禁止该类型使用偏向锁。
 */
static BiasedLocking::Condition bulk_revoke_or_rebias_at_safepoint(oop o,
                                                                   bool bulk_rebias,
                                                                   bool attempt_rebias_of_object,
                                                                   JavaThread* requesting_thread) {
  // 必须在安全点调用
  assert(SafepointSynchronize::is_at_safepoint(), "must be done at safepoint");

  if (TraceBiasedLocking) {
    tty->print_cr("* Beginning bulk revocation (kind == %s) because of object "
                  INTPTR_FORMAT " , mark " INTPTR_FORMAT " , type %s",
                  (bulk_rebias ? "rebias" : "revoke"),
                  p2i((void *) o), (intptr_t) o->mark(), o->klass()->external_name());
  }

  jlong cur_time = os::javaTimeMillis();
  // 设置批量撤销/重偏向的时间
  o->klass()->set_last_biased_lock_bulk_revocation_time(cur_time);


  Klass* k_o = o->klass();
  Klass* klass = k_o;

  if (bulk_rebias) {// 批量重偏向
    /**
     * Use the epoch in the klass of the object to implicitly(含蓄的;暗中地;)
     * revoke all biases of objects of this data type and force them to be
     * reacquired. However, we also need to walk the stacks of all
     * threads and update the headers of lightweight locked objects
     * with biases to have the current epoch.
     *
     * 使用klass中的epoch来隐式地撤销这种类型所有对象实例的偏向锁，并且强制他们重新获取。
     * 然而,我们也需要遍历所有线程的堆栈并且使用偏向锁的对象头信息更新轻量级锁锁对象的对象头信息用以获取当前的epoch值
     */

    /**
     * If the prototype header doesn't have the bias pattern, don't
     * try to update the epoch -- assume(假设) another VM operation came in
     * and reset the header to the unbiased state, which will
     * implicitly cause all existing biases to be revoked
     *
     * 如果"prototype
     * header"不支持偏向模式，那么不需要更新epoch,假设另外的VM操作来到了
     * 并且重置header为未偏向的状态，这将含蓄地导致所有现有的偏向被撤销
     */
    if (klass->prototype_header()->has_bias_pattern()) {
      // 旧的epoch
      int prev_epoch = klass->prototype_header()->bias_epoch();
      // 生成一个新的epoch
      klass->set_prototype_header(klass->prototype_header()->incr_bias_epoch());
      int cur_epoch = klass->prototype_header()->bias_epoch();

      /**
       * Now walk all threads' stacks and adjust epochs of any biased
       * and locked objects of this data type we encounter
       * 
       * 遍历所有线程的堆栈，且调整遇到的任意一个该类型的已偏向的和已被锁住的对象.
       */
      for (JavaThread* thr = Threads::first(); thr != NULL; thr = thr->next()) {
        GrowableArray<MonitorInfo*>* cached_monitor_info = get_or_compute_monitor_info(thr);
        for (int i = 0; i < cached_monitor_info->length(); i++) {
          MonitorInfo* mon_info = cached_monitor_info->at(i);
          oop owner = mon_info->owner();
          markOop mark = owner->mark();
          /**
           * 如果实例owner的类型是k_o(即是该klass类的实例),那么就需要重新设置一下epoch,即偏向超时了(被批量重偏向)
           * 
           * 注意，这里是批量重偏向
           */ 
          if ((owner->klass() == k_o) && mark->has_bias_pattern()) {
            // We might have encountered this object already in the case of recursive locking
            assert(mark->bias_epoch() == prev_epoch || mark->bias_epoch() == cur_epoch, "error in bias epoch adjustment");
            // 设置新的epoch
            owner->set_mark(mark->set_bias_epoch(cur_epoch));
          }
        }
      }
    }

    /**
     * At this point we're done. All we have to do is potentially(可能的，潜在的)
     * adjust the header of the given object to revoke its bias.
     * 
     * 这一步就完成了，我们所要做的就是潜在地调整给定对象的头部来撤销其偏差。
     *
     */
    revoke_bias(o, attempt_rebias_of_object && klass->prototype_header()->has_bias_pattern(), true, requesting_thread, NULL);
  } else { // 批量撤销
    if (TraceBiasedLocking) {
      ResourceMark rm;
      tty->print_cr("* Disabling biased locking for type %s", klass->external_name());
    }

    /**
     * Disable biased locking for this data type. Not only will this
     * cause future instances to not be biased, but existing biased
     * instances will notice that this implicitly caused their biases
     * to be revoked.
     *
     * 禁用此数据类型的偏向锁定。这不仅会使未来的例子不带有偏见，
     * 而且现有的有偏见的例子也会注意到，这含蓄地导致了他们的偏见被撤销。(即禁止重偏向了)
     */
    klass->set_prototype_header(markOopDesc::prototype());

    // Now walk all threads' stacks and forcibly(强行) revoke the biases of
    // any locked and biased objects of this data type we encounter.
    for (JavaThread* thr = Threads::first(); thr != NULL; thr = thr->next()) {
      GrowableArray<MonitorInfo*>* cached_monitor_info = get_or_compute_monitor_info(thr);
      for (int i = 0; i < cached_monitor_info->length(); i++) {
        MonitorInfo* mon_info = cached_monitor_info->at(i);
        oop owner = mon_info->owner();
        markOop mark = owner->mark();
        /**
         * 如果实例owner的类型是k_o(即是该klass类的实例)
         * 批量撤销
         */ 
        if ((owner->klass() == k_o) && mark->has_bias_pattern()) {
          revoke_bias(owner, false, true, requesting_thread, NULL);
        }
      }
    }

    // Must force the bias of the passed object to be forcibly revoked
    // as well to ensure guarantees to callers
    revoke_bias(o, false, true, requesting_thread, NULL);
  }

  if (TraceBiasedLocking) {
    tty->print_cr("* Ending bulk revocation");
  }

  BiasedLocking::Condition status_code = BiasedLocking::BIAS_REVOKED;

  if (attempt_rebias_of_object &&
      o->mark()->has_bias_pattern() &&
      klass->prototype_header()->has_bias_pattern()) {
    markOop new_mark = markOopDesc::encode(requesting_thread, o->mark()->age(),
                                           klass->prototype_header()->bias_epoch());
    o->set_mark(new_mark);
    status_code = BiasedLocking::BIAS_REVOKED_AND_REBIASED;
    if (TraceBiasedLocking) {
      tty->print_cr("  Rebiased object toward thread " INTPTR_FORMAT, (intptr_t) requesting_thread);
    }
  }

  assert(!o->mark()->has_bias_pattern() ||
         (attempt_rebias_of_object && (o->mark()->biased_locker() == requesting_thread)),
         "bug in bulk bias revocation");

  return status_code;
}


static void clean_up_cached_monitor_info() {
  // Walk the thread list clearing out the cached monitors
  for (JavaThread* thr = Threads::first(); thr != NULL; thr = thr->next()) {
    thr->set_cached_monitor_info(NULL);
  }
}


class VM_RevokeBias : public VM_Operation {
protected:
  Handle* _obj;
  GrowableArray<Handle>* _objs;
  JavaThread* _requesting_thread;
  BiasedLocking::Condition _status_code;
  traceid _biased_locker_id;

public:
  VM_RevokeBias(Handle* obj, JavaThread* requesting_thread)
    : _obj(obj)
    , _objs(NULL)
    , _requesting_thread(requesting_thread)
    , _status_code(BiasedLocking::NOT_BIASED)
    , _biased_locker_id(0) {}

  VM_RevokeBias(GrowableArray<Handle>* objs, JavaThread* requesting_thread)
    : _obj(NULL)
    , _objs(objs)
    , _requesting_thread(requesting_thread)
    , _status_code(BiasedLocking::NOT_BIASED)
    , _biased_locker_id(0) {}

  virtual VMOp_Type type() const { return VMOp_RevokeBias; }

  virtual bool doit_prologue() {
    // Verify that there is actual work to do since the callers just
    // give us locked object(s). If we don't find any biased objects
    // there is nothing to do and we avoid a safepoint.
    if (_obj != NULL) {
      markOop mark = (*_obj)()->mark();
      if (mark->has_bias_pattern()) {
        return true;
      }
    } else {
      for ( int i = 0 ; i < _objs->length(); i++ ) {
        markOop mark = (_objs->at(i))()->mark();
        if (mark->has_bias_pattern()) {
          return true;
        }
      }
    }
    return false;
  }

  virtual void doit() {
    if (_obj != NULL) {
      if (TraceBiasedLocking) {
        tty->print_cr("Revoking bias with potentially per-thread safepoint:");
      }

      JavaThread* biased_locker = NULL;
      _status_code = revoke_bias((*_obj)(), false, false, _requesting_thread, &biased_locker);
#if INCLUDE_JFR
      if (biased_locker != NULL) {
        _biased_locker_id = JFR_THREAD_ID(biased_locker);
      }
#endif // INCLUDE_JFR

      clean_up_cached_monitor_info();
      return;
    } else {
      if (TraceBiasedLocking) {
        tty->print_cr("Revoking bias with global safepoint:");
      }
      BiasedLocking::revoke_at_safepoint(_objs);
    }
  }

  BiasedLocking::Condition status_code() const {
    return _status_code;
  }

  traceid biased_locker() const {
    return _biased_locker_id;
  }
};


class VM_BulkRevokeBias : public VM_RevokeBias {
private:
  bool _bulk_rebias;
  bool _attempt_rebias_of_object;

public:
  /**
   * @param obj 线程信息和锁对象信息
   * @param requesting_thread 当前执行线程
   * @param bulk_rebias true:批量重偏向;false:批量撤销
   * @param attempt_rebias_of_object 是否允许重偏向
   *
   */
  VM_BulkRevokeBias(Handle *obj, JavaThread *requesting_thread,
                    bool bulk_rebias, bool attempt_rebias_of_object)
      : VM_RevokeBias(obj, requesting_thread), _bulk_rebias(bulk_rebias),
        _attempt_rebias_of_object(attempt_rebias_of_object) {}

  virtual VMOp_Type type() const { return VMOp_BulkRevokeBias; }
  virtual bool doit_prologue()   { return true; }

  virtual void doit() {
    // 批量撤销/重偏向
    _status_code = bulk_revoke_or_rebias_at_safepoint((*_obj)(), _bulk_rebias, _attempt_rebias_of_object, _requesting_thread);
    clean_up_cached_monitor_info();
  }
};


/**
 * 取消偏向锁，进行重偏向
 * 
 * @param obj 线程信息和锁对象信息
 * @param attempt_rebias 是否尝试重偏向
 * 
 * @return 本函数的操作类型
 */ 
BiasedLocking::Condition BiasedLocking::revoke_and_rebias(Handle obj, bool attempt_rebias, TRAPS) {
  assert(!SafepointSynchronize::is_at_safepoint(), "must not be called while at safepoint");

  // We can revoke the biases of anonymously-biased objects
  // efficiently enough that we should not cause these revocations to
  // update the heuristics because doing so may cause unwanted bulk
  // revocations (which are expensive) to occur.
  markOop mark = obj->mark();
  // 如果是匿名偏向且不允许重偏向
  if (mark->is_biased_anonymously() && !attempt_rebias) {
    // 偏向锁撤销
    // We are probably trying to revoke the bias of this object due to
    // an identity hash code computation. Try to revoke the bias
    // without a safepoint. This is possible if we can successfully
    // compare-and-exchange an unbiased header into the mark word of
    // the object, meaning that no other thread has raced to acquire
    // the bias of the object.
    markOop biased_value       = mark;
    markOop unbiased_prototype = markOopDesc::prototype()->set_age(mark->age());
    markOop res_mark = (markOop) Atomic::cmpxchg_ptr(unbiased_prototype, obj->mark_addr(), mark);
    if (res_mark == biased_value) {
      return BIAS_REVOKED;
    }
  } else if (mark->has_bias_pattern()) { // 如果处于偏向锁模式
    Klass* k = obj->klass();
    /**
     * prototype_header 是一个模板，所有创建的对象的初始状态都是该值。
     * 
     */ 
    markOop prototype_header = k->prototype_header();
    // 锁对象处于偏向锁模式,但是偏向锁是禁止的
    if (!prototype_header->has_bias_pattern()) {
      // bulk revocation: 批量撤销

      /**
       * This object has a stale(陈旧的) bias from before the bulk revocation for this data type occurred.
       * >> 言外之意就是 批量重偏向之后就禁止偏向锁了吗? 待验证
       * It's pointless(毫无意义的) to update the heuristics at this point so simply update the header with a CAS.
       * If we fail this race, the object's bias has been revoked by another thread so we simply return 
       * and let the caller deal with it.
       * 
       */ 
      markOop biased_value       = mark;
      // 撤销偏向锁
      markOop res_mark = (markOop) Atomic::cmpxchg_ptr(prototype_header, obj->mark_addr(), mark);
      assert(!(*(obj->mark_addr()))->has_bias_pattern(), "even if we raced, should still be revoked");
      return BIAS_REVOKED;
    } else if (prototype_header->bias_epoch() != mark->bias_epoch()) { // 偏向时间戳过期,表明没有偏向
      /** The epoch of this biasing has expired indicating that the
       * object is effectively(实际上;有效地;) unbiased.
       * >> epoch过期了，表明该锁对象实际上并不是处于偏向锁。
       * Depending on whether we need  to rebias or revoke the bias of this
       * object we can do it efficiently enough with a CAS that we shouldn't
       * update the heuristics. This is normally done in the
       * assembly(议会;组装;装配;汇编(计算机)) code but we can reach this point
       * due to various(各式各样的) points in the runtime needing to revoke
       * biases.
       */
      if (attempt_rebias) { // 允许重偏向
        assert(THREAD->is_Java_thread(), "");
        markOop biased_value       = mark;
        // 进行偏向锁撤销和重偏向操作
        markOop rebiased_prototype = markOopDesc::encode((JavaThread*) THREAD, mark->age(), prototype_header->bias_epoch());
        markOop res_mark = (markOop) Atomic::cmpxchg_ptr(rebiased_prototype, obj->mark_addr(), mark);
        if (res_mark == biased_value) {
          return BIAS_REVOKED_AND_REBIASED;
        }
      } else { // 不允许重偏向
        markOop biased_value       = mark;
        markOop unbiased_prototype = markOopDesc::prototype()->set_age(mark->age());
        // 撤销偏向锁
        markOop res_mark = (markOop) Atomic::cmpxchg_ptr(unbiased_prototype, obj->mark_addr(), mark);
        if (res_mark == biased_value) {
          return BIAS_REVOKED;
        }
      }
    }
  }

  /**
   * heuristics: 启发法
   * 启发式更新，到底是什么意思呢?
   *
   * 根据阈值来选择不同的操作: 单个偏向/批量撤销/批量重偏向
   */
  HeuristicsResult heuristics = update_heuristics(obj(), attempt_rebias);
  // 根据操作类型来执行具体的操作(真正执行)
  if (heuristics == HR_NOT_BIASED) {// 锁对象不处于偏向模式
    return NOT_BIASED;
  } else if (heuristics == HR_SINGLE_REVOKE) { // 单个撤销
    Klass *k = obj->klass();
    // 获取原型对象头(模板对象头)
    markOop prototype_header = k->prototype_header();
    /**
     * mark->biased_locker() 获取当前持有锁的线程的线程指针
     * 
     */ 
    if (mark->biased_locker() == THREAD && // 持有锁的线程是当前线程
        prototype_header->bias_epoch() == mark->bias_epoch()) { // 没有发生过重偏向操作
      /**
       *
       *  A thread is trying to revoke the bias of an object biased
       * toward it, again likely due to an identity hash code
       * computation. We can again avoid a safepoint in this case
       * since we are only going to walk our own stack. There are no
       * races with revocations occurring in other threads because we
       * reach no safepoints in the revocation path.
       * Also check the epoch because even if threads match, another thread
       * can come in with a CAS to steal the bias of an object that has a
       * stale epoch.
       *
       * 一个线程试图撤销一个对象对它的偏向，同样可能是由于hash
       * code的计算,在这种情况下，
       * 我们可以避免安全点，因为我们只会遍历我们自己的堆栈.在其他线程中没有发生撤销的竞争，
       * 因为我们在撤销路径中没有到达安全点。还要检查epoch，因为即使线程匹配，
       * 另一个线程也可能使用CAS来窃取具有过时epoch的对象的偏差。
       *
       */
      ResourceMark rm;
      if (TraceBiasedLocking) {
        tty->print_cr("Revoking bias by walking my own stack:");
      }
      EventBiasedLockSelfRevocation event;
      // 撤销偏向操作
      BiasedLocking::Condition cond = revoke_bias(obj(), false, false, (JavaThread*) THREAD, NULL);
      ((JavaThread*) THREAD)->set_cached_monitor_info(NULL);
      assert(cond == BIAS_REVOKED, "why not?");
      if (event.should_commit()) {
        event.set_lockClass(k);
        event.commit();
      }
      return cond;
    } else { // 单个撤销操作，但是此时可能发生重偏向或执行了hashCode方法，导致线程ID丢失
      EventBiasedLockRevocation event;
      // 偏向锁撤销: 调用revoke_bias方法实现
      VM_RevokeBias revoke(&obj, (JavaThread*) THREAD);
      VMThread::execute(&revoke);
      if (event.should_commit() && (revoke.status_code() != NOT_BIASED)) {
        event.set_lockClass(k);
        // Subtract 1 to match the id of events committed inside the safepoint
        event.set_safepointId(SafepointSynchronize::safepoint_counter() - 1);
        event.set_previousOwner(revoke.biased_locker());
        event.commit();
      }
      return revoke.status_code();
    }
  }

  // 批量撤销/批量重偏向
  assert((heuristics == HR_BULK_REVOKE) ||
         (heuristics == HR_BULK_REBIAS), "?");
  EventBiasedLockClassRevocation event;
  VM_BulkRevokeBias bulk_revoke(&obj, (JavaThread*) THREAD,
                                (heuristics == HR_BULK_REBIAS),
                                attempt_rebias);
  VMThread::execute(&bulk_revoke);
  if (event.should_commit()) {
    event.set_revokedClass(obj->klass());
    event.set_disableBiasing((heuristics != HR_BULK_REBIAS));
    // Subtract 1 to match the id of events committed inside the safepoint
    event.set_safepointId(SafepointSynchronize::safepoint_counter() - 1);
    event.commit();
  }
  return bulk_revoke.status_code();
}


void BiasedLocking::revoke(GrowableArray<Handle>* objs) {
  assert(!SafepointSynchronize::is_at_safepoint(), "must not be called while at safepoint");
  if (objs->length() == 0) {
    return;
  }
  VM_RevokeBias revoke(objs, JavaThread::current());
  VMThread::execute(&revoke);
}


void BiasedLocking::revoke_at_safepoint(Handle h_obj) {
  assert(SafepointSynchronize::is_at_safepoint(), "must only be called while at safepoint");
  oop obj = h_obj();
  HeuristicsResult heuristics = update_heuristics(obj, false);
  if (heuristics == HR_SINGLE_REVOKE) {
    revoke_bias(obj, false, false, NULL, NULL);
  } else if ((heuristics == HR_BULK_REBIAS) ||
             (heuristics == HR_BULK_REVOKE)) {
    bulk_revoke_or_rebias_at_safepoint(obj, (heuristics == HR_BULK_REBIAS), false, NULL);
  }
  clean_up_cached_monitor_info();
}


void BiasedLocking::revoke_at_safepoint(GrowableArray<Handle>* objs) {
  assert(SafepointSynchronize::is_at_safepoint(), "must only be called while at safepoint");
  int len = objs->length();
  for (int i = 0; i < len; i++) {
    oop obj = (objs->at(i))();
    HeuristicsResult heuristics = update_heuristics(obj, false);
    if (heuristics == HR_SINGLE_REVOKE) {
      revoke_bias(obj, false, false, NULL, NULL);
    } else if ((heuristics == HR_BULK_REBIAS) ||
               (heuristics == HR_BULK_REVOKE)) {
      bulk_revoke_or_rebias_at_safepoint(obj, (heuristics == HR_BULK_REBIAS), false, NULL);
    }
  }
  clean_up_cached_monitor_info();
}


void BiasedLocking::preserve_marks() {
  if (!UseBiasedLocking)
    return;

  assert(SafepointSynchronize::is_at_safepoint(), "must only be called while at safepoint");

  assert(_preserved_oop_stack  == NULL, "double initialization");
  assert(_preserved_mark_stack == NULL, "double initialization");

  // In order to reduce the number of mark words preserved during GC
  // due to the presence of biased locking, we reinitialize most mark
  // words to the class's prototype during GC -- even those which have
  // a currently valid bias owner. One important situation where we
  // must not clobber a bias is when a biased object is currently
  // locked. To handle this case we iterate over the currently-locked
  // monitors in a prepass and, if they are biased, preserve their
  // mark words here. This should be a relatively small set of objects
  // especially compared to the number of objects in the heap.
  _preserved_mark_stack = new (ResourceObj::C_HEAP, mtInternal) GrowableArray<markOop>(10, true);
  _preserved_oop_stack = new (ResourceObj::C_HEAP, mtInternal) GrowableArray<Handle>(10, true);

  ResourceMark rm;
  Thread* cur = Thread::current();
  for (JavaThread* thread = Threads::first(); thread != NULL; thread = thread->next()) {
    if (thread->has_last_Java_frame()) {
      RegisterMap rm(thread);
      for (javaVFrame* vf = thread->last_java_vframe(&rm); vf != NULL; vf = vf->java_sender()) {
        GrowableArray<MonitorInfo*> *monitors = vf->monitors();
        if (monitors != NULL) {
          int len = monitors->length();
          // Walk monitors youngest to oldest
          for (int i = len - 1; i >= 0; i--) {
            MonitorInfo* mon_info = monitors->at(i);
            if (mon_info->owner_is_scalar_replaced()) continue;
            oop owner = mon_info->owner();
            if (owner != NULL) {
              markOop mark = owner->mark();
              if (mark->has_bias_pattern()) {
                _preserved_oop_stack->push(Handle(cur, owner));
                _preserved_mark_stack->push(mark);
              }
            }
          }
        }
      }
    }
  }
}


void BiasedLocking::restore_marks() {
  if (!UseBiasedLocking)
    return;

  assert(_preserved_oop_stack  != NULL, "double free");
  assert(_preserved_mark_stack != NULL, "double free");

  int len = _preserved_oop_stack->length();
  for (int i = 0; i < len; i++) {
    Handle owner = _preserved_oop_stack->at(i);
    markOop mark = _preserved_mark_stack->at(i);
    owner->set_mark(mark);
  }

  delete _preserved_oop_stack;
  _preserved_oop_stack = NULL;
  delete _preserved_mark_stack;
  _preserved_mark_stack = NULL;
}


int* BiasedLocking::total_entry_count_addr()                   { return _counters.total_entry_count_addr(); }
int* BiasedLocking::biased_lock_entry_count_addr()             { return _counters.biased_lock_entry_count_addr(); }
int* BiasedLocking::anonymously_biased_lock_entry_count_addr() { return _counters.anonymously_biased_lock_entry_count_addr(); }
int* BiasedLocking::rebiased_lock_entry_count_addr()           { return _counters.rebiased_lock_entry_count_addr(); }
int* BiasedLocking::revoked_lock_entry_count_addr()            { return _counters.revoked_lock_entry_count_addr(); }
int* BiasedLocking::fast_path_entry_count_addr()               { return _counters.fast_path_entry_count_addr(); }
int* BiasedLocking::slow_path_entry_count_addr()               { return _counters.slow_path_entry_count_addr(); }


// BiasedLockingCounters

int BiasedLockingCounters::slow_path_entry_count() {
  if (_slow_path_entry_count != 0) {
    return _slow_path_entry_count;
  }
  int sum = _biased_lock_entry_count   + _anonymously_biased_lock_entry_count +
            _rebiased_lock_entry_count + _revoked_lock_entry_count +
            _fast_path_entry_count;

  return _total_entry_count - sum;
}

void BiasedLockingCounters::print_on(outputStream* st) {
  tty->print_cr("# total entries: %d", _total_entry_count);
  tty->print_cr("# biased lock entries: %d", _biased_lock_entry_count);
  tty->print_cr("# anonymously biased lock entries: %d", _anonymously_biased_lock_entry_count);
  tty->print_cr("# rebiased lock entries: %d", _rebiased_lock_entry_count);
  tty->print_cr("# revoked lock entries: %d", _revoked_lock_entry_count);
  tty->print_cr("# fast path lock entries: %d", _fast_path_entry_count);
  tty->print_cr("# slow path lock entries: %d", slow_path_entry_count());
}
