package xyz.easyboot.liveauction.handler.queue.consumer;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import xyz.easyboot.liveauction.handler.queue.DelayJob;

import java.util.List;

@Slf4j
@Component
@AllArgsConstructor
@SuppressWarnings("all")
public class ConsumerFactory {

    private final List<MessageConsumer> messageConsumers;

    /**
     * 匹配合适的消费者
     * @param delayJob
     * @param <T>
     */
    public <T> void consume(DelayJob<T> delayJob) {
        for (MessageConsumer messageConsumer : messageConsumers) {
            if (messageConsumer.match(delayJob.getTopic())) {
                messageConsumer.consume(delayJob);
            }
        }
    }

    public int size() {
        return messageConsumers.size();
    }

    public List<String> getTopics() {
        return messageConsumers.stream().map(MessageConsumer::topic).toList();
    }

}
