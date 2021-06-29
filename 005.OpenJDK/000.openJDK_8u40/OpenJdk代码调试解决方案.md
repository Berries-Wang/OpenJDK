# OpenJDK代码Debug
## GDB命令行调试 
OpenJdk使用GDB调试问题以及解决方案
## 问题一: openjdk gdb 导致segment fault 不gdb就没问题
+ 具体的错误信息: Thread 2 "java" received signal SIGSEGV, Segmentation fault.
   - 调试命令: gdb --args ./linux-x86_64-normal-server-slowdebug/jdk/bin/java D
       - D: 由D.java编译(${workspaceFolder}/005.OpenJDK/000.openJDK_8u40/build/linux-x86_64-normal-server-slowdebug/jdk/bin/javac -g D.java)而来的class文件
+ 解决方案: [(gdb) handle SIGSEGV nostop noprint pass](https://www.zhihu.com/question/39925554)

------------------

## VSCode可视化调试
launch.json配置
&nbsp;&nbsp;在005.OpenJDK/000.openJDK_8u40/build下有D.java源文件，内容如下:
```java
   public class D {

    public static void main(String[] args) {

        System.out.println("Hello WOrld");

    }

   }
```

&nbsp;&nbsp;现使用vscode进行调试(注意，要使用源码构建出的javac,java命令进行调试)，编译命令如下:
+ 编译: ${workspaceFolder}/005.OpenJDK/000.openJDK_8u40/build/linux-x86_64-normal-server-slowdebug/jdk/bin/javac -g D.java

&nbsp;&nbsp;launch.json配置如下:
```json
{
    // Use IntelliSense to learn about possible attributes.
    // Hover to view descriptions of existing attributes.
    // For more information, visit: https://go.microsoft.com/fwlink/?linkid=830387
    "version": "0.2.0",
    "configurations": [
        {
            "name": "java H",
            "type": "cppdbg",
            "request": "launch",
            "program": "${workspaceFolder}/005.OpenJDK/000.openJDK_8u40/build/linux-x86_64-normal-server-slowdebug/jdk/bin/java",
            "args": ["D"],
            "stopAtEntry": false,
            "cwd": "${workspaceFolder}/005.OpenJDK/000.openJDK_8u40/build",
            "environment": [],
            "externalConsole": false,
            "MIMode": "gdb",
            "setupCommands": [
                {
                    "description": "为 gdb 启用整齐打印",
                    "text": "-enable-pretty-printing",
                    "ignoreFailures": true
                }
            ]
        }
    ]
}
```

-----------------

## 调试注意事项
1. 注意构建脚本(005.OpenJDK/000.openJDK_8u40/openJdkBuild.sh)参数ZIP_DEBUGINFO_FILES，设置了这个参数会生成debuginfo信息(如：libjvm.debuginfo、libjsig.debuginfo)，才可以打断点进行调试