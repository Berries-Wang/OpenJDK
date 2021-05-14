# AbstractQueuedSynchronizer（以下均称为AQS）
## 功能
+ AQS是一个用于构建锁、同步器、协作工具类的工具类。
+ AQS是一种提供了原子式管理同步状态、阻塞和唤醒功能以及队列模型的简单框架。

## AQS内部原理解析
### 核心一： state(The synchronization state.)
#### 1. 在不同的协作工具类的实现中，代表的含义不一样
####　注意：在所有的线程协作类之中，state都代表这临界资源？
+ PS: 这样的理解还有待验证
### 核心二：控制线程抢锁和配合的FIFO队列
#### 排队管理员的作用
+ 该队列会被用来存放 “等待的线程”，AQS就是排队管理器，当多个线程争用同一把锁的时候，必须有排队机制将那些没能拿到锁的线程串在一起。当锁释放的时候，锁管理器就会挑选一个合适的线程来占有这个刚刚释放的锁。
#### FIFO队列
+ AQS会维护一个等待的线程队列，把线程都放在这个队列里面。该队列所使用的数据结构为**双向链表**
### 核心三：期望协作工具类去实现的获取/释放等重要方法
+ 这里的获取和释放方法，是利用AQS的协作工具类里最重要的方法，是由协作类自己去实现的，并且含义各不相同。
    - 获取方法：
       + 获取操作会依赖state变量，经常会阻塞（比如获取不到锁的时候）
          - 在Semaphore中，获取就是acquire方法，作用是获取一个许可证
          - 在CountDownLatch中，获取就是await方法，作用是 “等待，直到倒数结束”
    - 释放方法
       + 释放操作不会阻塞
          - 在Semaphore中，释放就是release方法，所用就是释放一个许可证
          - 在CountDownLatch中，获取就是countDown方法，作用就是“倒数一个数”
+ 需要线程协作工具类自己去实现tryRelease和tryAcquire方法 

## AQS用法
### 1. 写一个类，想好协作的逻辑，实现获取/释放方法
### 2. 内部写一个Sync类继承自AQS
### 3. 根据是否独占来重写tryAcquire/tryRelease 或 tryAcquireShared/tryReleaseShared等方法，在之前写的获取/释放方法中调用AQS的acquire/release方法或者shared方法

### 分析
#### 从ReentrantLock来分析AQS

## 参考资料
1. [美团技术团队《从ReentrantLock的实现看AQS的原理及应用》](https://mp.weixin.qq.com/s/sA01gxC4EbgypCsQt5pVog)
2. [打通 Java 任督二脉 —— 并发数据结构的基石](https://juejin.im/post/5c11d6376fb9a049e82b6253)
3. [一行一行源码分析清楚AbstractQueuedSynchronizer](https://www.javadoop.com/post/AbstractQueuedSynchronizer#toc_0)
4. [AbstractQueuedSynchronizer 源码分析 (基于Java 8)](https://www.jianshu.com/p/e7659436538b)
5. [Java并发之AQS详解](https://www.cnblogs.com/waterystone/p/4920797.html)
6. [《The java.util.concurrent Synchronizer Framework》 JUC同步器框架（AQS框架）原文翻译](https://www.cnblogs.com/dennyzhangdd/p/7218510.html)