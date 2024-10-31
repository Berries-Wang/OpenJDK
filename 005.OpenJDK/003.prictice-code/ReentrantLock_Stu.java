import java.util.concurrent.locks.ReentrantLock;

public class ReentrantLock_Stu {
    public static void main(String[] args) {
        ReentrantLock lock = new ReentrantLock();
        lock.lock();
        System.out.println("Hello ReentrantLock");
        lock.unlock();
    }
}
