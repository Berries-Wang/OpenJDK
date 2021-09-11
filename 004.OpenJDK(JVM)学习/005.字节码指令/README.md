# 字节码指令
## 1. 字节码对应的JVM源码如何查看
&nbsp;&nbsp;在HotSpot的中有两处地方对字节码指令进行解析：一个是在bytecodeInterpreter.cpp ，另一个是在templateTable_x86_64.cpp。前者是JVM中的字节码解释器(bytecodeInterpreter)，用C++实现了每条JVM指令（如monitorenter、invokevirtual等），其优点是实现相对简单且容易理解，缺点是执行慢。后者是模板解释器(templateInterpreter)，其对每个指令都写了一段对应的汇编代码，启动时将每个指令与对应汇编代码入口绑定，可以说是效率做到了极致。在HotSpot中，只用到了模板解释器，字节码解释器根本就没用到。

&nbsp;&nbsp;其实bytecodeInterpreter的逻辑和templateInterpreter的逻辑是大同小异的，因为templateInterpreter中都是汇编代码，比较晦涩，所以看bytecodeInterpreter的实现会便于理解一点.但是，注意：
1. 在jdk8u之前，bytecodeInterpreter并没有实现偏向锁的逻辑