package xyz.easyboot.liveauction.handler.queue.consumer;

import xyz.easyboot.liveauction.handler.queue.DelayJob;

/**
 * 消费
 * @param <T>
 */
public interface MessageConsumer<T> {

    /**
     * 定义topic
     */
    String topic();

    /**
     * 原则上只要topic名称匹配上就执行，但是也允许其他特殊的匹配条件
     */
    boolean match(String topic);

    /**
     * 消费
     */
    void consume(DelayJob<T> delayJob);

}
