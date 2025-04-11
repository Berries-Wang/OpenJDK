## JVM 安全点

### JVM 信号处理函数
> [005.OpenJDK/002.OpenJDK8u312-GA/OpenJDK8U312-GA/hotspot/src/os_cpu/linux_x86/vm/os_linux_x86.cpp](../../005.OpenJDK/002.OpenJDK8u312-GA/OpenJDK8U312-GA/hotspot/src/os_cpu/linux_x86/vm/os_linux_x86.cpp)
```c
extern "C" JNIEXPORT int
JVM_handle_linux_signal(int sig,
                        siginfo_t* info,
                        void* ucVoid,
                        int abort_if_unrecognized);

  // Q1. 信号处理函数如何注册
  // 02. 异常处理函数如何调用(进入安全点)

  // 内存访问异常会产生 SIGSEGV  信号
```

##### 信号处理函数安装调用流程
```txt
# 使用的信号处理函数：sigaction() 是一个 POSIX 标准 的系统调用（UNIX/Linux），用于更精细地控制进程对信号（signal）的处理方式。它比传统的 signal() 函数更强大、更灵活，是现代 Linux/UNIX 编程中推荐的信号处理方式。
libjvm.so!os::Linux::set_signal_handler(int sig, bool set_installed) (/home/wei/OPEN_SOURCE/OpenJDK/005.OpenJDK/002.OpenJDK8u312-GA/OpenJDK8U312-GA/hotspot/src/os/linux/vm/os_linux.cpp:4726)
libjvm.so!os::Linux::install_signal_handlers() (/home/wei/OPEN_SOURCE/OpenJDK/005.OpenJDK/002.OpenJDK8u312-GA/OpenJDK8U312-GA/hotspot/src/os/linux/vm/os_linux.cpp:4796)
libjvm.so!os::init_2() (/home/wei/OPEN_SOURCE/OpenJDK/005.OpenJDK/002.OpenJDK8u312-GA/OpenJDK8U312-GA/hotspot/src/os/linux/vm/os_linux.cpp:5176)
libjvm.so!Threads::create_vm(JavaVMInitArgs * args, bool * canTryAgain) (/home/wei/OPEN_SOURCE/OpenJDK/005.OpenJDK/002.OpenJDK8u312-GA/OpenJDK8U312-GA/hotspot/src/share/vm/runtime/thread.cpp:3390)
libjvm.so!JNI_CreateJavaVM(JavaVM ** vm, void ** penv, void * args) (/home/wei/OPEN_SOURCE/OpenJDK/005.OpenJDK/002.OpenJDK8u312-GA/OpenJDK8U312-GA/hotspot/src/share/vm/prims/jni.cpp:5250)
libjli.so!InitializeJVM(JavaVM ** pvm, JNIEnv ** penv, InvocationFunctions * ifn) (/home/wei/OPEN_SOURCE/OpenJDK/005.OpenJDK/002.OpenJDK8u312-GA/OpenJDK8U312-GA/jdk/src/share/bin/java.c:1242)
libjli.so!JavaMain(void * _args) (/home/wei/OPEN_SOURCE/OpenJDK/005.OpenJDK/002.OpenJDK8u312-GA/OpenJDK8U312-GA/jdk/src/share/bin/java.c:377)
libpthread.so.0!start_thread(void * arg) (/build/glibc-FcRMwW/glibc-2.31/nptl/pthread_create.c:477)
libc.so.6!clone() (/build/glibc-FcRMwW/glibc-2.31/sysdeps/unix/sysv/linux/x86_64/clone.S:95)
```



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