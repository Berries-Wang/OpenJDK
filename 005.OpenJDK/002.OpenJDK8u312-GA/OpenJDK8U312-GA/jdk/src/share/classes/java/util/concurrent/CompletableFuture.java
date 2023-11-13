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

import java.util.function.Supplier;
import java.util.function.Consumer;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.BiFunction;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletionException;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.locks.LockSupport;

/**
 * A {@link Future} that may be explicitly completed (setting its
 * value and status), and may be used as a {@link CompletionStage},
 * supporting dependent functions and actions that trigger upon its
 * completion.(可以显式完成的Future(设置其值和状态)，可以用作CompletionStage，支持在其完成时触发的依赖函数和操作。)
 *
 * <p>
 * When two or more threads attempt to
 * {@link #complete complete},
 * {@link #completeExceptionally completeExceptionally}, or
 * {@link #cancel cancel}
 * a CompletableFuture, only one of them succeeds.
 * (当两个或多个线程尝试complete、completeexception或取消一个CompletableFuture时，只有一个线程成功。)
 *
 * <p>
 * In addition to these and related(adj.相关的，有联系的；) methods for directly
 * manipulating(v.操纵；假造；手动（manipulate 的 ing 形式) status and results,
 * CompletableFuture implements
 * interface {@link CompletionStage} with the following policies:
 * (除了这些和直接操作状态和结果的相关方法之外，CompletableFuture用以下策略实现了接口CompletionStage:)
 *
 * <ul>
 *
 * <li>Actions supplied for dependent completions of
 * <em>non-async</em> methods may be performed by the thread that
 * completes the current CompletableFuture, or by any other caller of
 * a completion
 * method.(为非异步方法的依赖完成提供的操作可以由完成当前CompletableFuture的线程执行，也可以由完成方法的任何其他调用者执行。)</li>
 *
 * <li>All <em>async</em> methods without an explicit Executor
 * argument are performed using the {@link ForkJoinPool#commonPool()}
 * (unless it does not support a parallelism level of at least two, in
 * which case, a new Thread is created to run each task). To simplify
 * monitoring, debugging, and tracking, all generated asynchronous
 * tasks are instances of the marker interface {@link
 * AsynchronousCompletionTask}.
 * (所有没有显式Executor参数的异步方法都使用ForkJoinPool.commonPool()执行(除非它不支持至少两个并行级别，在这种情况下，创建一个新线程来运行每个任务)。
 * 为了简化监视、调试和跟踪，所有生成的异步任务都是标记接口CompletableFuture.AsynchronousCompletionTask的实例。)</li>
 *
 * <li>All CompletionStage methods are implemented independently(adv.独立地；自立地) of
 * other public methods, so the behavior of one method is not
 * impacted(v.装紧；挤满（impact 的过去分词）)
 * by overrides of others in subclasses.
 * (所有CompletionStage方法的实现都独立于其他公共方法，因此一个方法的行为不受子类中其他方法的覆盖的影响。)</li>
 * </ul>
 *
 * <p>
 * CompletableFuture also implements {@link Future} with the following
 * policies:
 * <ul>
 *
 * <li>Since (unlike {@link FutureTask}) this class has no direct
 * control over the computation that causes it to be completed,
 * cancellation is treated as just another form of exceptional
 * completion. Method {@link #cancel cancel} has the same effect as
 * {@code completeExceptionally(new CancellationException())}. Method
 * {@link #isCompletedExceptionally} can be used to determine if a
 * CompletableFuture completed in any exceptional
 * fashion.(由于(与FutureTask不同)该类对导致其完成的计算没有直接控制，因此取消被视为另一种形式的异常完成。
 * 方法cancel与completeExceptionally(new
 * CancellationException())具有相同的效果。方法iscompletedexception可用于确定CompletableFuture是否以任何异常方式完成。)</li>
 *
 * <li>In case of exceptional completion with a CompletionException,
 * methods {@link #get()} and {@link #get(long, TimeUnit)} throw an
 * {@link ExecutionException} with the same cause as held in the
 * corresponding CompletionException. To simplify usage in most
 * contexts, this class also defines methods {@link #join()} and
 * {@link #getNow} that instead throw the CompletionException directly
 * in these cases.(如果出现 CompletionException 异常完成，方法 get() 和 get(long, TimeUnit)
 * 会抛出 ExecutionException，其原因与相应 CompletionException 中的原因相同。
 * 为了简化大多数上下文中的使用，此类还定义了 join() 和 getNow 方法，在这些情况下它们直接抛出
 * CompletionException。)</li>
 * </ul>
 *
 * @author Doug Lea
 * @since 1.8
 */
public class CompletableFuture<T> implements Future<T>, CompletionStage<T> {

    /*
     * Overview:
     *
     * A CompletableFuture may have dependent completion actions,
     * collected in a linked stack. It atomically completes by CASing
     * a result field, and then pops off and runs those actions. This
     * applies across normal vs exceptional outcomes, sync vs async
     * actions, binary triggers, and various forms of completions.
     * <p>
     * CompletableFuture 可能有依赖的完成操作，这些操作收集在栈中。
     * 他通过CAS操作result字段来自动完成，并且然后弹出并执行这些动作。
     * 这适用于正常结果与异常结果、同步操作与异步操作、二进制触发器以及各种形式的完成。
     * </p>
     *
     * [**执行结束的标记**]Non-nullness of field result (set via CAS) indicates done. An
     * AltResult is used to box null as a result, as well as to hold
     * exceptions. Using a single field makes completion simple to
     * detect and trigger. Encoding and decoding is straightforward(直截了当的)
     * but adds to the sprawl of trapping and associating exceptions
     * with targets. Minor（较小的) simplifications(简单化) rely on (static) NIL (to
     * box null results) being the only AltResult with a null
     * exception field, so we don't usually need explicit comparisons.
     * Even though some of the generics casts are unchecked (see
     * SuppressWarnings annotations), they are placed to be
     * appropriate even if checked.
     * <p>
     * reslut字段非空（通过 CAS 设置）标志着执行完成。 AltResult 用于将null装箱作为执行结果，以及保存异常。
     * 使用单个字段使完成易于检测和触发。 编码和解码很简单，但会增加捕获异常以及将异常与目标关联起来的蔓延。
     * 较小的简化依赖于NIL是唯一具有空异常字段的 AltResult，因此我们通常不需要显式比较。
     * 即使某些泛型转换未被检查（请参阅 SuppressWarnings 注释），即使选中它们也会被放置在适当的位置。
     * </p>
     * 
     * Dependent actions are represented by Completion objects linked
     * as Treiber stacks headed by field "stack". There are Completion
     * classes for each kind of action, grouped into single-input
     * (UniCompletion), two-input (BiCompletion), projected
     * (BiCompletions using either (not both) of two inputs), shared
     * (CoCompletion, used by the second of two sources), zero-input
     * source actions, and Signallers that unblock waiters. Class
     * Completion extends ForkJoinTask to enable async execution
     * (adding no space overhead because we exploit its "tag" methods
     * to maintain claims). It is also declared as Runnable to allow
     * usage with arbitrary executors.
     * <p>
     * 依赖的动作由存储在以stack为首的Treiber Stack里的Completion对象表示。
     * 每种类型的操作都有Completion类，分为:
     * * * - single-input(uniccompletion)
     * * * - two-input(biccompletion)
     * * * - projected(biccompletions使用两种输入中的一种(不是两种))、
     * * * - shared(CoCompletion，由两个源中的第二个使用)、
     * * * - zero-input
     * * * - 解除等待阻塞的Signallers
     * Class Completion扩展了ForkJoinTask以支持异步执行(没有增加空间开销，因为我们利用了它的“tag”方法来维护声明)。
     * 它还被声明为Runnable，以允许任意执行器使用。
     * </p>
     *
     * Support for each kind of CompletionStage relies on a separate
     * class, along with two CompletableFuture methods:
     * (对每种CompletionStage的支持依赖于一个单独的类，以及两个CompletableFuture方法:)
     *
     * * A Completion class with name X corresponding to function,
     * prefaced<sup>v.为……作序；以……作为开场白，作为……的开端（preface 的过去式和过去分词）</sup> with "Uni",
     * "Bi", or "Or". Each class contains
     * fields for source(s), actions, and dependent. They are
     * boringly(adv.无趣地；沉闷地) similar, differing from others only with respect to
     * underlying(adj.根本的，潜在的；表面下的，下层的；) functional forms. We do this so that users
     * don't encounter(v.遭遇；偶遇，邂逅) layers of adaptors in common usages. We also
     * include "Relay"(v.传递，传达) classes/methods that don't correspond to user
     * methods; they copy results from one stage to another.
     * > with respect to : 就...而言,关于
     * <p>
     * 以X为名的Completion类与以"Uni"、"Bi"、"Or"为前缀的函数相对应，每个类都包含
     * source(s)、actions、dependent字段。他们极其相似，不同之处在于表面下的功能形式。
     * 我们这样做是为了让用户在日常使用中不会遇到适配器层.我们还包含与用户方法不对应的中继方法，
     * 他们将结果从一个stage复制到另一个。
     * </p>
     *
     * * Boolean CompletableFuture method x(...) (for example
     * uniApply) takes all of the arguments needed to check that an
     * action is triggerable, and then either runs the action or
     * arranges(v.安排,筹划;) its async execution by executing its Completion
     * argument, if present. The method returns true if known to be
     * complete.
     * <p>
     * 返回值为Boolean的CompletableFuture方法(如uniApply)会拿到所有要校验的参数去判断一个action是否
     * 可以被触发，然后要么执行这个action或安排他异步执行Completion参数，如果完成了，这个方法会返回true.
     * </p>
     *
     * * Completion method tryFire(int mode) invokes the
     * associated(adj.有关联的，相关的；联合的，联营的) x
     * method with its held arguments, and on success cleans up.
     * The mode argument allows tryFire to be called twice (SYNC,
     * then ASYNC); the first to screen and trap exceptions while
     * arranging(v.安排（arrange 的 ing 形式）) to execute, and the second when called from
     * a task. (A few classes are not used async so take slightly
     * different forms.) The claim() callback suppresses function
     * invocation if already claimed by another thread.
     * (完成方法 tryFire(int mode) 使用其保存的参数调用关联的 x 方法，并在成功时进行清理。
     * mode 参数允许 tryFire 被调用两次（SYNC，然后 ASYNC）； 第一个在安排执行时筛选并捕获异常，第二个在从任务调用时进行。
     * （一些类不是异步使用的，因此采用略有不同的形式。）如果已被另一个线程声明，则claim()回调会抑制函数调用。)
     *
     * * CompletableFuture method xStage(...) is called from a public
     * stage method of CompletableFuture x. It screens(screen复数,n.屏幕;v.屏蔽;) user
     * arguments and invokes and/or creates the stage object. If
     * not async and x is already complete, the action is run
     * immediately(adv.立即;马上). Otherwise a Completion c is created, pushed to
     * x's stack (unless done), and started or triggered via
     * c.tryFire. This also covers races possible if x completes
     * while pushing. Classes with two inputs (for example BiApply)
     * deal with races across both while pushing actions. The
     * second completion is a CoCompletion pointing to the first,
     * shared so that at most one performs the action. The
     * multiple-arity methods allOf and anyOf do this
     * pairwise(adv.成对地;成双地;adj.成对发生的) to
     * form trees of completions.
     *
     * Note that the generic type parameters of methods vary(变化;改变;) according
     * to whether "this" is a source, dependent, or completion.
     *
     * Method postComplete is called upon(将要发生，马上来临) completion unless the target
     * is guaranteed not to be observable (i.e., not yet returned or
     * linked). Multiple threads can call postComplete, which
     * atomically pops each dependent action, and tries to trigger it
     * via method tryFire, in NESTED mode. Triggering can propagate
     * recursively, so NESTED mode returns its completed dependent (if
     * one exists) for further processing by its caller (see method
     * postFire). (方法 postComplete 在完成时被调用，除非目标保证不可观察（即，尚未返回或链接）。
     * 多个线程可以调用 postComplete，它会自动弹出每个相关操作，并尝试通过方法 tryFire 在嵌套模式下触发它。
     * 触发可以递归传播，因此 NESTED 模式返回其完整的依赖项（如果存在）以供其调用者进一步处理（请参阅方法 postFire）。)
     *
     * Blocking methods get() and join() rely(依赖) on Signaller Completions
     * that wake up waiting threads. The mechanics(n.机械学，力学；机制，运作方式；机械部件，运转部件；结构，构成)
     * are similar to
     * Treiber stack wait-nodes used in FutureTask, Phaser, and
     * SynchronousQueue. See their internal documentation for
     * algorithmic details.
     * <p>
     * 阻塞方法依赖Signaller Completions唤醒等待的线程。
     * </p>
     *
     * Without precautions(预防措施), CompletableFutures would be prone to
     * garbage accumulation as chains of Completions build up, each
     * pointing back to its sources. So we null out fields as soon as
     * possible (see especially method Completion.detach). The
     * screening checks needed anyway harmlessly ignore null arguments
     * that may have been obtained during races with threads nulling
     * out fields. We also try to unlink fired Completions from
     * stacks that might never be popped (see method postFire).
     * Completion fields need not be declared as final or volatile
     * because they are only visible to other threads upon safe
     * publication.
     * (如果不采取预防措施，随着Completions链的建立，CompletableFutures 很容易出现垃圾堆积，每个完成链都指向其来源。
     * 因此，我们尽快清空字段（特别参见方法 Completion.detach）。
     * 无论如何，所需的筛选检查都会无害地忽略在线程清空字段的竞争期间可能获得的空参数。
     * 我们还尝试从可能永远不会弹出的堆栈中取消触发的完成（请参阅方法 postFire）。 完成字段不需要声明为 Final 或
     * 易失性的，因为它们仅在安全发布时对其他线程可见。)
     * 
     * prone to: 倾向于倾于易于做某事
     * accumulation : n.积累，堆积；堆积物，堆积量
     */

