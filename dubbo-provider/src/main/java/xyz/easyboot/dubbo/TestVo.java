package xyz.easyboot.dubbo;

import java.io.Serializable;

/**
 * @author wujiawei0926@yeah.net
 * @see
 * @since 2020/5/14
 */
public class TestVo implements Serializable {

    private String id;

    private String name;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
