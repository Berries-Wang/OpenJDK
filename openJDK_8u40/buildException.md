# OpenJdk8编译问题
## 问题1： OS问题
+ 错误信息: *** This OS is not supported: Linux Wang 5.8.0-45-generic #51~20.04.1-Ubuntu SMP Tue Feb 23 13:46:31 UTC 2021 x86_64 x86_64 x86_64 GNU/Linux
+ 处理： hotspot/make/linux/Makefile 文件，将：
    - SUPPORTED_OS_VERSION = 2.4% 2.5% 2.6% 3% 修改为 SUPPORTED_OS_VERSION = 2.4% 2.5% 2.6% 3% 4% 5%  ，添加了4% 5% ,即说明允许4.x 5.x版本的内核编译这个OpenJdk

## 问题2： Error occurred during initialization of VM
+ 这个是由脚本common/autoconf/generated-configure.sh抛出的问题,该问题出现的原因是: --with-boot-jdk 的jdk路径上有中文。