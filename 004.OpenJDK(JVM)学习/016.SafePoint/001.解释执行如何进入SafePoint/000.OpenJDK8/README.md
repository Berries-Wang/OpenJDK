StubRoutines::call_stub()(
        (address)&link,
        // (intptr_t*)&(result->_value), // see NOTE above (compiler problem)
        result_val_address,          // see NOTE above (compiler problem)
        result_type,
        method(),
        entry_point,
        args->parameters(),
        args->size_of_parameters(),
        CHECK
      );


https://zhuanlan.zhihu.com/p/559913612

005.OpenJDK/002.OpenJDK8u312-GA/OpenJDK8U312-GA/hotspot/src/share/vm/runtime/javaCalls.cpp


普通Java方法的entry point是这样生成出来的：
```txt
src/cpu/x86/vm/templateInterpreter_x86_64.cpp
//
// Generic interpreted method entry to (asm) interpreter
//
address InterpreterGenerator::generate_normal_entry(bool synchronized) 
```

其中一直要到这里才开始执行方法的第一条字节码：
```txt
address InterpreterGenerator::generate_normal_entry(bool synchronized) { 方法下的
  __ dispatch_next(vtos);
```

 JVM_handle_linux_signal(int sig,   os_linux_x86.cpp



https://www.zhihu.com/question/37796041

不要指望用C++源码级调试来调试HotSpot的模板解释器。

一定要不怕困难去啃汇编级调试才行

在stub()的调用处，汇编级单步步进，就会进到CallStub中，而紧接着就会去到entry_point所指向的解释器方法入口。

wei@Berries-Wang:~/OPEN_SOURCE/OpenJDK/005.OpenJDK/003.prictice-code$ gdb --args /home/wei/OPEN_SOURCE/OpenJDK/005.OpenJDK/002.OpenJDK8u312-GA/OpenJDK8U312-GA/build/linux-x86_64-normal-server-slowdebug/jdk/bin/java -Xint SynchronizedStuV2