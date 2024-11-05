package xyz.easyboot.generator.demo;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.math.BigDecimal;

/**
 * 直播间商品
 * @author code-generator
 */
@TableName(value ="live_goods")
@Data
public class LiveGoods implements Serializable {

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;


	/**  */
	@TableId(type = IdType.AUTO)
	private Long id;

	/** 商户id */
	private Long orgId;

	/** 直播间id */
	private Long liveRoomId;

	/** 拍号，唯一且固定不变 */
	private String liveGoodsCode;

	/** ERP商品ID */
	private Long globalGoodsId;

	/** 商品状态: 10-待上架, 20-上架中, 30-竞拍中, 40-已流拍, 50-已成交 */
	private Integer goodsStatus;

	/** 结算状态: 00-待结算, 10-结算中, 20-已结算 */
	private Integer settleStatus;

	/** 起拍价 */
	private BigDecimal startPrice;

	/** 加价幅度 */
	private BigDecimal increasePrice;

	/** 竞拍时长，上架时设置 */
	private Integer auctionDuration;

	/** 实际竞拍时长 */
	private Integer actualAuctionDuration;

	/** 上架时间 */
	private Date putawayAt;

	/** 竞拍开始时间 */
	private Date auctionStartAt;

	/** 商品结束时间，含流拍、成交、撤回 */
	private Date endAt;

	/** 售出价格 */
	private BigDecimal sellPrice;

	/** 成交人 */
	private Long belongUserId;

	/** 成交人席位 */
	private Long belongSeatId;

	/** 买手商户id */
	private Long belongOrgId;

	/** 成交类型：0-竞拍成交，10-还价成交，20-传送成交 */
	private Integer belongType;

	/**  */
	private Boolean transactionMethod;

	/** 商品排序号: 初始化时值为拍号 */
	private Integer sort;

	/** 预约感兴趣数量 */
	private Integer subscribeCount;

	/** 导播备注，仅导播和主播可见 */
	private String directorRemark;

	/** 传送记录id */
	private Long transferRecordId;

	/** 直播回放地址 */
	private String liveVideoUrl;

	/** 是否同步到云展，0-未同步，1-已同步 */
	private Boolean ifSyncCe;

	/** 同步到云展时间 */
	private Date syncCeAt;

	/** 是否是即拍即上商品 0-不是， 1-是 */
	private Boolean ifOffhandGoods;

	/** 创建时间 */
	@TableField(fill = FieldFill.INSERT)
	private Date createdAt;

	/** 更新时间 */
	@TableField(fill = FieldFill.INSERT_UPDATE)
	private Date updatedAt;

	/** 删除时间 */
	@TableLogic
	private Date deletedAt;

	/** 创建人 */
	@TableField(fill = FieldFill.INSERT)
	private Long createUserId;

	/** 创建人席位 */
	@TableField(fill = FieldFill.INSERT)
	private Long createSeatId;

	/** 更新人 */
	@TableField(fill = FieldFill.INSERT_UPDATE)
	private Long updateUserId;

	/** 更新人席位 */
	@TableField(fill = FieldFill.INSERT_UPDATE)
	private Long updateSeatId;

	/** 平台分类 */
	private String platformClassifyId;

	/** 取消状态  0 未取消  10 待审核 20 审核通过 30 驳回 */
	private Integer cancelStatus;


}