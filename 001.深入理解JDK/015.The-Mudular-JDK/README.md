# The Mudular JDK (模块化JDK)
## Goals（目标）
Divide the JDK into a set of modules that can be combined at compile time, build time, and run time into a variety of configurations including, but not limited to:(将 JDK 划分为一组模块，这些模块可以在编译时、构建时和运行时组合成各种配置，包括但不限于：)
+ Configurations corresponding to the full Java SE Platform, the full JRE, and the full JDK;（对应于完整 Java SE 平台、完整 JRE 和完整 JDK 的配置;）
+ Configurations roughly equivalent in content to each of the Compact Profiles defined in Java SE 8; and （配置在内容上大致相当于 Java SE 8 中定义的每个紧凑配置文件；并且）
+ Custom configurations which contain only a specified set of modules possibly augmented by external library and application modules, and the modules transitively required by all of these modules.（自定义配置仅包含一组指定的模块（可能由外部库和应用程序模块扩充），以及所有这些模块所传递所需的模块。）

```txt
- 灵活配置，降低运行时所占磁盘大小.
```


## Motivation（动机）
Project Jigsaw aims to design and implement a standard module system for the Java SE Platform and to apply that system to the Platform itself, and to the JDK. Its primary goals are to make implementations of the Platform more easily scalable down to small devices, improve security and maintainability, enable improved application performance, and provide developers with better tools for programming in the large.（Jigsaw 项目旨在为 Java SE 平台设计和实现一个标准的模块系统，并将该系统应用于平台本身和 JDK。其主要目标是使平台的实现更容易扩展到小型设备，提高安全性和可维护性，提升应用程序性能，并为开发人员提供更强大的大型编程工具。）


## 从OpenJDK9了解模块化
> 阅读[了解 Java 9 模块](./999.REFS/了解%20Java%209%20模块%20_%20Oracle%20中国.pdf)

