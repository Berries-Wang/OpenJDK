# MetaSpace-元空间
### 1. 为什么没有永久代(为什么使用元空间替代永久代)
> [JEP 122: Remove the Permanent Generation](./999.REFS/JEP%20122_%20Remove%20the%20Permanent%20Generation.pdf)
This is part of the JRockit and Hotspot convergence effort. JRockit customers do not need to configure the permanent generation (since JRockit does not have a permanent generation) and are accustomed to not configuring the permanent generation.（这是 JRockit 和 Hotspot 融合努力的一部分。JRockit 用户无需配置永久代（因为 JRockit 没有永久代），并且已经习惯了不配置永久代。）



## 参考资料
1. [https://wiki.openjdk.org/display/HotSpot/Metaspace](https://wiki.openjdk.org/display/HotSpot/Metaspace)
2. [JEP 122: Remove the Permanent Generation](https://openjdk.org/jeps/122)