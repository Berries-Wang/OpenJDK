/*
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

/*
 * This file is available under and governed by the GNU General Public
 * License version 2 only, as published by the Free Software Foundation.
 * However, the following notice accompanied the original version of this
 * file:
 *
 * Written by Doug Lea with assistance from members of JCP JSR-166
 * Expert Group and released to the public domain, as explained at
 * http://creativecommons.org/publicdomain/zero/1.0/
 */

package java.util.concurrent;

import java.util.Collection;
import java.util.Queue;

/**
 * A {@link java.util.Queue} that additionally(adv.此外;另外;更加;分外地;) supports operations
 * that wait for the queue to become non-empty when retrieving(v.取回;检索数据;) an
 * element, and wait for space to become available in the queue when
 * storing an element.
 * <p>额外提供在检索元素时，等待队列变为非空；在存储元素时，等待队列有空间可用这些操作的队列. - 即提供阻塞功能，阻塞到有元素可以返回或者有空间来存储元素.
 *
 * <p>{@code BlockingQueue} methods come in four forms, with different ways
 * of handling(n.处理;adj.操作的;v.负责；对待(handle 的ing形式)) operations that cannot be satisfied(adj.满意的;满足的;v.使满意; 翻译成调用成功更合适) immediately, but may be
 * satisfied at some point in the future:
 * <p>BlockingQueue 定义了四种模式的方法,不同的操作方式可能不能立即成功，但是可能在未来的某个时刻成功</p>
 * <li>one throws an exception, <p> 第一种抛出异常
 * <li>the second returns a special value (either {@code null} or {@code false}, depending on the operation),<p> 第二种返回null 或 false，取决于操作
 * <li>the third blocks the current thread indefinitely(adv.无限期的;不确定的;) until the operation can succeed,<p> 第三种是无限期阻塞当前线程直到操作成功
 * <li>and the fourth blocks for only a given maximum time limit before giving up. <p>第四种则是可以设置最大的超时时间
 * <p>These methods are summarized in the following table:
 * <p>
 * <table BORDER CELLPADDING=3 CELLSPACING=1>
 * <caption>Summary of BlockingQueue methods</caption>
 * <tr>
 * <td></td>
 * <td ALIGN=CENTER><em>Throws exception</em></td>
 * <td ALIGN=CENTER><em>Special value</em></td>
 * <td ALIGN=CENTER><em>Blocks</em></td>
 * <td ALIGN=CENTER><em>Times out</em></td>
 * </tr>
 * <tr>
 * <td><b>Insert</b></td>
 * <td>{@link #add add(e)}</td>
 * <td>{@link #offer offer(e)}</td>
 * <td>{@link #put put(e)}</td>
 * <td>{@link #offer(Object, long, TimeUnit) offer(e, time, unit)}</td>
 * </tr>
 * <tr>
 * <td><b>Remove</b></td>
 * <td>{@link #remove remove()}</td>
 * <td>{@link #poll poll()}</td>
 * <td>{@link #take take()}</td>
 * <td>{@link #poll(long, TimeUnit) poll(time, unit)}</td>
 * </tr>
 * <tr>
 * <td><b>Examine(v.检查;调查;考核;测验; 这里应该指的是检索元素)</b></td>
 * <td>{@link #element element()}</td>
 * <td>{@link #peek peek()}</td>
 * <td><em>not applicable</em></td>
 * <td><em>not applicable (不适用)</em></td>
 * </tr>
 * </table>
 *
 * <p>A {@code BlockingQueue} does not accept {@code null} elements.
 * Implementations throw {@code NullPointerException} on attempts
 * to {@code add}, {@code put} or {@code offer} a {@code null}.  A
 * {@code null} is used as a sentinel(n.哨兵;vt.守卫;放哨；) value to indicate failure of
 * {@code poll} operations.
 * <p>BlockingQueue 不接受null元素，当尝试add、put 、offer一个null时，BlockingQueue的实现者会抛出NPE
 * null 是一个标记值，用于表示poll是一个失败的操作。
 * <p>
 *
 * <p>A {@code BlockingQueue} may be capacity bounded(adj.有界限的;). At any given
 * time it may have a {@code remainingCapacity} beyond which no
 * additional elements can be {@code put} without blocking.
 * A {@code BlockingQueue} without any intrinsic(adj.内在的;固有的;) capacity constraints(n.约束;限制;) always
 * reports a remaining(adj.剩下的;遗留的;) capacity of {@code Integer.MAX_VALUE}.
 * <p>BlockingQueue 可能有容量限制，在任何给定的时间，它都可能有一个剩余容量，超过这个容量就不能添加额外的元素而不会阻塞。
 * BlockingQueue 没有内在的容量限制，报告的总是基于Integer.MAX_VALUE的剩余容量。
 * <p>
 * <p>{@code BlockingQueue} implementations are designed to be used
 * primarily for producer-consumer queues, but additionally support
 * the {@link java.util.Collection} interface.  So, for example, it is
 * possible to remove an arbitrary(adj.任意的;随心所欲的;) element from a queue using
 * {@code remove(x)}. However, such operations are in general(adj.总体的;普遍的;)
 * <em>not</em> performed very efficiently(adv.高效地), and are intended(adj.预期地;打算中的;v.意思是；计划;打算) for only
 * occasional(adj.偶尔的;不经常的;) use, such as when a queued message is cancelled.
 * <p> BlockingQueue的实现 被设计主要用户生产-消费队列，但另外的一些也支持java.util.Collection的接口，
 * 所以，例如，他可能支持使用remove(x)方法从队列中移除一个元素。然而，这类操作一般表现并不高效且并不打算经常使用，例如队列中的消息被取消时.
 *
 * <p>
 * <p>{@code BlockingQueue} implementations are thread-safe.  All
 * queuing(v.排队，queue的ing形式) methods achieve(v.达到；取得；实现) their effects atomically using internal
 * locks or other forms of concurrency control. However, the
 * <em>bulk</em> Collection operations {@code addAll},
 * {@code containsAll}, {@code retainAll} and {@code removeAll} are
 * <em>not</em> necessarily(adv.必定;必然;必要地;) performed atomically unless specified
 * otherwise in an implementation. So it is possible, for example, for
 * {@code addAll(c)} to fail (throwing an exception) after adding
 * only some of the elements in {@code c}.
 * <p>BlockingQueue的实现是线程安全的，所有排队方法都使用内部锁或其他形式的并发控制原子地实现它们的效果
 * 然而,大批量的集合操作: addAll 、containsAll , retainAll , removeAll 并不是一定要是原子性的，除非在实现中另有指定
 *
 * <p>
 *
 * <p>A {@code BlockingQueue} does <em>not</em> intrinsically(adv.本质地;固有地;) support
 * any kind of &quot;close&quot; or &quot;shutdown&quot; operation to
 * indicate that no more items will be added.  The needs and usage of
 * such features(n.产品特点;特征;v.是..的特色;) tend(v.倾向于;往往会;) to be implementation-dependent(实现相关的). For example, a
 * common tactic(n.策略;手法;adj.按顺序的;) is for producers to insert special
 * <em>end-of-stream(结束)</em> or <em>poison(毒药;)</em> objects, that are
 * interpreted(v.解释;说明;) accordingly(adv.相应地;因此;所以;) when taken by consumers.
 * <p> BlockingQueue 并不支持close 或者 shutdown操作来说明没有更多的元素将要被添加。
 * 这个需求和用法更倾向于与实现相关.例如:一种常见的策略是生产者插入特殊的流结束对象或有毒对象，当消费者使用这些对象时进行相应的解释。
 * <p>
 * Usage example, based on a typical producer-consumer scenario(n.设想，可能发生的情况;).
 * Note that a {@code BlockingQueue} can safely be used with multiple
 * producers and multiple consumers.
 * <pre> {@code
 * class Producer implements Runnable {
 *   private final BlockingQueue queue;
 *   Producer(BlockingQueue q) { queue = q; }
 *   public void run() {
 *     try {
 *       while (true) { queue.put(produce()); }
 *     } catch (InterruptedException ex) { ... handle ...}
 *   }
 *   Object produce() { ... }
 * }
 *
 * class Consumer implements Runnable {
 *   private final BlockingQueue queue;
 *   Consumer(BlockingQueue q) { queue = q; }
 *   public void run() {
 *     try {
 *       while (true) { consume(queue.take()); }
 *     } catch (InterruptedException ex) { ... handle ...}
 *   }
 *   void consume(Object x) { ... }
 * }
 *
 * class Setup {
 *   void main() {
 *     BlockingQueue q = new SomeQueueImplementation();
 *     Producer p = new Producer(q);
 *     Consumer c1 = new Consumer(q);
 *     Consumer c2 = new Consumer(q);
 *     new Thread(p).start();
 *     new Thread(c1).start();
 *     new Thread(c2).start();
 *   }
 * }}</pre>
 *
 * <p>Memory consistency(n.一致性，连贯性；黏稠度，平滑度) effects: As with other concurrent
 * collections, actions in a thread prior(adj.先前的;事先的;优先的；更重要的；) to placing an object into a
 * {@code BlockingQueue}
 * <a href="package-summary.html#MemoryVisibility"><i>happen-before</i></a>
 * actions subsequent(adj.随后的；接着的;) to the access or removal(n.移走;去掉;) of that element from
 * the {@code BlockingQueue} in another thread.
 *
 * <p>This interface is a member of the
 * <a href="{@docRoot}/../technotes/guides/collections/index.html">
 * Java Collections Framework</a>.
 *
 * @param <E> the type of elements held in this collection
 * @author Doug Lea
 * @since 1.5
 */