    /**
     * 如上注释: result 非空表示完成: 异常 or 正常
     */
    volatile Object result; // Either the result or boxed AltResult
    /**
     * Top of Treiber stack of dependent actions
     * <p>
     * ? dependent actions:
     * 指的是依赖于${this}执行完成的的actions,即${this}执行完成了，就会触发执行${this.stack}里的dependent
     * actions
     */
    volatile Completion stack;

    final boolean internalComplete(Object r) { // CAS from null to r
        return UNSAFE.compareAndSwapObject(this, RESULT, null, r);
    }

    final boolean casStack(Completion cmp, Completion val) {
        return UNSAFE.compareAndSwapObject(this, STACK, cmp, val);
    }

    /**
     * Returns true if successfully pushed c onto stack.
     * 当成功将c入栈，返回true;否则返回false;
     */
    final boolean tryPushStack(Completion c) {
        // 获取栈顶元素
        Completion h = stack;
        // 赋值: c.next = h , 即将c入栈
        lazySetNext(c, h);
        // 入栈最后一步: 将c设置为栈顶
        return UNSAFE.compareAndSwapObject(this, STACK, h, c);
    }

    /**
     * Unconditionally(无条件地) pushes c onto stack, retrying if necessary.
     */
    final void pushStack(Completion c) {
        do {
        } while (!tryPushStack(c));
    }

    /* ------------- Encoding and decoding outcomes -------------- */

    static final class AltResult { // See above
        final Throwable ex; // null only for NIL

        AltResult(Throwable x) {
            this.ex = x;
        }
    }

    /**
     * The encoding of the null value.
     */
    static final AltResult NIL = new AltResult(null);

    /**
     * Completes with the null value, unless already completed(除非，除非在……情况下).
     */
    final boolean completeNull() {
        return UNSAFE.compareAndSwapObject(this, RESULT, null,
                NIL);
    }

    /**
     * Returns the encoding of the given non-exceptional value.
     */
    final Object encodeValue(T t) {
        return (t == null) ? NIL : t;
    }

    /**
     * Completes with a non-exceptional result, unless already completed.
     */
    final boolean completeValue(T t) {
        return UNSAFE.compareAndSwapObject(this, RESULT, null,
                (t == null) ? NIL : t);
    }

    /**
     * Returns the encoding of the given (non-null) exception as a
     * wrapped CompletionException unless it is one already.
     */
    static AltResult encodeThrowable(Throwable x) {
        return new AltResult((x instanceof CompletionException) ? x : new CompletionException(x));
    }

    /**
     * Completes with an exceptional result, unless already completed.
     */
    final boolean completeThrowable(Throwable x) {
        return UNSAFE.compareAndSwapObject(this, RESULT, null,
                encodeThrowable(x));
    }

    /**
     * Returns the encoding of the given (non-null) exception as a
     * wrapped CompletionException unless it is one already. May
     * return the given Object r (which must have been the result of a
     * source future) if it is equivalent, i.e. if this is a simple
     * relay of an existing CompletionException.
     */
    static Object encodeThrowable(Throwable x, Object r) {
        if (!(x instanceof CompletionException))
            x = new CompletionException(x);
        else if (r instanceof AltResult && x == ((AltResult) r).ex)
            return r;
        return new AltResult(x);
    }

    /**
     * Completes with the given (non-null) exceptional result as a
     * wrapped CompletionException unless it is one already, unless
     * already completed. May complete with the given Object r
     * (which must have been the result of a source future) if it is
     * equivalent, i.e. if this is a simple propagation of an
     * existing CompletionException.
     */
    final boolean completeThrowable(Throwable x, Object r) {
        return UNSAFE.compareAndSwapObject(this, RESULT, null,
                encodeThrowable(x, r));
    }

    /**
     * Returns the encoding of the given arguments: if the exception
     * is non-null, encodes as AltResult. Otherwise uses the given
     * value, boxed as NIL if null.
     */
    Object encodeOutcome(T t, Throwable x) {
        return (x == null) ? (t == null) ? NIL : t : encodeThrowable(x);
    }

    /**
     * Returns the encoding of a copied outcome; if exceptional,
     * rewraps as a CompletionException, else returns argument.
     */
    static Object encodeRelay(Object r) {
        Throwable x;
        return (((r instanceof AltResult) &&
                (x = ((AltResult) r).ex) != null &&
                !(x instanceof CompletionException)) ? new AltResult(new CompletionException(x)) : r);
    }

    /**
     * Completes with r or a copy of r, unless already completed.
     * If exceptional, r is first coerced to a CompletionException.
     */
    final boolean completeRelay(Object r) {
        return UNSAFE.compareAndSwapObject(this, RESULT, null,
                encodeRelay(r));
    }

    /**
     * Reports result using Future.get conventions.
     */
    private static <T> T reportGet(Object r)
            throws InterruptedException, ExecutionException {
        if (r == null) // by convention below, null means interrupted
            throw new InterruptedException();
        if (r instanceof AltResult) {
            Throwable x, cause;
            if ((x = ((AltResult) r).ex) == null)
                return null;
            if (x instanceof CancellationException)
                throw (CancellationException) x;
            if ((x instanceof CompletionException) &&
                    (cause = x.getCause()) != null)
                x = cause;
            throw new ExecutionException(x);
        }
        @SuppressWarnings("unchecked")
        T t = (T) r;
        return t;
    }

    /**
     * Decodes outcome to return result or throw unchecked exception.
     */
    private static <T> T reportJoin(Object r) {
        if (r instanceof AltResult) {
            Throwable x;
            if ((x = ((AltResult) r).ex) == null)
                return null;
            if (x instanceof CancellationException)
                throw (CancellationException) x;
            if (x instanceof CompletionException)
                throw (CompletionException) x;
            throw new CompletionException(x);
        }
        @SuppressWarnings("unchecked")
        T t = (T) r;
        return t;
    }

    /*
     * ------------- Async task preliminaries(n.预赛；初期微震；正文前书页（preliminary 的复数）)
     * --------------
     */

    /**
     * A marker interface identifying asynchronous tasks produced by
     * {@code async} methods. This may be useful for monitoring,
     * debugging, and tracking asynchronous activities.
     *
     * @since 1.8
     */
    public static interface AsynchronousCompletionTask {
    }

    private static final boolean useCommonPool = (ForkJoinPool.getCommonPoolParallelism() > 1);

    /**
     * Default executor -- ForkJoinPool.commonPool() unless it cannot
     * support parallelism.
     */
    private static final Executor asyncPool = useCommonPool ? ForkJoinPool.commonPool() : new ThreadPerTaskExecutor();

    /**
     * Fallback if ForkJoinPool.commonPool() cannot support parallelism
     */
    static final class ThreadPerTaskExecutor implements Executor {
        public void execute(Runnable r) {
            new Thread(r).start();
        }
    }

    /**
     * Null-checks user executor argument, and translates uses of
     * commonPool to asyncPool in case parallelism disabled.
     */
    static Executor screenExecutor(Executor e) {
        if (!useCommonPool && e == ForkJoinPool.commonPool())
            return asyncPool;
        if (e == null)
            throw new NullPointerException();
        return e;
    }

    // Modes for Completion.tryFire. Signedness matters.
    static final int SYNC = 0;
    static final int ASYNC = 1;
    static final int NESTED = -1;

    /**
     * Spins before blocking in waitingGet.
     * There is no need to spin on uniprocessors.
     * <p>
     * Call to Runtime.availableProcessors is expensive, cache the value here.
     * This unfortunately relies on the number of available CPUs during first
     * initialization. This affects the case when MP system would report only
     * one CPU available at startup, initialize SPINS to 0, and then make more
     * CPUs online. This would incur some performance penalty due to less spins
     * than would otherwise happen.
     */
    private static final int SPINS = (Runtime.getRuntime().availableProcessors() > 1 ? 1 << 8 : 0);

    /* ------------- Base Completion classes and operations -------------- */

    @SuppressWarnings("serial")
    abstract static class Completion extends ForkJoinTask<Void>
            implements Runnable, AsynchronousCompletionTask {
        volatile Completion next; // Treiber stack link

        /**
         * Performs completion action if triggered, returning a
         * dependent that may need propagation, if one exists.
         *
         * @param mode SYNC, ASYNC, or NESTED
         */
        abstract CompletableFuture<?> tryFire(int mode);

        /**
         * Returns true if possibly still triggerable. Used by cleanStack.
         */
        abstract boolean isLive();

        public final void run() {
            tryFire(ASYNC);
        }

        public final boolean exec() {
            tryFire(ASYNC);
            return true;
        }

        public final Void getRawResult() {
            return null;
        }

        public final void setRawResult(Void v) {
        }
    }

    /**
     * 赋值操作,即: c.next = next
     *
     * @param c
     * @param next
     */
    static void lazySetNext(Completion c, Completion next) {
        UNSAFE.putOrderedObject(c, NEXT, next);
    }

    /**
     * Pops and tries to trigger all reachable dependents. Call only
     * when known to be done.
     */
    final void postComplete() {
        /*
         * On each step, variable f holds current dependents to pop
         * and run. It is extended along only one path at a time,
         * pushing others to avoid unbounded recursion.
         */
        CompletableFuture<?> f = this;
        Completion h;
        while ((h = f.stack) != null ||
                (f != this && (h = (f = this).stack) != null)) {
            CompletableFuture<?> d;
            Completion t;
            if (f.casStack(h, t = h.next)) {
                if (t != null) {
                    if (f != this) {
                        pushStack(h);
                        continue;
                    }
                    h.next = null; // detach
                }
                f = (d = h.tryFire(NESTED)) == null ? this : d;
            }
        }
    }

    /**
     * Traverses stack and unlinks dead Completions.
     */
    final void cleanStack() {
        for (Completion p = null, q = stack; q != null;) {
            Completion s = q.next;
            if (q.isLive()) {
                p = q;
                q = s;
            } else if (p == null) {
                casStack(q, s);
                q = stack;
            } else {
                p.next = s;
                if (p.isLive())
                    q = s;
                else {
                    p = null; // restart
                    q = stack;
                }
            }
        }
    }

    /* ------------- One-input Completions -------------- */

