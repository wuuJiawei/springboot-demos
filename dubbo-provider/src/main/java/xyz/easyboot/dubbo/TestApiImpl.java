package xyz.easyboot.dubbo;

import org.apache.dubbo.config.annotation.Service;

import java.util.UUID;

/**
 *
 * 接口实现类
 *
 * 注意这里注解@Service指的是{@link org.apache.dubbo.config.annotation.Service}下的注解
 *
 * @author wujiawei0926@yeah.net
 * @see
 * @since 2020/5/14
 */
@Service
public class TestApiImpl implements ITestApi{


    @Override
    public TestVo print(String str) {
        TestVo vo = new TestVo();
        vo.setId(UUID.randomUUID().toString());
        vo.setName(str);
        return vo;
    }
}
