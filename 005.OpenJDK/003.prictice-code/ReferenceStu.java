package link.bosswang.wei;

import java.lang.ref.SoftReference;

public class ReferenceStu {
    public static void main(String[] args) throws InterruptedException {

        while (true){
            {
                Object o = new Object();
                SoftReference<Object> objectSoftReference = new SoftReference<>(o);
                SoftReference<Object> objectSoftReference1 = new SoftReference<>(o);
                SoftReference<Object> objectSoftReference2 = new SoftReference<>(o);
                SoftReference<Object> objectSoftReference3 = new SoftReference<>(o);
            }

            Byte[] bytes = new Byte[512];
            System.out.println("Hello");

            Thread.sleep(300);
        }


    }
}
