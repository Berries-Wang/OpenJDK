# JVM 执行引擎 <sup>执行引擎，就是一个运算器，能识别输入的指令，并根据输入的指令执行一套特定的逻辑，最终输出特定的结果</sup>
> 先阅读:[揭秘Java虚拟机#第9章：执行引擎](./../../../006.BOOKs/Unlocking-The-Java-Virtual-Machine/009.Unlocking-The-Java-Virtual-Machine-9.pdf) & [002.物理CPU-OS执行流程.md](./../002.物理CPU-OS执行流程.md)
>>> `JVM执行引擎只是个虚拟系统`，本身不具备真正的运算能力，其内部仍然需要依靠物理CPU才能完成运算功能，而物理CPU仅识别二进制机器指令，JVM执行引擎既然需要依赖物理CPU，就必然需要将字节码指令最终转换为二进制机器指令。

JVM 也完全继承了CPU（OS）的设计思想<sup>取指 -> 译码 -> 执行 -> 继续取指</sup>： a).JVM 有自己的指令集； b).JVM通过软件模拟硬件译码电路来识别JVM指令集；


### 字节码指令定义
> [005.OpenJDK/002.OpenJDK8u312-GA/OpenJDK8U312-GA/hotspot/src/share/vm/interpreter/bytecodes.cpp](../../005.OpenJDK/002.OpenJDK8u312-GA/OpenJDK8U312-GA/hotspot/src/share/vm/interpreter/bytecodes.cpp)
>> 字节码指令的宽度对于JVM虚拟机完成取指是至关重要的，JVM只有知道了每一个字节码指令所占的宽度，才能完成 "取指 -> 译码 -> 执行 -> 取指" 这种循环，将程序一直运行下去
```c
/**
 * <pre>
 *      format 列记录了操作码和操作数的长度:
 *             b: 总长为1(字节)
 *             bc: 表示操作码后面会跟一个宽度为1字节的操作数,如bipush
 *
 *      ### Java的每个字节码指令都仅占一个字节 ###
 *
 *      ldc 整条指令，操作码和操作数一共只占2字节。
 *      ldc_w ,操作码和操作数一共只占3字节。
 *
 *   字节码指令的宽度对于JVM虚拟机完成取指是至关重要的，JVM只有知道了每一个字节码指令所占的宽度，才能完成
 * "取指 -> 译码 -> 执行 -> 取指" 这种循环，将程序一直运行下去
 * </pre>
 *
 * <pre>
 *     ldc: load constant
 * </pre>
 * JVM启动期间被调用，该函数执行完成之后，各个字节码指令所占的内存宽度便会被JVM所记录，JVM在运行期执行Java程序时会不断读取该函数所维护的表，
 * 计算每个字节码指令的长度
 */
void Bytecodes::initialize() {
  if (_is_initialized) return;
  assert(number_of_codes <= 256, "too many bytecodes");
  // ......
}
```

### 取指
[005.OpenJDK/002.OpenJDK8u312-GA/OpenJDK8U312-GA/hotspot/src/cpu/x86/vm/templateInterpreter_x86_64.cpp](../../../005.OpenJDK/002.OpenJDK8u312-GA/OpenJDK8U312-GA/hotspot/src/cpu/x86/vm/templateInterpreter_x86_64.cpp)

```cpp
address InterpreterGenerator::generate_normal_entry(bool synchronized) {
  ....
  /**
   * 跳转到目标Java方法的第一条字节码指令，并执行其对应的机器指令
   * > 则，由此，进入到Java的世界中去了
   * > 主要工作: 取指
   */
  __ dispatch_next(vtos);
 
  ....
  
  }

```

### HotSpot —— 栈式指令集虚拟机
HotSpot 是一款基于栈式指令集的虚拟机，栈式指令集虚拟机（Stack-based Instruction Set Virtual Machine）是一种基于栈结构来执行指令的虚拟机设计模型。它与基于寄存器的虚拟机形成对比，是两种主要的虚拟机架构之一。

栈式虚拟机的的设计是将数据压入到栈中，然后运算时弹栈，运算后的结果再压栈，如此反复进行。所以栈式虚拟机相对来说实现起来简单，无需考虑寄存器的分配算法。如JVM主要就是一个栈式虚拟机。<sub>可以将堆栈视为任意大数量的寄存器。</sub>

寄存器虚拟机的设计其实是根据物理机的CPU架构设计是一致的，用寄存器来做数据的存取和运算，所以是一个软CPU的模式，容易实现JIT

Java虚拟机（JVM）主要是一个栈式虚拟机（Stack-based Virtual Machine）。在JVM中，大多数操作都是通过操作栈来进行的，包括算术运算、方法调用、返回值处理等。每个线程在JVM中都有自己的栈，用于存储局部变量、中间计算结果、方法调用的上下文等。

栈式虚拟机的特点是代码简洁、指令少，因为大部分操作都是通过操作栈来完成的。与寄存器虚拟机（Register-based Virtual Machine）相比，栈式虚拟机通常不需要复杂的寄存器分配算法。
`然而，值得注意的是`，JVM并不是纯粹的栈式虚拟机。在JVM的内部实现中，为了优化性能，它也使用了一些寄存器。例如，即时编译器（JIT）在将字节码转换为机器码时，可能会使用寄存器来提高执行效率。此外，Java 7引入的 invokedynamic 指令和 Java 8 引入的 Lambda 表达式等特性，也在某种程度上依赖寄存器操作。
总的来说，虽然JVM在字节码层面上是一个栈式虚拟机，但在底层实现上，它也利用了寄存器的特性来提升性能。
