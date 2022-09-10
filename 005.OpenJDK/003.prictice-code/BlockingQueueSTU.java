package link.bosswang.wei;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.SynchronousQueue;

public class BlockingQueueSTU {
    public static void main(String[] args) throws InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(2);

        SynchronousQueue<Integer> synchronousQueue = new SynchronousQueue<>();
        Runnable producer = () -> {
            System.out.println("producer - begin");
            try {
                synchronousQueue.put(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("producer end");
        };

        Runnable consumer = () -> {
            System.out.println("consumer start");
            Integer take = null;
            try {
                take = synchronousQueue.take();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("consumer end");
        };

        /**
         * 输出
         * --->   <---
         * producer 1
         * consumer 1
         */
        executorService.submit(producer); // 会阻塞，直到take方法被调用
        Thread.sleep(200);
        System.out.println("--->   <---");
        executorService.submit(consumer);

        executorService.submit(consumer);
        Thread.sleep(200);
        System.out.println("--->   <---");
        executorService.submit(producer);


        Thread.sleep(30000000);
    }
}
