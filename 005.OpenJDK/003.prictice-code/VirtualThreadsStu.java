import java.lang.Thread;
import java.lang.Runnable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.Executors;

public class VirtualThreadsStu {
    public static void main(String[] args) throws Exception {
        // Creating a Virtual Thread with the Thread Class and the Thread.Builder
        // Interface
        Thread thread = Thread.ofVirtual()
                .start(() -> System.out.println("I am a Virtual Thread: " + Thread.currentThread().getName()));
        thread.join();

        Thread vThread231202 = Thread.ofVirtual().name("Virtual Thread 2023-12-02").start(() -> {
            System.out.println("Running Thread : " + Thread.currentThread().getName());
        });
        vThread231202.join();

        /**
         * 使用Thread.Builder 创建并启动两个虚拟线程
         * 输出:
         * Thread ID: 23
         * Worker-0 terminated
         * Thread ID: 24
         * Worker-1 terminated
         */
        Thread.Builder vThreadBuilder = Thread.ofVirtual().name("Worker-", 0);
        Runnable task = () -> {
            System.out.println("Thread ID: " + Thread.currentThread().threadId());
        };
        Thread vT1 = vThreadBuilder.start(task);
        vT1.join();
        System.out.println(vT1.getName() + " terminated");

        Thread vT2 = vThreadBuilder.start(task);
        vT2.join();
        System.out.println(vT2.getName() + " terminated");

        /**
         * 使用 Executors.newVirtualThreadPerTaskExecutor() 创建并运行虚拟线程
         * 输出:
         * Running thread
         * Task Completed
         */
        ExecutorService vTExecutor = Executors.newVirtualThreadPerTaskExecutor();
        Future<?> future = vTExecutor.submit(() -> {
            System.out.println("Running thread");
        });
        future.get();
        System.out.println("Task Completed");
    }
}
