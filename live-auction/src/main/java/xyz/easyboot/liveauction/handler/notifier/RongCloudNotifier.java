package xyz.easyboot.liveauction.handler.notifier;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@AllArgsConstructor
public class RongCloudNotifier extends AbstractNotifier {

    private final StringRedisTemplate redisTemplate;

    @Override
    public boolean enabled() {
        // 查询渠道配置缓存中，融云是否开启
        Object enabled = redisTemplate.opsForHash().get(NOTIFIER_CONFIG, "rongCloud");
        return enabled != null && Boolean.parseBoolean(enabled.toString());
    }

    @Override
    public void notify(String receiver, String content) {
        log.info("Sending 融云 to {}: {}", receiver, content);
    }
}
