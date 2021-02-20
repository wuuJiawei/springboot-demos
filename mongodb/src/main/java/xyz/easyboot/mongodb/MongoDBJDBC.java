package xyz.easyboot.mongodb;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

/**
 * @author wujiawei
 * @see
 * @since 2021/2/20 上午10:46
 */
public class MongoDBJDBC {
    
    public static void main(String[] args) {
        // 连接mongodb
        MongoClient mongoClient = new MongoClient("localhost", 27017);
        // 连接数据库
        MongoDatabase mongoDatabase = mongoClient.getDatabase("demo");
        System.out.println("Connect successfully!");
        // 创建集合
//        mongoDatabase.createCollection("test");
//        System.out.println("Collection created successfully!");
        //选择集合
        MongoCollection<Document> collection = mongoDatabase.getCollection("test");
        System.out.println("Collection selected!");
        // 插入文档
        Document document = new Document("title", "MongoDB")
                .append("description", "database")
                .append("likes", 100)
                .append("by", "Wjw");
        List<Document> documents = new ArrayList<Document>();
        documents.add(document);
        collection.insertMany(documents);
        System.out.println("Document insert successfully!");
        
        
    }
}
