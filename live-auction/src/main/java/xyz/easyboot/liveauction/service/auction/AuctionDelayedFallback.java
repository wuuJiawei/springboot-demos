package xyz.easyboot.liveauction.service.auction;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import xyz.easyboot.liveauction.handler.queue.fallback.IFallback;

@Component
@Slf4j
@AllArgsConstructor
public class AuctionDelayedFallback implements IFallback {

    @Override
    public void onError(Exception e) {
        log.info("Redis补偿机制触发: {}", this.getClass().getName());
    }
}
