import java.util.concurrent.locks.ReentrantLock;

public class ThreadTest {
    private static final ReentrantLock reentrant_lock = new ReentrantLock();

    public static void main(String[] args) throws InterruptedException {
        Thread thread_a = new Thread(() -> {
            reentrant_lock.lock();
            try {
                Thread.sleep(Long.MAX_VALUE);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }, "Thread.sleep");

        Thread thread_b = new Thread(() -> {
            reentrant_lock.lock();
        }, "ReentrantLock");

        thread_a.start();
        thread_b.start();

        for (; ; ) {
            Thread.sleep(10000);
            System.out.println("Hello World");
        }
    }
}
