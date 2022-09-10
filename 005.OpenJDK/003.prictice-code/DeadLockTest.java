package link.bosswang.wei;

import java.util.concurrent.locks.ReentrantLock;

public class DeadLockTest {
    public static void main(String[] args) {

        ReentrantLock lock1 = new ReentrantLock();
        ReentrantLock lock2 = new ReentrantLock();

        (new Thread(() -> {
            lock1.lock();

            System.out.println("T1");

            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            lock2.lock();
        })).start();


        (new Thread(() -> {
            lock2.lock();

            System.out.println("T1");

            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            lock1.lock();
        })).start();

    }
}
