package link.bosswang.prototype;

public class Main {
    public static void main(String[] args) throws CloneNotSupportedException {
        Mail mail = new Mail();
        mail.setContext("初始化模板");

        for (int i = 0; i < 10; i++) {

            // 使用克隆模式，避免重复 new 对象
            Mail mailTemp = (Mail) mail.clone();

            mailTemp.setName("姓名: " + i);
            mailTemp.setEmailAddress("姓名:" + i + "@xxx.com");
            mailTemp.setContext("恭喜你，中奖了");

            MailUtil.sendMail(mail);
        }

        MailUtil.saveOriginMailRecord(mail);
    }

    // 输出如下
    /**
     *
     * Mail Class Constructor
     * 向null同学，邮件地址null,邮件内容:null 发送成功
     * 向null同学，邮件地址null,邮件内容:null 发送成功
     * 向null同学，邮件地址null,邮件内容:null 发送成功
     * 向null同学，邮件地址null,邮件内容:null 发送成功
     * 向null同学，邮件地址null,邮件内容:null 发送成功
     * 向null同学，邮件地址null,邮件内容:null 发送成功
     * 向null同学，邮件地址null,邮件内容:null 发送成功
     * 向null同学，邮件地址null,邮件内容:null 发送成功
     * 向null同学，邮件地址null,邮件内容:null 发送成功
     * 向null同学，邮件地址null,邮件内容:null 发送成功
     * 存储原始邮件，originMail: 初始化模板
     *
     */
}
