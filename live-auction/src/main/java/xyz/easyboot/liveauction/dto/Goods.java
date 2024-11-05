package xyz.easyboot.liveauction.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
@Accessors(chain = true)
public class Goods {

    private String id;
    private String name;
    private String description;
    private BigDecimal salePrice;
    private List<String> imageUrlList;

    //TODO 考虑支持定时开启竞拍
    private Date startTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date endTime;

    // 商品状态
    private Boolean sold;

    // 最高出价
    private BigDecimal highestBidAmount;

    // 最高出价人
    private String highestBidder;

}
