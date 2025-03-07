public class ExitThreadByStop {
    public void run() {
        while (true) {
            System.out.println("继续执行");

            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                // 处理中断，如果需要
                Thread.currentThread().interrupt(); // 重新设置中断状态
            }
        }
    }

    public static void main(String[] args) throws Exception {
        ExitThreadByStop exitThreadByStop = new ExitThreadByStop();
        Thread thread = new Thread(exitThreadByStop::run);
        thread.start();

        Thread.sleep(3000);

        // 强制终止: 在JVM 内部，通过VM_ThreadStop VM_Operation 来停止线程
        thread.stop();
    }
}
