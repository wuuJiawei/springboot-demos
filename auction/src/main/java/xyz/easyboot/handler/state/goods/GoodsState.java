package xyz.easyboot.handler.state.goods;

/**
 * 订单状态：待付款、待发货、平台查验、待收货、已完成、退款中、退款完成、取消支付
 */
public enum GoodsState {

    READY, // 待付款
    SHIPPED, // 待发货
    CHECKED, // 平台查验
    RECEIVED, // 待收货
    COMPLETED, // 已完成
    REFUNDING, // 退款中
    REFUNDED, // 退款完成
    CANCELLED; // 取消支付

}
