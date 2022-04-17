/*
 * Copyright (c) 1997, 2017, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
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
 */

package java.lang.ref;

import sun.misc.Cleaner;
import sun.misc.JavaLangRefAccess;
import sun.misc.SharedSecrets;

/**
 * Abstract base class for reference objects.  This class defines the
 * operations common to all reference objects.  Because reference objects are
 * implemented in close cooperation(合作，协作) with the garbage collector, this class may
 * not be subclassed directly.
 * <p>
 * <p>
 * close cooperation： 紧密合作
 *
 * @author Mark Reinhold
 * @since 1.2
 */

public abstract class Reference<T> {

    /* A Reference instance is in one of four possible internal states:
     * 一个引用实例可能处于四种内部状态之一:
     *
     *     Active: Subject to special treatment by the garbage collector.  Some
     *     time after the collector detects(发现) that the reachability(可达性) of the
     *     referent has changed to the appropriate(合适的) state, it changes the
     *     instance's state to either Pending or Inactive, depending upon
     *     whether or not the instance was registered with a queue when it was
     *     created.  In the former(旧时的) case it also adds the instance to the
     *     pending-Reference list.  Newly-created instances are Active.
     *     Active： 受到垃圾收集器的特殊处理。
     *
     *     Pending: An element of the pending-Reference list, waiting to be
     *     enqueued by the Reference-handler thread.  Unregistered instances
     *     are never in this state.
     *     Pending: 在pending-Reference list中的一个元素，等待着被Reference-handler线程入队。
     *              未被注册的实例不会有该状态。
     *
     *     Enqueued: An element of the queue with which the instance was
     *     registered when it was created.  When an instance is removed from
     *     its ReferenceQueue, it is made Inactive.  Unregistered instances are
     *     never in this state.
     *     Enqueued：队列中的一个元素，在创建实例时，该实例被注册到其中。当一个实例从他的ReferenceQueue
     *               中移出的时候，他被置为Inactive;
     *               未被注册的实例不会有这个状态.
     *
     *     Inactive: Nothing more to do.  Once an instance becomes Inactive its
     *     state will never change again.
     *     非活跃: 不再做任何事。一旦一个实例变成了Inactive状态，他的状态将不会再发生改变。
     *
     * The state is encoded in the queue and next fields as follows:
     *
     *     Active: queue = ReferenceQueue with which instance is registered, or
     *     ReferenceQueue.NULL if it was not registered with a queue; next =
     *     null. 对象是活跃的，即是可达的对象或者对象是符合活跃规则的软引用对象
     *
     *     Pending: queue = ReferenceQueue with which instance is registered;
     *     next = this. 指对象进入pending-list,即将被送入引用队列
     *
     *     Enqueued: queue = ReferenceQueue.ENQUEUED; next = Following instance
     *     in queue, or this if at end of list. 指引用线程ReferenceHandler将pending_list的对象加入引用队列
     *
     *     Inactive: queue = ReferenceQueue.NULL; next = this.  对象不活跃，可以将对象回收了
     *
     * With this scheme(计划；方案) the collector need only examine(检查，调查) the next field in order
     * to determine whether a Reference instance requires special treatment(治疗；对待): If
     * the next field is null then the instance is active; if it is non-null,
     * then the collector should treat the instance normally.
     *
     * > 在这种方案下，收集器仅仅需要检查 next 字段来判断一个引用实例是否需要特别的处理:
     * >> 如果next 字段为null，则说明这个实例是活跃的；
     * >> 如果next 字段不是null，则收集器需要按照正常的流程处理这个实例。
     *
     * To ensure that a concurrent collector can discover active Reference
     * objects without interfering(adj. 干涉的；多管闲事的) with application threads that may apply
     * the enqueue() method to those objects, collectors should link
     * discovered objects through the discovered field. The discovered
     * field is also used for linking Reference objects in the pending list.
     *
     * 为了确保并发收集器能够发现活跃的引用对象且不受应用线程的影响，或许可以为这些调用enqueue方法。
     * 收集器应当通过通过discovered字段来链接发现的对象。discovered也被用于链接在pending list中的引用对象。
     */

    //Reference 指向的对象 ,即该引用关联的对象
    private T referent;         /* Treated specially by GC */


    /**
     * Reference所指向的队列，如果创建时没有指定，那么队列就是: ReferenceQueue.NULL,
     * 这是一个空队列，这个时候所有插入的对象都会被丢弃。这个队列一般是自定义，可以自己处理。
     * 示例： weakhashmap , FinalReference
     */
    volatile ReferenceQueue<? super T> queue;