    /**
     * A Completion with a source, dependent, and executor.
     */
    @SuppressWarnings("serial")
    abstract static class UniCompletion<T, V> extends Completion {
        Executor executor; // executor to use (null if none)
        CompletableFuture<V> dep; // the dependent to complete
        CompletableFuture<T> src; // source for action

        UniCompletion(Executor executor, CompletableFuture<V> dep,
                CompletableFuture<T> src) {
            this.executor = executor;
            this.dep = dep;
            this.src = src;
        }

        /**
         * Returns true if action can be run. Call only when known to
         * be triggerable. Uses FJ tag bit to ensure that only one
         * thread claims ownership. If async, starts as task -- a
         * later call to tryFire will run action.
         */
        final boolean claim() {
            Executor e = executor;
            if (compareAndSetForkJoinTaskTag((short) 0, (short) 1)) {
                if (e == null)
                    return true;
                executor = null; // disable
                e.execute(this);
            }
            return false;
        }

        final boolean isLive() {
            return dep != null;
        }
    }

    /**
     * Pushes the given completion (if it exists) unless done.
     */
    final void push(UniCompletion<?, ?> c) {
        if (c != null) {
            while (result == null && !tryPushStack(c))
                lazySetNext(c, null); // clear on failure
        }
    }

    /**
     * Post-processing by dependent after successful UniCompletion
     * tryFire. Tries to clean stack of source a, and then either runs
     * postComplete or returns this to caller, depending on mode.
     */
    final CompletableFuture<T> postFire(CompletableFuture<?> a, int mode) {
        if (a != null && a.stack != null) {
            if (mode < 0 || a.result == null)
                a.cleanStack();
            else
                a.postComplete();
        }
        if (result != null && stack != null) {
            if (mode < 0)
                return this;
            else
                postComplete();
        }
        return null;
    }

    @SuppressWarnings("serial")
    static final class UniApply<T, V> extends UniCompletion<T, V> {
        // 业务处理函数，即会调用f来执行上一个CF的执行结果。
        Function<? super T, ? extends V> fn;

        UniApply(Executor executor, CompletableFuture<V> dep,
                CompletableFuture<T> src,
                Function<? super T, ? extends V> fn) {
            super(executor, dep, src);
            this.fn = fn;
        }
        
        /**
         * 尝试触发执行
         * @param mode SYNC：同步;  ASYNC:异步
         */
        final CompletableFuture<V> tryFire(int mode) {
            CompletableFuture<V> d;
            CompletableFuture<T> a;
            if ((d = dep) == null ||
                    !d.uniApply(a = src, fn, mode > 0 ? null : this))
                return null;
            dep = null;
            src = null;
            fn = null;
            return d.postFire(a, mode);
        }
    }

    /**
     * 
     * @param a 上一个CF,即当前是等待a执行的结果
     * @param f 处理函数，负责处理a的处理结果
     * @param c
     * @return 业务是否执行，即f是否被调用
     */
    final <S> boolean uniApply(CompletableFuture<S> a,
            Function<? super S, ? extends T> f,UniApply<S, T> c) {
        Object r;
        Throwable x;
        // a不存在(那就不会有执行结果了，也就thenApply就可以立即执行了)或当a未执行完成，或者f不存在
        if (a == null || (r = a.result) == null || f == null)
            return false;
        tryComplete: if (result == null) { // 当前的CF未执行完成
            if (r instanceof AltResult) { // 当a已经执行完成
                if ((x = ((AltResult) r).ex) != null) { // 当a执行发生了异常
                    completeThrowable(x, r);
                    break tryComplete;
                }
                r = null;
            }
            // a 已经执行完成，并且正常结束，未发生异常
            try {
                // 看 thenApply参数，c==this，若不为null则是异步执行。
                if (c != null && !c.claim())
                    return false;
                @SuppressWarnings("unchecked")
                S s = (S) r;
                // 执行业务逻辑，并设置执行结果
                completeValue(f.apply(s));
            } catch (Throwable ex) {
                // 异常了，则将异常包装起来，作为执行结果
                completeThrowable(ex);
            }
        }
        return true;
    }

    private <V> CompletableFuture<V> uniApplyStage(
            Executor e, Function<? super T, ? extends V> f) {
        if (f == null)
            throw new NullPointerException();
        // 创建一个新的CompletableFuture
        CompletableFuture<V> d = new CompletableFuture<V>();
        
        // 当this还没有执行结束时
        if (e != null || !d.uniApply(this, f, null)) {
            // 创建一个新的UniApply
            UniApply<T, V> c = new UniApply<T, V>(e, d, this, f);
            // 将c入栈
            push(c);
            // 尝试触发一下执行(同步方式)
            c.tryFire(SYNC);
        }
        return d;
    }

    @SuppressWarnings("serial")
    static final class UniAccept<T> extends UniCompletion<T, Void> {
        Consumer<? super T> fn;

        UniAccept(Executor executor, CompletableFuture<Void> dep,
                CompletableFuture<T> src, Consumer<? super T> fn) {
            super(executor, dep, src);
            this.fn = fn;
        }

        final CompletableFuture<Void> tryFire(int mode) {
            CompletableFuture<Void> d;
            CompletableFuture<T> a;
            if ((d = dep) == null ||
                    !d.uniAccept(a = src, fn, mode > 0 ? null : this))
                return null;
            dep = null;
            src = null;
            fn = null;
            return d.postFire(a, mode);
        }
    }

    final <S> boolean uniAccept(CompletableFuture<S> a,
            Consumer<? super S> f, UniAccept<S> c) {
        Object r;
        Throwable x;
        if (a == null || (r = a.result) == null || f == null)
            return false;
        tryComplete: if (result == null) {
            if (r instanceof AltResult) {
                if ((x = ((AltResult) r).ex) != null) {
                    completeThrowable(x, r);
                    break tryComplete;
                }
                r = null;
            }
            try {
                if (c != null && !c.claim())
                    return false;
                @SuppressWarnings("unchecked")
                S s = (S) r;
                f.accept(s);
                completeNull();
            } catch (Throwable ex) {
                completeThrowable(ex);
            }
        }
        return true;
    }

    private CompletableFuture<Void> uniAcceptStage(Executor e,
            Consumer<? super T> f) {
        if (f == null)
            throw new NullPointerException();
        CompletableFuture<Void> d = new CompletableFuture<Void>();
        if (e != null || !d.uniAccept(this, f, null)) {
            UniAccept<T> c = new UniAccept<T>(e, d, this, f);
            push(c);
            c.tryFire(SYNC);
        }
        return d;
    }

    @SuppressWarnings("serial")
    static final class UniRun<T> extends UniCompletion<T, Void> {
        Runnable fn;

        UniRun(Executor executor, CompletableFuture<Void> dep,
                CompletableFuture<T> src, Runnable fn) {
            super(executor, dep, src);
            this.fn = fn;
        }

        final CompletableFuture<Void> tryFire(int mode) {
            CompletableFuture<Void> d;
            CompletableFuture<T> a;
            if ((d = dep) == null ||
                    !d.uniRun(a = src, fn, mode > 0 ? null : this))
                return null;
            dep = null;
            src = null;
            fn = null;
            return d.postFire(a, mode);
        }
    }

    final boolean uniRun(CompletableFuture<?> a, Runnable f, UniRun<?> c) {
        Object r;
        Throwable x;
        if (a == null || (r = a.result) == null || f == null)
            return false;
        if (result == null) {
            if (r instanceof AltResult && (x = ((AltResult) r).ex) != null)
                completeThrowable(x, r);
            else
                try {
                    if (c != null && !c.claim())
                        return false;
                    f.run();
                    completeNull();
                } catch (Throwable ex) {
                    completeThrowable(ex);
                }
        }
        return true;
    }

    private CompletableFuture<Void> uniRunStage(Executor e, Runnable f) {
        if (f == null)
            throw new NullPointerException();
        CompletableFuture<Void> d = new CompletableFuture<Void>();
        if (e != null || !d.uniRun(this, f, null)) {
            UniRun<T> c = new UniRun<T>(e, d, this, f);
            push(c);
            c.tryFire(SYNC);
        }
        return d;
    }

    @SuppressWarnings("serial")
    static final class UniWhenComplete<T> extends UniCompletion<T, T> {
        BiConsumer<? super T, ? super Throwable> fn;

        UniWhenComplete(Executor executor, CompletableFuture<T> dep,
                CompletableFuture<T> src,
                BiConsumer<? super T, ? super Throwable> fn) {
            super(executor, dep, src);
            this.fn = fn;
        }

        final CompletableFuture<T> tryFire(int mode) {
            CompletableFuture<T> d;
            CompletableFuture<T> a;
            if ((d = dep) == null ||
                    !d.uniWhenComplete(a = src, fn, mode > 0 ? null : this))
                return null;
            dep = null;
            src = null;
            fn = null;
            return d.postFire(a, mode);
        }
    }

    final boolean uniWhenComplete(CompletableFuture<T> a,
            BiConsumer<? super T, ? super Throwable> f,
            UniWhenComplete<T> c) {
        Object r;
        T t;
        Throwable x = null;
        if (a == null || (r = a.result) == null || f == null)
            return false;
        if (result == null) {
            try {
                if (c != null && !c.claim())
                    return false;
                if (r instanceof AltResult) {
                    x = ((AltResult) r).ex;
                    t = null;
                } else {
                    @SuppressWarnings("unchecked")
                    T tr = (T) r;
                    t = tr;
                }
                f.accept(t, x);
                if (x == null) {
                    internalComplete(r);
                    return true;
                }
            } catch (Throwable ex) {
                if (x == null)
                    x = ex;
            }
            completeThrowable(x, r);
        }
        return true;
    }

    private CompletableFuture<T> uniWhenCompleteStage(
            Executor e, BiConsumer<? super T, ? super Throwable> f) {
        if (f == null)
            throw new NullPointerException();
        CompletableFuture<T> d = new CompletableFuture<T>();
        if (e != null || !d.uniWhenComplete(this, f, null)) {
            UniWhenComplete<T> c = new UniWhenComplete<T>(e, d, this, f);
            push(c);
            c.tryFire(SYNC);
        }
        return d;
    }

    @SuppressWarnings("serial")
    static final class UniHandle<T, V> extends UniCompletion<T, V> {
        BiFunction<? super T, Throwable, ? extends V> fn;

        UniHandle(Executor executor, CompletableFuture<V> dep,
                CompletableFuture<T> src,
                BiFunction<? super T, Throwable, ? extends V> fn) {
            super(executor, dep, src);
            this.fn = fn;
        }

        final CompletableFuture<V> tryFire(int mode) {
            CompletableFuture<V> d;
            CompletableFuture<T> a;
            if ((d = dep) == null ||
                    !d.uniHandle(a = src, fn, mode > 0 ? null : this))
                return null;
            dep = null;
            src = null;
            fn = null;
            return d.postFire(a, mode);
        }
    }

    final <S> boolean uniHandle(CompletableFuture<S> a,
            BiFunction<? super S, Throwable, ? extends T> f,
            UniHandle<S, T> c) {
        Object r;
        S s;
        Throwable x;
        if (a == null || (r = a.result) == null || f == null)
            return false;
        if (result == null) {
            try {
                if (c != null && !c.claim())
                    return false;
                if (r instanceof AltResult) {
                    x = ((AltResult) r).ex;
                    s = null;
                } else {
                    x = null;
                    @SuppressWarnings("unchecked")
                    S ss = (S) r;
                    s = ss;
                }
                completeValue(f.apply(s, x));
            } catch (Throwable ex) {
                completeThrowable(ex);
            }
        }
        return true;
    }

    private <V> CompletableFuture<V> uniHandleStage(
            Executor e, BiFunction<? super T, Throwable, ? extends V> f) {
        if (f == null)
            throw new NullPointerException();
        CompletableFuture<V> d = new CompletableFuture<V>();
        if (e != null || !d.uniHandle(this, f, null)) {
            UniHandle<T, V> c = new UniHandle<T, V>(e, d, this, f);
            push(c);
            c.tryFire(SYNC);
        }
        return d;
    }

