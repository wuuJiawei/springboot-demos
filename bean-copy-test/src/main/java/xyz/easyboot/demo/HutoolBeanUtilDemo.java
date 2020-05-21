package xyz.easyboot.demo;

import cn.hutool.core.bean.BeanUtil;

/**
 * @author wujiawei0926@yeah.net
 * @see
 * @since 2020/5/21
 */
public class HutoolBeanUtilDemo {

    public static void main(String[] args) {
        for (int j = 0; j < 5; j++) {
            long start = System.currentTimeMillis();
            for (int i = 0; i < 10000000; i++) {
                FromVo from = FromVo.create();
                ToVo to = new ToVo();
                BeanUtil.copyProperties(from, to);
            }
            long end = System.currentTimeMillis();
            System.out.println(end - start);
        }
    }

}
