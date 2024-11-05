package xyz.easyboot.handler;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;
import xyz.easyboot.handler.notifier.Notifier;
import xyz.easyboot.handler.notifier.NotifierFactory;

import java.util.*;

@Service
@Slf4j
@AllArgsConstructor
public class AuctionService {

    private final StringRedisTemplate redisTemplate;
    private final NotifierFactory notifierFactory;

    private String buildKey(String productId) {
        return "auction:" + productId;
    }

    private String buildBidsKey(String productId) {
        return "auction:" + productId + ":bids";
    }

    @SuppressWarnings("unchecked")
    public Set<Object> getAllAuctions() {
        Set<ZSetOperations.TypedTuple<String>> typedTuples = redisTemplate.opsForZSet().reverseRangeWithScores("auction:*", 0, -1);
        if (typedTuples == null || typedTuples.isEmpty()) {
            log.info("No auctions found");
            return Collections.EMPTY_SET;
        }
        log.info("Found {} auctions", typedTuples.size());
        return typedTuples.stream().map(ZSetOperations.TypedTuple::getValue).filter(Objects::nonNull).collect(java.util.stream.Collectors.toSet());
    }

    /**
     * 开始竞拍
     */
    public void start(String productId, Date endTime) {
        String key = buildKey(productId);
        redisTemplate.opsForValue().set(key, "假装是一个商品信息JSON");
        // 设置竞拍结束时间
        redisTemplate.expireAt(key, endTime);
        log.info("Starting auction for product {}, end time: {}", productId, endTime);
    }

    /**
     * 结算竞拍，通知最高出价者
     */
    public void finalize(String productId) {
        String key = buildBidsKey(productId);
        // 获取最高出价者和其出价
        Set<ZSetOperations.TypedTuple<String>> bids = redisTemplate.opsForZSet().reverseRangeWithScores(key, 0, 0);
        if (bids == null || bids.isEmpty()) {
            notifyAbortiveAuction(productId);
            return;
        }
        ZSetOperations.TypedTuple<String> highestBid = bids.iterator().next();
        if (highestBid == null || highestBid.getValue() == null) {
            notifyAbortiveAuction(productId);
            return;
        }
        notifyWinner(highestBid.getValue(), highestBid.getScore());
        // 竞拍结束后清理数据
        redisTemplate.delete(key);
        redisTemplate.delete(buildKey(productId));
    }

    /**
     * 用户出价
     */
    public void bid(String productId, String bidder, Double bidAmount) {
        String key = buildBidsKey(productId);
        // 出价计数器，确保每次出价都有唯一标识
        Long bidIndex = redisTemplate.opsForValue().increment("bidCount:" + productId, 1);
        // 添加新的出价到有序集合，标记为bidder:bidIndex确保唯一性
        redisTemplate.opsForZSet().add(key, bidder + ":" + bidIndex, bidAmount);

        //TODO 异步存储数据库
    }

    /**
     * 强制终止
     * @param productId
     */
    public void terminate(String productId) {
        String key = buildKey(productId);
        redisTemplate.expireAt(key, new Date());
    }

    /**
     * 通知最高出价者
     */
    public void notifyWinner(String bidInfo, Double bidAmount) {
        String winner = bidInfo.substring(0, bidInfo.indexOf(':'));
        notifierFactory.notify(winner, winner + " won " + bidAmount);
    }

    /**
     * 流拍的通知
     * @param productId
     */
    public void notifyAbortiveAuction(String productId) {
        notifierFactory.notify(productId, productId + " aborted, let's try again");
    }
}