    /*
     * 用于维护ReferenceQueue队列结构，在队列结构中指向下一个节点
     *
     * When active:   NULL
     *     pending:   this
     *    Enqueued:   next reference in queue (or this if last)
     *    Inactive:   this
     *
     * 不同的值代表不同的状态
     */
    @SuppressWarnings("rawtypes")
    volatile Reference next;

    /*
     * 用于维护 "pending-reference list" 队列结构，在队列结构中指向下一个节点，相当于next属性，不过各自维护不同的队列结构
     *
     * When active:   next element in a discovered reference list maintained(维护；维持) by GC (or this if last)
     *     pending:   next element in the pending list (or null if last)
     *   otherwise:   NULL
     *
     *  不同的状态对应着不同的值。该字段由JVM来使用(赋值)
     */
    transient private Reference<T> discovered;  /* used by VM */


    /* Object used to synchronize with the garbage collector.  The collector
     * must acquire this lock at the beginning of each collection cycle.  It is
     * therefore critical(至关重要的) that any code holding this lock complete as quickly
     * as possible, allocate no new objects, and avoid calling user code.
     *
     * Lock对象被用于垃圾收集器的同步，收集器必须在每个收集周期开始时获取这个锁。因此任何获取该锁的代码应尽可能快地执行。
     * 不要分配新对象，并避免调用用户代码。
     */
    static private class Lock {
    }

    private static Lock lock = new Lock();


    /* List of References waiting to be enqueued.  The collector adds(增加了)
     * References to this list, while the Reference-handler thread removes
     * them.  This list is protected by the above lock object. The
     * list uses the discovered field to link its elements.
     * >> 该list中的元素等待着被Reference-handler入队列，具体的可以查看Reference-handler逻辑。
     *
     * 静态变量，垃圾回收线程做的事就是将discovered的元素赋值到pending中，并且把JVM中的pending链表元素放到Reference类中的
     * Pending链表中
     *
     * 005.OpenJDK/002.OpenJDK8u312-GA/OpenJDK8U312-GA/hotspot/src/share/vm/gc_implementation/parNew/parNewGeneration.cpp#ParNewGeneration::collect
     * ->(调用) 005.OpenJDK/002.OpenJDK8u312-GA/OpenJDK8U312-GA/hotspot/src/share/vm/memory/referenceProcessor.cpp#ReferenceProcessor::enqueue_discovered_references
     * --> bool enqueue_discovered_ref_helper
     *
     *
     *
     * pending: 等待被回收
     */
    private static Reference<Object> pending = null;

    /* High-priority thread to enqueue pending References
     */
    private static class ReferenceHandler extends Thread {

        private static void ensureClassInitialized(Class<?> clazz) {
            try {
                Class.forName(clazz.getName(), true, clazz.getClassLoader());
            } catch (ClassNotFoundException e) {
                throw (Error) new NoClassDefFoundError(e.getMessage()).initCause(e);
            }
        }

        static {
            // pre-load and initialize InterruptedException and Cleaner classes
            // so that we don't get into trouble later in the run loop if there's
            // memory shortage while loading/initializing them lazily.
            ensureClassInitialized(InterruptedException.class);
            ensureClassInitialized(Cleaner.class);
        }

        ReferenceHandler(ThreadGroup g, String name) {
            super(g, name);
        }

        public void run() {
            while (true) {
                tryHandlePending(true);
            }
        }
    }

