package xyz.easyboot.model;

import java.io.Serializable;

/**
 * @author wujiawei0926@yeah.net
 * @see
 * @since 2020/5/29
 */
public class User implements Serializable {

    private String id;

    private String username;

    private String qqOpenId;

    private String wxOpenId;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getQqOpenId() {
        return qqOpenId;
    }

    public void setQqOpenId(String qqOpenId) {
        this.qqOpenId = qqOpenId;
    }

    public String getWxOpenId() {
        return wxOpenId;
    }

    public void setWxOpenId(String wxOpenId) {
        this.wxOpenId = wxOpenId;
    }
}
