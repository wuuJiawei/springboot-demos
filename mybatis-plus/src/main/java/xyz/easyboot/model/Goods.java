package xyz.easyboot.model;

import cn.hutool.core.lang.Dict;
import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.Data;
import xyz.easyboot.enums.TypeEnum;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author wujiawei0926@yeah.net
 * @see
 * @since 2020/5/29
 */
@TableName(value ="goods", autoResultMap = true)
@Data
public class Goods {

    @TableId(type = IdType.AUTO)
    private Long id;

//    private String name;
//
//    private Integer status;
//
//    private Date upTime;

    private List<String> images;

    private List<Integer> imageIds;

    private List<String> imagesArr;

    private List<Integer> otherIds;

    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<OtherInfo> others;

    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<Map<String, ?>> other2;

//    @TableField(typeHandler = JacksonTypeHandler.class)
//    private Map<String, Object> otherData;

//    private Object obj;
//
//    private JSONObject jsonObj;
//
//    private Dict dict;
//
//    private TypeEnum type;

}
