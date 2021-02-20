package xyz.easyboot.mongodb.map;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Field;
import dev.morphia.annotations.Id;
import dev.morphia.annotations.Index;
import dev.morphia.annotations.Indexes;
import dev.morphia.annotations.Property;
import dev.morphia.annotations.Reference;
import org.bson.types.ObjectId;

import java.util.List;

/**
 * @author wujiawei
 * @see
 * @since 2021/2/20 下午2:06
 */
@Entity("employee")
@Indexes(
        @Index(fields = @Field("salary"))
)
public class Employee {
    
    @Id
    private ObjectId id;
    
    private String name;
    
    @Reference
    private Employee manager;
    
    @Reference
    private List<Employee> directReports;
    
    @Property("wage")
    private Double salary;
    
    
}
