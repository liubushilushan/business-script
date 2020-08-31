package com.liuapi.http.httpclient4;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.*;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.config.SocketConfig;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@Slf4j
public class HttpClients {
    public static final HttpClient httpclient;

    static {
        httpclient = HttpClientBuilder.create()
                .setMaxConnPerRoute(5)
                .setMaxConnTotal(20)
                // 设置tcp连接的属性
                .setDefaultSocketConfig(SocketConfig.custom()
                        .setSoKeepAlive(true)
                        .build())
                // 设置重试策略,针对连接超时，读超时进行一些重试
                .setRetryHandler((ex, retryCount,httpContext) -> {
                        if (retryCount > 2) {
                            log.info("retry end but still not success! ex: {} ",ex.getMessage());
                            return false;
                        }

                        if (ex instanceof NoHttpResponseException) {
                            log.info("retrying reach to {} times,cause NoHttpResponse, ex: {} ",retryCount,ex.getMessage());
                            return true;
                        }
                        if (ex instanceof InterruptedIOException){
                            log.info("retrying reach to {} times,cause InterruptedIOException, ex: {} ",retryCount,ex.getMessage());
                            return true;
                        }

                        return false;
                    })
                // 超时控制
                .setDefaultRequestConfig(RequestConfig.custom()
                        // 从连接池获取连接的超时时间
                        .setConnectionRequestTimeout(1*1000)
                        // 建立连接的超时时间，超时后抛ConnectionTimeOutException
                        .setConnectTimeout(1*1000)
                        // 即为SO_TIMEOUT 超时会抛SocketTimeOutException
                        .setSocketTimeout(10*1000).build())
                .build();
    }

    public static String doGet(String url, Map<String, String> map) throws IOException, URISyntaxException {
        List<NameValuePair> nvps = turnMapToNameValuePair(map);
        URIBuilder uriBuilder = new URIBuilder(url);
        uriBuilder.addParameters(nvps);
        HttpGet httpGet = new HttpGet(uriBuilder.build());
        HttpResponse response = httpclient.execute(httpGet);
        return EntityUtils.toString(response.getEntity(), Consts.UTF_8);
    }

    public static String doPostWithFormData(String url, Map<String, String> map) throws IOException {
        HttpPost httpPost = new HttpPost(url);
        if (null != map && map.size() > 0) {
            MultipartEntityBuilder builder = MultipartEntityBuilder.create();
            for (Map.Entry<String, String> entry : map.entrySet()) {
                if (entry.getValue() == null)
                    continue;
                builder.addTextBody(entry.getKey(), entry.getValue().toString(), ContentType.TEXT_PLAIN);
            }
            HttpEntity entity = builder.build();
            httpPost.setEntity(entity);
        }
        HttpResponse response = httpclient.execute(httpPost);
        return EntityUtils.toString(response.getEntity(), Consts.UTF_8);
    }

    public static String doPostWithXWwwFormUrlencoded(String url, Map<String, String> map) throws IOException {
        HttpPost httpPost = new HttpPost(url);
        if (null != map && map.size() > 0) {
            List<NameValuePair> nameValuePairs = turnMapToNameValuePair(map);
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs, Consts.UTF_8));
        }
        HttpResponse response = httpclient.execute(httpPost);
        return EntityUtils.toString(response.getEntity(), Consts.UTF_8);
    }

    public static String doPostWithJson(String url, String json) throws IOException {
        HttpPost httpPost = new HttpPost(url);
        StringEntity stringEntity = new StringEntity(json, ContentType.APPLICATION_JSON);
        httpPost.setEntity(stringEntity);
        HttpResponse response = httpclient.execute(httpPost);
        return EntityUtils.toString(response.getEntity(), Consts.UTF_8);
    }

    private static List<NameValuePair> turnMapToNameValuePair(Map<String, String> params) {
        List<NameValuePair> list = new ArrayList<NameValuePair>();
        if (params == null) {
            return list;
        }
        Iterator<String> itr = params.keySet().iterator();
        while (itr.hasNext()) {
            String key = itr.next();
            String value = params.get(key);
            list.add(new BasicNameValuePair(key, value));
        }
        return list;
    }
}