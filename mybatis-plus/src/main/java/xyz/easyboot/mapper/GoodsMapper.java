package xyz.easyboot.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import xyz.easyboot.model.Goods;

import java.util.List;

public interface GoodsMapper extends BaseMapper<Goods> {

    List<Goods> selectAll();

}
