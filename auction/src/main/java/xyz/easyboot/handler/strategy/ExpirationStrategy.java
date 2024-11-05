package xyz.easyboot.handler.strategy;

public interface ExpirationStrategy {

    boolean canHandle(String key);

    void handleExpiration(String key);

}
