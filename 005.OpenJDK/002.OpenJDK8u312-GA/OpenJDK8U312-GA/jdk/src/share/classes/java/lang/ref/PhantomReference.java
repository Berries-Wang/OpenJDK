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
 * Phantom reference objects, which are enqueued after the collector
 * determines that their referents may otherwise be reclaimed(回收的，再生的).  Phantom
 * references are most often used for scheduling pre-mortem cleanup actions in
 * a more flexible(灵活的) way than is possible with the Java finalization mechanism.<p/>
 * 虚引用对象，在垃圾收集器确定他们关联的对象被回收时加入队列。虚引用经常被用于比Java终结机制更灵活的调度之前的清理操作。
 *
 * > mortem: 事后检验
 *
 * <p> If the garbage collector determines at a certain point in time that the
 * referent of a phantom reference is <a
 * href="package-summary.html#reachability">phantom reachable</a>, then at that
 * time or at some later time it will enqueue the reference.<p/>
 * > 如果收集器在一个时间点确定这个虚引用关联的对象是虚可达的，那么在这个时间点或者稍后会将这个引用入队。
 *
 * <p> In order to ensure that a reclaimable object remains(剩余物，依然是) so, the referent of
 * a phantom reference may not be retrieved(恢复；补偿；挽救): The <code>get</code> method of a
 * phantom reference always returns <code>null</code>. <p/>
 * 为了确保可回收对象保持原样，虚引用关联的对象不会被挽救。虚引用的get方法永远返回null
 *
 * <p> Unlike soft and weak references, phantom references are not
 * automatically cleared by the garbage collector as they are enqueued.  An
 * object that is reachable via phantom references will remain so until all
 * such references are cleared or themselves(他们自己) become unreachable.<p/>
 * 与软引用和弱引用不同，虚引用在进入队列后不会被垃圾收集器自动清理。通过虚引用可访问的对象将保持不变，直到所有此类引用被清除或它们本身变得不可访问
 *
 * @author   Mark Reinhold
 * @since    1.2
 */

public class PhantomReference<T> extends Reference<T> {

    /**
     * Returns this reference object's referent.  Because the referent of a
     * phantom reference is always inaccessible, this method always returns
     * <code>null</code>.<p/>
     * 返回这个引用对象的关联对象。因为虚引用的关联对象永远是不可达的，这个方法永远返回null.
     *
     * @return  <code>null</code>
     */
    public T get() {
        return null;
    }

    /**
     * Creates a new phantom reference that refers to the given object and
     * is registered with the given queue.
     *
     * <p> It is possible to create a phantom reference with a <tt>null</tt>
     * queue, but such a reference is completely useless: Its <tt>get</tt>
     * method will always return null and, since it does not have a queue, it
     * will never be enqueued.<p/>
     *
     * 创建一个引用给定对象并注册到给定队列的新的虚引用。
     *
     * 可以用一个空队列创建一个虚引用，但是这样的引用是完全无用的:它的get方法总是返回null，而且，由于它没有队列，它永远不会被加入队列。
     *
     * @param referent the object the new phantom reference will refer to
     * @param q the queue with which the reference is to be registered,
     *          or <tt>null</tt> if registration is not required
     */
    public PhantomReference(T referent, ReferenceQueue<? super T> q) {
        super(referent, q);
    }

}
