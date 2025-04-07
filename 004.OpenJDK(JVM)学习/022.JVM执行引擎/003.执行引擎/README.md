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

