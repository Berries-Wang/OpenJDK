# OpenJdk使用GDB调试问题以及解决方案
## 问题一: openjdk gdb 导致segment fault 不gdb就没问题
+ 具体的错误信息: Thread 2 "java" received signal SIGSEGV, Segmentation fault.
   - 调试命令: gdb --args ./linux-x86_64-normal-server-slowdebug/jdk/bin/java D
       - D: 由D.java编译(${workspaceFolder}/005.OpenJDK/000.openJDK_8u40/build/linux-x86_64-normal-server-slowdebug/jdk/bin/javac -g D.java)而来的class文件
+ 解决方案: [(gdb) handle SIGSEGV nostop noprint pass](https://www.zhihu.com/question/39925554)