import jdk.internal.vm.Continuation;
import jdk.internal.vm.ContinuationScope;

/**
 * OpenJDK 21
 * VM Options: --add-exports java.base/jdk.internal.vm=ALL-UNNAMED
 */
public class ContinuationDemo {
    /**
     * <pre>
     *   wei@Berries-Wang:~/OPEN_SOURCE/OpenJDK/005.OpenJDK/003.prictice-code$ /home/wei/OPEN_SOURCE/OpenJDK/005.OpenJDK/007.OpenJDK21-GA/OpenJDK21-GA/build/linux-x86_64-server-slowdebug/jdk/bin/javac --add-exports java.base/jdk.internal.vm=ALL-UNNAMED  ContinuationDemo.java 
     *   wei@Berries-Wang:~/OPEN_SOURCE/OpenJDK/005.OpenJDK/003.prictice-code$ /home/wei/OPEN_SOURCE/OpenJDK/005.OpenJDK/007.OpenJDK21-GA/OpenJDK21-GA/build/linux-x86_64-server-slowdebug/jdk/bin/java --add-exports java.base/jdk.internal.vm=ALL-UNNAMED  ContinuationDemo
     *   A
     *   Main Method 0
     *   B
     *   Main Method 1
     *   C
     *   Main Method 2
     * </pre>
     */
    public static void main(String[] args) {
        Continuation continuation = getContinuation();
        for (int i = 0; !continuation.isDone(); i++) {
            continuation.run();
            System.out.println("Main Method " + i);
        }

    }

    private static Continuation getContinuation() {
        ContinuationScope scope = new ContinuationScope("ContinuationDemo");
        Continuation cont = new Continuation(scope, () -> {
            System.out.println("A");
            Continuation.yield(scope);
            System.out.println("B");
            Continuation.yield(scope);
            System.out.println("C");
        });
        return cont;
    }
}