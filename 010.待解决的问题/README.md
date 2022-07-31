# 总结
## 锁
+ 乐观锁和悲观锁的区别？
  - 见[011.JDK细节集锦/000.JDK之锁/003.乐观锁和悲观锁.md](../011.JDK细节集锦/000.JDK之锁/003.乐观锁和悲观锁.md)
+ 如何实现一个乐观锁？
+ AQS是如何唤醒下一个线程的？
    - 见:[011.JDK细节集锦/000.JDK之锁/000.AQS是如何唤醒下一个线程.md](../011.JDK细节集锦/000.JDK之锁/000.AQS是如何唤醒下一个线程.md)
+ ReentrantLock如何实现公平和非公平锁是如何实现？
    - 见:[011.JDK细节集锦/000.JDK之锁/004.ReentrantLock公平和非公平锁实现.md](../011.JDK细节集锦/000.JDK之锁/004.ReentrantLock公平和非公平锁实现.md)
+ [还需补充]CountDownLatch和CyclicBarrier的区别？各自适用于什么场景？了解过ReentrantLock、Semaphore吗，介绍一下?
    - 见:[011.JDK细节集锦/000.JDK之锁/005.CountDownLatch与CyclicBarrier.md](../011.JDK细节集锦/000.JDK之锁/005.CountDownLatch与CyclicBarrier.md)
+ 使用ThreadLocal时要注意什么？比如说内存泄漏?
    - 见[011.JDK细节集锦/001.Thread解密/000.ThreadLocal源码剖析.md](../011.JDK细节集锦/001.Thread解密/000.ThreadLocal源码剖析.md)
+ 说一说往线程池里提交一个任务会发生什么？线程池的几个参数如何设置？ 线程池的非核心线程什么时候会被释放？
+ 如何排查死锁？
   - 见[003.内功心法/006.Java死锁检测.md](../003.内功心法/006.Java死锁检测.md)

## 引用
+ 了解Java中的软引用、弱引用、虚引用的适用场景以及释放机制。 软引用什么时候会被释放？弱引用什么时候会被释放？
   - 答案请参考: [004.OpenJDK(JVM)学习/009.GC/007.JVM-Reference.md](../004.OpenJDK(JVM)学习/009.GC/007.JVM-Reference.md)

## 类加载
+ 双亲委派机制的作用？
+ Tomcat的classloader结构
+ 如何自己实现一个classloader打破双亲委派
+ 答案请参考: [004.OpenJDK(JVM)学习/010.类加载](../004.OpenJDK(JVM)学习/010.类加载)

## IO
+ 同步阻塞、非阻塞、异步的区别？
+ select、poll、eopll的区别？
+ java NIO与BIO的区别？
+ reactor线程模型是什么?
+ 参考:[014.Unix网络](../014.Unix网络)

## GC
### GC理论
+ 为什么要划分成年轻代和老年代？:[分代收集理论](../004.OpenJDK(JVM)学习/009.GC/README.md)
  - 分代收集理论: 
     1. 弱分代假说
     2. 强分代假说
     3. 跨代引用假说
+ 年轻代为什么被划分成eden、survivor区域？
+ 年轻代为什么采用的是复制算法？
+ 老年代为什么采用的是标记清除、标记整理算法
+ 什么情况下使用堆外内存？要注意些什么？ 
+ 堆外内存如何被回收？
   - [JVM堆外内存](../004.OpenJDK(JVM)学习/009.GC/017.JVM堆外内存.md)
+ jvm内存区域划分是怎样的？:[JVM内存区域划分](../004.OpenJDK(JVM)学习/009.GC/016.JVM内存区域划分.md)
+ 请参考: [004.OpenJDK(JVM)学习/009.GC](../004.OpenJDK(JVM)学习/009.GC)
### GC实战
#### CMS + ParNew
+ CMS GC回收分为哪几个阶段？分别做了什么事情？
+ CMS有哪些重要参数？
+ Concurrent Model Failure和ParNew promotion failed什么情况下会发生？
  - ParNew promotion failed： 空间分配担保 
  - Concurrent Model Failure：并发失败，用户线程和GC线程并发执行，用户线程无法申请到足够的内存而导致并发失败
