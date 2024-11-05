package xyz.easyboot.config.json;

import cn.hutool.core.lang.Dict;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.handlers.AbstractJsonTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;

import java.lang.reflect.Field;
import java.util.Map;

/**
 * Mybatis数组和字符串互转
 * <p>
 * MappedJdbcTypes 数据库中的数据类型 MappedTypes java中的的数据类型
 *
 * @author wuu
 */
@MappedTypes(value = { Dict.class })
@MappedJdbcTypes(value = JdbcType.VARCHAR)
public class DictTypeHandler extends AbstractJsonTypeHandler<Dict> {


	/**
	 * 默认初始化
	 *
	 * @param type 类型
	 */
	public DictTypeHandler(Class<?> type) {
		super(type);
	}

	/**
	 * 通过字段初始化
	 *
	 * @param type  类型
	 * @param field 字段
	 * @since 3.5.6
	 */
	public DictTypeHandler(Class<?> type, Field field) {
		super(type, field);
	}

	/**
	 * 反序列化json
	 *
	 * @param json json字符串
	 * @return T
	 */
	@Override
	public Dict parse(String json) {
		return JSONUtil.toBean(json, Dict.class);
	}

	/**
	 * 序列化json
	 *
	 * @param obj 对象信息
	 * @return json字符串
	 */
	@Override
	public String toJson(Dict obj) {
		return JSONUtil.toJsonStr(obj);
	}
}