public interface BlockingQueue<E> extends Queue<E> {
    /**
     * Inserts the specified element into this queue if it is possible to do
     * so immediately without violating（v.违背，违反（violate 的现在分词）） capacity restrictions(n.限制；限制条件（restriction 的复数）),
     * returning {@code true} upon(adv.在上面地;此后;prep.在..上;用在一些动词后) success and throwing an
     * {@code IllegalStateException} if no space is currently available.
     * When using a capacity-restricted queue, it is generally preferable(adj.更好的;更合适的；) to
     * use {@link #offer(Object) offer}.
     * <p> 在没有违背容量限制的条件下立即向队列中插入指定的元素，在成功后返回true;或者在没有空间可用的时候抛出一个IllegalStateException
     * 异常。当适用容量受限制的队列时，使用offer更为合适。
     *
     * @param e the element to add 需要添加的元素
     * @return {@code true} (as(adv.同样;一样;例如;conj.像...一样;照..方式;正如;如同;在...时候;虽然;尽管;prep.被看作...;作为) specified by {@link Collection#add})
     * @throws IllegalStateException    if the element cannot be added at this
     *                                  time due to capacity restrictions
     * @throws ClassCastException       if the class of the specified element
     *                                  prevents it from being added to this queue
     * @throws NullPointerException     if the specified element is null
     * @throws IllegalArgumentException if some property of the specified
     *                                  element prevents it from being added to this queue
     */
    boolean add(E e);

