package xyz;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.mail.MailAccount;
import cn.hutool.extra.mail.MailUtil;

import java.util.Collection;

/**
 * @author wujiawei
 * @see
 * @since 2022/7/26 10:33
 */
public class MailSender {
    
    public void send(Param param) {
        MailAccount mailAccount = new MailAccount();
        mailAccount.setHost(param.fromHost);
        mailAccount.setFrom(param.fromMail);
        mailAccount.setUser(param.fromUser);
        mailAccount.setPass(param.fromPass);
        mailAccount.setSslEnable(true);
    
        Collection<String> tos = StrUtil.split(param.toMail, ',');
        Collection<String> ccs = StrUtil.split(param.toCcMail, ',');
        MailUtil.send(mailAccount,  tos, ccs,null, param.subject, param.content, true);
    }
    
    public static class Param {
        String fromHost;
        String fromMail;
        String fromUser;
        String fromPass;
        String toMail;
        String toCcMail;
        String subject;
        String content;
    
        public String getFromHost() {
            return fromHost;
        }
    
        public void setFromHost(String fromHost) {
            this.fromHost = fromHost;
        }
    
        public String getFromMail() {
            return fromMail;
        }
    
        public void setFromMail(String fromMail) {
            this.fromMail = fromMail;
        }
    
        public String getFromUser() {
            return fromUser;
        }
    
        public void setFromUser(String fromUser) {
            this.fromUser = fromUser;
        }
    
        public String getFromPass() {
            return fromPass;
        }
    
        public void setFromPass(String fromPass) {
            this.fromPass = fromPass;
        }
    
        public String getToMail() {
            return toMail;
        }
    
        public void setToMail(String toMail) {
            this.toMail = toMail;
        }
    
        public String getToCcMail() {
            return toCcMail;
        }
    
        public void setToCcMail(String toCcMail) {
            this.toCcMail = toCcMail;
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
