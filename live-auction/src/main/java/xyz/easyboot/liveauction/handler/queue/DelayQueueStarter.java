package xyz.easyboot.liveauction.handler.queue;

import cn.hutool.extra.spring.SpringUtil;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class DelayQueueStarter implements ApplicationListener<ApplicationStartedEvent> {
    @Override
    public void onApplicationEvent(ApplicationStartedEvent event) {
        DelayQueueManager producer = SpringUtil.getBean(DelayQueueManager.class);
        producer.load();
    }
}
