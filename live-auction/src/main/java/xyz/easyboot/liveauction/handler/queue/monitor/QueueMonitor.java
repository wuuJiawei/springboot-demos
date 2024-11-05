package xyz.easyboot.liveauction.handler.queue.monitor;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.Dict;
import cn.hutool.core.util.ReUtil;
import lombok.AllArgsConstructor;
import org.redisson.api.RBlockingQueue;
import org.redisson.api.RKeys;
import org.redisson.api.RedissonClient;
import org.redisson.client.protocol.ScoredEntry;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import xyz.easyboot.liveauction.handler.queue.fallback.IFallback;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

@Component
@AllArgsConstructor
@SuppressWarnings("all")
public class QueueMonitor {

    private final RedissonClient redissonClient;
    private final RedisTemplate redisTemplate;
    private final List<IFallback> fallbacks;

    public List<Dict> getTopicList() {
        RKeys keys = redissonClient.getKeys();
        Iterable<String> allQueueNames = keys.getKeysByPattern("redisson_delay_queue_timeout:*");

        List<Dict> list = new ArrayList<>();
        allQueueNames.forEach(queueName -> {
            Dict dict = new Dict();
            String topicName = ReUtil.get("\\{(.+?)\\}", queueName, 1);

            // 名称
            dict.set("name", topicName);

            // 数量
            RBlockingQueue<Object> blockingQueue = redissonClient.getBlockingQueue(topicName);
            dict.set("size", blockingQueue.size());

            List<Dict> jobList = new ArrayList<>();
            Iterator<ScoredEntry<Object>> scoredEntryIterator = redissonClient.getScoredSortedSet(queueName).entryIterator();
            while (scoredEntryIterator.hasNext()) {
                ScoredEntry<Object> next = scoredEntryIterator.next();
                double timestamp = next.getScore();
                DateTime date = DateUtil.date((long) timestamp);
                jobList.add(new Dict()
                        .set("expireTime", timestamp)
                        .set("value", next.getValue())
                );
            }

            list.add(dict);
        });
        return list;
    }

    /**
     * 通过定时任务监控 Redis 连接状态，为 Redis 崩溃提供基本的补偿保护
     */
    @Scheduled(fixedDelay = 5000)
    public void start() {
        if (redissonClient.isShutdown() || redissonClient.isShuttingDown()) {
            return;
        }

        try {
            redissonClient.getNodesGroup().pingAll();
        } catch (Exception e) {
            for (IFallback fallback : fallbacks) {
                fallback.onError(e);
            }
        }
    }

}
