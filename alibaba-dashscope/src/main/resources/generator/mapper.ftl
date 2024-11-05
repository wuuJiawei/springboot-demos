package ${config.mapper.packagePath};

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import ${config.entity.packagePath}.${table.upperCamelTableName};
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * ${table.comment} Mapper
 * @author code-generator
 */
public interface ${table.upperCamelTableName}Mapper extends BaseMapper<${table.upperCamelTableName}> {

}
