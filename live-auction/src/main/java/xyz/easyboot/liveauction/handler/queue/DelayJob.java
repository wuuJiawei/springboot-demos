package xyz.easyboot.liveauction.handler.queue;

import lombok.Data;

import java.util.concurrent.TimeUnit;

@Data
public class DelayJob<T> {

    /**
     * 主题，用于匹配消费者
     */
    private String topic;

    /**
     * 任务ID，主题内保持唯一
     */
    private String jobId;

    /**
     * 延迟的时间
     */
    private Long delay;

    private TimeUnit timeUnit;

    private T data;

}
