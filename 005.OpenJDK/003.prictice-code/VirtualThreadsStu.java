import java.lang.Thread;

public class VirtualThreadsStu {
    public static void main(String[] args) throws Exception {
        Thread thread = Thread.ofVirtual()
                .start(() -> System.out.println("I am a Virtual Thread: " + Thread.currentThread().getName()));
        thread.join();
    }
}
