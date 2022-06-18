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
+ 为什么要划分成年轻代和老年代？
+ 年轻代为什么被划分成eden、survivor区域？
+ 年轻代为什么采用的是复制算法？
+ 老年代为什么采用的是标记清除、标记整理算法
+ 什么情况下使用堆外内存？要注意些什么？
+ 堆外内存如何被回收？
+ jvm内存区域划分是怎样的？
### GC实战
#### CMS + ParNew
+ CMS GC回收分为哪几个阶段？分别做了什么事情？
+ CMS有哪些重要参数？
+ Concurrent Model Failure和ParNew promotion failed什么情况下会发生？
+ CMS的优缺点？
+ 有做过哪些GC调优？


## 并发
+ 了解synchronized(偏向锁、轻量级锁、重量级锁的概念以及升级机制)和ReentrantLock的区别
   - [011.JDK细节集锦/000.JDK之锁/001.synchronized/008.JDK之synchronized解析.md](../011.JDK细节集锦/000.JDK之锁/001.synchronized/008.JDK之synchronized解析.md)
+ 了解AtomicInteger实现原理、CAS适用场景、如何实现乐观锁
   - [011.JDK细节集锦/002.atomic/000.AtomicInteger解析.md](../011.JDK细节集锦/002.atomic/000.AtomicInteger解析.md)
## 集合
### HashMap
+ hashmap如何解决hash冲突，为什么hashmap中的链表需要转成红黑树？
+ hashmap什么时候会触发扩容？
+ jdk1.8之前并发操作hashmap时为什么会有死循环的问题？
+ hashmap扩容时每个entry需要再计算一次hash吗？
+ hashmap的数组长度为什么要保证是2的幂？
+ 如何用LinkedHashMap实现LRU？
+ 如何用TreeMap实现一致性hash？
### ConcurrentHashMap
+ ConcurrentHashMap：了解实现原理、扩容时做的优化、与HashTable对比。
+ BlockingQueue： 了解LinkedBlockingQueue、ArrayBlockingQueue、DelayQueue、SynchronousQueue
+ ConcurrentHashMap是如何在保证并发安全的同时提高性能？ ConcurrentHashMap是如何让多线程同时参与扩容？

### CopyOnWriteArrayList
+ CopyOnWriteArrayList： 了解写时复制机制、了解其适用场景、是如何保证线程安全的？思考为什么没有ConcurrentArrayList