    /**
     * Inserts the specified element into this queue if it is possible to do
     * so immediately without violating capacity restrictions, returning
     * {@code true} upon success and {@code false} if no space is currently
     * available.  When using a capacity-restricted queue, this method is
     * generally preferable to {@link #add}, which can fail to insert an
     * element only by throwing an exception.
     * <p>在没有违背容量限制的条件下立即向队列中插入指定的元素，在成功后返回true;或者在没有空间可用的时候返回false。
     * 当适用容量受限制的队列时，使用add更为合适。
     *
     * @param e the element to add
     * @return {@code true} if the element was added to this queue, else
     * {@code false}
     * @throws ClassCastException       if the class of the specified element
     *                                  prevents it from being added to this queue
     * @throws NullPointerException     if the specified element is null
     * @throws IllegalArgumentException if some property of the specified
     *                                  element prevents it from being added to this queue
     */
    boolean offer(E e);

    /**
     * Inserts the specified element into this queue, waiting if necessary  for space to become available.
     * <p> 向队列中插入指定的元素，阻塞插入操作直到有空间可用。
     *
     * @param e the element to add
     * @throws InterruptedException     if interrupted while waiting
     * @throws ClassCastException       if the class of the specified element
     *                                  prevents it from being added to this queue
     * @throws NullPointerException     if the specified element is null
     * @throws IllegalArgumentException if some property of the specified
     *                                  element prevents it from being added to this queue
     */
    void put(E e) throws InterruptedException;

