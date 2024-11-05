package xyz.easyboot.liveauction.handler.queue.monitor;

import cn.hutool.core.lang.Dict;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("queue/monitor")
@AllArgsConstructor
public class MonitorEndpoint {

    private final QueueMonitor queueMonitor;

    @GetMapping("topics")
    public List<Dict> topics() {
        return queueMonitor.getTopicList();
    }

}
