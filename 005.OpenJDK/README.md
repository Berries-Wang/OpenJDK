# OpenJDK
&nbsp;&nbsp;目前仅有OpenJDK8，可编译可运行。在对应的目录下均有对应的编译时出现的问题以及解决方案 以及 调试方式，可供参考。

## 探索新世界
&nbsp;&nbsp;新世界的大门: 005.OpenJDK/002.OpenJDK8u312-GA/OpenJDK8U312-GA/jdk/src/share/bin/main.c

## 开启Debug模式
```log
   # 1. 是否打印debug日志
   > 005.OpenJDK/002.OpenJDK8u312-GA/OpenJDK8U312-GA/jdk/src/share/bin/jli_util.c
    _launcher_debug
   通过void JLI_SetTraceLauncher();函数可知，通过设置环境变量即可开启debug模式:
   launch.json
   // 设置环境变量
   "environment": [{"name": "_JAVA_LAUNCHER_DEBUG","value": "1"}],
   
```

## 版本说明
1. GA版本: General Availability，正式发布的版本，官方开始推荐广泛使用，国外有的用GA来表示release版本。
## OpenJDK编译平台支持
1. [https://wiki.openjdk.java.net/display/Build/Supported+Build+Platforms](https://wiki.openjdk.java.net/display/Build/Supported+Build+Platforms)
2. [http://openjdk.java.net/groups/build/doc/building.html](http://openjdk.java.net/groups/build/doc/building.html)