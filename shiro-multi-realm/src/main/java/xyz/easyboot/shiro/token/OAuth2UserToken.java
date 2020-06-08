package xyz.easyboot.shiro.token;

import org.apache.shiro.authc.AuthenticationToken;

/**
 * 第三方授权登录凭证
 * 注意这里要实现AuthenticationToken，不能继承UsernamePasswordToken
 * 同时重写getPrincipal()和getCredentials()两个方法
 * @author wujiawei0926@yeah.net
 */
public class OAuth2UserToken implements AuthenticationToken {

    /**
     *  授权类型
     *  这里可以使用枚举
     */
    private String type;

    // 第三方登录后获取的用户信息
    private OAuth2User user;

    public OAuth2UserToken(final String type, final OAuth2User user) {
        this.type = type;
        this.user = user;
    }

    @Override
    public Object getPrincipal() {
        return this.getUser();
    }

    @Override
    public Object getCredentials() {
        return this.getUser().getOpenid();
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public OAuth2User getUser() {
        return user;
    }

    public void setUser(OAuth2User user) {
        this.user = user;
    }

    /**
     * 用户信息类，用于新用户注册
     * 可根据自己的具体业务进行拓展
     */
    public static class OAuth2User {

        public OAuth2User(){};

        private String openid;
        private String username;
        private String nickname;
        private String avatar;
        private String email;
        private String remark;
        private Integer sex;

        public String getOpenid() {
            return openid;
        }

        public void setOpenid(String openid) {
            this.openid = openid;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getNickname() {
            return nickname;
        }

        public void setNickname(String nickname) {
            this.nickname = nickname;
        }

        public String getAvatar() {
            return avatar;
        }

        public void setAvatar(String avatar) {
            this.avatar = avatar;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getRemark() {
            return remark;
        }

        public void setRemark(String remark) {
            this.remark = remark;
        }

        public Integer getSex() {
            return sex;
        }

        public void setSex(Integer sex) {
            this.sex = sex;
        }
    }

}
