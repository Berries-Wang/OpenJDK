# JVM 自动内存管理
&nbsp;&nbsp;重新学习一下自动内存管理，而不是之前的"浏览式"学习，要更深刻，更深入的去理解,能够在实际场景中应用。

## JVM(JDK1.8)内存划分
### 1. 程序计数器
- 参考[001.程序计数器.md](./001.程序计数器.md)
  
### 2. 虚拟机栈
- 参考: [002.JavaControlStack-虚拟机栈.md](./002.JavaControlStack-虚拟机栈.md)

### 3. 堆

### 4. 元空间
#### 运行时常量池(Runtime Constant Pool)
  1. Class文件中除了有类的版本、字段、方法、接口等描述信息之外，还有一项是常量池表(Constant Pool Table),用来存放编译期间生成的各种字面量和符号引用，这部分信息将在类加载后存放到方法区的运行时常量池中。
  2. 除了保存Class文件中描述的符号引用，还会将由符号引用翻译出来的直接引用也存储在运行时常量池中。

### 5. 本地方法栈

### 6. 直接内存
+ JDK1.4中新加入了NIO，引入了一种基于通道与缓冲区的I/O方式，使用Native函数库直接分配堆外内存，然后通过一个存储在Java堆里面的DirectByteBuff对象作为这块内存的引用进行操作。

### 7.CodeCache
+ Codecache最大的用户: JIT,JIT将编译后的方法存放到codecache中，codecache空间不足会导致JIT停止编译方法


---
## 学习目标
1. 深入了解JVM内存的各个组成部分
2. 每个部分的功能，大小计算规则，JVM参数

---
## 参考资料
1. 《深入理解Java虚拟机:JVM高级特性与实践 第3版 周志明》
2. [Oracle](https://docs.oracle.com/javase/specs/jvms/se7/html/jvms-2.html#jvms-2.5.5)