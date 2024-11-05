package xyz.easyboot.liveauction.handler.notifier;

import xyz.easyboot.liveauction.consts.Constants;

import java.util.Date;
import java.util.concurrent.TimeUnit;

public abstract class AbstractNotifier implements Notifier, Constants {
    @Override
    public boolean enabled() {
        return true;
    }

    @Override
    public void notify(String receiver, String content, Date notifyTime) {
        long delay = buildDelayMills(notifyTime);
        scheduledExecutorService.schedule(() -> {
            notify(receiver, content);
        }, delay, TimeUnit.MILLISECONDS);
    }

    @Override
    public void notify(String receiver, String content) {
        throw new UnsupportedOperationException("notify method is not implemented");
    }
}
