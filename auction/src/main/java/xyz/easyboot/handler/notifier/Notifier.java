package xyz.easyboot.handler.notifier;

import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public interface Notifier {

    boolean enabled();

    /**
     * 延迟到指定时间发送
     * @param receiver
     * @param content
     * @param notifyTime
     */
    void notify(String receiver, String content, Date notifyTime);

    /**
     * 立即发送
     * @param receiver
     * @param content
     */
    void notify(String receiver, String content);

    // 延迟发送的线程池
    ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(10);

    // 根据时间计算延迟单位和延迟时间
    default long buildDelayMills(Date notifyTime) {
        return notifyTime.getTime() - System.currentTimeMillis();
    }
}
