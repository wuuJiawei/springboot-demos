package xyz.easyboot.controller;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import xyz.easyboot.mapper.GoodsMapper;
import xyz.easyboot.model.Goods;

import java.util.Date;

@AllArgsConstructor
@RestController
@Slf4j
public class GoodsController {

    private final GoodsMapper goodsMapper;

//    @GetMapping("insert")
//    public String insert() {
//        Goods goods = new Goods();
//        goods.setName("测试");
//        goods.setStatus(0);
//        goods.setUpTime(new Date());
//        goodsMapper.insert(goods);
//        return "success";
//    }
//
//    @GetMapping("update")
//    public String update(Long id) {
//        Goods goods = new Goods();
//        goods.setId(id);
//        goods.setName("测试:" + RandomUtil.randomNumber());
//        goods.setStatus(1);
//        goods.setUpTime(new Date());
//        goodsMapper.update(Wrappers.lambdaUpdate(Goods.class)
//                .like(Goods::getName, "测试")
//                .set(Goods::getName, goods.getName())
//                .set(Goods::getStatus, goods.getStatus())
//                .set(Goods::getUpTime, goods.getUpTime())
//        );
//        return "success";
//    }
//
//    @GetMapping("delete")
//    public String delete(Long id) {
//        goodsMapper.deleteById(id);
//        return "success";
//    }

    @GetMapping("query")
    public Object query(Long id) {
        Goods goods = goodsMapper.selectById(id);
        log.info("goods: {}", goods);
        var map = goods.getOther2().getFirst();
        log.info("map: {}", map);
        return goods;
    }

    @GetMapping("query-all")
    public Object queryAll() {
        return goodsMapper.selectAll();
    }
}
