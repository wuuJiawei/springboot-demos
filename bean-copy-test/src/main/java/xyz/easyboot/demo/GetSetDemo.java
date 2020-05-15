package xyz.easyboot.demo;

/**
 * @author wujiawei0926@yeah.net
 * @see
 * @since 2020/5/15
 */
public class GetSetDemo {

    public static void main(String[] args) {
        for (int j = 0; j < 5; j++) {
            long start = System.currentTimeMillis();
            for (int i = 0; i < 10000000; i++) {
                FromVo from = FromVo.create();
                ToVo to = new ToVo();
                to.setDate(from.getDate());
                to.setId(from.getId());
                to.setMoney(from.getMoney());
                to.setName(from.getName());
            }
            long end = System.currentTimeMillis();
            System.out.println(end - start);
        }
    }

}