    @SuppressWarnings("serial")
    static final class UniExceptionally<T> extends UniCompletion<T, T> {
        Function<? super Throwable, ? extends T> fn;

        UniExceptionally(CompletableFuture<T> dep, CompletableFuture<T> src,
                Function<? super Throwable, ? extends T> fn) {
            super(null, dep, src);
            this.fn = fn;
        }

        final CompletableFuture<T> tryFire(int mode) { // never ASYNC
            // assert mode != ASYNC;
            CompletableFuture<T> d;
            CompletableFuture<T> a;
            if ((d = dep) == null || !d.uniExceptionally(a = src, fn, this))
                return null;
            dep = null;
            src = null;
            fn = null;
            return d.postFire(a, mode);
        }
    }

    final boolean uniExceptionally(CompletableFuture<T> a,
            Function<? super Throwable, ? extends T> f,
            UniExceptionally<T> c) {
        Object r;
        Throwable x;
        if (a == null || (r = a.result) == null || f == null)
            return false;
        if (result == null) {
            try {
                if (r instanceof AltResult && (x = ((AltResult) r).ex) != null) {
                    if (c != null && !c.claim())
                        return false;
                    completeValue(f.apply(x));
                } else
                    internalComplete(r);
            } catch (Throwable ex) {
                completeThrowable(ex);
            }
        }
        return true;
    }

    private CompletableFuture<T> uniExceptionallyStage(
            Function<Throwable, ? extends T> f) {
        if (f == null)
            throw new NullPointerException();
        CompletableFuture<T> d = new CompletableFuture<T>();
        if (!d.uniExceptionally(this, f, null)) {
            UniExceptionally<T> c = new UniExceptionally<T>(d, this, f);
            push(c);
            c.tryFire(SYNC);
        }
        return d;
    }

    @SuppressWarnings("serial")
    static final class UniRelay<T> extends UniCompletion<T, T> { // for Compose
        UniRelay(CompletableFuture<T> dep, CompletableFuture<T> src) {
            super(null, dep, src);
        }

        final CompletableFuture<T> tryFire(int mode) {
            CompletableFuture<T> d;
            CompletableFuture<T> a;
            if ((d = dep) == null || !d.uniRelay(a = src))
                return null;
            src = null;
            dep = null;
            return d.postFire(a, mode);
        }
    }

    final boolean uniRelay(CompletableFuture<T> a) {
        Object r;
        if (a == null || (r = a.result) == null)
            return false;
        if (result == null) // no need to claim
            completeRelay(r);
        return true;
    }

    @SuppressWarnings("serial")
    static final class UniCompose<T, V> extends UniCompletion<T, V> {
        Function<? super T, ? extends CompletionStage<V>> fn;

        UniCompose(Executor executor, CompletableFuture<V> dep,
                CompletableFuture<T> src,
                Function<? super T, ? extends CompletionStage<V>> fn) {
            super(executor, dep, src);
            this.fn = fn;
        }

        final CompletableFuture<V> tryFire(int mode) {
            CompletableFuture<V> d;
            CompletableFuture<T> a;
            if ((d = dep) == null ||
                    !d.uniCompose(a = src, fn, mode > 0 ? null : this))
                return null;
            dep = null;
            src = null;
            fn = null;
            return d.postFire(a, mode);
        }
    }

    final <S> boolean uniCompose(
            CompletableFuture<S> a,
            Function<? super S, ? extends CompletionStage<T>> f,
            UniCompose<S, T> c) {
        Object r;
        Throwable x;
        if (a == null || (r = a.result) == null || f == null)
            return false;
        tryComplete: if (result == null) {
            if (r instanceof AltResult) {
                if ((x = ((AltResult) r).ex) != null) {
                    completeThrowable(x, r);
                    break tryComplete;
                }
                r = null;
            }
            try {
                if (c != null && !c.claim())
                    return false;
                @SuppressWarnings("unchecked")
                S s = (S) r;
                CompletableFuture<T> g = f.apply(s).toCompletableFuture();
                if (g.result == null || !uniRelay(g)) {
                    UniRelay<T> copy = new UniRelay<T>(this, g);
                    g.push(copy);
                    copy.tryFire(SYNC);
                    if (result == null)
                        return false;
                }
            } catch (Throwable ex) {
                completeThrowable(ex);
            }
        }
        return true;
    }

    private <V> CompletableFuture<V> uniComposeStage(
            Executor e, Function<? super T, ? extends CompletionStage<V>> f) {
        if (f == null)
            throw new NullPointerException();
        Object r;
        Throwable x;
        if (e == null && (r = result) != null) {
            // try to return function result directly
            if (r instanceof AltResult) {
                if ((x = ((AltResult) r).ex) != null) {
                    return new CompletableFuture<V>(encodeThrowable(x, r));
                }
                r = null;
            }
            try {
                @SuppressWarnings("unchecked")
                T t = (T) r;
                CompletableFuture<V> g = f.apply(t).toCompletableFuture();
                Object s = g.result;
                if (s != null)
                    return new CompletableFuture<V>(encodeRelay(s));
                CompletableFuture<V> d = new CompletableFuture<V>();
                UniRelay<V> copy = new UniRelay<V>(d, g);
                g.push(copy);
                copy.tryFire(SYNC);
                return d;
            } catch (Throwable ex) {
                return new CompletableFuture<V>(encodeThrowable(ex));
            }
        }
        CompletableFuture<V> d = new CompletableFuture<V>();
        UniCompose<T, V> c = new UniCompose<T, V>(e, d, this, f);
        push(c);
        c.tryFire(SYNC);
        return d;
    }

    /* ------------- Two-input Completions -------------- */

    /**
     * A Completion for an action with two sources
     */
    @SuppressWarnings("serial")
    abstract static class BiCompletion<T, U, V> extends UniCompletion<T, V> {
        CompletableFuture<U> snd; // second source for action

        BiCompletion(Executor executor, CompletableFuture<V> dep,
                CompletableFuture<T> src, CompletableFuture<U> snd) {
            super(executor, dep, src);
            this.snd = snd;
        }
    }

    /**
     * A Completion delegating to a BiCompletion
     */
    @SuppressWarnings("serial")
    static final class CoCompletion extends Completion {
        BiCompletion<?, ?, ?> base;

        CoCompletion(BiCompletion<?, ?, ?> base) {
            this.base = base;
        }

        final CompletableFuture<?> tryFire(int mode) {
            BiCompletion<?, ?, ?> c;
            CompletableFuture<?> d;
            if ((c = base) == null || (d = c.tryFire(mode)) == null)
                return null;
            base = null; // detach
            return d;
        }

        final boolean isLive() {
            BiCompletion<?, ?, ?> c;
            return (c = base) != null && c.dep != null;
        }
    }

    /**
     * Pushes completion to this and b unless both done.
     */
    final void bipush(CompletableFuture<?> b, BiCompletion<?, ?, ?> c) {
        if (c != null) {
            Object r;
            while ((r = result) == null && !tryPushStack(c))
                lazySetNext(c, null); // clear on failure
            if (b != null && b != this && b.result == null) {
                Completion q = (r != null) ? c : new CoCompletion(c);
                while (b.result == null && !b.tryPushStack(q))
                    lazySetNext(q, null); // clear on failure
            }
        }
    }

    /**
     * Post-processing after successful BiCompletion tryFire.
     */
    final CompletableFuture<T> postFire(CompletableFuture<?> a,
            CompletableFuture<?> b, int mode) {
        if (b != null && b.stack != null) { // clean second source
            if (mode < 0 || b.result == null)
                b.cleanStack();
            else
                b.postComplete();
        }
        return postFire(a, mode);
    }

    @SuppressWarnings("serial")
    static final class BiApply<T, U, V> extends BiCompletion<T, U, V> {
        BiFunction<? super T, ? super U, ? extends V> fn;

        BiApply(Executor executor, CompletableFuture<V> dep,
                CompletableFuture<T> src, CompletableFuture<U> snd,
                BiFunction<? super T, ? super U, ? extends V> fn) {
            super(executor, dep, src, snd);
            this.fn = fn;
        }

        final CompletableFuture<V> tryFire(int mode) {
            CompletableFuture<V> d;
            CompletableFuture<T> a;
            CompletableFuture<U> b;
            if ((d = dep) == null ||
                    !d.biApply(a = src, b = snd, fn, mode > 0 ? null : this))
                return null;
            dep = null;
            src = null;
            snd = null;
            fn = null;
            return d.postFire(a, b, mode);
        }
    }

    final <R, S> boolean biApply(CompletableFuture<R> a,
            CompletableFuture<S> b,
            BiFunction<? super R, ? super S, ? extends T> f,
            BiApply<R, S, T> c) {
        Object r, s;
        Throwable x;
        if (a == null || (r = a.result) == null ||
                b == null || (s = b.result) == null || f == null)
            return false;
        tryComplete: if (result == null) {
            if (r instanceof AltResult) {
                if ((x = ((AltResult) r).ex) != null) {
                    completeThrowable(x, r);
                    break tryComplete;
                }
                r = null;
            }
            if (s instanceof AltResult) {
                if ((x = ((AltResult) s).ex) != null) {
                    completeThrowable(x, s);
                    break tryComplete;
                }
                s = null;
            }
            try {
                if (c != null && !c.claim())
                    return false;
                @SuppressWarnings("unchecked")
                R rr = (R) r;
                @SuppressWarnings("unchecked")
                S ss = (S) s;
                completeValue(f.apply(rr, ss));
            } catch (Throwable ex) {
                completeThrowable(ex);
            }
        }
        return true;
    }

    private <U, V> CompletableFuture<V> biApplyStage(
            Executor e, CompletionStage<U> o,
            BiFunction<? super T, ? super U, ? extends V> f) {
        CompletableFuture<U> b;
        if (f == null || (b = o.toCompletableFuture()) == null)
            throw new NullPointerException();
        CompletableFuture<V> d = new CompletableFuture<V>();
        if (e != null || !d.biApply(this, b, f, null)) {
            BiApply<T, U, V> c = new BiApply<T, U, V>(e, d, this, b, f);
            bipush(b, c);
            c.tryFire(SYNC);
        }
        return d;
    }

    @SuppressWarnings("serial")
    static final class BiAccept<T, U> extends BiCompletion<T, U, Void> {
        BiConsumer<? super T, ? super U> fn;

        BiAccept(Executor executor, CompletableFuture<Void> dep,
                CompletableFuture<T> src, CompletableFuture<U> snd,
                BiConsumer<? super T, ? super U> fn) {
            super(executor, dep, src, snd);
            this.fn = fn;
        }

        final CompletableFuture<Void> tryFire(int mode) {
            CompletableFuture<Void> d;
            CompletableFuture<T> a;
            CompletableFuture<U> b;
            if ((d = dep) == null ||
                    !d.biAccept(a = src, b = snd, fn, mode > 0 ? null : this))
                return null;
            dep = null;
            src = null;
            snd = null;
            fn = null;
            return d.postFire(a, b, mode);
        }
    }

    final <R, S> boolean biAccept(CompletableFuture<R> a,
            CompletableFuture<S> b,
            BiConsumer<? super R, ? super S> f,
            BiAccept<R, S> c) {
        Object r, s;
        Throwable x;
        if (a == null || (r = a.result) == null ||
                b == null || (s = b.result) == null || f == null)
            return false;
        tryComplete: if (result == null) {
            if (r instanceof AltResult) {
                if ((x = ((AltResult) r).ex) != null) {
                    completeThrowable(x, r);
                    break tryComplete;
                }
                r = null;
            }
            if (s instanceof AltResult) {
                if ((x = ((AltResult) s).ex) != null) {
                    completeThrowable(x, s);
                    break tryComplete;
                }
                s = null;
            }
            try {
                if (c != null && !c.claim())
                    return false;
                @SuppressWarnings("unchecked")
                R rr = (R) r;
                @SuppressWarnings("unchecked")
                S ss = (S) s;
                f.accept(rr, ss);
                completeNull();
            } catch (Throwable ex) {
                completeThrowable(ex);
            }
        }
        return true;
    }

