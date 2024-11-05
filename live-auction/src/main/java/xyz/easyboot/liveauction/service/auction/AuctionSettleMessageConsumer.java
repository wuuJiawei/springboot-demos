package xyz.easyboot.liveauction.service.auction;

import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import xyz.easyboot.liveauction.consts.Constants;
import xyz.easyboot.liveauction.dto.Goods;
import xyz.easyboot.liveauction.handler.queue.DelayJob;
import xyz.easyboot.liveauction.handler.queue.consumer.MessageConsumer;
import xyz.easyboot.liveauction.service.GoodsService;

@Component
public class AuctionSettleMessageConsumer implements MessageConsumer<Goods>, Constants {

    private static final Logger log = LoggerFactory.getLogger(AuctionSettleMessageConsumer.class);

    @Override
    public String topic() {
        return AUCTION_QUEUE;
    }

    @Override
    public boolean match(String topic) {
        return topic().equals(topic);
    }

    @Override
    public void consume(DelayJob<Goods> delayJob) {
        String goodsId = delayJob.getJobId();
        log.info("收到结算消息：{} --> {}", goodsId, JSONUtil.toJsonStr(delayJob.getData()));
        GoodsService goodsService = SpringUtil.getBean(GoodsService.class);
        goodsService.settle(goodsId);
    }
}