    /**
     * Inserts the specified element into this queue, waiting up to the
     * specified wait time if necessary for space to become available.
     * <p>将指定的元素插入到该队列中，如果需要空间可用，则等待到指定的等待时间。
     *
     * @param e       the element to add
     * @param timeout how long to wait before giving up, in units of
     *                {@code unit}
     * @param unit    a {@code TimeUnit} determining how to interpret the
     *                {@code timeout} parameter
     * @return {@code true} if successful, or {@code false} if the specified waiting time elapses(v.消逝;n.时间的流逝;) before space is available
     * @throws InterruptedException     if interrupted while waiting
     * @throws ClassCastException       if the class of the specified element
     *                                  prevents it from being added to this queue
     * @throws NullPointerException     if the specified element is null
     * @throws IllegalArgumentException if some property of the specified
     *                                  element prevents it from being added to this queue
     */
    boolean offer(E e, long timeout, TimeUnit unit)
            throws InterruptedException;

    /**
     * Retrieves(vt.检索;恢复;重新得到;) and removes the head of this queue, waiting if necessary
     * until an element becomes available.
     * <p>当没有元素返回时，阻塞，直到有元素可以返回.</p>
     *
     * @return the head of this queue
     * @throws InterruptedException if interrupted while waiting
     */
    E take() throws InterruptedException;

    /**
     * Retrieves(vt.检索;恢复;重新得到;)  and removes the head of this queue, waiting up to the
     * specified wait time if necessary for an element to become available.
     * <p>当没有元素时，等待一段时间。
     *
     * @param timeout how long to wait before giving up, in units of
     *                {@code unit}
     * @param unit    a {@code TimeUnit} determining how to interpret the
     *                {@code timeout} parameter
     * @return the head of this queue, or {@code null} if the specified waiting time elapses before an element is available. 返回:队列的头部元素或者等待一段时间后依然没有可用的元素,那么久返回null.
     * @throws InterruptedException if interrupted while waiting
     */
    E poll(long timeout, TimeUnit unit)
            throws InterruptedException;

    /** 重要!!!
     * Returns the number of additional(adj.附加的;额外的;) elements that this queue can ideally(adv.理想的;观念上地;)
     * (in the absence(n. 缺席，缺勤，不在；缺乏，没有；缺席期间，休假期间；不注意) of memory or resource constraints(n.约束;限制;约束条件;)) accept without
     * blocking, or {@code Integer.MAX_VALUE} if there is no intrinsic(adj.内在的;固有的;) limit.
     *<p>返 回这个队列在理想情况下(在没有内存或资源约束的情况下)可以不阻塞地接受的额外元素的数量，或返回Integer.MAX_VALUE，如果没有内在限制。
     *
     * <p>Note that you <em>cannot</em> always tell if an attempt to insert
     * an element will succeed by inspecting(v.检查) {@code remainingCapacity}
     * because it may be the case that another thread is about to
     * insert or remove an element.
     * <p>注意，你>>>不能<<根据检查remainingCapacity的返回值来判断是否需要往队列中插入元素，因为其他线程可能去插入或者移除一个元素.
     *
     * @return the remaining capacity . 剩余的容量
     */
    int remainingCapacity();

