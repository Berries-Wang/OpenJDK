# [Jdk命令行工具&可视化工具](https://docs.oracle.com/javase/7/docs/technotes/tools/index.html)
## jps （Jvm process Status Tool） 显示指定系统内所有的HotSpot虚拟机**进程**
### 使用方式
+ jps [ options ] [ hostid ]
  - options: 选项
  - hostid: RMI注册表中注册的主机名
+ 输出：正在运行的虚拟机**进程**
   - 进程的本地虚拟机唯一ID(Local Virtual Machine Identifier.LVMID)，对于本地虚拟机进程来说，LVMID **等于** PID
   - 虚拟机执行主类(Main Class .main方法所在的类)
+ 选项  

|选项|作用|
|---|---|
|-q|**仅**输出LVMID，省略类的名称|
|-m|输出虚拟机进程启动时传递给主类main()方法的参数|
|-l|输出主类的全名，如果进程执行的jar包，输出jar路径|
|-v|输出虚拟机进程启动的jvm参数(启动时显示指定的)|

### 原理
+ 当java进程启动，会在System.getProperties("java.io.tmpdir);(在Linux中为/tmp/hsperfdata_{userName}/)下生成文件。文件名就是java进程的PID.至于类名、JVM参数等都可以通过解析这个文件来获取
## jstat(JVM Statics Monitoring Tool)虚拟机统计信息监视工具
+ 用于监视虚拟机各种运行状态信息的命令行工具。可以显示本地或者远程虚拟机进程中的类加载、内存、垃圾收集、JIT编译等运行数据
+ 格式： jstat [ option lvmid [ interval [s|ms] [ count ]]]
   - lvmid 虚拟机进程ID
   - interval:查询间隔
   - count:查询数量
#### option
+ 类装载
  - -class 
    + 输出:Loaded、Bytes、Unloaded、Bytes、Time
    + 解释:已加载Class数量、所占空间大小、未加载Class数量、所占空间大小、时间
+ 垃圾收集
  - -gc:监视java堆状况，包括Eden，survivor，老年代等容量，已用空间，gc时间合计等信息
     + S0C	Current survivor space 0 capacity (KB).
     + S1C	Current survivor space 1 capacity (KB).
     + S0U	Survivor space 0 utilization (KB).
     + S1U	Survivor space 1 utilization (KB).
     + EC	Current eden space capacity (KB).
     + EU	Eden space utilization）(利用率) (KB).
     + OC	Current old space capacity (KB).
     + OU	Old space utilization (KB).
     + PC	Current permanent space capacity (KB).(JDK 1.8改为了MC，即元空间)
     + PU	Permanent space utilization (KB).(JDK 1.8改为了MU，即元空间)
     + YGC	Number of young generation GC Events.
     + YGCT	Young generation garbage collection time.
     + FGC	Number of full GC events.
     + FGCT	Full garbage collection time.
     + GCT	Total garbage collection time.
  - -gccapacity:监视内容与-gc基本相同，但输出主要关注java堆各个区域所使用到的最大、最小空间
     + NGCMN	Minimum new generation capacity (KB).
     + NGCMX	Maximum new generation capacity (KB).
     + NGC	Current new generation capacity (KB).
     + S0C	Current survivor space 0 capacity (KB).
     + S1C	Current survivor space 1 capacity (KB).
     + EC	Current eden space capacity (KB).
     + OGCMN	Minimum old generation capacity (KB).
     + OGCMX	Maximum old generation capacity (KB).
     + OGC	Current old generation capacity (KB).
     + OC	Current old space capacity (KB).
     + PGCMN	Minimum permanent generation capacity (KB).
     + PGCMX	Maximum Permanent generation capacity (KB).
     + PGC	Current Permanent generation capacity (KB).
     + PC	Current Permanent space capacity (KB).
     + YGC	Number of Young generation GC Events.
     + FGC	Number of Full GC Events.
  - -gcutil:监视内容与-gc基本一致，单输出内容主要关注各个区域所占百分比
     + S0	Survivor space 0 utilization as a percentage of the space's current capacity.
     + S1	Survivor space 1 utilization as a percentage of the space's current capacity.
     + E	Eden space utilization as a percentage of the space's current capacity.
     + O	Old space utilization as a percentage of the space's current capacity.
     + P	Permanent space utilization as a percentage of the space's current capacity.（JDK 1.8使用M，即元空间）
     + YGC	Number of young generation GC events.
     + YGCT	Young generation garbage collection time.
     + FGC	Number of full GC events.
     + FGCT	Full garbage collection time.
     + GCT	Total garbage collection time.
  - -gccause:与-gcutil输出基本一致，但是会输出导致上一次gc的原因
    + LGCC	Cause of last Garbage Collection.
    + GCC	Cause of current Garbage Collection.
+ 运行期编译状况
  - -compiler:输出JIT编译器编译过的方法，耗时等信息
  - -printcompilation:输出已被JIT编译的方法
## jinfo
+ 实时查看可调整虚拟机各项参数
   + -flag name
     - prints the name and value of the given command line flag.
   + -flag [+|-]name
     - enables or disables the given boolean command line flag.
   + -flag name=value
     - sets the given command line flag to the specified value.
## jmap
+ 生成堆转储快照
+ 格式：
  - jmap [option] vmid
+ option
   - -dump:生成java堆转储快照**jmap -dump:format=b,file=18412.dump 18412**
   - -finalizerinfo：显示在F-Queue中等待Finalizer线程执行finalize方法的对象。
   - -heap:显示java堆详细信息，如使用哪种回收器、参数配置、分代情况等
   - -histo:显示堆中对象统计信息
   - -F:当虚拟机进程对-dump选项没有响应，使用这个选项强制生成
## jhat
+ 与jmap搭配使用，分析java堆存储快照
+ 格式：
  - jhat dump文件**如:jhat 18412.dump**
## jstack
+ 生成虚拟机当前时刻的线程快照，便于快速定位线程等待时间长的原因
+ 格式：
    - jstack [option] vmid
+ option
   - -F 强制输出线程堆栈
   - -l 除堆栈外，还输出锁信息
   - -m 如何使用了本地方法，还输出C/C++堆栈信息 
+ 示例:
```java
Found one Java-level deadlock:
=============================
"Thread-1":
  waiting to lock monitor 0x00007f9b140062c8 (object 0x00000000d7163ca0, a java.lang.Object),
  which is held by "Thread-0"
"Thread-0":
  waiting to lock monitor 0x00007f9b14004e28 (object 0x00000000d7163cb0, a java.lang.Object),
  which is held by "Thread-1"

Java stack information for the threads listed above:
===================================================
"Thread-1":
	at link.bosswang.test.Main.lambda$main$1(Main.java:42)
	- waiting to lock <0x00000000d7163ca0> (a java.lang.Object)
	- locked <0x00000000d7163cb0> (a java.lang.Object)
	at link.bosswang.test.Main$$Lambda$2/2093631819.run(Unknown Source)
	at java.lang.Thread.run(Thread.java:748)
"Thread-0":
	at link.bosswang.test.Main.lambda$main$0(Main.java:25)
	- waiting to lock <0x00000000d7163cb0> (a java.lang.Object)
	- locked <0x00000000d7163ca0> (a java.lang.Object)
	at link.bosswang.test.Main$$Lambda$1/1023892928.run(Unknown Source)
	at java.lang.Thread.run(Thread.java:748)

Found 1 deadlock.

```
