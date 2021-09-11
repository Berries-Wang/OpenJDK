package link.bosswang.prototype;


/**
 * 原型模式
 */
public class Mail implements Cloneable {

    private String name;
    private String emailAddress;
    private String context;

    public Mail() {
        System.out.println("Mail Class Constructor");
    }

    /**
     * @description: `原型模式`
     * @Return 'java.lang.Object'
     * @By Wei.Wang
     * @date 2021/9/11 上午9:23
     */
    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }
}
