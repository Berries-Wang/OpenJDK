# The Mudular JDK (模块化JDK)
## Goals（目标）
Divide the JDK into a set of modules that can be combined at compile time, build time, and run time into a variety of configurations including, but not limited to:(将 JDK 划分为一组模块，这些模块可以在编译时、构建时和运行时组合成各种配置，包括但不限于：)
+ Configurations corresponding to the full Java SE Platform, the full JRE, and the full JDK;（对应于完整 Java SE 平台、完整 JRE 和完整 JDK 的配置;）
+ Configurations roughly equivalent in content to each of the Compact Profiles defined in Java SE 8; and （配置在内容上大致相当于 Java SE 8 中定义的每个紧凑配置文件；并且）
+ Custom configurations which contain only a specified set of modules possibly augmented by external library and application modules, and the modules transitively required by all of these modules.（自定义配置仅包含一组指定的模块（可能由外部库和应用程序模块扩充），以及所有这些模块所传递所需的模块。）


## Motivation（动机）
Project Jigsaw aims to design and implement a standard module system for the Java SE Platform and to apply that system to the Platform itself, and to the JDK. Its primary goals are to make implementations of the Platform more easily scalable down to small devices, improve security and maintainability, enable improved application performance, and provide developers with better tools for programming in the large.（Jigsaw 项目旨在为 Java SE 平台设计和实现一个标准的模块系统，并将该系统应用于平台本身和 JDK。其主要目标是使平台的实现更容易扩展到小型设备，提高安全性和可维护性，提升应用程序性能，并为开发人员提供更强大的大型编程工具。）


## 从OpenJDK9了解模块化
> 阅读[了解 Java 9 模块](./999.REFS/了解%20Java%209%20模块%20_%20Oracle%20中国.pdf)

## 从OpenJDK了解模块化
通过查看安装包(以OpenJDK21为例)，发现，在安装包下，并没有jre目录







