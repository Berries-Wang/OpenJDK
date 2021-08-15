package link.bosswang.singleton;

/**
 * 使用枚举来实现单例模式，既可以防止序列化也可以防止反射攻击<br/>
 * 
 * JDK 不允许使用反射来创建枚举实例 <br/>
 */
public enum EnumInstance {

    INSTANCE;

    private Object data;

    private Object getData() {
        return this.data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public static EnumInstance getInstance() {
        return INSTANCE;
    }

}