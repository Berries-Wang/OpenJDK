# Klass(C++) 和 Class(Java)
> 阅读[深入剖析Java虚拟机#第二章](../../006.BOOKs/深入剖析Java虚拟机.epub)

## 摘要
+ HotSpot 使用Klass表示Java类；使用oop表示Java对象;
+ Java类中可能定义了静态或非静态字段，因此将非静态字段值存储在oop中，静态字段值存储在表示[当前Java类的java.lang.Class对象]中
   ```txt
      静态字段值存储在java.lang.Class对应的oop对象中. 2.1.3 InstanceKlass的子类
   ```
+ 静态字段如何保存的？[klass.hpp#_java_mirror;](../../005.OpenJDK/002.OpenJDK8u312-GA/OpenJDK8U312-GA/hotspot/src/share/vm/oops/klass.hpp)
+ 数组的元组类型和元素类型:[005.OpenJDK/002.OpenJDK8u312-GA/OpenJDK8U312-GA/hotspot/src/share/vm/oops/arrayKlass.hpp](../../005.OpenJDK/002.OpenJDK8u312-GA/OpenJDK8U312-GA/hotspot/src/share/vm/oops/arrayKlass.hpp)
   ```txt
      元素类型: 数组元素类型（Element Type）指的是数组去掉所有维度的类型
      元组类型: 而数组的组件类型（Component Type）指的是数组去掉一个维度的类型
   ```
+ 数组类创建
   ```c++
        // openjdk/hotspot/src/share/vm/oops/klass.hpp
        Klass* array_klass(int rank, TRAPS) 
        
        // 多维数组类创建逻辑
        Klass* InstanceKlass::array_klass_impl(instanceKlassHandle this_oop, bool or_null, int n, TRAPS);

   ```

---

##  Klass(C++) 和 Class(Java) 对应关系
|C++ Klass|Java Class||
|-|-|-|
|InstanceMirrorKlass|java.lang.Class||
|InstanceRefKlass|java.lang.ref.Reference|通过_reference_type可以将普通类与引用类型区分开，因为引用类型需要垃圾收集器进行特殊处理。|
|InstanceKlass|一个具体的Java类型（这里的Java类型不包括Java数组类型）||
|InstanceClassLoaderKlass|java.lang.ClassLoader|InstanceClassLoaderKlass类没有添加新的字段，但增加了新的oop遍历方法，在垃圾回收阶段遍历类加载器加载的所有类来标记引用的所有对象|
|ArrayKlass||ArrayKlass类继承自Klass类，是所有数组类的抽象基类|
|TypeArrayKlass||表示数组组件类型是Java基本类型|
|ObjArrayKlass||ObjArrayKlass是ArrayKlass的子类，其属性用于判断数组元素是类还是数组|