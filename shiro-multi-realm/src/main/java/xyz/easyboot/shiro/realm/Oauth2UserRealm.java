package xyz.easyboot.shiro.realm;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.stereotype.Component;
import xyz.easyboot.model.User;
import xyz.easyboot.service.UserDao;
import xyz.easyboot.shiro.token.OAuth2UserToken;

/**
 * @author wujiawei0926@yeah.net
 * @see
 * @since 2020/5/29
 */
@Component
public class Oauth2UserRealm extends AuthorizingRealm {
    public static final String REALM_NAME = "oauth2_user_realm";

    private final UserDao userDao;

    public Oauth2UserRealm(UserDao userDao) {
        this.userDao = userDao;
    }

    @Override
    public String getName() {
        return REALM_NAME;
    }

    /**
     * 检查是否支持该Realm
     * 一定要重写support()方法，在后面的身份验证器中会用到
     * @param token
     * @return
     */
    @Override
    public boolean supports(AuthenticationToken token) {
        return token instanceof OAuth2UserToken;
    }

    /**
     * 授权
     * @param principalCollection
     * @return
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        User user = (User) SecurityUtils.getSubject().getPrincipal();
        SimpleAuthorizationInfo authorizationInfo = new SimpleAuthorizationInfo();
        return authorizationInfo;
    }

    /**
     * 认证
     * 在这个方法中，完成老用户的登录与新用户的注册
     * @param authenticationToken
     * @return
     * @throws AuthenticationException
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        OAuth2UserToken token = (OAuth2UserToken)authenticationToken;
        OAuth2UserToken.OAuth2User oAuth2User = token.getUser();

        // 校验openid
        if (oAuth2User == null) {
            throw new AuthenticationException();
        }

        // 根据openid查询用户数据
        String openid = oAuth2User.getOpenid();
        User user = null;
        switch (token.getType()) {
            case "qq":
                user = userDao.findByQqOpenid(openid);
                break;
            case "weixin":
                user = userDao.findByWxOpenid(openid);
                break;
            default:
                break;
        }

        if (user == null) {
            // TODO 获取oAuth2User中用户信息进行注册
        }

        // 完成登录，注意这里传的principal和credentials
        // principal: OAuth2UserToken类中getPrincipal()的返回值
        // credentials: OAuth2UserToken类中getCredentials()的返回值
        SimpleAuthenticationInfo authenticationInfo = new SimpleAuthenticationInfo(user, openid, getName());
        return authenticationInfo;
    }
}
