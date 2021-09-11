package link.bosswang.prototype;

import java.text.MessageFormat;

public class MailUtil {

    /**
     * @description: `发送邮件`
     * @param: mail
     * @Return 'void'
     * @By Wei.Wang
     * @date 2021/9/11 上午9:30
     */
    public static void sendMail(Mail mail) {
        String outputContext = "向{0}同学，邮件地址{1},邮件内容:{1} 发送成功";
        System.out.println(MessageFormat.format(outputContext, mail.getName()
                , mail.getEmailAddress(), mail.getContext()));
    }


    /**
     * @description: `存储原始邮件模板`
     * @param: mail
     * @Return 'void'
     * @By Wei.Wang
     * @date 2021/9/11 上午9:32
     */
    public static void saveOriginMailRecord(Mail mail) {
        System.out.println("存储原始邮件，originMail: " + mail.getContext());
    }

}
