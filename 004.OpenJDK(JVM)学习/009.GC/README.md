# JVM GC(Garbage Collection)
## 分代收集理论
&nbsp;&nbsp;分代收集理论实质上是一套符合大多数程序运行实际情况的经验法则，他建立在两个分代假说上：
1. 弱分代假说
   > 绝大多数对象都是朝生夕灭的
2. 强分代假说
   > 熬过越多次垃圾收集过程的对象就越难以消亡。

&nbsp;&nbsp;第1、2两个分代假说共同奠定了多款常用的垃圾收集器一致的设计原则： <font color="red">**收集器应该将Java堆划分出不同的区域，然后将回收对象依据其年龄分配到不同的区域之中存储**</font>.

&nbsp;&nbsp;将对象根据年龄来划分存储，通过针对性的回收方式，对内存进行高效率低代价地回收。
  - 如果一个区域大多数是朝生夕灭的，那么把他们集中在一起，每次回收时只需要关注如何保留少部分存活的对象而不是去标记那些将要回收的对象，就能以较低代价会收到大量的空间。
  - 如果剩下的都是难以消亡的对象，那把他们集中在一起，虚拟机便能使用较低的频率来回收这个区域，这就同时兼顾了垃圾收集的时间开销和内存空间的有效使用)
  - 基于上述两点，因此才有了Minor GC 、 Major GC 、 Full GC.(划分为不同的区域后，垃圾收集器可以每次回收其中某一个或某一些区域)，也产生了针对性的垃圾收集算法(标记-清理，标记-整理，标记-复制)

3. 跨代引用假说
   > 跨代引用相对于同代引用来说仅占极少数。
      - 根据这条假说，在回收时就不应该为了少量的跨代引用去扫描整个老年代。目前的JVM使用了一个全局的数据结构：**记忆集**(Remembered Set),这个结构将老年代划分为若干小块，标识出老年代的哪一块内存会存在跨代引用。此后当发生Minor GC时，只有包含了跨代引用的小块内存里的若干对象才会被加入到GC Roots中进行扫描。

   > 基于1、2分代假说，JVM将堆进行了划分，因此才有了跨代引用问题

---
### GC 分类
1. 部分收集(Partial GC): 指目标不是完整收集整个Java堆的垃圾收集，其中又分为:
   - 新生代收集(Minor GC / Young GC):指目标仅完成新生代的垃圾收集
   - 老年代收集(Major GC/Old GC):指目标仅完成老年代的垃圾收集，注意:
      + 目前仅有CMS垃圾收集器会有单独收集老年代的行为
   - 混合收集(Mixed GC):指目标是收集整个新生代以及部分老年代的垃圾收集，目前仅有G1收集器会有这种收集行为
2. 整堆收集(Full GC):收集整个Java堆和方法区的垃圾收集
   
---
## JVM GC
1. 哪些内存需要回收
2. 什么时候回收
   1. 当对象内存分配失败了，就会触发GC。以此作为分析的切入点
3. 如何回收
### 评判GC的两个核心标准
&nbsp;&nbsp;以下两个评判GC的核心标准，也可作为GC调优的方向，也是学习JVM GC的主要目标。
1. <font color="red" >**延迟**</font>
> 也可以理解为最大停顿时间，即垃圾收集过程中一次 STW 的最长时间，越短越好，一定程度上可以接受频次的增大，GC 技术的主要发展方向。
2. <font color="red" >**吞吐率**</font>
> 应用系统的生命周期内，由于 GC 线程会占用 Mutator 当前可用的 CPU 时钟周期，吞吐量即为 Mutator 有效花费的时间占系统总运行时间的百分比，例如系统运行了 100 min，GC 耗时 1 min，则系统吞吐量为 99%，吞吐量优先的收集器可以接受较长的停顿。
>> 吞吐量大不代表响应能力高，吞吐量一般这么描述：在一个时间段内完成了多少个事务操作；在一个小时之内完成了多少批量操作
3. 备注
> 除了这两个指标之外还有 Footprint（资源量大小测量）、反应速度等指标，互联网这种实时系统追求低延迟，而很多嵌入式系统则追求 Footprint。

## JVM内存空间初始化
> 004.OpenJDK(JVM)学习/003.JVM启动/001.Universe初始化/README.md

## JVM堆的划分(仅Heap)
&nbsp;&nbsp; 不同的收集器有着自己不同的实现，具体还得根据收集器来分析判断

&nbsp;&nbsp; 从 005.OpenJDK/001.openJdk8-b120/jdk-jdk8-b120/hotspot/src/share/vm/memory/universe.cpp#universe_init开始
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


