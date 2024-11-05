package xyz.easyboot.liveauction.handler.queue.fallback;

/**
 * 回滚方案
 */
@FunctionalInterface
public interface IFallback {

    void onError(Exception e);

}
