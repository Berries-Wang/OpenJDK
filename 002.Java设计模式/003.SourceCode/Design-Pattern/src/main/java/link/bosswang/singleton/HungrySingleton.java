package link.bosswang.singleton;

/**
 * 饿汉式单例模式
 */
public final class HungrySingleton {

    private final static HungrySingleton INSTANCE = new HungrySingleton();

    private HungrySingleton() {

        /**
         * 防止反射破坏单例模式,只有在特定模式(即饿汉式下才生效，对于懒汉式则不生效，如果要解决这个问题，则只能使用枚举来实现单例模式)下生效
         */
        if (null == INSTANCE) {
            throw new RuntimeException("单例模式防止反射攻击");
        }

    }

    public static HungrySingleton getInstance() {
        return HungrySingleton.INSTANCE;
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
        return HungrySingleton.INSTANCE;
    }

}
