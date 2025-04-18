package link.bosswang.singleton;

/**
 * 静态内部类实现的单例模式(基于类初始化)
 */
public final  class StaticInnerClassSingleton {

    /**
     * 依赖于JVM class初始化所加的锁来防止初始化多个实例
     */
    private static class InnerClass {
        private static StaticInnerClassSingleton INSTANCE = new StaticInnerClassSingleton();

        /**
         * 
         * 方式序列化破坏单例模式<br/>
         * 
         * 为什么这个方法可以，详见如下代码<br/>
         * 005.OpenJDK/000.openJDK_8u40/jdk/src/share/classes/java/io/ObjectInputStream.java
         * 
         * @return
         */
        public Object readResolve() {
            return InnerClass.INSTANCE;
        }
    }

    private StaticInnerClassSingleton() {

    }

    public static StaticInnerClassSingleton getInstance() {
        return InnerClass.INSTANCE;
    }

}
