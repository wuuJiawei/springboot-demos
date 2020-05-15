package xyz.easyboot.demo;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @author wujiawei0926@yeah.net
 * @see
 * @since 2020/5/15
 */
public class FromVo {

    public static FromVo create(){
        FromVo vo = new FromVo();
        vo.setDate(LocalDateTime.now());
        vo.setMoney(new BigDecimal(10000));
        vo.setId(9999);
        vo.setName("演示");
        return vo;
    }

    private String name;

    private Integer id;

    private LocalDateTime date;

    private BigDecimal money;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public BigDecimal getMoney() {
        return money;
    }

    public void setMoney(BigDecimal money) {
        this.money = money;
    }
}
