package xyz.easyboot.demo;

import org.springframework.beans.BeanUtils;

/**
 * @author wujiawei0926@yeah.net
 * @see
 * @since 2020/5/15
 */
public class SpringBeanUtilsDemo {

    public static void main(String[] args) {
        for (int j = 0; j < 5; j++) {
            long start = System.currentTimeMillis();
            for (int i = 0; i < 10000000; i++) {
                FromVo from = FromVo.create();
                ToVo to = new ToVo();
                BeanUtils.copyProperties(from, to);
            }
            long end = System.currentTimeMillis();
            System.out.println(end - start);
        }
    }

}
