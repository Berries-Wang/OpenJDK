/*
 * Copyright (c) 1999, 2014, Oracle and/or its affiliates. All rights reserved.
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

#ifndef SHARE_VM_MEMORY_THREADLOCALALLOCBUFFER_INLINE_HPP
#define SHARE_VM_MEMORY_THREADLOCALALLOCBUFFER_INLINE_HPP

#include "gc_interface/collectedHeap.hpp"
#include "memory/threadLocalAllocBuffer.hpp"
#include "runtime/atomic.hpp"
#include "runtime/thread.hpp"
#include "utilities/copy.hpp"

/**
 * 从TLAB 中分配内存空间。
 * 
 * @return 分配成功,返回对象地址;失败,返回NULL;
 */ 
inline HeapWord* ThreadLocalAllocBuffer::allocate(size_t size) {
  invariants();
  HeapWord* obj = top();
  // 判断是否有足够空间
  if (pointer_delta(end(), obj) >= size) {
    // successful thread-local allocation
#ifdef ASSERT
    // Skip mangling the space corresponding to the object header to
    // ensure that the returned space is not considered parsable by
    // any concurrent GC thread.
    size_t hdr_size = oopDesc::header_size();
    Copy::fill_to_words(obj + hdr_size, size - hdr_size, badHeapWordVal);
#endif // ASSERT
    // This addition is safe because we know that top is
    // at least size below end, so the add can't wrap.
    set_top(obj + size);

    invariants();
    return obj;
  }
  return NULL;
}

/**
 * 为Thread重新分配一个TLAB
 * 
 * @param obj_size:待分配内存空间的对象大小.单位(HeapWord),真实大小= obj_size * HeapWordSize * 8字节
 * 
 * Q: 新分配的TLAB的大小计算公式是什么样子的呢?
 */ 
inline size_t ThreadLocalAllocBuffer::compute_size(size_t obj_size) {
  const size_t aligned_obj_size = align_object_size(obj_size);

  // Compute the size for the new TLAB.
  // The "last" tlab may be smaller to reduce fragmentation.
  // unsafe_max_tlab_alloc is just a hint.
  // 当前HR中剩余可以给TLAB可分配的空间,即当前HeapRegion中可以使用的内存大小
  const size_t available_size = Universe::heap()->unsafe_max_tlab_alloc(myThread()) /
                                                  HeapWordSize;
  // TLAB 期望大小(desired_size()) + 当前需要分配的空间大小 
  size_t new_tlab_size = MIN2(available_size, desired_size() + aligned_obj_size);

  // Make sure there's enough room for object and filler int[].
  const size_t obj_plus_filler_size = aligned_obj_size + alignment_reserve();
  
  // 确保大小大于 dummy obj 对象头
  if (new_tlab_size < obj_plus_filler_size) {
    // If there isn't enough room for the allocation, return failure.
    if (PrintTLAB && Verbose) {
      gclog_or_tty->print_cr("ThreadLocalAllocBuffer::compute_size(" SIZE_FORMAT ")"
                    " returns failure",
                    obj_size);
    }
    return 0;
  }
  if (PrintTLAB && Verbose) {
    gclog_or_tty->print_cr("ThreadLocalAllocBuffer::compute_size(" SIZE_FORMAT ")"
                  " returns " SIZE_FORMAT,
                  obj_size, new_tlab_size);
  }
  return new_tlab_size;
}


/**
 * TLAB 慢速分配记录
 * 
 * @param obj_size ，慢速分配失败的对象大小.
 * 
 */ 
void ThreadLocalAllocBuffer::record_slow_allocation(size_t obj_size) {
  // Raise(提高) size required to bypass(绕开，避过) TLAB next time. Why? Else there's
  // a risk(危险，隐患) that a thread that repeatedly allocates objects of one
  // size will get stuck on this slow path.
  //提高下次绕过TLAB所需的尺寸。为什么?否则，重复分配一个大小相同的对象的线程将有被卡在这条缓慢路径上的风险。

  // 重新设置refill_waste_limit的值,避免重复进入到该代码分支，从而浪费性能
  set_refill_waste_limit(refill_waste_limit() + refill_waste_limit_increment());

  _slow_allocations++;

  if (PrintTLAB && Verbose) {
    Thread* thrd = myThread();
    gclog_or_tty->print("TLAB: %s thread: " INTPTR_FORMAT " [id: %2d]"
                        " obj: " SIZE_FORMAT
                        " free: " SIZE_FORMAT
                        " waste: " SIZE_FORMAT "\n",
                        "slow", p2i(thrd), thrd->osthread()->thread_id(),
                        obj_size, free(), refill_waste_limit());
  }
}

#endif // SHARE_VM_MEMORY_THREADLOCALALLOCBUFFER_INLINE_HPP
