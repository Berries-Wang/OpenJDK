# VirtualThread
> 先学习:[Continuation实现原理-1](./000.LESSONS/000.Continuation实现原理/continuation-001.mp4) & [Continuation实现原理-2](./000.LESSONS/000.Continuation实现原理/continuation-002.mp4)
>> 学习视频后，个人理解: 平台线程执行的基础就是堆栈和寄存器，通过切换平台线程的堆栈，从而达到执行不同的虚拟线程的目的。

VirtualThread = Scheduler <sub>Java. Core Libraries</sub> + Continuation<sub>VM</sub>


## VirtualThread 实现原理
### Continuation 使用Demo
> [ContinuationDemo.java](../../005.OpenJDK/003.prictice-code/ContinuationDemo.java)
```java
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
```


## Q&A
### 1. GC Roots枚举问题
+ 对于平台线程，当进行GC时，线程堆栈是GC Root, 所以开始一个GC周期时，需要扫描这些线程的堆栈，发现其中的oop，因为没有那么多线程，因此线程堆栈内存并没有那么大，因此这是可行的。
+ 但是虚拟线程并不一样，可以有数百万个虚拟线程，虚拟线程可以在堆栈中存储相当多的数据，所以，虚拟线程的堆栈不会成为GC Root —— StackChunk , GC通过解析StackChunk来获取oop信息，
  - 惰性复制StackChunk<sub>每次复制一两帧，再设置返回屏障 </sub> + 返回屏障<sub>触及返回屏障，就会解冻其他帧，然后再设置返回屏障...</sub> 来继续执行虚拟线程。
  - GC 从StackChunk 中解析 oop