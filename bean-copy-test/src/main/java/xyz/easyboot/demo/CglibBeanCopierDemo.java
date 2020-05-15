package xyz.easyboot.demo;

import net.sf.cglib.beans.BeanCopier;

/**
 * @author wujiawei0926@yeah.net
 * @see
 * @since 2020/5/15
 */
public class CglibBeanCopierDemo {

    public static void main(String[] args) {
        final BeanCopier copier = BeanCopier.create(FromVo.class, ToVo.class, false);
        for (int j = 0; j < 5; j++) {
            long start = System.currentTimeMillis();
            for (int i = 0; i < 1000001100; i++) {
                FromVo from = FromVo.create();
                ToVo to = new ToVo();
                copier.copy(from, to, null);
            }
            long end = System.currentTimeMillis();
            System.out.println(end - start);
        }
    }

}
