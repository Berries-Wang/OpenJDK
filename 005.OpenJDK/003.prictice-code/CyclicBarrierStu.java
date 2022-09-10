package link.bosswang.wei;

import java.util.concurrent.CyclicBarrier;

public class CyclicBarrierStu {
    public static void main(String[] args) {
        int threadNum = 3;


        CyclicBarrier cyclicBarrier = new CyclicBarrier(threadNum, () -> {
            System.out.println("BarrierAction: 每一个批次await后的收尾动作");
        });

        for (int index = 0; index < threadNum; index++) {
            int threadIndex = index;
            (new Thread(() -> {

                System.out.printf("Thread %s 在到达A的途中%n", threadIndex);
                try {
                    cyclicBarrier.await();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                System.out.printf("Thread %s 到达了A%n", threadIndex);


                try {
                    Thread.sleep(3000);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                System.out.printf("Thread %s 在到达B的途中%n", threadIndex);
                try {
                    cyclicBarrier.await();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                System.out.printf("Thread %s 到达了B%n", threadIndex);

            })).start();
        }
    }
}
