## invokeDynamic字节码指令
#### Java方法的构成
1. 方法名
2. 方法签名--这里指的是:参数列表+返回值
3. 定义方法的类
4. 方法体(代码)
#### 方法的调用
一次方法的调用，除了需要方法的实体之外，还需要方法的调用者(caller，即方法调用语句所在的类),和方法的接收者(reciver,一个对象，如隐藏的this。即xxx.方法名();，xxx就是一个方法调用时的接收者)。
#### 从代码中看方法的构成和方法的调用的接收者&调用者    
```java
package link.bosswang.study;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;

public class MethodHandle {

    static class ClassA {
        private String name = "  Wang";

        public void println(String s) {
            System.out.println(s + this.name);
        }
    }

    public static void main(String[] args) throws Throwable {

        getPrintlnMH(System.out).invokeExact("System.out Hello World"); //System.out Hello World

        getPrintlnMH(new ClassA()).invokeExact("ClassA Hello World"); // ClassA Hello World  Wang

    }

    private static java.lang.invoke.MethodHandle getPrintlnMH(Object reveiver) throws NoSuchMethodException, IllegalAccessException {

        //获取方法描述符
        /**
         * 在这里，MethodType:代表“方法的类型”，包含了方法的返回值(methodType()第一个参数代表方法的返回值)
         * 和具体的参数(methodType()方法第二个以及之后的参数)
         */
        MethodType methodType = MethodType.methodType(void.class, String.class);

        /**
         * MethodHandles.lookup():代表调用者,也就是当前类。调用者决定有没有权限去调用该方法
         */
        MethodHandles.Lookup lookup = MethodHandles.lookup();

        /**
         *loopup.findVirtual()的三个参数分别代表:定义方法的类，方法名，签名(描述符)
         */
        java.lang.invoke.MethodHandle mh = lookup.findVirtual(reveiver.getClass(), "println", methodType);

        /**
         * 确定方法的接收者
         *
         * 因为这里调用的是一个虚方法，按照Java语言规则，方法第一个参数是隐式的，代表该方法的接收者，也就是this指向的对象。这个参数之前是放在参数列表中的，
         * 而现在提供了bindTo方法来完成这件事情
         */
        java.lang.invoke.MethodHandle methodHandle = mh.bindTo(reveiver);

        return methodHandle;
    }


}
```
+  其实，方法getPrintlnMH()中模拟了invokevirtual指令的执行过程

#### MethodHandle与Reflection的区别
1. Reflection和MethodHandle机制都是在模拟方法的调用，但是Reflection是模拟Java代码层次的方法调用，而MethodHandle是在模拟字节码层次的方法调用。MethodHandle.loop中的三个方法分别对应了不同字节码指令的执行权限校验行为。
   + MethodHandle.lookup().findStatic  对应 invokestatic
   + MethodHandle.lookup().findVirtual 对应 invokevirtual & invokeinterface
   + MethodHandle.lookup().findSpecial 对应 invokeSpecial 
2. Reflection中的java.lang.reflect.Method 对象远比MethodHandle机制中的java.lang.invoke.MethodHandle对象包含的信息多。Reflection包含了方法的签名，描述符，方法属性表中各种属性的Java端表示方法，包含了执行权限等的运行期信息。而MethodHandle仅仅包含了与执行该方法相关的信息。即：Reflection是重量级，MethodHandle是轻量级
3. 由于MethodHandle是对字节码的方法调用的模拟，所以理论上虚拟机在这方面做的各种优化，在MethodHandle上也应当可以采用类似思路去支持
4. Reflection仅仅是为了给Java使用，MethodHandle是可服务于所有运行于Java虚拟机之上的语言
#### invokeDynamic指令
+ 在某种程度上，invokeDynamic指令与MethodHandle机制的作用是一样的，都是为了解决原有4条**invokeXX**指令方法分派规则固化在虚拟机之中的问题，把如何查找目标方法的决定权从虚拟机转嫁到具体用户代码之中，让用户(包含其他语言的设计者)有更高的自由度。
+ 或者说这样来理解:为了达到同一个目的，一个采用上层Java代码和API来实现;另一个则是用字节码指令和Class中的其他属性、常量来完成。
##### invokeDynamic指令原理
+ 每一处由invokedynamic指令的位置都称为动态调用点，这条字节码指令的第一个参数不再是代表该方法符号引用的CONSTANT_Methodref_info常量，而是CONSTANT_InvokeDynamic_info常量。从这个常量中可以得到信息:
1. 引导方法(Bootstrap Mehthod ，BSM),此方法存放在新增的BootstrapMethods属性中。
    - 引导方法有固定的参数，并且返回值是java.lang.invoke.CallSite对象，代表着真正要执行的目标方法调用。根据CONSTANT_InvokeDynamic_info常量提供的信息，虚拟机可以找到并执行引导方法，从而获得一个CallSite对象，最终调用要执行的方法。