## 从OpenJDK了解模块化
通过查看安装包(以OpenJDK21为例)，发现，在安装包下，并没有jre目录,被 .jmod <sub>需要与 jlink 工具配合创建定制运行时</sub> 代替
```shell
.  OpenJDK21
├── bin
│   ├── jar
│   ├── jarsigner
│   ├── java
│   ├── javac
│   ├── javadoc
│   ├── javap
│   ├── jcmd
│   ├── jconsole
│   ├── jdb
│   ├── jdeprscan
│   ├── jdeps
│   ├── jfr
│   ├── jhsdb
│   ├── jimage
│   ├── jinfo
│   ├── jlink
│   ├── jmap
│   ├── jmod
│   ├── jpackage
│   ├── jps
│   ├── jrunscript
│   ├── jshell
│   ├── jstack
│   ├── jstat
│   ├── jstatd
│   ├── jwebserver
│   ├── keytool
│   ├── rmiregistry
│   └── serialver
├── conf
│   ├── jaxp.properties
│   ├── logging.properties
│   ├── management
│   │   ├── jmxremote.access
│   │   ├── jmxremote.password.template
│   │   └── management.properties
│   ├── net.properties
│   ├── security
│   │   ├── java.policy
│   │   ├── java.security
│   │   └── policy
│   │       ├── README.txt
│   │       ├── limited
│   │       │   ├── default_US_export.policy
│   │       │   ├── default_local.policy
│   │       │   └── exempt_local.policy
│   │       └── unlimited
│   │           ├── default_US_export.policy
│   │           └── default_local.policy
│   └── sound.properties
├── include
│   ├── classfile_constants.h
│   ├── darwin
│   │   ├── jawt_md.h
│   │   └── jni_md.h
│   ├── jawt.h
│   ├── jdwpTransport.h
│   ├── jni.h
│   ├── jvmti.h
│   └── jvmticmlr.h
├── jmods
│   ├── java.base.jmod
│   ├── java.compiler.jmod
│   ├── java.datatransfer.jmod
│   ├── java.desktop.jmod
│   ├── java.instrument.jmod
│   ├── java.logging.jmod
│   ├── java.management.jmod
│   ├── java.management.rmi.jmod
│   ├── java.naming.jmod
│   ├── java.net.http.jmod
│   ├── java.prefs.jmod
│   ├── java.rmi.jmod
│   ├── java.scripting.jmod
│   ├── java.se.jmod
│   ├── java.security.jgss.jmod
│   ├── java.security.sasl.jmod
│   ├── java.smartcardio.jmod
│   ├── java.sql.jmod
│   ├── java.sql.rowset.jmod
│   ├── java.transaction.xa.jmod
│   ├── java.xml.crypto.jmod
│   ├── java.xml.jmod
│   ├── jdk.accessibility.jmod
│   ├── jdk.attach.jmod
│   ├── jdk.charsets.jmod
│   ├── jdk.compiler.jmod
│   ├── jdk.crypto.cryptoki.jmod
│   ├── jdk.crypto.ec.jmod
│   ├── jdk.dynalink.jmod
│   ├── jdk.editpad.jmod
│   ├── jdk.hotspot.agent.jmod
│   ├── jdk.httpserver.jmod
│   ├── jdk.incubator.vector.jmod
│   ├── jdk.internal.ed.jmod
│   ├── jdk.internal.jvmstat.jmod
│   ├── jdk.internal.le.jmod
│   ├── jdk.internal.opt.jmod
│   ├── jdk.internal.vm.ci.jmod
│   ├── jdk.internal.vm.compiler.jmod
│   ├── jdk.internal.vm.compiler.management.jmod
│   ├── jdk.jartool.jmod
│   ├── jdk.javadoc.jmod
│   ├── jdk.jcmd.jmod
│   ├── jdk.jconsole.jmod
│   ├── jdk.jdeps.jmod
│   ├── jdk.jdi.jmod
│   ├── jdk.jdwp.agent.jmod
│   ├── jdk.jfr.jmod
│   ├── jdk.jlink.jmod
│   ├── jdk.jpackage.jmod
│   ├── jdk.jshell.jmod
│   ├── jdk.jsobject.jmod
│   ├── jdk.jstatd.jmod
│   ├── jdk.localedata.jmod
│   ├── jdk.management.agent.jmod
│   ├── jdk.management.jfr.jmod
│   ├── jdk.management.jmod
│   ├── jdk.naming.dns.jmod
│   ├── jdk.naming.rmi.jmod
│   ├── jdk.net.jmod
│   ├── jdk.nio.mapmode.jmod
│   ├── jdk.random.jmod
│   ├── jdk.sctp.jmod
│   ├── jdk.security.auth.jmod
│   ├── jdk.security.jgss.jmod
│   ├── jdk.unsupported.desktop.jmod
│   ├── jdk.unsupported.jmod
│   ├── jdk.xml.dom.jmod
│   └── jdk.zipfs.jmod
├── legal
│   ├── java.base
│   │   ├── ADDITIONAL_LICENSE_INFO
│   │   ├── ASSEMBLY_EXCEPTION
│   │   ├── LICENSE
│   │   ├── aes.md
│   │   ├── asm.md
│   │   ├── c-libutl.md
│   │   ├── cldr.md
│   │   ├── icu.md
│   │   ├── public_suffix.md
│   │   ├── unicode.md
│   │   └── zlib.md
│   ├── java.compiler
│   │   ├── ADDITIONAL_LICENSE_INFO
│   │   ├── ASSEMBLY_EXCEPTION
│   │   └── LICENSE
│   ├── java.datatransfer
│   │   ├── ADDITIONAL_LICENSE_INFO
│   │   ├── ASSEMBLY_EXCEPTION
│   │   └── LICENSE
│   ├── java.desktop
│   │   ├── ADDITIONAL_LICENSE_INFO
│   │   ├── ASSEMBLY_EXCEPTION
│   │   ├── LICENSE
│   │   ├── colorimaging.md
│   │   ├── freetype.md
│   │   ├── giflib.md
│   │   ├── harfbuzz.md
│   │   ├── jpeg.md
│   │   ├── lcms.md
│   │   ├── libpng.md
│   │   ├── mesa3d.md
│   │   ├── pipewire.md
│   │   └── xwd.md
│   ├── java.instrument
│   │   ├── ADDITIONAL_LICENSE_INFO
│   │   ├── ASSEMBLY_EXCEPTION
│   │   └── LICENSE
│   ├── java.logging
│   │   ├── ADDITIONAL_LICENSE_INFO
│   │   ├── ASSEMBLY_EXCEPTION
│   │   └── LICENSE
│   ├── java.management
│   │   ├── ADDITIONAL_LICENSE_INFO
│   │   ├── ASSEMBLY_EXCEPTION
│   │   └── LICENSE
│   ├── java.management.rmi
│   │   ├── ADDITIONAL_LICENSE_INFO
│   │   ├── ASSEMBLY_EXCEPTION
│   │   └── LICENSE
│   ├── java.naming
│   │   ├── ADDITIONAL_LICENSE_INFO
│   │   ├── ASSEMBLY_EXCEPTION
│   │   └── LICENSE
│   ├── java.net.http
│   │   ├── ADDITIONAL_LICENSE_INFO
│   │   ├── ASSEMBLY_EXCEPTION
│   │   └── LICENSE
│   ├── java.prefs
│   │   ├── ADDITIONAL_LICENSE_INFO
│   │   ├── ASSEMBLY_EXCEPTION
│   │   └── LICENSE
│   ├── java.rmi
│   │   ├── ADDITIONAL_LICENSE_INFO
│   │   ├── ASSEMBLY_EXCEPTION
│   │   └── LICENSE
│   ├── java.scripting
│   │   ├── ADDITIONAL_LICENSE_INFO
│   │   ├── ASSEMBLY_EXCEPTION
│   │   └── LICENSE
│   ├── java.se
│   │   ├── ADDITIONAL_LICENSE_INFO
│   │   ├── ASSEMBLY_EXCEPTION
│   │   └── LICENSE
│   ├── java.security.jgss
│   │   ├── ADDITIONAL_LICENSE_INFO
│   │   ├── ASSEMBLY_EXCEPTION
│   │   └── LICENSE
│   ├── java.security.sasl
│   │   ├── ADDITIONAL_LICENSE_INFO
│   │   ├── ASSEMBLY_EXCEPTION
│   │   └── LICENSE
│   ├── java.smartcardio
│   │   ├── ADDITIONAL_LICENSE_INFO
│   │   ├── ASSEMBLY_EXCEPTION
│   │   ├── LICENSE
│   │   └── pcsclite.md
│   ├── java.sql
│   │   ├── ADDITIONAL_LICENSE_INFO
│   │   ├── ASSEMBLY_EXCEPTION
│   │   └── LICENSE
│   ├── java.sql.rowset
│   │   ├── ADDITIONAL_LICENSE_INFO
│   │   ├── ASSEMBLY_EXCEPTION
│   │   └── LICENSE
│   ├── java.transaction.xa
│   │   ├── ADDITIONAL_LICENSE_INFO
│   │   ├── ASSEMBLY_EXCEPTION
│   │   └── LICENSE
│   ├── java.xml
│   │   ├── ADDITIONAL_LICENSE_INFO
│   │   ├── ASSEMBLY_EXCEPTION
│   │   ├── LICENSE
│   │   ├── bcel.md
│   │   ├── dom.md
│   │   ├── jcup.md
│   │   ├── xalan.md
│   │   └── xerces.md
│   ├── java.xml.crypto
│   │   ├── ADDITIONAL_LICENSE_INFO
│   │   ├── ASSEMBLY_EXCEPTION
│   │   ├── LICENSE
│   │   └── santuario.md
│   ├── jdk.accessibility
│   │   ├── ADDITIONAL_LICENSE_INFO
│   │   ├── ASSEMBLY_EXCEPTION
│   │   └── LICENSE
│   ├── jdk.attach
│   │   ├── ADDITIONAL_LICENSE_INFO
│   │   ├── ASSEMBLY_EXCEPTION
│   │   └── LICENSE
│   ├── jdk.charsets
│   │   ├── ADDITIONAL_LICENSE_INFO
│   │   ├── ASSEMBLY_EXCEPTION
│   │   └── LICENSE
│   ├── jdk.compiler
│   │   ├── ADDITIONAL_LICENSE_INFO
│   │   ├── ASSEMBLY_EXCEPTION
│   │   └── LICENSE
│   ├── jdk.crypto.cryptoki
│   │   ├── ADDITIONAL_LICENSE_INFO
│   │   ├── ASSEMBLY_EXCEPTION
│   │   ├── LICENSE
│   │   ├── pkcs11cryptotoken.md
│   │   └── pkcs11wrapper.md
│   ├── jdk.crypto.ec
│   │   ├── ADDITIONAL_LICENSE_INFO
│   │   ├── ASSEMBLY_EXCEPTION
│   │   └── LICENSE
│   ├── jdk.dynalink
│   │   ├── ADDITIONAL_LICENSE_INFO
│   │   ├── ASSEMBLY_EXCEPTION
│   │   ├── LICENSE
│   │   └── dynalink.md
│   ├── jdk.editpad
│   │   ├── ADDITIONAL_LICENSE_INFO
│   │   ├── ASSEMBLY_EXCEPTION
│   │   └── LICENSE
│   ├── jdk.hotspot.agent
│   │   ├── ADDITIONAL_LICENSE_INFO
│   │   ├── ASSEMBLY_EXCEPTION
│   │   └── LICENSE
│   ├── jdk.httpserver
│   │   ├── ADDITIONAL_LICENSE_INFO
│   │   ├── ASSEMBLY_EXCEPTION
│   │   └── LICENSE
│   ├── jdk.incubator.vector
│   │   ├── ADDITIONAL_LICENSE_INFO
│   │   ├── ASSEMBLY_EXCEPTION
│   │   └── LICENSE
│   ├── jdk.internal.ed
│   │   ├── ADDITIONAL_LICENSE_INFO
│   │   ├── ASSEMBLY_EXCEPTION
│   │   └── LICENSE
│   ├── jdk.internal.jvmstat
│   │   ├── ADDITIONAL_LICENSE_INFO
│   │   ├── ASSEMBLY_EXCEPTION
│   │   └── LICENSE
│   ├── jdk.internal.le
│   │   ├── ADDITIONAL_LICENSE_INFO
│   │   ├── ASSEMBLY_EXCEPTION
│   │   ├── LICENSE
│   │   └── jline.md
│   ├── jdk.internal.opt
│   │   ├── ADDITIONAL_LICENSE_INFO
│   │   ├── ASSEMBLY_EXCEPTION
│   │   ├── LICENSE
│   │   └── jopt-simple.md
│   ├── jdk.internal.vm.ci
│   │   ├── ADDITIONAL_LICENSE_INFO
│   │   ├── ASSEMBLY_EXCEPTION
│   │   └── LICENSE
│   ├── jdk.internal.vm.compiler
│   │   ├── ADDITIONAL_LICENSE_INFO
│   │   ├── ASSEMBLY_EXCEPTION
│   │   └── LICENSE
│   ├── jdk.internal.vm.compiler.management
│   │   ├── ADDITIONAL_LICENSE_INFO
│   │   ├── ASSEMBLY_EXCEPTION
│   │   └── LICENSE
│   ├── jdk.jartool
│   │   ├── ADDITIONAL_LICENSE_INFO
│   │   ├── ASSEMBLY_EXCEPTION
│   │   └── LICENSE
│   ├── jdk.javadoc
│   │   ├── ADDITIONAL_LICENSE_INFO
│   │   ├── ASSEMBLY_EXCEPTION
│   │   ├── LICENSE
│   │   ├── jquery.md
│   │   └── jqueryUI.md
│   ├── jdk.jcmd
│   │   ├── ADDITIONAL_LICENSE_INFO
│   │   ├── ASSEMBLY_EXCEPTION
│   │   └── LICENSE
│   ├── jdk.jconsole
│   │   ├── ADDITIONAL_LICENSE_INFO
│   │   ├── ASSEMBLY_EXCEPTION
│   │   └── LICENSE
│   ├── jdk.jdeps
│   │   ├── ADDITIONAL_LICENSE_INFO
│   │   ├── ASSEMBLY_EXCEPTION
│   │   └── LICENSE
│   ├── jdk.jdi
│   │   ├── ADDITIONAL_LICENSE_INFO
│   │   ├── ASSEMBLY_EXCEPTION
│   │   └── LICENSE
│   ├── jdk.jdwp.agent
│   │   ├── ADDITIONAL_LICENSE_INFO
│   │   ├── ASSEMBLY_EXCEPTION
│   │   └── LICENSE
│   ├── jdk.jfr
│   │   ├── ADDITIONAL_LICENSE_INFO
│   │   ├── ASSEMBLY_EXCEPTION
│   │   └── LICENSE
│   ├── jdk.jlink
│   │   ├── ADDITIONAL_LICENSE_INFO
│   │   ├── ASSEMBLY_EXCEPTION
│   │   └── LICENSE
│   ├── jdk.jpackage
│   │   ├── ADDITIONAL_LICENSE_INFO
│   │   ├── ASSEMBLY_EXCEPTION
│   │   └── LICENSE
│   ├── jdk.jshell
│   │   ├── ADDITIONAL_LICENSE_INFO
│   │   ├── ASSEMBLY_EXCEPTION
│   │   └── LICENSE
│   ├── jdk.jsobject
│   │   ├── ADDITIONAL_LICENSE_INFO
│   │   ├── ASSEMBLY_EXCEPTION
│   │   └── LICENSE
│   ├── jdk.jstatd
│   │   ├── ADDITIONAL_LICENSE_INFO
│   │   ├── ASSEMBLY_EXCEPTION
│   │   └── LICENSE
│   ├── jdk.localedata
│   │   ├── ADDITIONAL_LICENSE_INFO
│   │   ├── ASSEMBLY_EXCEPTION
│   │   ├── LICENSE
│   │   ├── cldr.md
│   │   └── thaidict.md
│   ├── jdk.management
│   │   ├── ADDITIONAL_LICENSE_INFO
│   │   ├── ASSEMBLY_EXCEPTION
│   │   └── LICENSE
│   ├── jdk.management.agent
│   │   ├── ADDITIONAL_LICENSE_INFO
│   │   ├── ASSEMBLY_EXCEPTION
│   │   └── LICENSE
│   ├── jdk.management.jfr
│   │   ├── ADDITIONAL_LICENSE_INFO
│   │   ├── ASSEMBLY_EXCEPTION
│   │   └── LICENSE
│   ├── jdk.naming.dns
│   │   ├── ADDITIONAL_LICENSE_INFO
│   │   ├── ASSEMBLY_EXCEPTION
│   │   └── LICENSE
│   ├── jdk.naming.rmi
│   │   ├── ADDITIONAL_LICENSE_INFO
│   │   ├── ASSEMBLY_EXCEPTION
│   │   └── LICENSE
│   ├── jdk.net
│   │   ├── ADDITIONAL_LICENSE_INFO
│   │   ├── ASSEMBLY_EXCEPTION
│   │   └── LICENSE
│   ├── jdk.nio.mapmode
│   │   ├── ADDITIONAL_LICENSE_INFO
│   │   ├── ASSEMBLY_EXCEPTION
│   │   └── LICENSE
│   ├── jdk.random
│   │   ├── ADDITIONAL_LICENSE_INFO
│   │   ├── ASSEMBLY_EXCEPTION
│   │   └── LICENSE
│   ├── jdk.sctp
│   │   ├── ADDITIONAL_LICENSE_INFO
│   │   ├── ASSEMBLY_EXCEPTION
│   │   └── LICENSE
│   ├── jdk.security.auth
│   │   ├── ADDITIONAL_LICENSE_INFO
│   │   ├── ASSEMBLY_EXCEPTION
│   │   └── LICENSE
│   ├── jdk.security.jgss
│   │   ├── ADDITIONAL_LICENSE_INFO
│   │   ├── ASSEMBLY_EXCEPTION
│   │   └── LICENSE
│   ├── jdk.unsupported
│   │   ├── ADDITIONAL_LICENSE_INFO
│   │   ├── ASSEMBLY_EXCEPTION
│   │   └── LICENSE
│   ├── jdk.unsupported.desktop
│   │   ├── ADDITIONAL_LICENSE_INFO
│   │   ├── ASSEMBLY_EXCEPTION
│   │   └── LICENSE
│   ├── jdk.xml.dom
│   │   ├── ADDITIONAL_LICENSE_INFO
│   │   ├── ASSEMBLY_EXCEPTION
│   │   └── LICENSE
│   └── jdk.zipfs
│       ├── ADDITIONAL_LICENSE_INFO
│       ├── ASSEMBLY_EXCEPTION
│       └── LICENSE
├── lib
│   ├── classlist
│   ├── ct.sym
│   ├── fontconfig.bfc
│   ├── fontconfig.properties.src
│   ├── jfr
│   │   ├── default.jfc
│   │   └── profile.jfc
│   ├── jrt-fs.jar
│   ├── jspawnhelper
│   ├── jvm.cfg
│   ├── libattach.dylib
│   ├── libawt.dylib
│   ├── libawt_lwawt.dylib
│   ├── libdt_socket.dylib
│   ├── libextnet.dylib
│   ├── libfontmanager.dylib
│   ├── libfreetype.dylib
│   ├── libinstrument.dylib
│   ├── libj2gss.dylib
│   ├── libj2pcsc.dylib
│   ├── libj2pkcs11.dylib
│   ├── libjaas.dylib
│   ├── libjava.dylib
│   ├── libjavajpeg.dylib
│   ├── libjawt.dylib
│   ├── libjdwp.dylib
│   ├── libjimage.dylib
│   ├── libjli.dylib
│   ├── libjsig.dylib
│   ├── libjsound.dylib
│   ├── liblcms.dylib
│   ├── lible.dylib
│   ├── libmanagement.dylib
│   ├── libmanagement_agent.dylib
│   ├── libmanagement_ext.dylib
│   ├── libmlib_image.dylib
│   ├── libnet.dylib
│   ├── libnio.dylib
│   ├── libosx.dylib
│   ├── libosxapp.dylib
│   ├── libosxkrb5.dylib
│   ├── libosxsecurity.dylib
│   ├── libosxui.dylib
│   ├── libprefs.dylib
│   ├── librmi.dylib
│   ├── libsaproc.dylib
│   ├── libsplashscreen.dylib
│   ├── libsyslookup.dylib
│   ├── libverify.dylib
│   ├── libzip.dylib
│   ├── modules
│   ├── psfont.properties.ja
│   ├── psfontj2d.properties
│   ├── security
│   │   ├── blocked.certs
│   │   ├── cacerts
│   │   ├── default.policy
│   │   └── public_suffix_list.dat
│   ├── server
│   │   ├── libjsig.dylib
│   │   └── libjvm.dylib
│   ├── shaders.metallib
│   ├── src.zip
│   └── tzdb.dat
├── man
│   └── man1
│       ├── jar.1
│       ├── jarsigner.1
│       ├── java.1
│       ├── javac.1
│       ├── javadoc.1
│       ├── javap.1
│       ├── jcmd.1
│       ├── jconsole.1
│       ├── jdb.1
│       ├── jdeprscan.1
│       ├── jdeps.1
│       ├── jfr.1
│       ├── jhsdb.1
│       ├── jinfo.1
│       ├── jlink.1
│       ├── jmap.1
│       ├── jmod.1
│       ├── jpackage.1
│       ├── jps.1
│       ├── jrunscript.1
│       ├── jshell.1
│       ├── jstack.1
│       ├── jstat.1
│       ├── jstatd.1
│       ├── jwebserver.1
│       ├── keytool.1
│       ├── rmiregistry.1
│       └── serialver.1
└── release

87 directories, 452 files
```







