# ParNew + CMS
&nbsp;&nbsp;对于ParNew + CMS 的组合，实际案例分析请参考资料 [004.OpenJDK(JVM)学习/009.GC/004.ParNew+CMS/从实际案例聊聊Java应用的GC优化.pdf](./从实际案例聊聊Java应用的GC优化.pdf) 以及 [004.OpenJDK(JVM)学习/009.GC/004.ParNew+CMS/Java中9种常见的CMS-GC问题分析与解决.pdf](./Java中9种常见的CMS-GC问题分析与解决.pdf)。

&nbsp;&nbsp;以上资料详细讲述了GC的案例以及分析与解决方案，那么在这里，主要是通过源代码了解理论知识，如CMS的过程，Concurrent Mode Failure、碎片化

## 源码分析路径
&nbsp;&nbsp;当对象内存分配失败了，就会触发GC。以此作为分析的切入点,开始分析ParNew + CMS 源代码
```c
    
```
## 内存分区(OpenJDK1.8)
- <img src = "./pics/jsgct_dt_006_prm_gn_sz_new.png"/>
- + Virtual 即虚拟内存(未使用的内存，在初始化JVM堆或者元空间时进行的预留空间操作就是申请虚拟地址空间，即虚拟内存)，即UnCommit的内存。虚拟内存之前的是已经使用的内存，即commited的内存。
    -  [HotSpot Virtual Machine Garbage Collection Tuning Guide](../HotSpot%20Virtual%20Machine%20Garbage%20Collection%20Tuning%20Guide.pdf)
    - [HotSpot Virtual Machine Garbage Collection Tuning Guide](../hotspot-virtual-machine-garbage-collection-tuning-guide.pdf)

## 关于CMS-Full GC
1. 阅读[CMS介绍](./001.CMS介绍.md)可知，当并发失败,CMS会退化为单线程的Ful GC，性能会更差
2. [/深入理解Java虚拟机.pdf](../../../006.BOOKs/深入理解Java虚拟机.pdf) 可知,CMS 的Full GC 并不是单线程的,不要弄混了。

---

## CMS 收集器参数
### 1. -XX:+UseCMSCompactAtFullCollection
-XX:+UseCMSCompactAtFullCollection 是 JVM 的一个参数，用于控制 CMS（Concurrent Mark-Sweep）垃圾收集器 的行为。具体来说，它决定了在发生 Full GC 时，是否对老年代（Old Generation）的内存空间进行压缩整理（Compaction）

CMS 垃圾收集器在并发清理阶段不会对内存进行压缩，这可能导致内存碎片问题。内存碎片会使得老年代虽然有足够的空闲内存，但无法分配连续的大对象，从而触发不必要的 Full GC。启用内存压缩可以解决这个问题，但会增加 Full GC 的停顿时间

### 2. -XX:+UseConcMarkSweepGC
启用 CMS 垃圾收集器

### 3. -XX:CMSFullGCsBeforeCompaction=5
设置在执行多少次 Full GC 后才进行一次内存压缩。默认值为 0，表示每次 Full GC 都进行压缩。

示例：

-XX:CMSFullGCsBeforeCompaction=5 表示每 5 次 Full GC 才进行一次内存压缩

### 4. -XX:CMSInitiatingOccupancyFraction
该参数用于设置老年代内存使用率的阈值。当老年代的内存使用率达到这个百分比时，CMS 垃圾收集器会启动并发收集周期。

例如，-XX:CMSInitiatingOccupancyFraction=75 表示当老年代内存使用率达到 75% 时，触发 CMS 并发收集。

默认情况下，-XX:CMSInitiatingOccupancyFraction 的值是 -1，这意味着 JVM 会根据运行时的实际情况自动选择一个合适的值。

如果没有显式设置该参数，JVM 通常会使用一个保守的默认值（例如 92%）。

### 5. -XX:+CMSParallelRemarkEnabled 
-XX:+CMSParallelRemarkEnabled 是 JVM 的一个参数，用于控制 CMS（Concurrent Mark-Sweep）垃圾收集器 的行为。具体来说，它决定了在 CMS 垃圾收集器的 重新标记阶段（Remark Phase） 是否启用并行化处理。

### 6. -XX:+CMSClassUnloadingEnabled
-XX:+CMSClassUnloadingEnabled 是 JVM 的一个参数，用于控制 CMS（Concurrent Mark-Sweep）垃圾收集器 是否支持 类卸载（Class Unloading）。具体来说，它决定了在 CMS 垃圾收集周期中，是否可以卸载不再使用的类及其元数据。
- 在 JDK 8 及更早版本中，-XX:+CMSClassUnloadingEnabled 默认是 禁用的。

### 7. -XX:-UseParNewGC 
-XX:-UseParNewGC 是 Java 虚拟机（JVM）中的一个命令行参数，用于禁用 ParNew GC（Parallel New Garbage Collector）。ParNew GC 是一种并行的垃圾回收器，通常与 CMS（Concurrent Mark-Sweep）垃圾回收器一起使用，特别是在多处理器环境下，能够提高新生代垃圾回收的并行性。

---

## 参考
1. [006.BOOKs/深入理解Java虚拟机.pdf] 3.5.6 CMS收集器
2. [HotSpot Virtual Machine Garbage Collection Tuning Guide](../HotSpot%20Virtual%20Machine%20Garbage%20Collection%20Tuning%20Guide.pdf)
3. [HotSpot Virtual Machine Garbage Collection Tuning Guide](../hotspot-virtual-machine-garbage-collection-tuning-guide.pdf)