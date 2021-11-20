# JVM GC(Garbage Collection)
## CollectedHeap
&nbsp;&nbsp;CollectedHeap是一个接口，CollectedHeap类根据CollectorPolicy中设置的值确定策略。CollectedHeap类定义了对象的分配和回收的接口。
### 重要方法
1. allocate_from_tlab
2. mem_allocate
3. collect

---

## CollectorPolicy
&nbsp;&nbsp;CollectorPolicy类是一个定义了对象管理功能策略的类。该类保存与对象管理功能相关的设置值，例如，该类在执行Java命令时设置不同的参数(如GC算法);