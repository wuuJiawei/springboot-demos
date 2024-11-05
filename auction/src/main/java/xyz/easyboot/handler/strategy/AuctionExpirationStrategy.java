package xyz.easyboot.handler.strategy;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import xyz.easyboot.handler.AuctionService;

@Component
@AllArgsConstructor
@Slf4j
public class AuctionExpirationStrategy implements ExpirationStrategy {

    private final AuctionService auctionService;

    @Override
    public boolean canHandle(String key) {
        return key.startsWith("auction:");
    }

    @Override
    public void handleExpiration(String key) {
        String productId = key.substring("auction:".length());
        log.info("Auction expired: {}", productId);
        auctionService.finalize(productId);
    }

}
