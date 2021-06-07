# OpenJdk8编译问题
## 准备
+ --with-boot-jdk: /home/wei/workspace/Temp/jdk7/jdk1.7.0_80 , 不能包含中文路径，否则一直报错，即问题2，只要比编译的jdk版本打一个大的版本号就行。即对于jdk8，使用jdk7来作为boot jdk,至于是jdk7的那个小版本，这无所谓
+ gcc: gcc version 4.7.4 (Ubuntu/Linaro 4.7.4-3ubuntu12) , 对于该版本使用4.7版本即可，高版本不兼容
    - 可以使用sudo update-alternatives --config gcc命令来切换不同版本
+ g++:  gcc version 4.7.4 (Ubuntu/Linaro 4.7.4-3ubuntu12), 同gcc
## 问题1： OS问题
+ 错误信息: *** This OS is not supported: Linux Wang 5.8.0-45-generic #51~20.04.1-Ubuntu SMP Tue Feb 23 13:46:31 UTC 2021 x86_64 x86_64 x86_64 GNU/Linux
+ 处理： hotspot/make/linux/Makefile 文件，将：
    - SUPPORTED_OS_VERSION = 2.4% 2.5% 2.6% 3% 修改为 SUPPORTED_OS_VERSION = 2.4% 2.5% 2.6% 3% 4% 5%  ，添加了4% 5% ,即说明允许4.x 5.x版本的内核编译这个OpenJdk

## 问题2： Error occurred during initialization of VM
+ 这个是由脚本common/autoconf/generated-configure.sh抛出的问题,该问题出现的原因是: --with-boot-jdk 的jdk路径上有中文。

## 问题3:
+ 异常信息: 
   - cc1plus: all warnings being treated as errors 
   - 或者 os_linux.inline.hpp:127:18: error: 'int readdir_r(DIR*, dirent*, dirent**)' is deprecated [-Werror=deprecated-declarations]
+ 解决方案: 修改文件: hotspot/make/linux/makefiles/gcc.make , 将WARNINGS_ARE_ERRORS=-Werror 修改为 WARNINGS_ARE_ERRORS=-Wno-error

## 问题4： openJDK_8u40/hotspot/agent/src/share/classes/sun/jvm/hotspot/memory/TenuredSpace.java:35: 错误: 编码ascii的不可映射字符 (unmappable character for encoding ascii)
+ 问题出现原因： 代码中有中文注释
+ 解决方案：[点击进入](https://blog.csdn.net/BDX_Hadoop_Opt/article/details/29209829)
   -  全文件搜索-encoding ascii，将-encoding ascii删除掉。如
       1.  JAVACFLAGS += -encoding ascii 修改为  # JAVACFLAGS += -encoding ascii ，即注释掉，或者将ascii改为utf-8
       2.  JAVAC_FLAGS=-g -encoding ascii 修改为 JAVAC_FLAGS=-g 即去掉-encoding ascii，或者将ascii改为utf-8