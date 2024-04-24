
import java.util.concurrent.*;

public class PAThreadPoolExecutor extends ThreadPoolExecutor {

    public PAThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime,
            TimeUnit unit, BlockingQueue<Runnable> workQueue,
            ThreadFactory threadFactory, RejectedExecutionHandler handler) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory, handler);
    }

    @Override
    protected void afterExecute(Runnable task, Throwable exception) {

        // 因为线程池提交的方式不同，"task" 的类型也不一样,因此需要兼容
        if (task instanceof Future) {
            // try {
            //     Object result = ((Future<?>) task).get();

            //     if (result instanceof Exception) {
            //         Exception ex = (Exception) result;
            //         System.out.println("PAThreadPoolExecutor.afterExecute 出现异常，异常信息:[{}] [{}]" + ex.getMessage());
            //     }

            // } catch (InterruptedException | ExecutionException e) {
            //     System.out.println("PAThreadPoolExecutor.afterExecute 出现异常，异常信息:[{}] [{}]" + e.getMessage());

            // }
        }

        // 如果task 是 Future类型的，则 "exception"为null
        if (null != exception) {
            System.out.println("PAThreadPoolExecutor.afterExecute 出现异常，异常信息:[{}] [{}]" + exception.getMessage());

        }
    }
}