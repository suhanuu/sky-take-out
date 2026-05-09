package com.sky.test;

import com.google.gson.JsonObject;
import lombok.val;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class HttpClientTest {

    /**
     * 测试HttpClient (get方式请求）
     */
    @Test
    public void test1() throws Exception {
        // 创建HttpClient对象
        CloseableHttpClient httpClient = HttpClients.createDefault();
        // 创建HttpGet对象，设置url访问地址
        HttpGet httpGet = new HttpGet("http://localhost:8080/user/shop/status");
        // 使用HttpClient对象执行get请求，获取响应对象
        CloseableHttpResponse response = httpClient.execute(httpGet);

        // 获取响应状态码
        int statusCode = response.getStatusLine().getStatusCode();
        System.out.println("响应状态码：" + statusCode);

        //获取响应内容
        HttpEntity entity = response.getEntity();
        String content = EntityUtils.toString(entity);
        System.out.println("响应内容：" + content);

        //释放资源
        response.close();
        httpClient.close();

    }
    /**
     * 测试HttpClient (post方式请求）
     */
    @Test
    public void test2() throws Exception {
        // 创建HttpClient对象
        CloseableHttpClient httpClient = HttpClients.createDefault();
        // 创建HttpPost对象，设置url访问地址
        //HttpPost httpPost = new HttpPost("http://localhost:8080/admin/category/status/1");
        HttpPost httpPost = new HttpPost("http://localhost:8080/admin/employee/login");
        // 设置请求参数
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("username", "admin");
        jsonObject.addProperty("password", "123456");
        HttpEntity entitys = new StringEntity(jsonObject.toString(), ContentType.APPLICATION_JSON);
        httpPost.setEntity(entitys);
        //httpPost.setHeader("token", "eyJhbGciOiJIUzI1NiJ9.eyJlbXBJZCI6MSwiZXhwIjoxNzc4Mzk3NTQ5fQ.1rfSAjAkj9JUM8MQFs6dOOwTv9A_xxadK8mId6S1_bQ");
        // 使用HttpClient对象执行post请求，获取响应对象
        CloseableHttpResponse response = httpClient.execute(httpPost);
        // 获取响应状态码
        int statusCode = response.getStatusLine().getStatusCode();
        System.out.println("响应状态码：" + statusCode);
        //获取响应内容
        HttpEntity entity = response.getEntity();
        String content = EntityUtils.toString(entity);
        System.out.println("响应内容：" + content);

        //释放资源
        response.close();
        httpClient.close();
    }
}