    private <U> CompletableFuture<Void> biAcceptStage(
            Executor e, CompletionStage<U> o,
            BiConsumer<? super T, ? super U> f) {
        CompletableFuture<U> b;
        if (f == null || (b = o.toCompletableFuture()) == null)
            throw new NullPointerException();
        CompletableFuture<Void> d = new CompletableFuture<Void>();
        if (e != null || !d.biAccept(this, b, f, null)) {
            BiAccept<T, U> c = new BiAccept<T, U>(e, d, this, b, f);
            bipush(b, c);
            c.tryFire(SYNC);
        }
        return d;
    }

    @SuppressWarnings("serial")
    static final class BiRun<T, U> extends BiCompletion<T, U, Void> {
        Runnable fn;

        BiRun(Executor executor, CompletableFuture<Void> dep,
                CompletableFuture<T> src,
                CompletableFuture<U> snd,
                Runnable fn) {
            super(executor, dep, src, snd);
            this.fn = fn;
        }

        final CompletableFuture<Void> tryFire(int mode) {
            CompletableFuture<Void> d;
            CompletableFuture<T> a;
            CompletableFuture<U> b;
            if ((d = dep) == null ||
                    !d.biRun(a = src, b = snd, fn, mode > 0 ? null : this))
                return null;
            dep = null;
            src = null;
            snd = null;
            fn = null;
            return d.postFire(a, b, mode);
        }
    }

    final boolean biRun(CompletableFuture<?> a, CompletableFuture<?> b,
            Runnable f, BiRun<?, ?> c) {
        Object r, s;
        Throwable x;
        if (a == null || (r = a.result) == null ||
                b == null || (s = b.result) == null || f == null)
            return false;
        if (result == null) {
            if (r instanceof AltResult && (x = ((AltResult) r).ex) != null)
                completeThrowable(x, r);
            else if (s instanceof AltResult && (x = ((AltResult) s).ex) != null)
                completeThrowable(x, s);
            else
                try {
                    if (c != null && !c.claim())
                        return false;
                    f.run();
                    completeNull();
                } catch (Throwable ex) {
                    completeThrowable(ex);
                }
        }
        return true;
    }

    private CompletableFuture<Void> biRunStage(Executor e, CompletionStage<?> o,
            Runnable f) {
        CompletableFuture<?> b;
        if (f == null || (b = o.toCompletableFuture()) == null)
            throw new NullPointerException();
        CompletableFuture<Void> d = new CompletableFuture<Void>();
        if (e != null || !d.biRun(this, b, f, null)) {
            BiRun<T, ?> c = new BiRun<>(e, d, this, b, f);
            bipush(b, c);
            c.tryFire(SYNC);
        }
        return d;
    }

    @SuppressWarnings("serial")
    static final class BiRelay<T, U> extends BiCompletion<T, U, Void> { // for And
        BiRelay(CompletableFuture<Void> dep,
                CompletableFuture<T> src,
                CompletableFuture<U> snd) {
            super(null, dep, src, snd);
        }

        final CompletableFuture<Void> tryFire(int mode) {
            CompletableFuture<Void> d;
            CompletableFuture<T> a;
            CompletableFuture<U> b;
            if ((d = dep) == null || !d.biRelay(a = src, b = snd))
                return null;
            src = null;
            snd = null;
            dep = null;
            return d.postFire(a, b, mode);
        }
    }

    /**
     * relay: v. 传递，传达（信息、新闻等）；中继转发，转播（广播或电视信号）；
     *
     * true: 表示a、b都执行完成了，不需要传播;
     * false: 表示a、b至少有一个未执行完成，需要传播
     *
     * bi: 表示两个input,即两个action
     **/
    boolean biRelay(CompletableFuture<?> a, CompletableFuture<?> b) {
        Object r, s;
        Throwable x;
        if (a == null || (r = a.result) == null ||
                b == null || (s = b.result) == null)
            return false;
        if (result == null) {
            if (r instanceof AltResult && (x = ((AltResult) r).ex) != null)
                completeThrowable(x, r);
            else if (s instanceof AltResult && (x = ((AltResult) s).ex) != null)
                completeThrowable(x, s);
            else
                completeNull();
        }
        return true;
    }

    /**
     * Recursively constructs a tree of completions.(递归地构造一个完成树。)
     */
    static CompletableFuture<Void> andTree(CompletableFuture<?>[] cfs,
            int lo, int hi) {
        CompletableFuture<Void> d = new CompletableFuture<Void>();
        if (lo > hi) // empty
            // 直接就是完成状态
            d.result = NIL;
        else {
            CompletableFuture<?> a, b;
            int mid = (lo + hi) >>> 1;
            if ((a = (lo == mid ? cfs[lo] : andTree(cfs, lo, mid))) == null ||
                    (b = (lo == hi ? a : (hi == mid + 1) ? cfs[hi] : andTree(cfs, mid + 1, hi))) == null)
                throw new NullPointerException();
            if (!d.biRelay(a, b)) {
                BiRelay<?, ?> c = new BiRelay<>(d, a, b);
                a.bipush(b, c);
                c.tryFire(SYNC);
            }
        }
        return d;
    }

    /* ------------- Projected (Ored) BiCompletions -------------- */

    /**
     * Pushes completion to this and b unless either done.
     */
    final void orpush(CompletableFuture<?> b, BiCompletion<?, ?, ?> c) {
        if (c != null) {
            while ((b == null || b.result == null) && result == null) {
                if (tryPushStack(c)) {
                    if (b != null && b != this && b.result == null) {
                        Completion q = new CoCompletion(c);
                        while (result == null && b.result == null &&
                                !b.tryPushStack(q))
                            lazySetNext(q, null); // clear on failure
                    }
                    break;
                }
                lazySetNext(c, null); // clear on failure
            }
        }
    }

    @SuppressWarnings("serial")
    static final class OrApply<T, U extends T, V> extends BiCompletion<T, U, V> {
        Function<? super T, ? extends V> fn;

        OrApply(Executor executor, CompletableFuture<V> dep,
                CompletableFuture<T> src,
                CompletableFuture<U> snd,
                Function<? super T, ? extends V> fn) {
            super(executor, dep, src, snd);
            this.fn = fn;
        }

        final CompletableFuture<V> tryFire(int mode) {
            CompletableFuture<V> d;
            CompletableFuture<T> a;
            CompletableFuture<U> b;
            if ((d = dep) == null ||
                    !d.orApply(a = src, b = snd, fn, mode > 0 ? null : this))
                return null;
            dep = null;
            src = null;
            snd = null;
            fn = null;
            return d.postFire(a, b, mode);
        }
    }

    final <R, S extends R> boolean orApply(CompletableFuture<R> a,
            CompletableFuture<S> b,
            Function<? super R, ? extends T> f,
            OrApply<R, S, T> c) {
        Object r;
        Throwable x;
        if (a == null || b == null ||
                ((r = a.result) == null && (r = b.result) == null) || f == null)
            return false;
        tryComplete: if (result == null) {
            try {
                if (c != null && !c.claim())
                    return false;
                if (r instanceof AltResult) {
                    if ((x = ((AltResult) r).ex) != null) {
                        completeThrowable(x, r);
                        break tryComplete;
                    }
                    r = null;
                }
                @SuppressWarnings("unchecked")
                R rr = (R) r;
                completeValue(f.apply(rr));
            } catch (Throwable ex) {
                completeThrowable(ex);
            }
        }
        return true;
    }

    private <U extends T, V> CompletableFuture<V> orApplyStage(
            Executor e, CompletionStage<U> o,
            Function<? super T, ? extends V> f) {
        CompletableFuture<U> b;
        if (f == null || (b = o.toCompletableFuture()) == null)
            throw new NullPointerException();
        CompletableFuture<V> d = new CompletableFuture<V>();
        if (e != null || !d.orApply(this, b, f, null)) {
            OrApply<T, U, V> c = new OrApply<T, U, V>(e, d, this, b, f);
            orpush(b, c);
            c.tryFire(SYNC);
        }
        return d;
    }

    @SuppressWarnings("serial")
    static final class OrAccept<T, U extends T> extends BiCompletion<T, U, Void> {
        Consumer<? super T> fn;

        OrAccept(Executor executor, CompletableFuture<Void> dep,
                CompletableFuture<T> src,
                CompletableFuture<U> snd,
                Consumer<? super T> fn) {
            super(executor, dep, src, snd);
            this.fn = fn;
        }

        final CompletableFuture<Void> tryFire(int mode) {
            CompletableFuture<Void> d;
            CompletableFuture<T> a;
            CompletableFuture<U> b;
            if ((d = dep) == null ||
                    !d.orAccept(a = src, b = snd, fn, mode > 0 ? null : this))
                return null;
            dep = null;
            src = null;
            snd = null;
            fn = null;
            return d.postFire(a, b, mode);
        }
    }

    final <R, S extends R> boolean orAccept(CompletableFuture<R> a,
            CompletableFuture<S> b,
            Consumer<? super R> f,
            OrAccept<R, S> c) {
        Object r;
        Throwable x;
        if (a == null || b == null ||
                ((r = a.result) == null && (r = b.result) == null) || f == null)
            return false;
        tryComplete: if (result == null) {
            try {
                if (c != null && !c.claim())
                    return false;
                if (r instanceof AltResult) {
                    if ((x = ((AltResult) r).ex) != null) {
                        completeThrowable(x, r);
                        break tryComplete;
                    }
                    r = null;
                }
                @SuppressWarnings("unchecked")
                R rr = (R) r;
                f.accept(rr);
                completeNull();
            } catch (Throwable ex) {
                completeThrowable(ex);
            }
        }
        return true;
    }

    private <U extends T> CompletableFuture<Void> orAcceptStage(
            Executor e, CompletionStage<U> o, Consumer<? super T> f) {
        CompletableFuture<U> b;
        if (f == null || (b = o.toCompletableFuture()) == null)
            throw new NullPointerException();
        CompletableFuture<Void> d = new CompletableFuture<Void>();
        if (e != null || !d.orAccept(this, b, f, null)) {
            OrAccept<T, U> c = new OrAccept<T, U>(e, d, this, b, f);
            orpush(b, c);
            c.tryFire(SYNC);
        }
        return d;
    }

    @SuppressWarnings("serial")
    static final class OrRun<T, U> extends BiCompletion<T, U, Void> {
        Runnable fn;

        OrRun(Executor executor, CompletableFuture<Void> dep,
                CompletableFuture<T> src,
                CompletableFuture<U> snd,
                Runnable fn) {
            super(executor, dep, src, snd);
            this.fn = fn;
        }

        final CompletableFuture<Void> tryFire(int mode) {
            CompletableFuture<Void> d;
            CompletableFuture<T> a;
            CompletableFuture<U> b;
            if ((d = dep) == null ||
                    !d.orRun(a = src, b = snd, fn, mode > 0 ? null : this))
                return null;
            dep = null;
            src = null;
            snd = null;
            fn = null;
            return d.postFire(a, b, mode);
        }
    }

    final boolean orRun(CompletableFuture<?> a, CompletableFuture<?> b,
            Runnable f, OrRun<?, ?> c) {
        Object r;
        Throwable x;
        if (a == null || b == null ||
                ((r = a.result) == null && (r = b.result) == null) || f == null)
            return false;
        if (result == null) {
            try {
                if (c != null && !c.claim())
                    return false;
                if (r instanceof AltResult && (x = ((AltResult) r).ex) != null)
                    completeThrowable(x, r);
                else {
                    f.run();
                    completeNull();
                }
            } catch (Throwable ex) {
                completeThrowable(ex);
            }
        }
        return true;
    }

    private CompletableFuture<Void> orRunStage(Executor e, CompletionStage<?> o,
            Runnable f) {
        CompletableFuture<?> b;
        if (f == null || (b = o.toCompletableFuture()) == null)
            throw new NullPointerException();
        CompletableFuture<Void> d = new CompletableFuture<Void>();
        if (e != null || !d.orRun(this, b, f, null)) {
            OrRun<T, ?> c = new OrRun<>(e, d, this, b, f);
            orpush(b, c);
            c.tryFire(SYNC);
        }
        return d;
    }

