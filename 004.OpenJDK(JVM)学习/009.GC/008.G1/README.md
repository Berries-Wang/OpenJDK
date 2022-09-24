# G1(Garbage-First Garbage Collector,垃圾优先型垃圾回收器)
&nbsp;&nbsp;G1是目前最成熟的垃圾收集器，G1出现的目标就是代替CMS，CMS作为使用最广泛的垃圾收集器，对其众多的参数进行正确的设置也是头疼的事情。因此，G1在设计之初就希望降低程序员的负担，减少(注意，是减少)人工的介入。

&nbsp;&nbsp;G1 学习目标
1. 熟悉G1的原理，只有熟悉原理才能知道调优的方向。
2. 能分析和解读G1运行的日志信息，根据日志信息找到G1运行过程中的异常信息，并推断哪些参数可以解决这些异常。

## Garbage-First 即垃圾优先收集器，为什么叫垃圾优先收集器?
```txt
  / /  004.OpenJDK(JVM)学习/009.GC/008.G1/docs-en/Getting-Started-with-the-G1-Garbage-Collector.pdf
   G1 performs a concurrent global marking phase to determine the liveness of objects throughout the heap. After the mark phase completes, G1 knows which regions are mostly empty. It collects in these regions first, which usually yields a large amount of free space. This is why this method of garbage collection is called Garbage-First. 

   在G1对全局进行标记并决定堆上对象的活跃度后，它立刻就知道堆上的哪些区域几乎是空闲的。在标记阶段完成之后，G1知道哪些区域大部分是空的。他首先在这些区域收集，这通常会产生大量的空闲空间。这就是为什么这种垃圾收集方式叫做 Garbage-First
```
---
## G1基本概念
### 分区
见[G1分区](./002.G1分区.md)
## G1 的Refine线程
&nbsp;&nbsp;Refine线程是G1新引入的并发线程池，分为两大功能:
1. 用于处理新生代分区的抽样，并且在满足响应时间的这个指标下，更新YHR的数目。
2. 管理RSet,这是Refine最主要的功能。

## G1垃圾回收方式
&nbsp;&nbsp;G1新生代的回收方式是并行回收，采用复制算法。G1与其他JVM垃圾收集器一样，一旦发生一次新生代回收，整个新生代都会被回收。

&nbsp;&nbsp;G1与其他垃圾回收器的不同之处在于：
1. G1会根据预测时间动态地改变新生代的大小。
2. G1老年代的垃圾回收方式与其他JVM垃圾回收器堆老年代处理有着很大的不同
    > **Mixed GC**: G1不会为了释放老年代的空间而对整个老年代进行回收。相反，在任意时刻只有一部分老年代分区会被回收，且这部分老年代分区将在下一次增量回收时与所有新生代一起被回收。(<font color="red">**部分回收，高效地满足需求，更短的停顿时间**</font>)
    >> 在选择老年代分区时，优先考虑垃圾多的分区。

## G1垃圾回收的时机
&nbsp;&nbsp;G1垃圾回收发生在两个时机，且不同的回收时机选择的回收方式也不同。回收时机：
1. 在分配内存时发现内存不足，进入垃圾回收(同ParNew + CMS).
2. 外部显式调用回收的方法，如在Java代码中调用System.gc()进入回收。


---
## 参考资料
1. [Java Hotspot G1 GC的一些关键技术](https://tech.meituan.com/2016/09/23/g1.html)
2. [垃圾优先型垃圾回收器调优](https://www.oracle.com/cn/technical-resources/articles/java/g1gc.html)