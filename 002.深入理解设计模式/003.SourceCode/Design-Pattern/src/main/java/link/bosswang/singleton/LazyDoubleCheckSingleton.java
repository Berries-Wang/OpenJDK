package link.bosswang.singleton;

/**
 * 
 * 双重校验单例模式 - 懒汉式单例模式 <br/>
 * 
 * 无法防止反射攻击
 * 
 */
public final class LazyDoubleCheckSingleton {

    /**
     * 保存该类的单例实例 </br>
     * ___使用volatile来防止指令重排序
     */
    private volatile static LazyDoubleCheckSingleton INSTANCE = null;

    /**
     * 构造函数私有
     */
    private LazyDoubleCheckSingleton() {
    }

    /**
     * 单例模式-双重锁校验 </br>
     * 1. 为什么需要双重校验 </br>
     * _____多线程并发问题，可能会引起实例化多个实例的问题 </br>
     * 2. 为什么需要使用volatile </br>
     * _____防止指令重排序 - volatile
     */
    public static LazyDoubleCheckSingleton getInstance() {

        if (null == LazyDoubleCheckSingleton.INSTANCE) {
            synchronized (LazyDoubleCheckSingleton.class) {
                if (null == LazyDoubleCheckSingleton.INSTANCE) {
                    LazyDoubleCheckSingleton.INSTANCE = new LazyDoubleCheckSingleton();
                }
            }
        }

        return LazyDoubleCheckSingleton.INSTANCE;

    }

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
        return LazyDoubleCheckSingleton.INSTANCE;
    }

}