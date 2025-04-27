# Oop
Java对象用oop来表示，在Java创建对象的时候创建。也就是说，在Java应用程序运行过程中每创建一个Java对象，在HotSpot VM内部都会创建一个oop实例来表示Java对象。


## 对象属性存储与访问<sub>通过偏移获取</sub>
**Java对象的header信息可以存储在oopdesc类定义的_mark _metadata属性中，而Java对象的fields没有在oopDesc中定义相应的属性来存储，因此，只能申请一块连续的内存空间，然后按照一定的布局规则进行存储。对象字段存放在紧跟着oopdesc实例本身占用的内存空间之后，在获取时只能通过偏移来获取**