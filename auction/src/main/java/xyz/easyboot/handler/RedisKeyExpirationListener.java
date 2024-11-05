package xyz.easyboot.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.listener.KeyExpirationEventMessageListener;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Component;
import xyz.easyboot.handler.strategy.ExpirationStrategy;

import javax.annotation.Resource;
import java.util.List;

@Component
@Slf4j
public class RedisKeyExpirationListener extends KeyExpirationEventMessageListener implements ApplicationEventPublisherAware {

    @Resource
    private List<ExpirationStrategy> strategies;

    public RedisKeyExpirationListener(RedisMessageListenerContainer listenerContainer) {
        super(listenerContainer);
    }

    @Override
    public void onMessage(Message message, byte[] pattern) {
        String expiredKey = message.toString();
        log.info("Key expired: {}", expiredKey);

        dispatchEvent(expiredKey);
    }

    /**
     * 根据key触发到响应的策略
     * @param key
     */
    private void dispatchEvent(String key) {
        for (ExpirationStrategy strategy : strategies) {
            if (strategy.canHandle(key)) {
                strategy.handleExpiration(key);
                break;
            }
        }
    }
}
