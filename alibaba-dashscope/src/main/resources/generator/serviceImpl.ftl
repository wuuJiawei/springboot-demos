package ${config.serviceImpl.packagePath};

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import ${config.entity.packagePath}.${table.upperCamelTableName};
import ${config.mapper.packagePath}.${table.upperCamelTableName}Mapper;
import ${config.service.packagePath}.${table.upperCamelTableName}Service;
import org.springframework.stereotype.Service;

/**
 * ${table.comment} ServiceImpl
 * @author code-generator
 */
@Service
public class ${table.upperCamelTableName}ServiceImpl extends ServiceImpl<${table.upperCamelTableName}Mapper, ${table.upperCamelTableName}> implements ${table.upperCamelTableName}Service {

}