2. 方法类型(MethodType)
3. 名称
#### 通过代码分析
##### 代码
```java
    package link.bosswang.morethread;

    /**
    * @author wei
    * @date 1/31/19  2:52 PM
    */
    public class Demo1 {
        public static void main(String[] args) throws InterruptedException, NoSuchMethodException {

            Thread thread = new Thread(()->{
                System.out.println("Hello World");
            });

            thread.start();
        }
    }
```
##### 字节码 ----字节码需要结合类文件结构来分析，才能真正弄清楚是什么意思
```java
Classfile /home/wei/WorkSpace/IntelliJ_IDEA/JVM/out/production/JVM/link/bosswang/morethread/Demo1.class
  Last modified Dec 19, 2019; size 1351 bytes
  MD5 checksum be97e1a383187a1165d1dccea972d9bb
  Compiled from "Demo1.java"
public class link.bosswang.morethread.Demo1
  minor version: 0
  major version: 52
  flags: ACC_PUBLIC, ACC_SUPER
Constant pool:
   #1 = Methodref          #10.#30        // java/lang/Object."<init>":()V
   #2 = Class              #31            // java/lang/Thread
   #3 = InvokeDynamic      #0:#36         // #0:run:()Ljava/lang/Runnable;   //对应注解1
   #4 = Methodref          #2.#37         // java/lang/Thread."<init>":(Ljava/lang/Runnable;)V
   #5 = Methodref          #2.#38         // java/lang/Thread.start:()V
   #6 = Fieldref           #39.#40        // java/lang/System.out:Ljava/io/PrintStream;
   #7 = String             #41            // Hello World
   #8 = Methodref          #42.#43        // java/io/PrintStream.println:(Ljava/lang/String;)V
   #9 = Class              #44            // link/bosswang/morethread/Demo1
  #10 = Class              #45            // java/lang/Object
  #11 = Utf8               <init>
  #12 = Utf8               ()V
  #13 = Utf8               Code
  #14 = Utf8               LineNumberTable
  #15 = Utf8               LocalVariableTable
  #16 = Utf8               this
  #17 = Utf8               Llink/bosswang/morethread/Demo1;
  #18 = Utf8               main
  #19 = Utf8               ([Ljava/lang/String;)V
  #20 = Utf8               args
  #21 = Utf8               [Ljava/lang/String;
  #22 = Utf8               thread
  #23 = Utf8               Ljava/lang/Thread;
  #24 = Utf8               Exceptions
  #25 = Class              #46            // java/lang/InterruptedException
  #26 = Class              #47            // java/lang/NoSuchMethodException
  #27 = Utf8               lambda$main$0
  #28 = Utf8               SourceFile
  #29 = Utf8               Demo1.java
  #30 = NameAndType        #11:#12        // "<init>":()V
  #31 = Utf8               java/lang/Thread
  #32 = Utf8               BootstrapMethods
  #33 = MethodHandle       #6:#48         // invokestatic java/lang/invoke/LambdaMetafactory.metafactory:(Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodHandle;Ljava/lang/invoke/MethodType;)Ljava/lang/invoke/CallSite;
  #34 = MethodType         #12            //  ()V
  #35 = MethodHandle       #6:#49         // invokestatic link/bosswang/morethread/Demo1.lambda$main$0:()V
  #36 = NameAndType        #50:#51        // run:()Ljava/lang/Runnable;
  #37 = NameAndType        #11:#52        // "<init>":(Ljava/lang/Runnable;)V
  #38 = NameAndType        #53:#12        // start:()V
  #39 = Class              #54            // java/lang/System
  #40 = NameAndType        #55:#56        // out:Ljava/io/PrintStream;
  #41 = Utf8               Hello World
  #42 = Class              #57            // java/io/PrintStream
  #43 = NameAndType        #58:#59        // println:(Ljava/lang/String;)V
  #44 = Utf8               link/bosswang/morethread/Demo1
  #45 = Utf8               java/lang/Object
  #46 = Utf8               java/lang/InterruptedException
  #47 = Utf8               java/lang/NoSuchMethodException
  #48 = Methodref          #60.#61        // java/lang/invoke/LambdaMetafactory.metafactory:(Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodHandle;Ljava/lang/invoke/MethodType;)Ljava/lang/invoke/CallSite;
  #49 = Methodref          #9.#62         // link/bosswang/morethread/Demo1.lambda$main$0:()V
  #50 = Utf8               run
  #51 = Utf8               ()Ljava/lang/Runnable;
  #52 = Utf8               (Ljava/lang/Runnable;)V
  #53 = Utf8               start
  #54 = Utf8               java/lang/System
  #55 = Utf8               out
  #56 = Utf8               Ljava/io/PrintStream;
  #57 = Utf8               java/io/PrintStream
  #58 = Utf8               println
  #59 = Utf8               (Ljava/lang/String;)V
  #60 = Class              #63            // java/lang/invoke/LambdaMetafactory
  #61 = NameAndType        #64:#68        // metafactory:(Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodHandle;Ljava/lang/invoke/MethodType;)Ljava/lang/invoke/CallSite;
  #62 = NameAndType        #27:#12        // lambda$main$0:()V
  #63 = Utf8               java/lang/invoke/LambdaMetafactory
  #64 = Utf8               metafactory
  #65 = Class              #70            // java/lang/invoke/MethodHandles$Lookup
  #66 = Utf8               Lookup
  #67 = Utf8               InnerClasses
  #68 = Utf8               (Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodHandle;Ljava/lang/invoke/MethodType;)Ljava/lang/invoke/CallSite;
  #69 = Class              #71            // java/lang/invoke/MethodHandles
  #70 = Utf8               java/lang/invoke/MethodHandles$Lookup
  #71 = Utf8               java/lang/invoke/MethodHandles
{
  public link.bosswang.morethread.Demo1();
    descriptor: ()V
    flags: ACC_PUBLIC
    Code:
      stack=1, locals=1, args_size=1
         0: aload_0
         1: invokespecial #1                  // Method java/lang/Object."<init>":()V
         4: return
      LineNumberTable:
        line 13: 0
      LocalVariableTable:
        Start  Length  Slot  Name   Signature
            0       5     0  this   Llink/bosswang/morethread/Demo1;

  public static void main(java.lang.String[]) throws java.lang.InterruptedException, java.lang.NoSuchMethodException;
    descriptor: ([Ljava/lang/String;)V
    flags: ACC_PUBLIC, ACC_STATIC
    Code:
      stack=3, locals=2, args_size=1
         0: new           #2                  // class java/lang/Thread
         3: dup
         4: invokedynamic #3,  0              // InvokeDynamic #0:run:()Ljava/lang/Runnable;    // (#0代表BSM，即BootStrapMethod)
         9: invokespecial #4                  // Method java/lang/Thread."<init>":(Ljava/lang/Runnable;)V
        12: astore_1
        13: aload_1
        14: invokevirtual #5                  // Method java/lang/Thread.start:()V
        17: return
      LineNumberTable:
        line 16: 0
        line 20: 13
        line 21: 17
      LocalVariableTable:
        Start  Length  Slot  Name   Signature
            0      18     0  args   [Ljava/lang/String;
           13       5     1 thread   Ljava/lang/Thread;
    Exceptions:
      throws java.lang.InterruptedException, java.lang.NoSuchMethodException
}
SourceFile: "Demo1.java"
InnerClasses:
     public static final #66= #65 of #69; //Lookup=class java/lang/invoke/MethodHandles$Lookup of class java/lang/invoke/MethodHandles
BootstrapMethods:
  0: #33 invokestatic java/lang/invoke/LambdaMetafactory.metafactory:(Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodHandle;Ljava/lang/invoke/MethodType;)Ljava/lang/invoke/CallSite;
    Method arguments:
      #34 ()V
      #35 invokestatic link/bosswang/morethread/Demo1.lambda$main$0:()V
      #34 ()V
```
###### 注解
1. #3 = InvokeDynamic      #0:#36 ， 从CONSTANT_InvokeDynamic_info常量中可以得到三样信息
   - 引导方法
   - 方法类型
   - 方法名
   - 从常量池来看，#36代表方法的类型和名称，所以#0则代表引导方法，对应着第0个引导方法。
