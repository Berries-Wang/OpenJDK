# JVM 执行引擎
> 阅读: [揭秘 Java 虚拟机， JVM 设计原理与实现](../../006.BOOKs/Unlocking-The-Java-Virtual-Machine)

## 第二章: JVM 如何调用Java方法
Java字节码指令直接对应一段特定逻辑的本地代码，而JVM在解释执行Java字节码指令的时，会直接调用字节码指令所对应的本地机器码。JVM是使用C/C++编写而成的，因此JVM要直接执行本地机器码，便意味着必须要能够从C/C++程序中直接进入机器指令。这种技术实现的关键便是使用C语言提供的一种高级功能——函数指针，通过函数指针能够直接由C程序触发一段机器指令。

在JVM内部， call_stub 便是实现C程序调用字节码指令的第一步 —— 例如 Java 主函数的调用 。 在JVM执行Java主函数所对应的第一条字节码指令之前，必须经过 call_stub 函数指针进入对应的例程，然后在目标例程中触发对 Java主函数第一条字节码指令的调用。<sub>JVM进入Java方法：可以通过 java.lang.Thread#start()方法进行调试</sub>


通过 `第二章：Java 执行引擎工作原理：方法调用 ` ， 可以知道，stub_call 里面会通过method->entry_point进入Java方法调用，那么 entry_point是在哪里初始化的呢? 代码就在:[005.OpenJDK/002.OpenJDK8u312-GA/OpenJDK8U312-GA/hotspot/src/cpu/x86/vm/templateInterpreter_x86_64.cpp](../../005.OpenJDK/002.OpenJDK8u312-GA/OpenJDK8U312-GA/hotspot/src/cpu/x86/vm/templateInterpreter_x86_64.cpp)
```c
  InterpreterGenerator::InterpreterGenerator(StubQueue* code) : TemplateInterpreterGenerator(code) {
   generate_all(); // down here so it can be "virtual"
  }

  通过堆栈理解: 通过 entry_point 生成原理来打断点调试: 模板解释器(即字节码命令)都将被处理为`对应的本地机器码`

   # 调用字节码指令模板，为字节码指令生成entry_point 
   libjvm.so!TemplateTable::monitorenter() (/home/wei/OPEN_SOURCE/OpenJDK/005.OpenJDK/002.OpenJDK8u312-GA/OpenJDK8U312-GA/hotspot/src/cpu/x86/vm/templateTable_x86_64.cpp:3669)
   libjvm.so!Template::generate(Template * const this, InterpreterMacroAssembler * masm) (/home/wei/OPEN_SOURCE/OpenJDK/005.OpenJDK/002.OpenJDK8u312-GA/OpenJDK8U312-GA/hotspot/src/share/vm/interpreter/templateTable.cpp:63)
   libjvm.so!TemplateInterpreterGenerator::generate_and_dispatch(TemplateInterpreterGenerator * const this, Template * t, TosState tos_out) (/home/wei/OPEN_SOURCE/OpenJDK/005.OpenJDK/002.OpenJDK8u312-GA/OpenJDK8U312-GA/hotspot/src/share/vm/interpreter/templateInterpreter.cpp:551)
   #为字节码指令生成 entry_point
   libjvm.so!TemplateInterpreterGenerator::set_short_entry_points(TemplateInterpreterGenerator * const this, Template * t, address & bep, address & cep, address & sep, address & aep, address & iep, address & lep, address & fep, address & dep, address & vep) (/home/wei/OPEN_SOURCE/OpenJDK/005.OpenJDK/002.OpenJDK8u312-GA/OpenJDK8U312-GA/hotspot/src/share/vm/interpreter/templateInterpreter.cpp:514)
   libjvm.so!TemplateInterpreterGenerator::set_entry_points(TemplateInterpreterGenerator * const this, Bytecodes::Code code) (/home/wei/OPEN_SOURCE/OpenJDK/005.OpenJDK/002.OpenJDK8u312-GA/OpenJDK8U312-GA/hotspot/src/share/vm/interpreter/templateInterpreter.cpp:484)
   libjvm.so!TemplateInterpreterGenerator::set_entry_points_for_all_bytes(TemplateInterpreterGenerator * const this) (/home/wei/OPEN_SOURCE/OpenJDK/005.OpenJDK/002.OpenJDK8u312-GA/OpenJDK8U312-GA/hotspot/src/share/vm/interpreter/templateInterpreter.cpp:440)
   libjvm.so!TemplateInterpreterGenerator::generate_all(TemplateInterpreterGenerator * const this) (/home/wei/OPEN_SOURCE/OpenJDK/005.OpenJDK/002.OpenJDK8u312-GA/OpenJDK8U312-GA/hotspot/src/share/vm/interpreter/templateInterpreter.cpp:420)
   libjvm.so!InterpreterGenerator::InterpreterGenerator(InterpreterGenerator * const this, StubQueue * code) (/home/wei/OPEN_SOURCE/OpenJDK/005.OpenJDK/002.OpenJDK8u312-GA/OpenJDK8U312-GA/hotspot/src/cpu/x86/vm/templateInterpreter_x86_64.cpp:1976)
   libjvm.so!TemplateInterpreter::initialize() (/home/wei/OPEN_SOURCE/OpenJDK/005.OpenJDK/002.OpenJDK8u312-GA/OpenJDK8U312-GA/hotspot/src/share/vm/interpreter/templateInterpreter.cpp:52)
   libjvm.so!interpreter_init() (/home/wei/OPEN_SOURCE/OpenJDK/005.OpenJDK/002.OpenJDK8u312-GA/OpenJDK8U312-GA/hotspot/src/share/vm/interpreter/interpreter.cpp:118)
   libjvm.so!init_globals() (/home/wei/OPEN_SOURCE/OpenJDK/005.OpenJDK/002.OpenJDK8u312-GA/OpenJDK8U312-GA/hotspot/src/share/vm/runtime/init.cpp:109)
   libjvm.so!Threads::create_vm(JavaVMInitArgs * args, bool * canTryAgain) (/home/wei/OPEN_SOURCE/OpenJDK/005.OpenJDK/002.OpenJDK8u312-GA/OpenJDK8U312-GA/hotspot/src/share/vm/runtime/thread.cpp:3450)
   libjvm.so!JNI_CreateJavaVM(JavaVM ** vm, void ** penv, void * args) (/home/wei/OPEN_SOURCE/OpenJDK/005.OpenJDK/002.OpenJDK8u312-GA/OpenJDK8U312-GA/hotspot/src/share/vm/prims/jni.cpp:5250)
   libjli.so!InitializeJVM(JavaVM ** pvm, JNIEnv ** penv, InvocationFunctions * ifn) (/home/wei/OPEN_SOURCE/OpenJDK/005.OpenJDK/002.OpenJDK8u312-GA/OpenJDK8U312-GA/jdk/src/share/bin/java.c:1242)
   libjli.so!JavaMain(void * _args) (/home/wei/OPEN_SOURCE/OpenJDK/005.OpenJDK/002.OpenJDK8u312-GA/OpenJDK8U312-GA/jdk/src/share/bin/java.c:377)
   libpthread.so.0!start_thread(void * arg) (/build/glibc-FcRMwW/glibc-2.31/nptl/pthread_create.c:477)
   libc.so.6!clone() (/build/glibc-FcRMwW/glibc-2.31/sysdeps/unix/sysv/linux/x86_64/clone.S:95)
```