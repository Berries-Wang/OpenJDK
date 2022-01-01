# OpenJDK(JVM)学习
&nbsp;&nbsp;对着源码 && 数据 && 官方文档 && 博客来学习总结的，特此记录。



## 附录
### 1. 如何查看Java Native代码
在Java中有的代码使用native标识，这种代码是用非Java语言编写，通常实现在本地的动态链接库中，无法直接查看源代码，比如以下代码
``` 
private native final Class<?> findLoadedClass0(String name); //  是 java.lang.ClassLoader 类中的 native方法
```
#### 01.下载源代码并进入指定目录
解压进入根目录,再次进入：jdk/src/share/native/ 目录中，根据类的全路径名去查找类的native实现
#### 02. 查找并阅读对应源代码
即java.lang.ClassLoader 的native实现在jdk/src/share/native/java/lang/ClassLoader.c中，native中对应方法的声明为 Java_java_lang_ClassLoader_defineClass0 即可查看native的具体实现