+ CMS的优缺点？
+ 有做过哪些GC调优？
  - [004.OpenJDK(JVM)学习/009.GC/004.ParNew+CMS/001.CMS介绍.md](../004.OpenJDK(JVM)学习/009.GC/004.ParNew+CMS/001.CMS介绍.md)


## 并发
+ 了解synchronized(偏向锁、轻量级锁、重量级锁的概念以及升级机制)和ReentrantLock的区别
   - [011.JDK细节集锦/000.JDK之锁/001.synchronized/008.JDK之synchronized解析.md](../011.JDK细节集锦/000.JDK之锁/001.synchronized/008.JDK之synchronized解析.md)
+ 了解AtomicInteger实现原理、CAS适用场景、如何实现乐观锁
   - [011.JDK细节集锦/002.atomic/000.AtomicInteger解析.md](../011.JDK细节集锦/002.atomic/000.AtomicInteger解析.md)
## 集合
### HashMap
+ hashmap如何解决hash冲突，为什么hashmap中的链表需要转成红黑树？
  > 红黑树相比于AVL树，插入性能较好(AVL树每次插入、删除都要做调整，比较复杂、耗时)，但是查找性能不如AVL树，因为红黑树不是一个绝对平衡的二叉树(黑平衡)。
+ hashmap什么时候会触发扩容？
  ```txt
    -> java.util.HashMap#put,通过研究代码发现，在以下情况下会触发扩容:
       1. 添加元素且内部java.util.HashMap#table数组没有初始化或数组长度为0时
       2. 桶的数量未达到64 且 对应桶中的元素数量达到了8
  ```
+ hashmap扩容时每个entry需要再计算一次hash吗？
  ```java
     // 不会，只会计算一次，将hash值存放在java.util.HashMap.Node#hash(final修饰)中，后续使用计算好的hash值
  ```
+ hashmap的数组长度为什么要保证是2的幂？
  - 见:[001.Java源码分析/004.HashMap/000.高效程序的奥秘-取余.md](../001.Java源码分析/004.HashMap/000.高效程序的奥秘-取余.md)
+ 如何用LinkedHashMap实现LRU
  ```java
     // LinkedHashMap 是在HashMap的基础上添加了一个双向链表
     //通过钩子函数: java.util.LinkedHashMap#afterNodeAccess (根据配置，将最近访问的元素放到队列尾部),因此可以来实现LRU,测试代码如下:
        // true，表示遍历的顺序: true: 按访问顺序;false:按照插入顺序
        LinkedHashMap<Integer, Integer> map = new LinkedHashMap<>(16, 0.75f, true);
        map.put(1, 1);
        map.put(2, 2);
        map.put(3, 3);
        map.put(4, 4);

        map.get(1);

        map.forEach((key, value) -> System.out.println(key));
        //  输出: 2,3,4,1

  ```
+ 如何用TreeMap实现一致性hash？
  ```java
     // TreeMap 是一个有序的Map,想象成一个环
     package link.bosswang.wei;

    import java.util.SortedMap;
    import java.util.TreeMap;
    
    public class Consistent_Hash {

    private static TreeMap<Long, String> Consistent_Hash = new TreeMap<>();

    private static long hash(Object key) {
        int h;
        return (key == null) ? 0 : (h = key.hashCode()) ^ (h >>> 16);
    }

    private static String getServerName(Long hash) {
        if (Consistent_Hash.isEmpty()) {
            return null;
        }

        // 返回此地图部分的视图，其键大于等于 fromKey
        SortedMap<Long, String> tailMap = Consistent_Hash.tailMap(hash);

        // 如果为空，则表示已经到了Hash环的末尾，那么需要使用第一个Key
        if (!tailMap.isEmpty()) {
            return tailMap.get(tailMap.firstKey());
        }

        return Consistent_Hash.firstEntry().getValue();
    }

    public static void main(String[] args) {
        // 初始化服务器 2^n
        for (long i = 0; i < 8; i++) {
            Consistent_Hash.put(i, "Server-" + i);
        }

        System.out.println(getServerName((hash(4) & (8 - 1))));
        System.out.println(getServerName(hash(5) & (8 - 1)));
        System.out.println(getServerName(hash(6) & (8 - 1)));

    }
   }
  ```


