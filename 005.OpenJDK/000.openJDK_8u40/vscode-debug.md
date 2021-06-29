# launch.json配置
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