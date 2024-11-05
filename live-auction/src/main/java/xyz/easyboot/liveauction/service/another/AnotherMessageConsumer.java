package xyz.easyboot.liveauction.service.another;

import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import xyz.easyboot.liveauction.handler.queue.DelayJob;
import xyz.easyboot.liveauction.handler.queue.consumer.MessageConsumer;

@Slf4j
@Component
public class AnotherMessageConsumer implements MessageConsumer<Object> {

    @Override
    public String topic() {
        return "ANOTHER";
    }

    @Override
    public boolean match(String topic) {
        return false;
    }

    @Override
    public void consume(DelayJob<Object> delayJob) {
        log.info("Another 收到消息：{}", JSONUtil.toJsonStr(delayJob.getData()));
    }
}
