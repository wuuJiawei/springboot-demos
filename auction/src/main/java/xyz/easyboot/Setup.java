package xyz.easyboot;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import xyz.easyboot.handler.AuctionExpirationScheduler;
import xyz.easyboot.handler.AuctionService;
import xyz.easyboot.handler.Constants;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Component
@AllArgsConstructor
@Slf4j
public class Setup implements Constants {

    private final AuctionService auctionService;
    private final StringRedisTemplate redisTemplate;
    private AuctionExpirationScheduler auctionExpirationScheduler;

    private void initNotifier() {
        if (Boolean.FALSE.equals(redisTemplate.hasKey(NOTIFIER_CONFIG))) {
            redisTemplate.opsForHash().put(NOTIFIER_CONFIG, "sms", "true");
            redisTemplate.opsForHash().put(NOTIFIER_CONFIG, "rongCloud", "true");
        }
    }

    @PostConstruct
    public void init() {
        // 通知渠道初始化
        initNotifier();

        auctionExpirationScheduler.start();

        for (int i = 0; i < 100; i++) {
            auctionService.start(i + "", Date.from(LocalDateTime.now().plusSeconds(i*5).atZone(ZoneId.systemDefault()).toInstant()));
        }

        // 模拟有人出价
        for (int i = 0; i < 100; i++) {
            auctionService.bid(i + "", "User" + i,90.0);
        }

    }

}
