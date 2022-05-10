package link.bosswang.wei;

import org.openjdk.jol.info.ClassLayout;

/**
 * <pre>
 *     <dependency>
 *       <groupId>org.openjdk.jol</groupId>
 *       <artifactId>jol-core</artifactId>
 *       <version>0.8</version>
 *     </dependency>
 * </pre>
 */
public class SynchronizedStu {
    private static final SynchronizedStu obj = new SynchronizedStu();

    /**
     * VM参数:  -XX:BiasedLockingStartupDelay=0 
     */
    public static void main(String[] args) {

        // 打印锁对象头信息，输出内容如下:
        /**
         * <pre>
         * Main->: link.bosswang.wei.SynchronizedStu object internals:
         *  OFFSET  SIZE   TYPE DESCRIPTION                               VALUE
         *       0     4        (object header)                           05 00 00 00 (00000101 00000000 00000000 00000000) (5)
         *       4     4        (object header)                           00 00 00 00 (00000000 00000000 00000000 00000000) (0)
         *       8     4        (object header)                           06 c1 00 20 (00000110 11000001 00000000 00100000) (536920326)
         *      12     4        (loss due to the next object alignment)
         * Instance size: 16 bytes
         * Space losses: 0 bytes internal + 4 bytes external = 4 bytes total
         * </pre>
         */
        System.out.println("Main->: " + ClassLayout.parseInstance(obj).toPrintable());


       /* int i=0;
        while (++i<60){
            synchronized (obj){
                System.out.println(ClassLayout.parseInstance(obj).toPrintable());
            }
        }*/


    }

    public static void say() {
//        synchronized (obj) {
//            System.out.println("Hello World: " + Thread.currentThread().getId());
//        }
    }

}