    /**
     * Reference 状态变更: <br/>
     * -> (Create) [Active]   <br/>
     * --> java.lang.ref.Reference#queue是否为null?  <br/>
     * Y: Inactive  <br/>
     * N: Pending  <br/>
     * ---> enqueue: 元素在pending-list中，由ReferencrHander enqueue <br/>
     * ---> Dequeue，出队就要自己处理了。  <br/>
     *
     * <p/>
     *
     * Try handle pending {@link Reference} if there is one.<p>
     * Return {@code true} as a hint that there might be another
     * {@link Reference} pending or {@code false} when there are no more pending
     * {@link Reference}s at the moment and the program can do some other
     * useful work instead of looping.<p>
     * <p>
     * 如果有的话，尝试处理pending Reference。<p>
     * 返回true，表示可能有另一个Reference挂起，或者返回false，表示此时没有更多的Reference挂起，程序可以做一些其他有用的工作，而不是循环。 <p>
     *
     * @param waitForNotify if {@code true} and there was no pending
     *                      {@link Reference}, wait until notified from VM
     *                      or interrupted; if {@code false}, return immediately
     *                      when there is no pending {@link Reference}.<p/>
     *                      如果为true，并且没有pending Refence,那么就等待直到被VM唤醒或者中断。
     *                      如果为false，则立即返回(没有pending Reference)
     * @return {@code true} if there was a {@link Reference} pending and it
     * was processed, or we waited for notification and either got it
     * or thread was interrupted before being notified;
     * {@code false} otherwise.
     */
    static boolean tryHandlePending(boolean waitForNotify) {
        Reference<Object> r;
        Cleaner c;
        try {
            synchronized (lock) {
                if (pending != null) {
                    r = pending;
                    // 'instanceof' might throw OutOfMemoryError sometimes
                    // so do this before un-linking 'r' from the 'pending' chain...
                    // 如果实现了Cleaner,则需要进行清理操作
                    c = r instanceof Cleaner ? (Cleaner) r : null;
                    // unlink 'r' from 'pending' chain
                    // discovered -> pending
                    pending = r.discovered;
                    r.discovered = null;
                } else {
                    // The waiting on the lock may cause an OutOfMemoryError
                    // because it may try to allocate exception objects.
                    if (waitForNotify) {
                        lock.wait();
                    }
                    // retry if waited
                    return waitForNotify;
                }
            }
        } catch (OutOfMemoryError x) {
            // Give other threads CPU time so they hopefully drop some live references
            // and GC reclaims some space.
            // Also prevent CPU intensive spinning in case 'r instanceof Cleaner' above
            // persistently throws OOME for some time...
            Thread.yield();
            // retry
            return true;
        } catch (InterruptedException x) {
            // retry
            return true;
        }

        // Fast path for cleaners
        if (c != null) {
            c.clean();
            return true;
        }

        ReferenceQueue<? super Object> q = r.queue;

        // enqueue执行之后，那么Reference 就变为了 "Enqueued" 状态了
        if (q != ReferenceQueue.NULL){
            q.enqueue(r);
        }
        return true;
    }


    /**
     * JVM 在启动之后有几个线程，其中一个就是ReferenceHandler,这个线程主要做的工作就是将
     * 上面提到的pending里面的元素送到队列中
     */
    static {
        ThreadGroup tg = Thread.currentThread().getThreadGroup();
        for (ThreadGroup tgn = tg;
             tgn != null;
             tg = tgn, tgn = tg.getParent())
            ;
        Thread handler = new ReferenceHandler(tg, "Reference Handler");
        /* If there were a special system-only priority greater than
         * MAX_PRIORITY, it would be used here
         */
        handler.setPriority(Thread.MAX_PRIORITY);
        handler.setDaemon(true);
        handler.start();

        // provide access in SharedSecrets
        SharedSecrets.setJavaLangRefAccess(new JavaLangRefAccess() {
            @Override
            public boolean tryHandlePendingReference() {
                return tryHandlePending(false);
            }
        });
    }

    /* -- Referent accessor and setters -- */

    /**
     * Returns this reference object's referent.  If this reference object has
     * been cleared, either by the program or by the garbage collector, then
     * this method returns <code>null</code>.
     *
     * @return The object to which this reference refers, or
     * <code>null</code> if this reference object has been cleared
     */
    public T get() {
        return this.referent;
    }

    /**
     * Clears this reference object.  Invoking this method will not cause this
     * object to be enqueued.
     *
     * <p> This method is invoked only by Java code; when the garbage collector
     * clears references it does so directly, without invoking this method.
     */
    public void clear() {
        this.referent = null;
    }


    /* -- Queue operations -- */

    /**
     * Tells whether or not this reference object has been enqueued, either by
     * the program or by the garbage collector.  If this reference object was
     * not registered with a queue when it was created, then this method will
     * always return <code>false</code>.
     *
     * @return <code>true</code> if and only if this reference object has
     * been enqueued
     */
    public boolean isEnqueued() {
        return (this.queue == ReferenceQueue.ENQUEUED);
    }

    /**
     * Adds this reference object to the queue with which it is registered,
     * if any.
     *
     * <p> This method is invoked only by Java code; when the garbage collector
     * enqueues references it does so directly, without invoking this method.
     *
     * @return <code>true</code> if this reference object was successfully
     * enqueued; <code>false</code> if it was already enqueued or if
     * it was not registered with a queue when it was created
     */
    public boolean enqueue() {
        return this.queue.enqueue(this);
    }


    /* -- Constructors -- */

    Reference(T referent) {
        this(referent, null);
    }

    Reference(T referent, ReferenceQueue<? super T> queue) {
        this.referent = referent;
        this.queue = (queue == null) ? ReferenceQueue.NULL : queue;
    }

}