    @SuppressWarnings("serial")
    static final class OrRelay<T, U> extends BiCompletion<T, U, Object> { // for Or
        OrRelay(CompletableFuture<Object> dep, CompletableFuture<T> src,
                CompletableFuture<U> snd) {
            super(null, dep, src, snd);
        }

        final CompletableFuture<Object> tryFire(int mode) {
            CompletableFuture<Object> d;
            CompletableFuture<T> a;
            CompletableFuture<U> b;
            if ((d = dep) == null || !d.orRelay(a = src, b = snd))
                return null;
            src = null;
            snd = null;
            dep = null;
            return d.postFire(a, b, mode);
        }
    }

    final boolean orRelay(CompletableFuture<?> a, CompletableFuture<?> b) {
        Object r;
        if (a == null || b == null ||
                ((r = a.result) == null && (r = b.result) == null))
            return false;
        if (result == null)
            completeRelay(r);
        return true;
    }

    /**
     * Recursively constructs a tree of completions.
     */
    static CompletableFuture<Object> orTree(CompletableFuture<?>[] cfs,
            int lo, int hi) {
        CompletableFuture<Object> d = new CompletableFuture<Object>();
        if (lo <= hi) {
            CompletableFuture<?> a, b;
            int mid = (lo + hi) >>> 1;
            if ((a = (lo == mid ? cfs[lo] : orTree(cfs, lo, mid))) == null ||
                    (b = (lo == hi ? a : (hi == mid + 1) ? cfs[hi] : orTree(cfs, mid + 1, hi))) == null)
                throw new NullPointerException();
            if (!d.orRelay(a, b)) {
                OrRelay<?, ?> c = new OrRelay<>(d, a, b);
                a.orpush(b, c);
                c.tryFire(SYNC);
            }
        }
        return d;
    }

    /* ------------- Zero-input Async forms -------------- */

    @SuppressWarnings("serial")
    static final class AsyncSupply<T> extends ForkJoinTask<Void>
            implements Runnable, AsynchronousCompletionTask {
        CompletableFuture<T> dep;
        Supplier<T> fn;

        AsyncSupply(CompletableFuture<T> dep, Supplier<T> fn) {
            this.dep = dep;
            this.fn = fn;
        }

        public final Void getRawResult() {
            return null;
        }

        public final void setRawResult(Void v) {
        }

        public final boolean exec() {
            run();
            return true;
        }

        public void run() {
            CompletableFuture<T> d;
            Supplier<T> f;
            if ((d = dep) != null && (f = fn) != null) {
                dep = null;
                fn = null;
                if (d.result == null) {
                    try {
                        d.completeValue(f.get());
                    } catch (Throwable ex) {
                        d.completeThrowable(ex);
                    }
                }
                d.postComplete();
            }
        }
    }

    static <U> CompletableFuture<U> asyncSupplyStage(Executor e,
            Supplier<U> f) {
        if (f == null)
            throw new NullPointerException();
        CompletableFuture<U> d = new CompletableFuture<U>();
        e.execute(new AsyncSupply<U>(d, f));
        return d;
    }

    @SuppressWarnings("serial")
    static final class AsyncRun extends ForkJoinTask<Void>
            implements Runnable, AsynchronousCompletionTask {
        CompletableFuture<Void> dep;
        Runnable fn;

        AsyncRun(CompletableFuture<Void> dep, Runnable fn) {
            this.dep = dep;
            this.fn = fn;
        }

        public final Void getRawResult() {
            return null;
        }

        public final void setRawResult(Void v) {
        }

        public final boolean exec() {
            run();
            return true;
        }

        public void run() {
            CompletableFuture<Void> d;
            Runnable f;
            if ((d = dep) != null && (f = fn) != null) {
                dep = null;
                fn = null;
                if (d.result == null) {
                    try {
                        f.run();
                        d.completeNull();
                    } catch (Throwable ex) {
                        d.completeThrowable(ex);
                    }
                }
                d.postComplete();
            }
        }
    }

    static CompletableFuture<Void> asyncRunStage(Executor e, Runnable f) {
        if (f == null) {
            throw new NullPointerException();
        }
        // 创建一个新CompletableFuture , 注意泛型是Void,说明没有返回值
        CompletableFuture<Void> d = new CompletableFuture<Void>();
        // 提交到线程池执行
        e.execute(new AsyncRun(d, f));
        return d;
    }

    /* ------------- Signallers -------------- */

    /**
     * Completion for recording and releasing a waiting thread. This
     * class implements ManagedBlocker to avoid starvation(n.饥饿，捱饿，饿死) when
     * blocking actions pile up(pile up:堆积) in ForkJoinPools.
     */
    @SuppressWarnings("serial")
    static final class Signaller extends Completion
            implements ForkJoinPool.ManagedBlocker {
        long nanos; // wait time if timed
        final long deadline; // non-zero if timed
        volatile int interruptControl; // > 0: interruptible(adj.[计] 可中断的), < 0: interrupted
        volatile Thread thread;

        Signaller(boolean interruptible, long nanos, long deadline) {
            this.thread = Thread.currentThread();
            this.interruptControl = interruptible ? 1 : 0;
            this.nanos = nanos;
            this.deadline = deadline;
        }

        final CompletableFuture<?> tryFire(int ignore) {
            Thread w; // no need to atomically claim
            if ((w = thread) != null) {
                thread = null;
                LockSupport.unpark(w);
            }
            return null;
        }

        public boolean isReleasable() {
            if (thread == null)
                return true;
            if (Thread.interrupted()) {
                int i = interruptControl;
                interruptControl = -1;
                if (i > 0)
                    return true;
            }
            if (deadline != 0L &&
                    (nanos <= 0L || (nanos = deadline - System.nanoTime()) <= 0L)) {
                thread = null;
                return true;
            }
            return false;
        }

        public boolean block() {
            if (isReleasable())
                return true;
            else if (deadline == 0L)
                LockSupport.park(this);
            else if (nanos > 0L)
                LockSupport.parkNanos(this, nanos);
            return isReleasable();
        }

        final boolean isLive() {
            return thread != null;
        }
    }

    /**
     * Returns raw result after waiting, or null if interruptible and
     * interrupted.
     */
    private Object waitingGet(boolean interruptible) {
        Signaller q = null;
        boolean queued = false;
        int spins = -1;
        Object r;
        while ((r = result) == null) {
            if (spins < 0)
                spins = SPINS;
            else if (spins > 0) {
                if (ThreadLocalRandom.nextSecondarySeed() >= 0)
                    --spins;
            } else if (q == null)
                q = new Signaller(interruptible, 0L, 0L);
            else if (!queued)
                queued = tryPushStack(q); // 将signller入栈
            else if (interruptible && q.interruptControl < 0) {
                q.thread = null;
                cleanStack();
                return null;
            } else if (q.thread != null && result == null) {
                try {
                    // 将Signaller阻塞
                    ForkJoinPool.managedBlock(q);
                } catch (InterruptedException ie) {
                    q.interruptControl = -1;
                }
            }
        }
        if (q != null) {
            q.thread = null;
            if (q.interruptControl < 0) {
                if (interruptible)
                    r = null; // report interruption
                else
                    Thread.currentThread().interrupt();
            }
        }
        postComplete();
        return r;
    }

    /**
     * Returns raw result after waiting, or null if interrupted, or
     * throws TimeoutException on timeout.
     */
    private Object timedGet(long nanos) throws TimeoutException {
        if (Thread.interrupted())
            return null;
        if (nanos <= 0L)
            throw new TimeoutException();
        long d = System.nanoTime() + nanos;
        Signaller q = new Signaller(true, nanos, d == 0L ? 1L : d); // avoid 0
        boolean queued = false;
        Object r;
        // We intentionally don't spin here (as waitingGet does) because
        // the call to nanoTime() above acts much like a spin.
        while ((r = result) == null) {
            if (!queued)
                queued = tryPushStack(q);
            else if (q.interruptControl < 0 || q.nanos <= 0L) {
                q.thread = null;
                cleanStack();
                if (q.interruptControl < 0)
                    return null;
                throw new TimeoutException();
            } else if (q.thread != null && result == null) {
                try {
                    ForkJoinPool.managedBlock(q);
                } catch (InterruptedException ie) {
                    q.interruptControl = -1;
                }
            }
        }
        if (q.interruptControl < 0)
            r = null;
        q.thread = null;
        postComplete();
        return r;
    }

    /* ------------- public methods -------------- */

    /**
     * Creates a new incomplete CompletableFuture.
     */
    public CompletableFuture() {
    }

    /**
     * Creates a new complete CompletableFuture with given encoded result.
     */
    private CompletableFuture(Object r) {
        this.result = r;
    }

    /**
     * Returns a new CompletableFuture that is asynchronously completed
     * by a task running in the {@link ForkJoinPool#commonPool()} with
     * the value obtained by calling the given Supplier.
     *
     * @param supplier a function returning the value to be used
     *                 to complete the returned CompletableFuture
     * @param <U>      the function's return type
     * @return the new CompletableFuture
     */
    public static <U> CompletableFuture<U> supplyAsync(Supplier<U> supplier) {
        return asyncSupplyStage(asyncPool, supplier);
    }

    /**
     * Returns a new CompletableFuture that is asynchronously completed
     * by a task running in the given executor with the value obtained
     * by calling the given Supplier.
     *
     * @param supplier a function returning the value to be used
     *                 to complete the returned CompletableFuture
     * @param executor the executor to use for asynchronous execution
     * @param <U>      the function's return type
     * @return the new CompletableFuture
     */
    public static <U> CompletableFuture<U> supplyAsync(Supplier<U> supplier,
            Executor executor) {
        return asyncSupplyStage(screenExecutor(executor), supplier);
    }

    /**
     * Returns a new CompletableFuture that is asynchronously completed
     * by a task running in the {@link ForkJoinPool#commonPool()} after
     * it runs the given action.
     *
     * @param runnable the action to run before completing the
     *                 returned CompletableFuture
     * @return the new CompletableFuture
     */
    public static CompletableFuture<Void> runAsync(Runnable runnable) {
        return asyncRunStage(asyncPool, runnable);
    }

    /**
     * Returns a new CompletableFuture that is asynchronously completed
     * by a task running in the given executor after it runs the given
     * action.
     *
     * @param runnable the action to run before completing the
     *                 returned CompletableFuture
     * @param executor the executor to use for asynchronous execution
     * @return the new CompletableFuture
     */
    public static CompletableFuture<Void> runAsync(Runnable runnable,
            Executor executor) {
        return asyncRunStage(screenExecutor(executor), runnable);
    }

    /**
     * Returns a new CompletableFuture that is already completed with
     * the given value.
     *
     * @param value the value
     * @param <U>   the type of the value
     * @return the completed CompletableFuture
     */
    public static <U> CompletableFuture<U> completedFuture(U value) {
        return new CompletableFuture<U>((value == null) ? NIL : value);
    }

    /**
     * Returns {@code true} if completed in any fashion: normally,
     * exceptionally, or via cancellation.
     *
     * @return {@code true} if completed
     */
    public boolean isDone() {
        return result != null;
    }

    /**
     * Waits if necessary for this future to complete, and then
     * returns its result.
     *
     * @return the result value
     * @throws CancellationException if this future was cancelled
     * @throws ExecutionException    if this future completed exceptionally
     * @throws InterruptedException  if the current thread was interrupted
     *                               while waiting
     */
    public T get() throws InterruptedException, ExecutionException {
        Object r;
        return reportGet((r = result) == null ? waitingGet(true) : r);
    }

    /**
     * Waits if necessary for at most the given time for this future
     * to complete, and then returns its result, if available.
     *
     * @param timeout the maximum time to wait
     * @param unit    the time unit of the timeout argument
     * @return the result value
     * @throws CancellationException if this future was cancelled
     * @throws ExecutionException    if this future completed exceptionally
     * @throws InterruptedException  if the current thread was interrupted
     *                               while waiting
     * @throws TimeoutException      if the wait timed out
     */
    public T get(long timeout, TimeUnit unit)
            throws InterruptedException, ExecutionException, TimeoutException {
        Object r;
        long nanos = unit.toNanos(timeout);
        return reportGet((r = result) == null ? timedGet(nanos) : r);
    }

