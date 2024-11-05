package xyz.easyboot.generator.demo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class DemoEntity {

    private Long id;

    private Date createTime;

    private BigDecimal money;

}
