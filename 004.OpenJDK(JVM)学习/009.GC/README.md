# JVM GC(Garbage Collection)
> 004.OpenJDK(JVM)学习/003.JVM启动/001.Universe初始化/README.md

&nbsp;&nbsp; 从 005.OpenJDK/001.openJdk8-b120/jdk-jdk8-b120/hotspot/src/share/vm/memory/universe.cpp#Universe::initialize_heap开始
## CollectedHeap
&nbsp;&nbsp;CollectedHeap是一个接口，CollectedHeap类根据CollectorPolicy中设置的值确定策略。CollectedHeap类定义了对象的分配和回收的接口。
### 重要方法
1. allocate_from_tlab
2. mem_allocate
3. collect

---

## CollectorPolicy
&nbsp;&nbsp;CollectorPolicy类是一个定义了对象管理功能策略的类。该类保存与对象管理功能相关的设置值，例如，该类在执行Java命令时设置不同的参数(如GC算法);

---
## CollectedHeap之间的继承关系
+ 见文件: 005.OpenJDK/001.openJdk8-b120/jdk-jdk8-b120/hotspot/src/share/vm/gc_interface/collectedHeap.hpp
+ CollectedHeap
    - SharedHeap
       + GenCollectedHeap
       + G1CollectedHeap
    - ParallelScavengeHeap


## 学习目标是什么
1. 针对与不同的垃圾收集器，软引用，弱引用，强引用，虚引用分别在什么时机回收的?

---
## 备注
1. 学习所有的垃圾收集器没有必要，主要学习
    - ParNew + CMS的组合
    - G1垃圾收集器

---
## 遇到的一些VM参数
### 1. AdaptiveSizePolicy(自适应大小策略)
&nbsp;&nbsp;JDK 1.8 默认使用 UseParallelGC 垃圾回收器，该垃圾回收器默认启动了 AdaptiveSizePolicy，会根据GC的情况自动计算计算 Eden、From 和 To 区的大小
#### 注意事项：
 - 在 JDK 1.8 中，如果使用 CMS，无论 UseAdaptiveSizePolicy 如何设置，都会将 UseAdaptiveSizePolicy 设置为 false；不过不同版本的JDK存在差异；
 - UseAdaptiveSizePolicy不要和SurvivorRatio参数显示设置搭配使用，一起使用会导致参数失效；
 - 由于AdaptiveSizePolicy会动态调整 Eden、Survivor 的大小，有些情况存在Survivor 被自动调为很小，比如十几MB甚至几MB的可能，这个时候YGC回收掉 Eden区后，还存活的对象进入Survivor 装不下，就会直接晋升到老年代，导致老年代占用空间逐渐增加，从而触发FULL GC，如果一次FULL GC的耗时很长（比如到达几百毫秒），那么在要求高响应的系统就是不可取的。

## 收集策略的继承关系(在上的是超类，往下是子类)
- CHeapObj
-  + CollectorPolicy
-  +  + GenCollectorPolicy
-  +  +   + TwoGenerationCollectorPolicy
-  +  +   +   + ConcurrentMarkSweepPolicy
