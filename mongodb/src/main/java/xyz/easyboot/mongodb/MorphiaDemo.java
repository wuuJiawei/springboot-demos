package xyz.easyboot.mongodb;

import com.mongodb.client.MongoClients;
import dev.morphia.Datastore;
import dev.morphia.Morphia;
import xyz.easyboot.mongodb.map.Employee;

/**
 * @author wujiawei
 * @see
 * @since 2021/2/20 下午1:52
 */
public class MorphiaDemo {
    
    public static void main(String[] args) {
        final Datastore datastore = Morphia.createDatastore(MongoClients.create(), "demo");
        datastore.getMapper().mapPackage("xyz.easyboot.mongodb");
        datastore.ensureIndexes();
        
        final Employee elmer = new Employee();
   
    }
    
}
