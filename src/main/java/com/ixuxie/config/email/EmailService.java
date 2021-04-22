package com.ixuxie.config.email;

import cn.hutool.extra.mail.Mail;
import cn.hutool.extra.mail.MailAccount;
import com.ixuxie.exception.ApiRuntimeException;
import com.ixuxie.utils.InfoCode;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class EmailService {

    public void send(String email, String subject, String content) {
        //EmailVo
        EmailVo emailVo = new EmailVo();
        emailVo.setSubject(subject);
        emailVo.setContent(content);
        List<String> tos = new ArrayList<>();
        tos.add(email);
        emailVo.setTos(tos);
        send(emailVo);
    }


    public void send(EmailVo emailVo) {
        /**
         * 封装
         */
        /**
         * 封装
         */
        MailAccount account = new MailAccount();
        account.setHost(EmailConfig.host);
        account.setPort(EmailConfig.port);
        account.setAuth(true);
        try {
            // 对称解密
            account.setPass(EmailConfig.pass);
         //   account.setPass(EncryptUtils.desDecrypt(EmailConfig.pass));
        } catch (Exception e) {
            throw new ApiRuntimeException(InfoCode.EMAIL_SEND_FAIL,e.getMessage());
        }
        //TODO 为啥要拼在一起分开则不行
        account.setFrom(EmailConfig.user + "<" + EmailConfig.fromMail + ">");
        //ssl方式发送
        account.setSslEnable(true);
        account.setDebug(true);
        String content = emailVo.getContent();
        /**
         * 发送
         */
        try {
            Mail.create(account)
                    .setTos(emailVo.getTos().toArray(new String[emailVo.getTos().size()]))
                    .setTitle(emailVo.getSubject())
                    .setContent(content)
                    .setHtml(true)
                    //关闭session
                    .setUseGlobalSession(false)
                    .send();
        } catch (Exception e) {
            e.printStackTrace();
            throw new ApiRuntimeException(InfoCode.EMAIL_SEND_FAIL,e.getMessage());
        }
    }

    public void sendSync(EmailVo emailVo) {
        send(emailVo);
    }


    class EmailVo{

        /**
         * 收件人，支持多个收件人，用逗号分隔
         */
        private List<String> tos;

        /**
         * 邮件标题
         */
        private String subject;
        /**
         * 邮件内容
         */
        private String content;

        public List<String> getTos() {
            return tos;
        }

        public void setTos(List<String> tos) {
            this.tos = tos;
        }

        public String getSubject() {
            return subject;
        }

        public void setSubject(String subject) {
            this.subject = subject;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }
    }
}
