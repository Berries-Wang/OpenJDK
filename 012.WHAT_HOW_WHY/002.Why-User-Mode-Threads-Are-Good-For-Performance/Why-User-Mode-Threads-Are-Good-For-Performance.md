# 002.Why User-Mode Threads Are Good For Performance
+ <img src="./PPTS/why-usermode-threads-are-good-for-performance-1-2048.webp">
+ <img src="./PPTS/why-usermode-threads-are-good-for-performance-2-2048.webp">
+ <img src="./PPTS/why-usermode-threads-are-good-for-performance-3-2048.webp">
+ <img src="./PPTS/why-usermode-threads-are-good-for-performance-4-2048.webp">
+ <img src="./PPTS/why-usermode-threads-are-good-for-performance-5-2048.webp">
+ <img src="./PPTS/why-usermode-threads-are-good-for-performance-6-2048.webp">
+ <img src="./PPTS/why-usermode-threads-are-good-for-performance-7-2048.webp">
+ <img src="./PPTS/why-usermode-threads-are-good-for-performance-8-2048.webp">
  
  ```txt
      Little's Law （利特尔法则）
      In any stable system with long term averages（在任何具有长期平均值的稳定系统中）:
          λ —— arrival rate = exit rate = throughput
          W —— duration inside 
          L —— no. items inside
  ```

+ <img src="./PPTS/why-usermode-threads-are-good-for-performance-9-2048.webp">
+ <img src="./PPTS/why-usermode-threads-are-good-for-performance-10-2048.webp">
+ <img src="./PPTS/why-usermode-threads-are-good-for-performance-11-2048.webp">
+ <img src="./PPTS/why-usermode-threads-are-good-for-performance-12-2048.webp">
+ <img src="./PPTS/why-usermode-threads-are-good-for-performance-13-2048.webp">
+ <img src="./PPTS/why-usermode-threads-are-good-for-performance-14-2048.webp">
+ <img src="./PPTS/why-usermode-threads-are-good-for-performance-15-2048.webp">
+ <img src="./PPTS/why-usermode-threads-are-good-for-performance-16-2048.webp">
+ <img src="./PPTS/why-usermode-threads-are-good-for-performance-17-2048.webp">
+ <img src="./PPTS/why-usermode-threads-are-good-for-performance-18-2048.webp">
+ <img src="./PPTS/why-usermode-threads-are-good-for-performance-19-2048.webp">
+ <img src="./PPTS/why-usermode-threads-are-good-for-performance-20-2048.webp">
+ <img src="./PPTS/why-usermode-threads-are-good-for-performance-21-2048.webp">
+ <img src="./PPTS/why-usermode-threads-are-good-for-performance-22-2048.webp">
+ <img src="./PPTS/why-usermode-threads-are-good-for-performance-23-2048.webp">
+ <img src="./PPTS/why-usermode-threads-are-good-for-performance-24-2048.webp">
+ <img src="./PPTS/why-usermode-threads-are-good-for-performance-25-2048.webp">
+ <img src="./PPTS/why-usermode-threads-are-good-for-performance-26-2048.webp">
+ <img src="./PPTS/why-usermode-threads-are-good-for-performance-27-2048.webp">
+ <img src="./PPTS/why-usermode-threads-are-good-for-performance-28-2048.webp">
+ <img src="./PPTS/why-usermode-threads-are-good-for-performance-29-2048.webp">

   ```txt
       Not cooperative , but no time-sharing yet 
       - Non-cooperative scheduling is more composable.(非合作调度更具可组合性。)
       - ...but people overestimate the importance of time-sharing in servers.(但是人们高估了服务器中分时的重要性。)
       - structured concurrency allows waiting for a set of operations with on context-switch.(结构化并发允许在上下文切换时等待一组操作)
   ``` 

+ <img src="./PPTS/why-usermode-threads-are-good-for-performance-30-2048.webp">
  
   ```txt
       Summary（总结）:
       - Virtual threads allow higher throughput for the thread-per-request style —— the style harmonious with the platform —— by drastically increasing the request capacity of the server.(虚拟线程通过大幅增加服务器的请求容量，为thread-per-request风格（与平台和谐的风格）提供更高的吞吐量。)
       - we can juggle more balls not by adding hands but by enlarging the arch.(我们可以玩更多的球，不是通过增加手，而是通过扩大拱门。)
       - Context-switching cost could be important , but are not the main reason for the throughput increase.（上下文切换的开销可能很重要，但不是吞吐量增加的主要原因。）

       therad-per-request: 如上: A request consumes a thread(either new or borrowed from a pool) for it's duration.(请求在其持续时间内消耗一个线程（无论是新的还是从池中借用的）)
   ```

+ <img src="./PPTS/why-usermode-threads-are-good-for-performance-31-2048.webp">


## 参考资料
1. [why-user-mode-threads-are-good-for-performance](https://www.p99conf.io/session/why-user-mode-threads-are-good-for-performance)