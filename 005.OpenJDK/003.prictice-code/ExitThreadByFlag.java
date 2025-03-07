public class ExitThreadByFlag {
    private boolean exitFlag = false;

    public void stop() {
        exitFlag = true;
    }

    public void run() {
        while (!exitFlag) {
            System.out.println("继续执行");

            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                // 处理中断，如果需要
                Thread.currentThread().interrupt(); // 重新设置中断状态
            }
        }

        System.out.println("Thread 退出...");
    }

    public static void main(String[] args) throws Exception {
        ExitThreadByFlag exitThreadFlag = new ExitThreadByFlag();
        Thread thread = new Thread(exitThreadFlag::run);
        thread.start();

        Thread.sleep(3000);

        // 请求线程停止
        exitThreadFlag.stop();

    }

}
