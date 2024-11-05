package xyz.easyboot.liveauction.web;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.thread.ConcurrencyTester;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.RandomUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import xyz.easyboot.liveauction.dto.Goods;
import xyz.easyboot.liveauction.service.GoodsService;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@RestController
@Slf4j
@AllArgsConstructor
public class GoodsController {

    private final GoodsService goodsService;

    @GetMapping("hello")
    public String hello() {
        return "world";
    }

    @GetMapping("start/{goodsId}")
    public Goods start(@PathVariable String goodsId) {
        Goods goods = new Goods()
                .setId(goodsId)
                .setName("商品:" + goodsId)
                .setSalePrice(BigDecimal.ONE)
                .setSold(false)
                .setEndTime(DateUtil.offsetMinute(new Date(), 10));
        goodsService.start(goods);
        return goods;
    }

    @GetMapping("start-concurrency-common")
    public String startSome() {
        ConcurrencyTester concurrencyTester = ThreadUtil.concurrencyTest(1000, () -> {
            String id = IdUtil.getSnowflakeNextIdStr();
            Goods goods = new Goods()
                    .setId(id)
                    .setName("商品:" + id)
                    .setSalePrice(BigDecimal.ONE)
                    .setSold(false)
                    .setEndTime(DateUtil.offsetMinute(new Date(), 1));
            goodsService.start(goods);
        });
        return "success: " + concurrencyTester.getInterval() + "ms";
    }

    @GetMapping("start-concurrency")
    public String startSomeVisual() {
        ExecutorService executorService = Executors.newVirtualThreadPerTaskExecutor();
        int size = 10000;
        CountDownLatch countDownLatch = new CountDownLatch(size);
        for (int i = 0; i < size; i++) {
            executorService.submit(() -> {
                String id = IdUtil.getSnowflakeNextIdStr();
                Goods goods = new Goods()
                        .setId(id)
                        .setName("商品:" + id)
                        .setSalePrice(BigDecimal.ONE)
                        .setSold(false)
                        .setEndTime(DateUtil.offsetMinute(new Date(), 1));
                goodsService.start(goods);
                countDownLatch.countDown();
            });
        }

        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return "success: " + countDownLatch.getCount() + "ms";
    }

    @GetMapping("bid-async/{goodsId}")
    public String bidAsync(@PathVariable String goodsId) {
        ConcurrencyTester concurrencyTester = ThreadUtil.concurrencyTest(1000, () -> {
            goodsService.bid(goodsId, "User" + RandomUtil.randomInt(1, 100), RandomUtil.randomDouble(1, 100));
        });
        return "success: " + concurrencyTester.getInterval() + "ms";
    }

    @GetMapping("bid-sync/{goodsId}")
    public String bidSync(@PathVariable String goodsId) {
        goodsService.bid(goodsId, "User" + RandomUtil.randomInt(1, 100), RandomUtil.randomDouble(1, 100));
        return "success";
    }

    @GetMapping("get/{goodsId}")
    public Goods getGoods(@PathVariable String goodsId) {
        return goodsService.getGoodsCache(goodsId);
    }

}
