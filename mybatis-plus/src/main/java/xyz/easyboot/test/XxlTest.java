package xyz.easyboot.test;

import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;



@Slf4j
public class XxlTest {

    public static void main(String[] args) {
        HttpURLConnection connection = null;
        BufferedReader bufferedReader = null;
        try {
            // connection
            String url = "http://198.168.30.24:9997/run";
            URL realUrl = new URL(url);
            connection = (HttpURLConnection) realUrl.openConnection();

            // connection setting
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setUseCaches(false);
            connection.setReadTimeout(3000);
            connection.setConnectTimeout(3000);
            connection.setRequestProperty("connection", "Keep-Alive");
            connection.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
            connection.setRequestProperty("Accept-Charset", "application/json;charset=UTF-8");
            connection.setRequestProperty("XXL-JOB-ACCESS-TOKEN", "default_token");
            // do connection
            connection.connect();

            String requestBody = "{\n" +
                    "    \"jobId\": 111111,\n" +
                    "    \"executorHandler\": \"offlinePayJob\",\n" +
                    "    \"glueType\": \"BEAN\"\n" +
                    "}";
            log.info("xxl-job remoting (url="+url+") request body:"+requestBody);

            DataOutputStream dataOutputStream = new DataOutputStream(connection.getOutputStream());
            dataOutputStream.write(requestBody.getBytes("UTF-8"));
            dataOutputStream.flush();
            dataOutputStream.close();

            // valid StatusCode
            int statusCode = connection.getResponseCode();
            log.info("xxl-job remoting (url="+url+") response status invalid("+ statusCode +").");

            // result
            bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
            StringBuilder result = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                result.append(line);
            }
            String resultJson = result.toString();

        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            try {
                if (bufferedReader != null) {
                    bufferedReader.close();
                }
                if (connection != null) {
                    connection.disconnect();
                }
            } catch (Exception e2) {
                log.error(e2.getMessage(), e2);
            }
        }
    }

}