## 函数分析
### jdk-jdk8-b120/hotspot/src/share/vm/memory/universe.cpp#universe_init
  - 先为JVM堆申请内存，再为元空间申请内存,方式都是“预留空间”，即申请连续的虚拟地址空间，而非时间的物理内存
  ```c++
     jint universe_init() {
            assert(!Universe::_fully_initialized, "called after initialize_vtables");
            guarantee(1 << LogHeapWordSize == sizeof(HeapWord),
                        "LogHeapWordSize is incorrect.");
            guarantee(sizeof(oop) >= sizeof(HeapWord), "HeapWord larger than oop?");
            guarantee(sizeof(oop) % sizeof(HeapWord) == 0,
                        "oop size is not not a multiple of HeapWord size");
            TraceTime timer("Genesis", TraceStartupTime);
            // 禁止在启动的时候GC
            GC_locker::lock(); // do not allow gc during bootstrapping
            JavaClasses::compute_hard_coded_offsets();

            // 初始化JVM堆
            jint status = Universe::initialize_heap();
            if (status != JNI_OK) {
                return status;
            }
            
            /**
             * 元空间初始化
             * 内存是怎么申请的，内存多大
             */ 
            Metaspace::global_initialize();

            // Create memory for metadata.  Must be after initializing heap for
            // DumpSharedSpaces.
            ClassLoaderData::init_null_class_loader_data();

            // We have a heap so create the Method* caches before
            // Metaspace::initialize_shared_spaces() tries to populate them.
            Universe::_finalizer_register_cache = new LatestMethodCache();
            Universe::_loader_addClass_cache = new LatestMethodCache();
            Universe::_pd_implies_cache = new LatestMethodCache();

            /**
             * 当JVM启动时若配置-XX:+UseSharedSpaces,则它会通过内存映射文件的方式把classes.jsa文件的内存加载到自己的JVM进程空间中.
             *  classes.jsa对应的这一部分内存空间地址一般在永久代(现在是元空间了)内存地址空间的后面.
             * JVM这么做的目的就是让这个JVM的所有实例共享classlist中所有类的类型描述信息以达到节约物理内存的目标
             * 
             */ 
            if (UseSharedSpaces) {
                // Read the data structures supporting the shared spaces (shared
                // system dictionary, symbol table, etc.).  After that, access to
                // the file (other than the mapped regions) is no longer needed, and
                // the file is closed. Closing the file does not affect the
                // currently mapped regions.
                MetaspaceShared::initialize_shared_spaces();
                StringTable::create_table();
            } else {
                SymbolTable::create_table();
                StringTable::create_table();
                ClassLoader::create_package_info_table();
            }

            return JNI_OK;
            }
  ```



---
## JVM参数
### 1. CompressedClassSpaceSize （与UseCompressedOops区分）
> 这个参数主要是设置Klass Metaspace的大小，不过这个参数设置了也不一定起作用，前提是能开启压缩指针，假如-Xmx超过了32G，压缩指针是开启不来的。如果有Klass Metaspace，那这块内存是和Heap连着的。
- <img src="./pics/20160602101028338.png"/>
- <img src="./pics/compressed_class_space-001.png"/>
+ >> Klass Metaspace 的空间是包含在Metaspace里面的,
### 2. MetaspaceSize
+ 定义于文件: 005.OpenJDK/001.openJdk8-b120/jdk-jdk8-b120/hotspot/src/share/vm/runtime/globals.hpp
+ 这个JVM参数是指Metaspace扩容时触发FullGC的初始化阈值，也是最小的阈值。这里有几个要点需要明确：
  - 如果没有配置-XX:MetaspaceSize，那么触发FGC的阈值是21807104（约20.8m），可以通过jinfo -flag MetaspaceSize pid得到这个值；jps -v也可以查看jvm的参数设置情况。
  - 如果配置了-XX:MetaspaceSize，那么触发FGC的阈值就是配置的值；
  - Metaspace由于使用不断扩容到-XX:MetaspaceSize参数指定的量，就会发生FGC；且之后每次Metaspace扩容都可能会发生FGC（至于什么时候会，比较复杂，跟几个参数有关）；
  - 如果Old区配置CMS垃圾回收，那么扩容引起的FGC也会使用CMS算法进行回收；
  - 如果MaxMetaspaceSize设置太小，可能会导致频繁FullGC，甚至OOM；
+ 赋值
```txt
libjvm.so!Metaspace::ergo_initialize() (hotspot/src/share/vm/memory/metaspace.cpp:3011)
libjvm.so!Arguments::apply_ergo() (hotspot/src/share/vm/runtime/arguments.cpp:3670)
libjvm.so!Threads::create_vm(JavaVMInitArgs * args, bool * canTryAgain) (hotspot/src/share/vm/runtime/thread.cpp:3339)
libjvm.so!JNI_CreateJavaVM(JavaVM ** vm, void ** penv, void * args) (hotspot/src/share/vm/prims/jni.cpp:5166)
libjli.so!InitializeJVM(JavaVM ** pvm, JNIEnv ** penv, InvocationFunctions * ifn) (jdk/src/share/bin/java.c:1146)
libjli.so!JavaMain(void * _args) (jdk/src/share/bin/java.c:373)
libpthread.so.0!start_thread(void * arg) (/build/glibc-eX1tMB/glibc-2.31/nptl/pthread_create.c:477)
libc.so.6!clone() (/build/glibc-eX1tMB/glibc-2.31/sysdeps/unix/sysv/linux/x86_64/clone.S:95)
```

---
## 参考资料
- [深入理解堆外内存 Metaspace](https://javadoop.com/post/metaspace)
- https://tech.meituan.com/2017/12/29/jvm-optimize.html
- https://tech.meituan.com/2020/11/12/java-9-cms-gc.html