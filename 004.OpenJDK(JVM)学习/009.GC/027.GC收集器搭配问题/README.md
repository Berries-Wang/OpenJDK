# GC 收集器搭配问题
1. 为什么ParallelScavenge不能搭配CMS使用
   ```txt
      这些XXXGeneration都在HotSpot VM的“分代式GC框架( Generational GC framework )”内。本来HotSpot VM鼓励开发者尽量在这个框架内开发GC，但后来有个开发就是不愿意被这框架憋着，自己硬写了个没有使用已有框架的新并行GC，并拉拢性能测试团队用这个并行GC来跑分，成绩也还不错，于是这个GC就放进HotSpot VM里了。这就是我们现在看到的ParallelScavenge。
      > 
   ```
   + 参考文档:[http://blogs.oracle.com/jonthecollector/entry/our_collectors](http://blogs.oracle.com/jonthecollector/entry/our_collectors) 但是已经失效了 , 有机会找回来
      ```txt
       # 复制别人博客内容
       FAQ: 
        1) UseParNew and UseParallelGC both collect the young generation using multiple GC threads. Which is faster?(UseParNew 和 UseParallelGC 都使用多个 GC 线程收集新生代。哪个更快？)
           There's no one correct answer for this questions. Mostly they perform equally well, but I've seen one do better than the other in different situations. If you want to use GC ergonomics, it is only supported by UseParallelGC (and UseParallelOldGC) so that's what you'll have to use.(这个问题没有一个正确的答案。大多数情况下，它们的表现一样好，但我见过一种在不同情况下比另一种表现得更好。如果您想使用 GC 人体工程学，则只有 UseParallelGC（和 UseParallelOldGC）支持它，因此您必须使用此功能。)

        2) Why doesn't "ParNew" and "Parallel Old" work together?(为什么 “ParNew” 和 “Parallel Old” 不能一起工作？)
           "ParNew" is written in a style where each generation being collected offers certain interfaces for its collection. For example, "ParNew" (and "Serial") implements space_iterate() which will apply an operation to every object in the young generation. When collecting the tenured generation with either "CMS" or "Serial Old", the GC can use space_iterate() to do some work on the objects in the young generation. This makes the mix-and-match of collectors work but adds some burden to the maintenance of the collectors and to the addition of new collectors. And the burden seems to be quadratic in the number of collectors. Alternatively, "Parallel Scavenge" (at least with its initial implementation before "Parallel Old") always knew how the tenured generation was being collected and could call directly into the code in the "Serial Old" collector. "Parallel Old" is not written in the "ParNew" style so matching it with "ParNew" doesn't just happen without significant work. By the way, we would like to match "Parallel Scavenge" only with "Parallel Old" eventually and clean up any of the ad hoc code needed for "Parallel Scavenge" to work with both.
           “ParNew” 的编写风格是，被收集的每一代都为其集合提供特定的接口。例如，“ParNew” （和 “Serial”） 实现 space_iterate（） ，它将对年轻一代中的每个对象应用作。当使用 “CMS” 或 “Serial Old” 收集永久代时，GC 可以使用 space_iterate() 对年轻代中的对象进行一些工作。这使得收集器的混合搭配工作，但增加了收集器维护和添加新收集器的负担。而且负担似乎是收集者数量的二次方。或者，“Parallel Scavenge”（至少在 “Parallel Old” 之前的初始实现）始终知道永久代是如何被收集的，并且可以直接调用 “Serial Old” 收集器中的代码。“Parallel Old” 不是以 “ParNew” 样式编写的，因此将其与 “ParNew” 匹配不会在没有大量工作的情况下发生。顺便说一句，我们希望最终只将 “Parallel Scavenge” 与 “Parallel Old” 匹配，并清理 “Parallel Scavenge” 与两者一起使用所需的任何临时代码。

           Please don't think too much about the examples I used above. They are admittedly contrived and not worth your time.
           请不要过多考虑我上面使用的示例。诚然，它们是做作的，不值得你花时间。
      ```