    /**
     * Returns the result value when complete, or throws an
     * (unchecked) exception if completed exceptionally. To better
     * conform with the use of common functional forms, if a
     * computation involved in the completion of this
     * CompletableFuture threw an exception, this method throws an
     * (unchecked) {@link CompletionException} with the underlying
     * exception as its cause.
     *
     * @return the result value
     * @throws CancellationException if the computation was cancelled
     * @throws CompletionException   if this future completed
     *                               exceptionally or a completion computation threw
     *                               an exception
     */
    public T join() {
        Object r;
        return reportJoin((r = result) == null ? waitingGet(false) : r);
    }

    /**
     * Returns the result value (or throws any encountered exception)
     * if completed, else returns the given valueIfAbsent.
     *
     * @param valueIfAbsent the value to return if not completed
     * @return the result value, if completed, else the given valueIfAbsent
     * @throws CancellationException if the computation was cancelled
     * @throws CompletionException   if this future completed
     *                               exceptionally or a completion computation threw
     *                               an exception
     */
    public T getNow(T valueIfAbsent) {
        Object r;
        return ((r = result) == null) ? valueIfAbsent : reportJoin(r);
    }

    /**
     * If not already completed, sets the value returned by {@link
     * #get()} and related methods to the given value.
     *
     * @param value the result value
     * @return {@code true} if this invocation caused this CompletableFuture
     *         to transition to a completed state, else {@code false}
     */
    public boolean complete(T value) {
        boolean triggered = completeValue(value);
        postComplete();
        return triggered;
    }

    /**
     * If not already completed, causes invocations of {@link #get()}
     * and related methods to throw the given exception.
     *
     * @param ex the exception
     * @return {@code true} if this invocation caused this CompletableFuture
     *         to transition to a completed state, else {@code false}
     */
    public boolean completeExceptionally(Throwable ex) {
        if (ex == null)
            throw new NullPointerException();
        boolean triggered = internalComplete(new AltResult(ex));
        postComplete();
        return triggered;
    }

    /**
     * thenApply* 接收Function实例，作为处理结果。
     * 
     * 沿用上一个任务的线程池(推荐)
     * 
     * <pre>
     *    ThreadPoolExecutor THREAD_POOL = new ThreadPoolExecutor(4, 4, 60, TimeUnit.SECONDS, new LinkedBlockingDeque<>());
     *
     *  CompletableFuture.supplyAsync(() -> {
     *      System.out.println("Hello");
     *      return 1;
     *  }, THREAD_POOL).thenApply((res) -> {
     *      System.out.println("res -> " + res);
     *      return null;
     *  }).join();
     * 
     * --> 输出
     *    Hello
     *    res -> 1
     * </pre>
     */
    public <U> CompletableFuture<U> thenApply(
            Function<? super T, ? extends U> fn) {
        return uniApplyStage(null, fn);
    }

    /**
     * 使用默认的 ForkJoinPool 线程池（不推荐）
     */
    public <U> CompletableFuture<U> thenApplyAsync(
            Function<? super T, ? extends U> fn) {
        return uniApplyStage(asyncPool, fn);
    }

    /**
     * 使用自定义线程池(推荐)
     */
    public <U> CompletableFuture<U> thenApplyAsync(
            Function<? super T, ? extends U> fn, Executor executor) {
        return uniApplyStage(screenExecutor(executor), fn);
    }


    /**
     * thenAccept* 方法参数: Consumer<? super T>
     * 
     * <pre>
     *  CompletableFuture.supplyAsync(() -> {
     *       System.out.println("Hello");
     *       return 1;
     *   }, THREAD_POOL).thenApply((res) -> {
     *       System.out.println("res -> " + res);
     *       return res + " --> 1";
     *   }).thenAccept(System.out::println).join();
     * 
     * // 输出
     *    Hello
     *    res -> 1
     *    1 --> 1
     * </pre>
     */
    public CompletableFuture<Void> thenAccept(Consumer<? super T> action) {
        return uniAcceptStage(null, action);
    }

    public CompletableFuture<Void> thenAcceptAsync(Consumer<? super T> action) {
        return uniAcceptStage(asyncPool, action);
    }

    public CompletableFuture<Void> thenAcceptAsync(Consumer<? super T> action,
            Executor executor) {
        return uniAcceptStage(screenExecutor(executor), action);
    }

    /**
     * <pre>
     *     CompletableFuture.supplyAsync(() -> {
     *       System.out.println("Hello");
     *       return 1;
     *   }, THREAD_POOL).thenApply((res) -> {
     *       System.out.println("res -> " + res);
     *       return res + " --> 1";
     *   }).thenRun(() -> {
     *       System.out.println("thenRun");
     *   }).join();
     * 输出:   
     *   Hello
     *   res -> 1
     *   thenRun
     * <pre>
     */
    public CompletableFuture<Void> thenRun(Runnable action) {
        return uniRunStage(null, action);
    }

    public CompletableFuture<Void> thenRunAsync(Runnable action) {
        return uniRunStage(asyncPool, action);
    }

    public CompletableFuture<Void> thenRunAsync(Runnable action,
            Executor executor) {
        return uniRunStage(screenExecutor(executor), action);
    }


    /**
     * combine : 结合;组合;
     * 在两个任务执行完成之后，把两个任务的结果合并。两个任务时并行执行的，他们之间没有先后顺序.
     * 
     * <pre>
     *   CompletableFuture executeRes = CompletableFuture.supplyAsync(() -> {
     *      try {
     *          TimeUnit.SECONDS.sleep(8);
     *      } catch (InterruptedException e) {
     *      }
     *      System.out.println("大哥处理完成");
     *      return "大哥处理完成";
     *  }, THREAD_POOL).thenCombineAsync(CompletableFuture.supplyAsync(() -> {
     *      System.out.println("二弟处理完成");
     *      return "二弟处理完成";
     *  }), (res1, res2) -> {
     *      return res1 + res2;
     *  }, THREAD_POOL).thenComposeAsync((res) -> {
     *      System.out.println("处理结果: " + res);
     *      return CompletableFuture.completedFuture("处理完成: " + res);
     *  }, THREAD_POOL);
     *  System.out.println(executeRes.get());
     *  System.out.println("END...");
     * 
     * ---> 输出
     *    二弟处理完成
     *    大哥处理完成
     *    处理结果: 大哥处理完成二弟处理完成
     *    处理完成: 大哥处理完成二弟处理完成
     *    END...
     * </pre>
     * 
     */
    public <U, V> CompletableFuture<V> thenCombine(
            CompletionStage<? extends U> other,
            BiFunction<? super T, ? super U, ? extends V> fn) {
        return biApplyStage(null, other, fn);
    }

    public <U, V> CompletableFuture<V> thenCombineAsync(
            CompletionStage<? extends U> other,
            BiFunction<? super T, ? super U, ? extends V> fn) {
        return biApplyStage(asyncPool, other, fn);
    }

    public <U, V> CompletableFuture<V> thenCombineAsync(
            CompletionStage<? extends U> other,
            BiFunction<? super T, ? super U, ? extends V> fn, Executor executor) {
        return biApplyStage(screenExecutor(executor), other, fn);
    }

    public <U> CompletableFuture<Void> thenAcceptBoth(
            CompletionStage<? extends U> other,
            BiConsumer<? super T, ? super U> action) {
        return biAcceptStage(null, other, action);
    }

    public <U> CompletableFuture<Void> thenAcceptBothAsync(
            CompletionStage<? extends U> other,
            BiConsumer<? super T, ? super U> action) {
        return biAcceptStage(asyncPool, other, action);
    }

    public <U> CompletableFuture<Void> thenAcceptBothAsync(
            CompletionStage<? extends U> other,
            BiConsumer<? super T, ? super U> action, Executor executor) {
        return biAcceptStage(screenExecutor(executor), other, action);
    }

    public CompletableFuture<Void> runAfterBoth(CompletionStage<?> other,
            Runnable action) {
        return biRunStage(null, other, action);
    }

    public CompletableFuture<Void> runAfterBothAsync(CompletionStage<?> other,
            Runnable action) {
        return biRunStage(asyncPool, other, action);
    }

    public CompletableFuture<Void> runAfterBothAsync(CompletionStage<?> other,
            Runnable action,
            Executor executor) {
        return biRunStage(screenExecutor(executor), other, action);
    }

    public <U> CompletableFuture<U> applyToEither(
            CompletionStage<? extends T> other, Function<? super T, U> fn) {
        return orApplyStage(null, other, fn);
    }

    public <U> CompletableFuture<U> applyToEitherAsync(
            CompletionStage<? extends T> other, Function<? super T, U> fn) {
        return orApplyStage(asyncPool, other, fn);
    }

    public <U> CompletableFuture<U> applyToEitherAsync(
            CompletionStage<? extends T> other, Function<? super T, U> fn,
            Executor executor) {
        return orApplyStage(screenExecutor(executor), other, fn);
    }

    /**
     * 当其中任意一个任务执行完成就触发。
     * 
     * <pre>
     * CompletableFuture<String> task1 = CompletableFuture.supplyAsync(() -> {
     *     System.out.println("I am Task1");
     *     return "task1";
     * }, THREAD_POOL);
     *
     * CompletableFuture<String> task2 = CompletableFuture.supplyAsync(() -> {
     *     try {
     *         TimeUnit.SECONDS.sleep(2);
     *     } catch (InterruptedException e) {
     *
     *     }
     *     System.out.println("I am Task2");
     *     return "task2";
     * }, THREAD_POOL);
     *
     * // task1 , task2 任意一个完成，触发执行任务3
     * task1.acceptEither(task2, (res) -> {
     *     System.out.println("I am Task3");
     * });
     *
     * TimeUnit.SECONDS.sleep(5);
     * 
     * ---> 输出
     * I am Task1
     * I am Task3
     * I am Task2
     * </pre>
     * 
     */
    public CompletableFuture<Void> acceptEither(
            CompletionStage<? extends T> other, Consumer<? super T> action) {
        return orAcceptStage(null, other, action);
    }

    public CompletableFuture<Void> acceptEitherAsync(
            CompletionStage<? extends T> other, Consumer<? super T> action) {
        return orAcceptStage(asyncPool, other, action);
    }

    public CompletableFuture<Void> acceptEitherAsync(
            CompletionStage<? extends T> other, Consumer<? super T> action,
            Executor executor) {
        return orAcceptStage(screenExecutor(executor), other, action);
    }

    public CompletableFuture<Void> runAfterEither(CompletionStage<?> other,
            Runnable action) {
        return orRunStage(null, other, action);
    }

    public CompletableFuture<Void> runAfterEitherAsync(CompletionStage<?> other,
            Runnable action) {
        return orRunStage(asyncPool, other, action);
    }

    public CompletableFuture<Void> runAfterEitherAsync(CompletionStage<?> other,
            Runnable action,
            Executor executor) {
        return orRunStage(screenExecutor(executor), other, action);
    }
    
    /**
     * compose: v.组成，构成；作曲；撰写（信、诗或演讲稿）；使镇静，使平静；为（照片、图像）构图；排版，排稿
     * 
     * 可以使用thenCompose()按顺序链接两个CompletableFuture对象，实现异步的任务链。
     * 他的作用是将前一个任务的返回结果作为下一个任务的输入参数，从而形成一个依赖关系。(存在先后顺序)
     * 
     * <pre>
     *   CompletableFuture executeRes = CompletableFuture.supplyAsync(() -> {
     *       System.out.println("正在获取原材料");
     *       return "原材料";
     *   }, THREAD_POOL).thenCompose((res) -> {
     *       System.out.println("处理完成: " + res);
     *       return CompletableFuture.completedFuture("处理完成");
     *   });
     *   System.out.println(executeRes.get());
     *   System.out.println("END...");
     * 
     *  ---> 输出
     *  正在获取原材料
     *  处理完成: 原材料
     *  处理完成
     *  END...
     * 
     * </pre>
     * 
     * 
     */
    public <U> CompletableFuture<U> thenCompose(
            Function<? super T, ? extends CompletionStage<U>> fn) {
        return uniComposeStage(null, fn);
    }

