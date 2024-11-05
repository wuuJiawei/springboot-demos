package xyz.easyboot.config.json;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.handlers.AbstractJsonTypeHandler;
import lombok.SneakyThrows;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;

import java.lang.reflect.Field;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

/**
 * Mybatis数组和字符串互转
 * <p>
 * MappedJdbcTypes 数据库中的数据类型 MappedTypes java中的的数据类型
 *
 * @author wuu
 */
@MappedTypes(value = { Map.class })
@MappedJdbcTypes(value = JdbcType.VARCHAR)
public class MapTypeHandler extends AbstractJsonTypeHandler<Map> {


	/**
	 * 默认初始化
	 *
	 * @param type 类型
	 */
	public MapTypeHandler(Class<?> type) {
		super(type);
	}

	/**
	 * 通过字段初始化
	 *
	 * @param type  类型
	 * @param field 字段
	 * @since 3.5.6
	 */
	public MapTypeHandler(Class<?> type, Field field) {
		super(type, field);
	}

	/**
	 * 反序列化json
	 *
	 * @param json json字符串
	 * @return T
	 */
	@Override
	public Map parse(String json) {
		return JSONUtil.toBean(json, Map.class);
	}

	/**
	 * 序列化json
	 *
	 * @param obj 对象信息
	 * @return json字符串
	 */
	@Override
	public String toJson(Map obj) {
		return JSONUtil.toJsonStr(obj);
	}
}
