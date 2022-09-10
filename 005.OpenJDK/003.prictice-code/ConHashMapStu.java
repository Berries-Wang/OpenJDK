package link.bosswang.wei;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadLocalRandom;

public class ConHashMapStu {
    public static void main(String[] args) throws InterruptedException {
        ConcurrentHashMap<Integer, Integer> map = new ConcurrentHashMap<>();
        for (int i = 0; i < 8; i++) {
            int no = i;
            (new Thread(() -> {
                Thread.currentThread().setName("I am a test Thead , NO." + no);
                ThreadLocalRandom localRandom = ThreadLocalRandom.current();
                for (int j = 0; j < 10000; j++) {
                    int randomInt = localRandom.nextInt();
                    map.put(randomInt, randomInt);
                }
            })).start();
            System.out.println("启动了一个线程");
        }

        CountDownLatch countDownLatch = new CountDownLatch(1);
        countDownLatch.await();
    }
}