    public <U> CompletableFuture<U> thenComposeAsync(
            Function<? super T, ? extends CompletionStage<U>> fn) {
        return uniComposeStage(asyncPool, fn);
    }

    public <U> CompletableFuture<U> thenComposeAsync(
            Function<? super T, ? extends CompletionStage<U>> fn,
            Executor executor) {
        return uniComposeStage(screenExecutor(executor), fn);
    }

    /**
     * <pre>
     * whenComplete* : 参数类型: BiConsumer<? super T, ? super Throwable>, 
     * 第一个参数是上一个CompletableFuture的返回值，第二个参数代表抛出的异常
     * 
     * ----- 正常情况
     * CompletableFuture.supplyAsync(() -> {
     *       System.out.println("Hello");
     *       return 1;
     *   }, THREAD_POOL).whenComplete((res, ex) -> {
     *       System.out.println("res -> " + res + " , ex: " + (null == ex ? "NULL" : ex.getClass().getName()));
     *   }).join();
     *
     *   Hello
     *   res -> 1 , ex: NULL
     *
     *------- 异常情况
     *   CompletableFuture.supplyAsync(() -> {
     *               System.out.println("Hello");
     *               throw new RuntimeException();
     *           }, THREAD_POOL).whenComplete((res, ex) -> {
     *               System.out.println("res -> " + res + " , ex: " + (null == ex ? "NULL" : ex.getClass().getName()));
     *           }).join();
     * System.out.println("END...");
     *   Hello
     *   res -> null , ex: java.util.concurrent.CompletionException
     *   # 异常会抛出,即: END... 不会被输出,具体看源码
     ** </pre>
     */
    public CompletableFuture<T> whenComplete(
            BiConsumer<? super T, ? super Throwable> action) {
        return uniWhenCompleteStage(null, action);
    }

    public CompletableFuture<T> whenCompleteAsync(
            BiConsumer<? super T, ? super Throwable> action) {
        return uniWhenCompleteStage(asyncPool, action);
    }

    public CompletableFuture<T> whenCompleteAsync(
            BiConsumer<? super T, ? super Throwable> action, Executor executor) {
        return uniWhenCompleteStage(screenExecutor(executor), action);
    }

    /**
     * <pre>
     *   CompletableFuture.supplyAsync(() -> {
     *      System.out.println("Hello");
     *      throw new RuntimeException();
     *  }, THREAD_POOL).handle((res, ex) -> {
     *      System.out.println("res -> " + res + " , ex: " + (null == ex ? "NULL" : ex.getClass().getName()));
     *      return 0;
     *  }).whenComplete((res, ex) -> {
     *      System.out.println("异常处理完成 -> " + res + " , ex: " + (null == ex ? "NULL" : ex.getClass().getName()));
     *  }).join();
     *  System.out.println("END...");
     * ----> 输出
     *    Hello
     *    res -> null , ex: java.util.concurrent.CompletionException
     *    异常处理完成 -> 0 , ex: NULL
     *    END...
     * </pre>
     * 
     * 
     */
    public <U> CompletableFuture<U> handle(
            BiFunction<? super T, Throwable, ? extends U> fn) {
        return uniHandleStage(null, fn);
    }

    public <U> CompletableFuture<U> handleAsync(
            BiFunction<? super T, Throwable, ? extends U> fn) {
        return uniHandleStage(asyncPool, fn);
    }

    public <U> CompletableFuture<U> handleAsync(
            BiFunction<? super T, Throwable, ? extends U> fn, Executor executor) {
        return uniHandleStage(screenExecutor(executor), fn);
    }

    /**
     * Returns this CompletableFuture.
     *
     * @return this CompletableFuture
     */
    public CompletableFuture<T> toCompletableFuture() {
        return this;
    }

    // not in interface CompletionStage

    /**
     * Returns a new CompletableFuture that is completed when this
     * CompletableFuture completes, with the result of the given
     * function of the exception triggering this CompletableFuture's
     * completion when it completes exceptionally; otherwise, if this
     * CompletableFuture completes normally, then the returned
     * CompletableFuture also completes normally with the same value.
     * Note: More flexible versions of this functionality(功能) are
     * available using methods {@code whenComplete} and {@code handle}.
     * flexible: adj.灵活的;
     * 
     * <pre>
     *    除了handleX()方法处理异常，该方法也可以处理异常，如注释，但是不如handle 、whenComplete灵活
     *   CompletableFuture.supplyAsync(() -> {
     *       System.out.println("Hello");
     *       throw new RuntimeException();
     *   }, THREAD_POOL).exceptionally((ex) -> {
     *       System.out.println("异常了 --> " + ex.getClass().getName());
     *       return "Hello";
     *   }).whenComplete((res, ex) -> {
     *       System.out.println("流程执行完成: " + res + "  EX: " + (null == ex ? "NULL" : ex.getClass().getName()));
     *   }).join();
     *   System.out.println("END...");
     * ---> 输出:
     * Hello
     * 异常了 --> java.util.concurrent.CompletionException
     * 流程执行完成: Hello  EX: NULL
     * END...
     * </pre>
     *
     * @param fn the function to use to compute the value of the
     *           returned CompletableFuture if this CompletableFuture completed
     *           exceptionally
     * @return the new CompletableFuture
     */
    public CompletableFuture<T> exceptionally(
            Function<Throwable, ? extends T> fn) {
        return uniExceptionallyStage(fn);
    }

    /* ------------- Arbitrary-arity constructions -------------- */

    /**
     * Returns a new CompletableFuture that is completed when all of
     * the given CompletableFutures complete. If any of the given
     * CompletableFutures complete exceptionally, then the returned
     * CompletableFuture also does so, with a CompletionException
     * holding this exception as its cause. Otherwise, the results,
     * if any, of the given CompletableFutures are not reflected in
     * the returned CompletableFuture, but may be obtained by
     * inspecting them individually. If no CompletableFutures are
     * provided, returns a CompletableFuture completed with the value
     * {@code null}.
     *
     * <p>
     * Among(在..中) the applications of this method is to await completion
     * of a set of independent CompletableFutures before continuing a
     * program, as in: {@code CompletableFuture.allOf(c1, c2, c3).join();}.
     * 这种方法的应用之一是在继续一个程序之前等待一组独立的CompletableFutures的完成.例如: CompletableFuture.allOf(c1, c2, c3).join();
     *
     * @param cfs the CompletableFutures
     * @return a new CompletableFuture that is completed when all of the
     *         given CompletableFutures complete.(当所有指定的CompletableFuture都完成了，会返回一个新的CompletableFuture.)
     *         
     * @throws NullPointerException if the array or any of its elements are
     *                              {@code null}
     */
    public static CompletableFuture<Void> allOf(CompletableFuture<?>... cfs) {
        return andTree(cfs, 0, cfs.length - 1);
    }

    /**
     * Returns a new CompletableFuture that is completed when any of
     * the given CompletableFutures complete, with the same result.
     * Otherwise, if it completed exceptionally, the returned
     * CompletableFuture also does so, with a CompletionException
     * holding this exception as its cause. If no CompletableFutures
     * are provided, returns an incomplete CompletableFuture.
     *
     * @param cfs the CompletableFutures
     * @return a new CompletableFuture that is completed with the
     *         result or exception of any of the given CompletableFutures when
     *         one completes
     * @throws NullPointerException if the array or any of its elements are
     *                              {@code null}
     */
    public static CompletableFuture<Object> anyOf(CompletableFuture<?>... cfs) {
        return orTree(cfs, 0, cfs.length - 1);
    }

    /* ------------- Control and status methods -------------- */

    /**
     * If not already completed, completes this CompletableFuture with
     * a {@link CancellationException}. Dependent CompletableFutures
     * that have not already completed will also complete
     * exceptionally, with a {@link CompletionException} caused by
     * this {@code CancellationException}.
     *
     * @param mayInterruptIfRunning this value has no effect in this
     *                              implementation because interrupts are not used
     *                              to control
     *                              processing.
     * @return {@code true} if this task is now cancelled
     */
    public boolean cancel(boolean mayInterruptIfRunning) {
        boolean cancelled = (result == null) &&
                internalComplete(new AltResult(new CancellationException()));
        postComplete();
        return cancelled || isCancelled();
    }

    /**
     * Returns {@code true} if this CompletableFuture was cancelled
     * before it completed normally.
     *
     * @return {@code true} if this CompletableFuture was cancelled
     *         before it completed normally
     */
    public boolean isCancelled() {
        Object r;
        return ((r = result) instanceof AltResult) &&
                (((AltResult) r).ex instanceof CancellationException);
    }

    /**
     * Returns {@code true} if this CompletableFuture completed
     * exceptionally, in any way. Possible causes include
     * cancellation, explicit invocation of {@code
     * completeExceptionally}, and abrupt termination of a
     * CompletionStage action.
     *
     * @return {@code true} if this CompletableFuture completed
     *         exceptionally
     */
    public boolean isCompletedExceptionally() {
        Object r;
        return ((r = result) instanceof AltResult) && r != NIL;
    }

    /**
     * Forcibly sets or resets the value subsequently returned by
     * method {@link #get()} and related methods, whether or not
     * already completed. This method is designed for use only in
     * error recovery actions, and even in such situations may result
     * in ongoing dependent completions using established versus
     * overwritten outcomes.
     *
     * @param value the completion value
     */
    public void obtrudeValue(T value) {
        result = (value == null) ? NIL : value;
        postComplete();
    }

    /**
     * Forcibly causes subsequent invocations of method {@link #get()}
     * and related methods to throw the given exception, whether or
     * not already completed. This method is designed for use only in
     * error recovery actions, and even in such situations may result
     * in ongoing dependent completions using established versus
     * overwritten outcomes.
     *
     * @param ex the exception
     * @throws NullPointerException if the exception is null
     */
    public void obtrudeException(Throwable ex) {
        if (ex == null)
            throw new NullPointerException();
        result = new AltResult(ex);
        postComplete();
    }

    /**
     * Returns the estimated number of CompletableFutures whose
     * completions are awaiting completion of this CompletableFuture.
     * This method is designed for use in monitoring system state, not
     * for synchronization control.
     *
     * @return the number of dependent CompletableFutures
     */
    public int getNumberOfDependents() {
        int count = 0;
        for (Completion p = stack; p != null; p = p.next)
            ++count;
        return count;
    }

    /**
     * Returns a string identifying this CompletableFuture, as well as
     * its completion state. The state, in brackets, contains the
     * String {@code "Completed Normally"} or the String {@code
     * "Completed Exceptionally"}, or the String {@code "Not
     * completed"} followed by the number of CompletableFutures
     * dependent upon its completion, if any.
     *
     * @return a string identifying this CompletableFuture, as well as its state
     */
    public String toString() {
        Object r = result;
        int count;
        return super.toString() +
                ((r == null)
                        ? (((count = getNumberOfDependents()) == 0) ? "[Not completed]"
                                : "[Not completed, " + count + " dependents]")
                        : (((r instanceof AltResult) && ((AltResult) r).ex != null) ? "[Completed exceptionally]"
                                : "[Completed normally]"));
    }

    // Unsafe mechanics
    private static final sun.misc.Unsafe UNSAFE;
    private static final long RESULT;
    private static final long STACK;
    private static final long NEXT;

    static {
        try {
            final sun.misc.Unsafe u;
            UNSAFE = u = sun.misc.Unsafe.getUnsafe();
            Class<?> k = CompletableFuture.class;
            RESULT = u.objectFieldOffset(k.getDeclaredField("result"));
            STACK = u.objectFieldOffset(k.getDeclaredField("stack"));
            NEXT = u.objectFieldOffset(Completion.class.getDeclaredField("next"));
        } catch (Exception x) {
            throw new Error(x);
        }
    }
}
