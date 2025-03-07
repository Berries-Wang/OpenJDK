public class ThreadExitByInterrupt {
    public void run() {
        while (true) {
            System.out.println("执行Ing...");

            // 当线程被终止了
            if (Thread.currentThread().isInterrupted()) {
                break;
            }
        }
    }

    public static void main(String[] args) throws Exception {
        ThreadExitByInterrupt runnable = new ThreadExitByInterrupt();
        Thread thread = new Thread(runnable::run);

        thread.start();

        /**
         * 这个有点意思了，如果线程A Thread.sleep(xxx);了，如果此时还调用他的.interrupt()方法，那么线程A就会抛出异常
         * 
         * 抛出中断异常后，中断标志位会被清除
         */
        Thread.sleep(3000);

        // 中断线程: 只是告诉线程有人需要他停止执行，而不是立即让他停止。
        thread.interrupt();
    }
}
