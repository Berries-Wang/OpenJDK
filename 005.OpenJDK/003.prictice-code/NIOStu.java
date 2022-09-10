package link.bosswang.wei;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

/**
 * Java NIO代码示例
 * <pre>
 *    Java NIO 全称Java non-blocking IO,指JDK提供的新API。从JDK1.4开始，Java提供了一系列改进的输入输出的新特性，
 *    被统称为NIO(New IO),是 同步非阻塞的(多路复用).
 *
 *    NIO分为三大核心部分: Channel 、 Buffer 、 Selector
 *       > Selector 告知哪些Channel有数据到达，处理线程从channel中读取出数据并放到Buffer中，用户程序再从Buffer中读取出来
 *
 * </pre>
 */
public class NIOStu {
    public static void main(String[] args) throws IOException {

        NIOServer nioServer = new NIOServer();
        nioServer.listen();

    }

    public static class NIOServer {
        private final Selector selector;
        private final ServerSocketChannel listenChannel;


        public NIOServer() throws IOException {
            selector = Selector.open();

            listenChannel = ServerSocketChannel.open();

            listenChannel.socket().bind(new InetSocketAddress(9999));

            // 设置为非阻塞模式
            listenChannel.configureBlocking(false);

            // 将 listenChannel 注册到selector上
            listenChannel.register(selector, SelectionKey.OP_ACCEPT);
        }

        public void listen() {
            for (; ; ) {
                try {
                    // 等待事件发生
                    int eventNum = selector.select();

                    if (eventNum > 0) {
                        Iterator<SelectionKey> ite = selector.selectedKeys().iterator();
                        while (ite.hasNext()) {
                            SelectionKey selectionKey = ite.next();

                            // 当由客户端连接到达
                            if (selectionKey.isAcceptable()) {

                                // 建立客户端连接
                                SocketChannel clientChannel = listenChannel.accept();

                                // 设置为非阻塞IO
                                clientChannel.configureBlocking(false);

                                // 将已连接Channel注册到Selector上
                                clientChannel.register(selector, SelectionKey.OP_READ);

                                System.out.println(clientChannel.getRemoteAddress() + " 已连接");
                            }

                            // 当有数据到达
                            if (selectionKey.isReadable()) {
                                readData(selectionKey);
                            }

                            // 移出已经处理过的Key，避免重复处理
                            ite.remove();
                        }
                    }

                } catch (Throwable ignored) {

                }
            }
        }

        public void readData(SelectionKey selectionKey) {
            try {

                SocketChannel channel = (SocketChannel) selectionKey.channel();

                ByteBuffer buffer = ByteBuffer.allocate(1024);

                // 从Channel中读取数据
                int dataLen = channel.read(buffer);

                if (dataLen > 0) {
                    System.out.println("收到消息: " + (new String(buffer.array(), 0, dataLen)));
                }
            } catch (Throwable e) {
                e.printStackTrace();
            } finally {

            }
        }
    }
}