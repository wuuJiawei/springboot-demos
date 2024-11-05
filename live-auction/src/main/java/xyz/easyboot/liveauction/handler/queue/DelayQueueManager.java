package xyz.easyboot.liveauction.handler.queue;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.*;
import org.springframework.stereotype.Service;
import xyz.easyboot.liveauction.handler.queue.consumer.ConsumerFactory;

import java.util.concurrent.*;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Function;

/**
 * 延迟队列生产者
 */
@Service
@Slf4j
@AllArgsConstructor
@SuppressWarnings("all")
public class DelayQueueManager {

    private final RedissonClient redissonClient;
    private final ConsumerFactory consumerFactory;

    private static final int THREAD_POOL_SIZE = 10; // 根据实际需要调整
    private static final ExecutorService AUTO_CONSUME_THREAD_POOL = Executors.newFixedThreadPool(THREAD_POOL_SIZE);

    /**
     * 装载
     */
    public void load() {
        startAutoConsume();
    }

    public <T> void addToQueue(DelayJob<T> delayJob, String topic) {
        addToQueue(delayJob, topic);
    }

    public <T> void addToQueue(DelayJob<T> delayJob) {
        addToQueue(delayJob, true);
    }

    public <T> void addToQueue(DelayJob<T> delayJob, boolean override) {
        log.info("add to queue: {} - {}", delayJob.getTopic(), delayJob.getJobId());

//        RLock lock = redissonClient.getLock("lock:" + delayJob.getTopic());
//        lock.lock();
        try {
            RBlockingQueue<DelayJob<T>> originalQueue = redissonClient.getBlockingQueue(delayJob.getTopic());
            RDelayedQueue<DelayJob<T>> delayedQueue = redissonClient.getDelayedQueue(originalQueue);

            if (override) {
                delayedQueue.stream()
                        .filter(x -> {
                            return x.getJobId().equals(delayJob.getJobId());
                        })
                        .findFirst()
                        .ifPresent(x -> delayedQueue.remove(x));
            }

            // 延迟发送
            delayedQueue.offer(delayJob, delayJob.getDelay(), delayJob.getTimeUnit());
        } catch (Exception e) {
            log.error("Error adding to queue: " + delayJob.getTopic(), e);
        } finally {
//            lock.unlock();
        }
    }

    public void startAutoConsume() {
        for (String topic : consumerFactory.getTopics()) {
            AUTO_CONSUME_THREAD_POOL.execute(() -> processComsume(topic));
        }
    }

    private void processComsume(String queueName) {
        log.info("Start auto consume: " + queueName);
        RBlockingQueue<Object> blockingQueue = redissonClient.getBlockingQueue(queueName);
        if (blockingQueue == null) {
            log.info("Queue not found: " + queueName);
            return;
        }
        while (!Thread.currentThread().isInterrupted()) {
            try {
                DelayJob job = (DelayJob) blockingQueue.take();
                consumerFactory.consume(job);
            } catch (Exception e) {
                log.error("Error processing job from queue: " + queueName, e);
            }
        }
    }

}
