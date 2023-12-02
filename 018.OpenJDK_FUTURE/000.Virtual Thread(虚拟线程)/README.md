# Virtual Thread (虚拟线程)
&nbsp;&nbsp;虚拟线程在JDK中以用户模式实现。

Virtual Thread = Continuation + Scheduler 
- Scheduler: Java core libraries , 即在核心库中，用Java编写，本质上是一个ExcutorService
- Continuation 在VM中实现。

## 虚拟线程编码方式
> 传送门:[VirtualThreadsStu.java](../../005.OpenJDK/003.prictice-code/VirtualThreadsStu.java)


## 参考资料
1. [https://docs.oracle.com/en/java/javase/21/core/virtual-threads.html#GUID-BEC799E0-00E9-4386-B220-8839EA6B4F5C](https://docs.oracle.com/en/java/javase/21/core/virtual-threads.html#GUID-BEC799E0-00E9-4386-B220-8839EA6B4F5C)
2. [002.Why-User-Mode-Threads-Are-Good-For-Performance](../../012.WHAT_HOW_WHY/002.Why-User-Mode-Threads-Are-Good-For-Performance/Why-User-Mode-Threads-Are-Good-For-Performance.md)
3. [java-core-libraries-developer-guide.pdf#14Concurrency#Virtual Threads](./java-core-libraries-developer-guide.pdf)
4. [JEP 444_ Virtual Threads.pdf(https://openjdk.org/jeps/444)](./JEP%20444_%20Virtual%20Threads.pdf)
   > 通过3,4两点，可以知晓虚拟线程的实现方式、实现目的(提升吞吐量，而不是提升性能)、调度方式
