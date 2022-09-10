package link.bosswang.wei;

import java.util.concurrent.CountDownLatch;

public class CountDownLatchStu {
    public static void main(String[] args) throws InterruptedException {

        CountDownLatch countDownLatch = new CountDownLatch(3);

        Thread t1 = new Thread(() -> {
            try {

                System.out.println("Hello CountDownLatch : " + Thread.currentThread().getId());

                Thread.sleep(2000);

            } catch (Exception e) {

            } finally {
                countDownLatch.countDown();
            }
        });


        Thread t2 = new Thread(() -> {
            try {

                System.out.println("Hello CountDownLatch : " + Thread.currentThread().getId());

                Thread.sleep(2000);

            } catch (Exception e) {

            } finally {
                countDownLatch.countDown();
            }
        });


        Thread t3 = new Thread(() -> {
            try {

                System.out.println("Hello CountDownLatch : " + Thread.currentThread().getId());

                Thread.sleep(2000);

            } catch (Exception e) {

            } finally {
                countDownLatch.countDown();
            }
        });

        Thread t4 = new Thread(() -> {
            Thread.currentThread().setName("I am T4");
            try {
                countDownLatch.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            System.out.println("T4");
        });

        t4.start();

        t1.start();
        t2.start();
        t3.start();


        countDownLatch.await();
        System.out.println("Exit Main");
    }
}
