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

package java.util.concurrent.locks;

/**
 * A synchronizer that may be exclusively owned by a thread.  This
 * class provides a basis for creating locks and related synchronizers
 * that may entail a notion of ownership.  The
 * {@code AbstractOwnableSynchronizer} class itself does not manage or
 * use this information. However, subclasses and tools may use
 * appropriately maintained values to help control and monitor access
 * and provide diagnostics.
 * { 可以由线程以独占的方式拥有的同步器。此类为创建锁和相关同步器(伴随着所有权的概念)提供了基础。
 * AbstractOwnableSynchronizer类本身不管理或使用此信息，但是子类和工具可以使用释放维护的值
 * 帮助控制和见识访问以及提供诊断}
 *
 * @author Doug Lea
 * @since 1.6
 */
public abstract class AbstractOwnableSynchronizer
        implements java.io.Serializable {

    /**
     * Use serial ID even though all fields transient.
     */
    private static final long serialVersionUID = 3737899427754241961L;

    /**
     * Empty constructor for use by subclasses.
     */
    protected AbstractOwnableSynchronizer() {
    }

    /**
     * The current owner of exclusive mode synchronization.
     * 互斥模式同步下的当前线程，就是当前拥有锁的线程
     */
    private transient Thread exclusiveOwnerThread;

    /**
     * Sets the thread that currently owns exclusive access.
     * A {@code null} argument indicates that no thread owns access.
     * This method does not otherwise impose any synchronization or
     * {@code volatile} field accesses.
     *
     * @param thread the owner thread
     *               (设置当前拥有独占访问的线程。锁的拥有线程，null 参数表示没有线程拥有访问。
     *               此方法不另外施加任何同步或 volatile 字段访问)
     */
    protected final void setExclusiveOwnerThread(Thread thread) {
        exclusiveOwnerThread = thread;
    }

    /**
     * Returns the thread last set by {@code setExclusiveOwnerThread},
     * or {@code null} if never set.  This method does not otherwise
     * impose any synchronization or {@code volatile} field accesses.
     *
     * @return the owner thread
     * <p>
     * (返回由 setExclusiveOwnerThread 最后设置的线程； 如果从未设置，则返回 null。此方法不另外施加任何同步或 volatile 字段访问。)
     */
    protected final Thread getExclusiveOwnerThread() {
        return exclusiveOwnerThread;
    }
}
