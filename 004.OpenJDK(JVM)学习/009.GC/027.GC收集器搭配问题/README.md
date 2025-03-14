# GC 收集器搭配问题
1. 为什么ParallelScavenge不能搭配CMS使用
   ```txt
      这些XXXGeneration都在HotSpot VM的“分代式GC框架( Generational GC framework )”内。本来HotSpot VM鼓励开发者尽量在这个框架内开发GC，但后来有个开发就是不愿意被这框架憋着，自己硬写了个没有使用已有框架的新并行GC，并拉拢性能测试团队用这个并行GC来跑分，成绩也还不错，于是这个GC就放进HotSpot VM里了。这就是我们现在看到的ParallelScavenge。
      > 
   ```
   + 参考文档:[]()