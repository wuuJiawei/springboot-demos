package xyz.easyboot;

import cn.hutool.core.lang.Console;
import cn.hutool.core.util.IdUtil;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHost;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.common.xcontent.XContentType;

import java.io.IOException;

public class App {

    private static RestHighLevelClient getClient() {
        String[] ips = {"106.14.255.163"};
        HttpHost[] httpHosts = new HttpHost[ips.length];
        for (int i = 0; i < ips.length; i++) {
            httpHosts[i] = new HttpHost(ips[i], 9200, "http");
        }
        RestClientBuilder builder = RestClient.builder(httpHosts);
        return new RestHighLevelClient(builder);
    }

    public static void main(String[] args) throws IOException {
        Entity entity = new Entity();
        entity.setName("test");
        entity.setDescription("test");
        entity.setAge(18);
        String jsonStr = JSONUtil.toJsonStr(entity);

        String INDEX_NAME = "test";

        // 创建索引
//        CreateIndexRequest request = new CreateIndexRequest("test");
//        CreateIndexResponse createIndexResponse = getClient().indices().create(request, RequestOptions.DEFAULT);
//        Console.log(createIndexResponse.index());

        // 插入文档
        String docId = IdUtil.getSnowflakeNextIdStr();
        IndexRequest indexRequest = new IndexRequest(INDEX_NAME);
        indexRequest.id(docId);
        indexRequest.source(jsonStr, XContentType.JSON);
        IndexResponse indexResponse = getClient().index(indexRequest, RequestOptions.DEFAULT);
        Console.log(indexResponse.getResult().getLowercase());

        // 查询文档
        GetRequest getRequest = new GetRequest(INDEX_NAME, docId);
        Boolean exists = getClient().exists(getRequest, RequestOptions.DEFAULT);
        Console.log(exists);

    }
}
