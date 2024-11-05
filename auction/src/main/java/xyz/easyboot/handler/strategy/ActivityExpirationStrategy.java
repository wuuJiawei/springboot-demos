package xyz.easyboot.handler.strategy;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
@Slf4j
public class ActivityExpirationStrategy implements ExpirationStrategy {

    private final ApplicationEventPublisher eventPublisher;

    @Override
    public boolean canHandle(String key) {
        return key.startsWith("activity:");
    }

    @Override
    public void handleExpiration(String key) {
        String activityId = key.substring("activity:".length());
        log.info("Activity expired: {}", activityId);
    }

}
