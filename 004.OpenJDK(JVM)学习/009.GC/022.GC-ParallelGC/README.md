# ParallelGC
&nbsp;&nbsp;JDK8默认的垃圾收集器。

&nbsp;&nbsp;若使用者对于收集器运作不太了解，手工优化存在困难的话，使用Parallel Scavenge收集器配合自适应调节策略，把内存管理的调优任务交给虚拟机去完成也许是一个很不错的选择。只需要把基本的内存数据设置好（如-Xmx设置最大堆），然后使用-XX:MaxGCPauseMillis参数（更关注最大停顿时间）或-XX:GCTimeRatio更关注吞吐量）参数给虚拟机设立一个优化目标，那具体细节参数的调节工作就由虚拟机完成了。


##### 当满足一定条件，对象会在老年代分配内存
![WeChat_20241123162824.jpg](./pics/WeChat_20241123162824.jpg)

## 参考
1. [006.BOOKs/深入理解Java虚拟机.pdf] 3.5.5 Parallel Old收集器