### ConcurrentHashMap
+ ConcurrentHashMap：了解实现原理、扩容时做的优化、与HashTable对比。ConcurrentHashMap是如何在保证并发安全的同时提高性能？ ConcurrentHashMap是如何让多线程同时参与扩容？
    - HashTable 是在方法上添加了synchronized关键字,锁的粒度较大，同一时间只能有一个线程操作
    - ConcurrentHashMap 内部使用的数据结构和HashMap一致,ConcurrentHashMap使用了CAS操作和synchronized关键字，且synchronized锁住的是单个桶，这样就使得其他桶是可以操作的，且采用了协助扩容的方案，提升了扩容的效率.
    - ConcurrentHashMap是如何让多线程同时参与扩容？
        ```txt
            通过方法: ConcurrentHashMap#put ,ConcurrentHashMap#helpTransfer,ConcurrentHashMap#transfer 方法可知。
               关键： ConcurrentHashMap#MOVED 
               添加元素时(ConcurrentHashMap#put )如果table[i].hash == ConcurrentHashMap#MOVED , 说明Map正在扩容，且当前桶已经扩容完成了，如果需要往这个桶中添加元素，得先协助扩容。
               扩容(ConcurrentHashMap#transfer )时，桶元素迁移完成，会将table(新)[i]置为ConcurrentHashMap.ForwardingNode(.hash 为MOVED),然后再迁移其他桶
               
               且

               在添加元素 或 迁移桶数据的时候，都会使用synchronized将桶锁住,避免发生线程不安全事故
        ```

+ BlockingQueue： 了解LinkedBlockingQueue、ArrayBlockingQueue、DelayQueue、SynchronousQueue 
  ```txt
     均实现了接口java.util.concurrent.BlockingQueue , 定义了四种模式的方法
     具体差异各实现代码的注释.
     
  ```

### CopyOnWriteArrayList
+ CopyOnWriteArrayList： 了解写时复制机制、了解其适用场景、是如何保证线程安全的？思考为什么没有ConcurrentArrayList
  ```txt
   * // java.util.concurrent.CopyOnWriteArrayList
   * <pre>
   *     重点!!!
   *  1. 底层使用数组实现 
   *  2. copy on write  ， 即创建迭代器时会生成底层数组的快照(因为每添加一个元素，会新生成一个数组并替换底层数组，所以，之前的数组就是一个快照了;),后续迭代是迭代该快照.所以是线程安全的 
   *  3. 每添加一个元素时，会创建一个新数组，并替换原来的数据。过程中使用锁控制并发 
   * </pre>
   * <pre>
   *   
   *  * 适用场景:
   *  * 1. 读多写少，不适用实时读的场景;例如: 黑名单场景(其他list存在并发问题.)
   *  * 2. 数据量不大,因为每次修改都要生成新的底层数组，耗费内存.
   * </pre>
  
   为什么没有ConcurrentArrayList?
    > 没有ConcurrentArrayList的主要原因：很难开发一个通用并且没有并发瓶颈的线程安全的List。
    >> !!! 1. ConcurrentHashMap的真正价值并不是他们保障了线程安全，而是在保障了线程安全的同时不存在并发瓶颈。如ConcurrentHashMap使用了分段锁和弱一致性的Map迭代器去规避并发瓶颈.
    >>> 所以问题在于，像“Array List”这样的数据结构，你不知道如何去规避并发的瓶颈。拿contains() 这样一个操作来说，当你进行搜索的时候如何避免锁住整个list？
    >> !!! 2. 另一方面，Queue 和Deque (基于Linked List)有并发的实现是因为他们的接口相比List的接口有更多的限制，这些限制使得实现并发成为可能。
  ```