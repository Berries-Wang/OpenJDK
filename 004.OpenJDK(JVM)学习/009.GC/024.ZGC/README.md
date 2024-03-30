# ZGC
The Z Garbage Collector (ZGC) is a scalable low latency garbage collector. ZGC performs all expensive work concurrently, without stopping the execution of application threads for more than a millisecond. It is suitable for applications which require low latency. Pause times are independent of the heap size that is being used. ZGC works well with heap sizes from a few hundred megabytes to 16TB. 

ZGC was initially introduced as an experimental feature in JDK 11, and was declared Production Ready in JDK 15. In JDK 21 was reimplemented to support generations.
> 15及之后版本可用于生产环境；JDK21 ZGC才实现了分代收集

At a glance, ZGC is:
- Concurrent
- Region-based
- Compacting
- NUMA-aware
- Using colored pointers
- Using load barriers
- Using store barriers (in the generational mode)

At its core, ZGC is a concurrent garbage collector, meaning all heavy lifting work is done while Java threads continue to execute. This greatly limits the impact garbage collection will have on your application's response time.

## 使用
```txt
   If you're trying out ZGC for the first time, start by using the following GC options:
      -XX:+UseZGC -XX:+ZGenerational -Xmx<size> -Xlog:gc
    
    For more detailed logging, use the following options:
      -XX:+UseZGC -XX:+ZGenerational -Xmx<size> -Xlog:gc*
    
    若不使用分代ZGC，使用单代，则不需要加  -XX:+ZGenerational
    
    单代&分代ZGC性能差异： 004.OpenJDK(JVM)学习/009.GC/024.ZGC/000.ZGC-IS-Comming.md

```
- 通过 [04.OpenJDK(JVM)学习/009.GC/024.ZGC/000.ZGC-IS-Comming.md](./000.ZGC-IS-Comming.md) 知道，ZGC是一个自适应的GC，在运行Java程序期间，会自动调整分代大小。
- 分代 & 单代 ZGC 性能差异，参考: [04.OpenJDK(JVM)学习/009.GC/024.ZGC/000.ZGC-IS-Comming.md](./000.ZGC-IS-Comming.md)

### ZGC 参数
请参考:[Open ZGC Wiki 20240330](./Docs/Main%20-%20Main%20-%20OpenJDK%20Wiki-20240330.pdf)


## 参考资料
1. [https://wiki.openjdk.org/display/zgc/Main#Main-Overview](https://wiki.openjdk.org/display/zgc/Main#Main-Overview)