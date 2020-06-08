package xyz.easyboot.service;

import org.springframework.stereotype.Service;
import xyz.easyboot.model.User;

/**
 * @author wujiawei0926@yeah.net
 * @see
 * @since 2020/5/29
 */
@Service
public class UserDao {

    public User findByQqOpenid(String openid) {
        User user = newUser();
        user.setQqOpenId(openid);
        return user;
    }

    public User newUser(){
        User user = new User();
        user.setId("id");
        user.setUsername("用户和虎门");
        return user;
    }

    public User findByWxOpenid(String openid) {
        User user = newUser();
        user.setWxOpenId(openid);
        return user;
    }
}
