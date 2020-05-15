package xyz.easyboot.demo;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.lang.reflect.InvocationTargetException;

/**
 * @author wujiawei0926@yeah.net
 * @see
 * @since 2020/5/15
 */
public class ApacheBeanUtilsDemo {

    public static void main(String[] args) throws InvocationTargetException, IllegalAccessException {
        for (int j = 0; j < 5; j++) {
            long start = System.currentTimeMillis();
            for (int i = 0; i < 1000000; i++) {
                FromVo from = FromVo.create();
                ToVo to = new ToVo();
                BeanUtils.copyProperties(to, from);
            }
            long end = System.currentTimeMillis();
            System.out.println("processing: " + (end - start));
            System.out.println(".");
        }

    }

}
