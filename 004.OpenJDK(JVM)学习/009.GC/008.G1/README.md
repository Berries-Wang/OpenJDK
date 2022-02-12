# G1(Garbage-First Garbage Collector,垃圾优先型垃圾回收器)
&nbsp;&nbsp;G1是目前最成熟的垃圾收集器，G1出现的目标就是代替CMS，CMS作为使用最广泛的垃圾收集器，对其众多的参数进行正确的设置也是头疼的事情。因此，G1在设计之初就希望降低程序员的负担，减少(注意，是减少)人工的介入。

&nbsp;&nbsp;G1 学习目标
1. 熟悉G1的原理，只有熟悉原理才能知道调优的方向。
2. 能分析和解读G1运行的日志信息，根据日志信息找到G1运行过程中的异常信息，并推断哪些参数可以解决这些异常。

## G1基本概念
### 分区
见[G1分区](./002.G1分区.md)
## G1 的Refine线程
&nbsp;&nbsp;Refine线程是G1新引入的并发线程池，分为两大功能:
1. 用于处理新生代分区的抽样，并且在满足响应时间的这个指标下，更新YHR的数目。
2. 管理RSet,这是Refine最主要的功能。

---
## 参考资料
1. [https://tech.meituan.com/2016/09/23/g1.html](https://tech.meituan.com/2016/09/23/g1.html)
2. [垃圾优先型垃圾回收器调优](https://www.oracle.com/cn/technical-resources/articles/java/g1gc.html)