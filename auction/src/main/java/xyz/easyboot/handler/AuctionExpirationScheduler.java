package xyz.easyboot.handler;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 通过定时任务不间断检查状态，当redis因可靠性问题导致数据丢失或到期事件未执行时，该定时任务将作为保底方案 <br>
 * 除非redis宕机，一般来说需要保底的数据量并不大<br>
 * 但是考虑到直接查询数据库，还是会给数据库造成一定压力，因此将查询间隔调整 <br>
 * 对于执行的数据处理，务必做好<strong>幂等</strong>
 * 目前还没有想到更好的方案
 */
@Component
@AllArgsConstructor
@Slf4j
public class AuctionExpirationScheduler {

    private final ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(10);
    // 间隔时间可以放到Nacos中
    private final int delaySeconds = 10;
    // 最小单位是秒
    private final TimeUnit delayTimeUnit = TimeUnit.SECONDS;

    public void start() {
        scheduledExecutorService.schedule(() -> {
            try {
                execute();
            } catch (Exception e) {
                log.error("AuctionExpirationScheduler execute error", e);
            } finally {
                start();
            }
        }, delaySeconds, delayTimeUnit);
    }

    private void execute() {
        // 查询数据库中 结束时间>当前时间 && 状态=竞拍中 的商品

        // 查到之后结算

        // 结算后通知

        log.info("Execute AuctionExpirationScheduler");
    }

}