2.  #33 = MethodHandle       #6:#48         // invokestatic java/lang/invoke/LambdaMetafactory.metafactory:(Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodHandle;Ljava/lang/invoke/MethodType;)Ljava/lang/invoke/CallSite;
+ 这里的invokestatic字节码指令存在与CONSTANT_MethodHandle_info常量的 reference_kind中，代表了**方法句柄的字节码行为** 

#### MethodHandle与CallSite
+ 每一个CallSite（可以阅读代码：ConstantCallSite）都会绑定一个MehtodHandle，且不可变
#### 使用代码来解释invokeDynamic这个过程
```java
package link.bosswang.morethread;

import java.lang.invoke.CallSite;
import java.lang.invoke.ConstantCallSite;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;

public class Demo {
    public static void main(String[] args) throws Throwable {
        INDY_BootstrapMethod().invokeExact("Hello World");
    }

    public static void testMethod(String s) {
        System.out.println("Hello String: " + s);
    }

    /**
     * 引导方法，这里的三个参数是如何来的？
     * ---> 由方法INDY_BootstrapMethod中调用方法invokeWithArguments提供
     *
     * @param lookup     方法调用者
     * @param name       方法名
     * @param methodType 方法类型
     * @return
     * @throws Throwable
     */
    public static CallSite BootstrapMethod(MethodHandles.Lookup lookup, String name, MethodType methodType) throws Throwable {
        return new ConstantCallSite(lookup.findStatic(Demo.class, name, methodType));
    }

    private static java.lang.invoke.MethodHandle INDY_BootstrapMethod() throws Throwable {

        CallSite callSite = (CallSite) MH_BootstrapMethod().invokeWithArguments(MethodHandles.lookup(),
                "testMethod", MethodType.fromMethodDescriptorString("(Ljava/lang/String;)V", null));
        return callSite.dynamicInvoker();
    }

    /**
     * 获取引导方法句柄BSM
     *
     * @return 引导方法的方法句柄
     * @throws Throwable
     */
    private static java.lang.invoke.MethodHandle MH_BootstrapMethod() throws Throwable {
        java.lang.invoke.MethodHandle bootstrapMethod = MethodHandles.lookup().findStatic(Demo.class, "BootstrapMethod", MT_BootstrapMethod());
        return bootstrapMethod;
    }

    /**
     * 引导方法的方法类型
     *
     * @return 引导方法类型
     */
    private static MethodType MT_BootstrapMethod() {
        MethodType methodType = MethodType.fromMethodDescriptorString("(Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;" +
                "Ljava/lang/invoke/MethodType;)Ljava/lang/invoke/CallSite;", null);
        return methodType;
    }
}
```