package xyz.easyboot.handler.notifier;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@AllArgsConstructor
@Component
public class NotifierFactory {

    private final List<Notifier> notifiers;

    public void notify(String receiver, String content) {
        for (Notifier notifier : notifiers) {
            if (!notifier.enabled()) {
                log.info("{} notification is disabled", notifier.getClass().getSimpleName());
                return;
            }
            notifier.notify(receiver, content);
        }
    }
}
