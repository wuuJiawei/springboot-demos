package xyz.easyboot.liveauction.service;

import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.json.JSONUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RScoredSortedSet;
import org.redisson.api.RedissonClient;
import org.redisson.client.protocol.ScoredEntry;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import xyz.easyboot.liveauction.consts.Constants;
import xyz.easyboot.liveauction.dto.Goods;
import xyz.easyboot.liveauction.handler.queue.DelayJob;
import xyz.easyboot.liveauction.handler.queue.DelayQueueManager;
import xyz.easyboot.liveauction.handler.notifier.NotifierFactory;

import java.math.BigDecimal;
import java.util.Date;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
@AllArgsConstructor
@SuppressWarnings("all")
public class GoodsService implements Constants {

    private final NotifierFactory notifierFactory;
    private final RedissonClient redissonClient;
    private final DelayQueueManager delayQueueManager;
    private final StringRedisTemplate redisTemplate;

    /**
     * 开始竞拍
     */
    public void start(Goods goods) {
        try {
            // 缓存商品信息
            putGoodsCache(goods);
            // 启动延迟队列
            addToQueue(goods);
        } catch (Exception e) {
            log.error("开始竞拍失败", e);
            throw new RuntimeException("开始竞拍失败", e);
        }
        log.info("开始竞拍 {}, 结束时间: {}", goods.getId(), goods.getEndTime());
    }

    /**
     * 结算
     * @param goodsId
     */
    public void settle(String goodsId) {
        log.info("开始结算: {}", goodsId);

        // 取出缓存
        Goods goods = getGoodsCache(goodsId);

        // 检查状态，做幂等

        // 取出最高价和出价者
        ScoredEntry<Object> firstEntry = getHighestBid(goodsId);
        if (firstEntry == null) {
            //TODO 无人出价，更新流拍
            log.info("无人出价，更新流拍: {}", goodsId);
            return;
        }
        goods.setHighestBidder(firstEntry.getValue().toString());
        goods.setHighestBidAmount(BigDecimal.valueOf(firstEntry.getScore()));
        goods.setSold(true);
        putGoodsCache(goods);

        // 存储数据库
        ThreadUtil.sleep(500);

        // 通知用户
        notifierFactory.notify("用户**", "结算成功，商品ID：" + goodsId);

        log.info("结算: {} 成功", goodsId);
    }

    /**
     * 用户出价
     */
    public void bid(String goodsId, String bidderId, Double bidAmount) {
        Goods goods = getGoodsCache(goodsId);

        if (goods.getSold()) {
            return;
        }

        // 检查当前出价是否低于最高出价
        ScoredEntry<Object> highestBid = getHighestBid(goodsId);
        if (highestBid != null) {
            Double firstScore = highestBid.getScore();
            log.info("{} 当前出价: {}, 最高出价: {}", goodsId, bidAmount, firstScore);
            if (firstScore != null && bidAmount <= firstScore) {
                return;
            }
        }

        // 在截止时间30秒内出价，每次出价追加30秒
        Date now = new Date();
        if (goods.getEndTime().getTime() - now.getTime() <= 30000) {
            goods.setEndTime(DateUtil.offsetSecond(goods.getEndTime(), 30));
        }
        addToQueue(goods);

        // 更新商品缓存
        goods.setHighestBidAmount(BigDecimal.valueOf(bidAmount));
        goods.setHighestBidder(bidderId);
        putGoodsCache(goods);

        // 维护出价排名
        redissonClient.getScoredSortedSet(buildBidsZSetKey(goodsId)).add(bidAmount, bidderId + "@" + System.currentTimeMillis());

        // 异步存储数据库
        storeToDbAsync(goods);
    }

    @Async
    public void storeToDbAsync(Goods goods) {
        // 存储数据库
        ThreadUtil.sleep(500);
    }

    /**
     * 获取最高出价
     * @return
     */
    public ScoredEntry<Object> getHighestBid(String goodsId) {
        RScoredSortedSet<Object> scoredSortedSet = redissonClient.getScoredSortedSet(buildBidsZSetKey(goodsId));
        return scoredSortedSet.firstEntry();
    }

    private void addToQueue(Goods goods) {
        DelayJob<Goods> delayJob = new DelayJob<>();
        delayJob.setData(goods);
        delayJob.setDelay(DateUtil.between(new Date(), goods.getEndTime(), DateUnit.SECOND));
        delayJob.setTimeUnit(TimeUnit.SECONDS);
        delayJob.setJobId(goods.getId());
        delayJob.setTopic(AUCTION_QUEUE);
        delayQueueManager.addToQueue(delayJob);
    }

    private void putGoodsCache(Goods goods) {
        if (goods == null) {
            return;
        }
        redisTemplate.opsForHash().put(GOODS, goods.getId(), JSONUtil.toJsonStr(goods));
    }

    public Goods getGoodsCache(String goodsId) {
        Object o = redisTemplate.opsForHash().get(GOODS, goodsId);
        if (o == null) {
            throw new RuntimeException("商品不存在: " + goodsId);
        }
        return JSONUtil.toBean((String) o, Goods.class);
    }

    private String buildBidsZSetKey(String itemId) {
        return  "auction:zset:" + itemId;
    }

    private String buildBidsListKey(String itemId) {
        return  "auction:list:" + itemId;
    }

}