    /**
     * Removes a single instance of the specified element from this queue,
     * if it is present.  More formally(更正式地), removes an element {@code e} such
     * that {@code o.equals(e)}, if this queue contains one or more such
     * elements.
     * Returns {@code true} if this queue contained the specified element
     * (or equivalently(adv.相当于;相等地;), if this queue changed as a result of the call).
     *
     * <p> 当一个元素包含多次，那么移除一个还是多个呢?
     *
     * @param o element to be removed from this queue, if present
     * @return {@code true} if this queue changed as a result of the call
     * @throws ClassCastException   if the class of the specified element
     *                              is incompatible with this queue
     *                              (<a href="../Collection.html#optional-restrictions">optional</a>)
     * @throws NullPointerException if the specified element is null
     *                              (<a href="../Collection.html#optional-restrictions">optional</a>)
     */
    boolean remove(Object o);

    /**
     * Returns {@code true} if this queue contains the specified element.
     * More formally, returns {@code true} if and only if this queue contains
     * at least one element {@code e} such that {@code o.equals(e)}.
     *
     * @param o object to be checked for containment in this queue
     * @return {@code true} if this queue contains the specified element
     * @throws ClassCastException   if the class of the specified element
     *                              is incompatible with this queue
     *                              (<a href="../Collection.html#optional-restrictions">optional</a>)
     * @throws NullPointerException if the specified element is null
     *                              (<a href="../Collection.html#optional-restrictions">optional</a>)
     */
    public boolean contains(Object o);

    /**
     * Removes all available elements from this queue and adds them
     * to the given collection.  This operation may be more
     * efficient than repeatedly polling this queue.  A failure
     * encountered(v. 遇到；曾遭遇（encounter 的过去式）) while attempting to add elements to
     * collection {@code c} may result in elements being in neither,
     * either or both collections when the associated(adj.有关联的;相关的;v.联想;支持;) exception is
     * thrown.  Attempts to drain(v.排出;滤干;n.下水道;流失;消耗;) a queue to itself result in
     * {@code IllegalArgumentException}. Further(adv.进一步;此外;而且;), the behavior of
     * this operation is undefined if the specified collection is
     * modified while the operation is in progress.
     * <p>从队列中移除所有的元素且将他们添加到指定的集合中. 这个操作或许比重复从队列中poll更高效.
     * 在向集合c添加元素时可能发生异常从而导致元素既不在集合中，也不在其中一个或两个集合中。此外，如果在操作进行过程中修改了指定的集合，则此操作的行为未定义。
     *
     *
     * @param c the collection to transfer elements into
     * @return the number of elements transferred
     * @throws UnsupportedOperationException if addition of elements
     *                                       is not supported by the specified collection
     * @throws ClassCastException            if the class of an element of this queue
     *                                       prevents it from being added to the specified collection
     * @throws NullPointerException          if the specified collection is null
     * @throws IllegalArgumentException      if the specified collection is this
     *                                       queue, or some property of an element of this queue prevents
     *                                       it from being added to the specified collection
     */
    int drainTo(Collection<? super E> c);

    /**
     * Removes at most the given number of available elements from
     * this queue and adds them to the given collection.  A failure
     * encountered while attempting to add elements to
     * collection {@code c} may result in elements being in neither,
     * either or both collections when the associated exception is
     * thrown.  Attempts to drain a queue to itself result in
     * {@code IllegalArgumentException}. Further, the behavior of
     * this operation is undefined if the specified collection is
     * modified while the operation is in progress.
     * <p>从队列中获取指定数量的元素到集合中.
     *
     * @param c           the collection to transfer elements into
     * @param maxElements the maximum number of elements to transfer
     * @return the number of elements transferred
     * @throws UnsupportedOperationException if addition of elements
     *                                       is not supported by the specified collection
     * @throws ClassCastException            if the class of an element of this queue
     *                                       prevents it from being added to the specified collection
     * @throws NullPointerException          if the specified collection is null
     * @throws IllegalArgumentException      if the specified collection is this
     *                                       queue, or some property of an element of this queue prevents
     *                                       it from being added to the specified collection
     */
    int drainTo(Collection<? super E> c, int maxElements);
}
