package com.liuapi.http.httpclient4;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.*;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.config.SocketConfig;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.File;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.URISyntaxException;
import java.util.*;
import java.util.stream.Collectors;

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
                .setRetryHandler((ex, retryCount, httpContext) -> {
                    if (retryCount > 2) {
                        log.info("retry end but still not success! ex: {} ", ex.getMessage());
                        return false;
                    }

                    if (ex instanceof NoHttpResponseException) {
                        log.info("retrying reach to {} times,cause NoHttpResponse, ex: {} ", retryCount, ex.getMessage());
                        return true;
                    }
                    if (ex instanceof InterruptedIOException) {
                        log.info("retrying reach to {} times,cause InterruptedIOException, ex: {} ", retryCount, ex.getMessage());
                        return true;
                    }

                    return false;
                })
                // 超时控制
                .setDefaultRequestConfig(RequestConfig.custom()
                        // 从连接池获取连接的超时时间
                        .setConnectionRequestTimeout(1 * 1000)
                        // 建立连接的超时时间，超时后抛ConnectionTimeOutException
                        .setConnectTimeout(1 * 1000)
                        // 即为SO_TIMEOUT 超时会抛SocketTimeOutException
                        .setSocketTimeout(10 * 1000).build())
                .build();
    }

    public static String doGet(String url, Map<String, String> params,Map<String, String> headers) throws IOException, URISyntaxException {
        URIBuilder uriBuilder = new URIBuilder(url);
        List<NameValuePair> nvps = turnMapToNameValuePairs(params);
        uriBuilder.addParameters(nvps);
        HttpGet httpGet = new HttpGet(uriBuilder.build());
        httpGet.setHeaders(turnMapToHeaders(headers));
        HttpResponse response = httpclient.execute(httpGet);
        return EntityUtils.toString(response.getEntity(), Consts.UTF_8);
    }

    public static String doPostWithFormData(String url, Map<String, Object> params,Map<String, String> headers) throws IOException {
        HttpPost httpPost = new HttpPost(url);
        httpPost.setHeaders(turnMapToHeaders(headers));
        if (null != params && params.size() > 0) {
            MultipartEntityBuilder builder = MultipartEntityBuilder.create();
            for (Map.Entry<String, Object> entry : params.entrySet()) {
                Object value = entry.getValue();
                if (value == null) {
                    continue;
                }
                if(value instanceof byte[]){
                    /**
                     * Note: SpringMVC接收文件上传时，filename不能为空
                     */
                    builder.addBinaryBody(entry.getKey(),(byte[]) value,ContentType.DEFAULT_BINARY,entry.getKey());
                }else if(value instanceof File){
                    builder.addBinaryBody(entry.getKey(),(File)value);
                }else{
                    builder.addTextBody(entry.getKey(), String.valueOf(value), ContentType.TEXT_PLAIN);
                }
            }
            HttpEntity entity = builder.build();
            httpPost.setEntity(entity);
        }
        HttpResponse response = httpclient.execute(httpPost);
        return EntityUtils.toString(response.getEntity(), Consts.UTF_8);
    }

    public static String doPostWithXWwwFormUrlencoded(String url, Map<String, String> params, Map<String, String> headers) throws IOException {
        HttpPost httpPost = new HttpPost(url);
        httpPost.setHeaders(turnMapToHeaders(headers));
        if (null != params && params.size() > 0) {
            List<NameValuePair> nameValuePairs = turnMapToNameValuePairs(params);
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs, Consts.UTF_8));
        }
        HttpResponse response = httpclient.execute(httpPost);
        return EntityUtils.toString(response.getEntity(), Consts.UTF_8);
    }

    public static String doPostWithJson(String url, String json, Map<String, String> headers) throws IOException {
        HttpPost httpPost = new HttpPost(url);
        httpPost.setHeaders(turnMapToHeaders(headers));
        StringEntity stringEntity = new StringEntity(json, ContentType.APPLICATION_JSON);
        httpPost.setEntity(stringEntity);
        HttpResponse response = httpclient.execute(httpPost);
        return EntityUtils.toString(response.getEntity(), Consts.UTF_8);
    }

    private static List<NameValuePair> turnMapToNameValuePairs(Map<String, String> params) {
        if (params == null) {
            return Collections.emptyList();
        }
        return params.entrySet()
                .stream()
                .map(e -> new BasicNameValuePair(e.getKey(), e.getValue()))
                .collect(Collectors.toList());
    }

    private static Header[] turnMapToHeaders(Map<String, String> params) {
        if (params == null) {
            return null;
        }
        return params.entrySet()
                .stream()
                .map(e -> new BasicHeader(e.getKey(), e.getValue()))
                .toArray(BasicHeader[]::new);
    }
}