/*
 * Copyright (c) 1997, 2003, Oracle and/or its affiliates. All rights reserved.
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

/**
 * Soft reference objects, which are cleared at the discretion(n.
 * 自行决定权，判断力；谨慎，慎重) of the garbage
 * collector in response to memory demand(v. 强烈要求；需要，需求). Soft references are
 * most often used
 * to implement memory-sensitive caches.
 * 软引用对象，垃圾收集器根据内存需求自行清除这些对象。软引用最常用于实现内存敏感的缓存。
 *
 * <p>
 * Suppose(v.猜想；假设,设想；) that the garbage collector determines(决心；确定) at a
 * certain(必然的;确定的;)
 * point in time that an object is
 * <a href="package-summary.html#reachability">softly
 * reachable</a>. At that time it may choose to clear atomically all soft
 * references to that object and all soft references to any other
 * softly-reachable objects from which that object is reachable through a chain
 * of strong references. At the same time or at some later time it will
 * enqueue those newly-cleared soft references that are registered with
 * reference queues.
 * 假如垃圾收集器在某一时刻确定一个对象是软可达的，此时，他可以选择原子性地清除对该对象的所有引用，以及通过强引用链从该对象中
 * 可访问的任何其他软可达对象的所有软引用。与此同时，或者在稍后的某个时间，它将把那些注册到引用队列中的新清除的软引用排队。
 * > 为LRU算法做预备吗?
 *
 * <p>
 * All soft references to softly-reachable objects are guaranteed(保证；确保) to have
 * been cleared before the virtual machine throws an
 * <code>OutOfMemoryError</code>. Otherwise no constraints(限制) are placed upon
 * the
 * time at which a soft reference will be cleared or the order in which a set
 * of such references to different objects will be cleared. Virtual machine
 * implementations are, however, encouraged to bias against clearing
 * recently-created or recently-used soft references.
 * 对软可达的对象的所有软引用在VM抛出OOM之前都会被清理掉，
 * 否则，对清除软引用的时间或清除对不同对象的一组此类引用的顺序没有任何限制。
 * VM是这样实现的，然而，鼓励偏向于不清除最近创建或最近使用的软引用
 * > 软引用清理策略
 *
 * place upon 把重点放在；强调；着重于
 * encouraged： 支持，促进
 * 
 * <p>
 * Direct instances of this class may be used to implement simple caches;
 * this class or derived subclasses may also be used in larger data structures
 * to implement more sophisticated caches. As long as the referent of a soft
 * reference is strongly reachable, that is, is actually in use, the soft
 * reference will not be cleared. Thus a sophisticated cache can, for example,
 * prevent its most recently used entries from being discarded by keeping
 * strong referents to those entries, leaving the remaining entries to be
 * discarded at the discretion of the garbage collector.
 * 这个Class的直接对象可以被用于实现简单的缓存，这个类或派生的子类也可以用于更大的数据结构中，以实现更复杂的缓存.
 * 只要软引用的引用(这个“引用”应译为对象)是强可达的，即实际上正在使用中，软引用就不会被清除.因此可以实现一个复杂的缓存。例如:
 * 通过保持对那些条目的强引用，防止最近使用的条目被丢弃，而剩下的条目则由垃圾收集器自行丢弃。
 * > 意思应该是： 即使对象是软引用，只要软引用对象包装的那个对象有强引用的存在，那么就不会被清理。
 *
 * @author Mark Reinhold
 * @since 1.2
 */

/**
 * 
 * 通过该类可以实现软引用,如java.lang.ThreadLocal.ThreadLocalMap.Entry
 * 
 */
public class SoftReference<T> extends Reference<T> {

    /**
     * Timestamp clock, updated by the garbage collector
     * 时间戳时间，由GC更新
     */
    static private long clock;

    /**
     * Timestamp updated by each invocation of the get method. The VM may use
     * this field when selecting soft references to be cleared, but it is not
     * required to do so.
     * 由每一次调用get方法时更新，VM用这个字段来选择需要被清理的软引用，但这不是必须的
     */
    private long timestamp;

    /**
     * Creates a new soft reference that refers to the given object. The new
     * reference is not registered with any queue.
     * 
     * 给指定的对象创建一个新的软引用，这个新的应用没有在任何队列中注册
     *
     * @param referent object the new soft reference will refer to
     */
    public SoftReference(T referent) {
        super(referent);
        this.timestamp = clock;
    }

    /**
     * Creates a new soft reference that refers to the given object and is
     * registered with the given queue.
     * 
     * 给指定的对象创建一个新的软引用，并且注册到指定的队列中
     * > 为什么要注册到队列中?
     *
     * @param referent object the new soft reference will refer to
     * @param q        the queue with which the reference is to be registered,
     *                 or <tt>null</tt> if registration is not required
     *
     */
    public SoftReference(T referent, ReferenceQueue<? super T> q) {
        super(referent, q);
        this.timestamp = clock;
    }

    /**
     * Returns this reference object's referent. If this reference object has
     * been cleared, either by the program or by the garbage collector, then
     * this method returns <code>null</code>.
     *
     * @return The object to which this reference refers, or
     *         <code>null</code> if this reference object has been cleared
     */
    public T get() {
        T o = super.get();
        if (o != null && this.timestamp != clock)
            this.timestamp = clock;
        return o;
    }

}
