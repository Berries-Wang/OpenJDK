package link.bosswang.wei;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Java BIO代码示例
 *
 * <pre>
 *     Java BIO： 同步并阻塞(传统阻塞型)，服务器实现模式为一个连接一个线程，即客户端有连接请求时服务器端就需要启动一个线程进行处理，
 *     如果这个连接不做任何事情会造成不必要的开销.
 *
 *     可以使用 telnet 127.0.0.1 9999 测试该程序
 *
 *     存在的问题:
 *       1. 每个请求都需要创建独立的线程，与对应的客户端进行数据 Read，业务处理，数据 Write。
 *       2. 当并发数较大时，需要创建大量线程来处理连接，系统资源占用较大。
 *       3. 连接建立后，如果当前线程暂时没有数据可读，则线程就阻塞在 Read 操作上，造成线程资源浪费。
 * </pre>
 */
public class BIOStu {
    public static void main(String[] args) throws IOException {

        ExecutorService newCachedThreadPool = Executors.newCachedThreadPool();
        ServerSocket serverSocket = new ServerSocket(9999);
        System.out.println("服务器启动了");

        for (; ; ) {
            System.out.println("---> 等待连接");

            // 会阻塞在accept上
            Socket clientSocket = serverSocket.accept();

            newCachedThreadPool.execute(() -> {
                // 与客户端通信
                handle(clientSocket);
            });

        }

    }

    public static void handle(Socket clientSocket) {
        try {

            byte[] bytes = new byte[1024];
            InputStream inputStream = clientSocket.getInputStream();

            for (; ; ) {
                int read = inputStream.read(bytes);
                if (read != -1) {
                    System.out.println(new String(bytes, 0, read));
                } else {
                    break;
                }
            }
        } catch (Throwable ex) {
            ex.printStackTrace();
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                System.out.println("ClientSocket 关闭异常: " + e.getMessage());
            }
            System.out.println("ClientSocket 关闭完成");
        }
    }
}
