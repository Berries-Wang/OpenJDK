package link.bosswang.wei;

import java.nio.ByteBuffer;

public class DirectMemoryStu {
    public static void main(String[] args) {
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(1000);
    }
}
