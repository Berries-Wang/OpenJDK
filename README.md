# OpenJdk

## 介绍
&nbsp;&nbsp;分析JDK源代码，深入理解JVM(以书本为驱动，进行相应的源代码分析，深入理解),因代码中的中文注释，导致编译JDK时报错(error: unmappable character for encoding ascii),目前已初步解决，详见文档： 《OpenJdk编译问题以及解决方案集锦.md》.构建&调试见文件《005.OpenJDK/000.openJDK_8u40/OpenJdk代码调试解决方案.md》、《005.OpenJDK/000.openJDK_8u40/OpenJdk代码调试解决方案.md》

## JVM调优
```txt
   1. 调优，其实是一种权衡，当收益大于成本时，才有意义。
```

## OpenJDK 小知识
```txt
   1. <= OpenJDK8 , 内部使用UTF-16来存储字符串，即两个字节存储一个字符，>=OpenJDK9 使用 '002.JEP 254: Compact Strings' 修改了字符串存储方式 - 字节数组 + 编码标识。目的： 提升内存利用率
      > 具体请参考: 018.OpenJDK_FUTURE/002.JEP 254: Compact Strings/002.JEP 254: Compact Strings.md
```

#### 注意事项
&nbsp;&nbsp;直接看代码会导致你陷入细节和局部关注而看不到更高层次的设计，拼命盯着许多不同的类函数宏样式和注释时，很容易不知所措，试图推断出一些结构来帮助解释所有细节是如何联系起来的也有一定的难度。

## 仓库链接
1. [操作系统] 查阅 [UNIX-NOTE](https://github.com/Berries-Wang/UNIX-NOTE) 仓库

--- 
## 关于GC
> ps: 是否想了解 ”为什么单体吞吐量不会太高?“ 或其他迷惑的 ，那么请参考: [004.OpenJDK(JVM)学习/009.GC/README.md](./004.OpenJDK(JVM)学习/009.GC/README.md)

## 参考资料查询
1. Oracle:[https://docs.oracle.com/](https://docs.oracle.com/)
2. OpenJDK Wiki: [http://openjdk.java.net/](http://openjdk.java.net/)
