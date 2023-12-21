package com.star.easydoc.common.util;

import com.google.common.collect.Maps;
import com.intellij.openapi.diagnostic.Logger;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.HttpClientUtils;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

/**
 * HTTP工具类
 *
 * @author wangchao
 * @date 2019/09/01
 */
public class HttpUtil {

    // 日志记录器
    private static final Logger LOGGER = Logger.getInstance(HttpUtil.class);
    // 连接超时时间
    private static final int CONNECT_TIMEOUT = 1000;
    // 读取超时时间
    private static final int SOCKET_TIMEOUT = 1000;

    // 私有构造方法，防止实例化
    private HttpUtil() {
    }

    /**
     * GET请求
     *
     * @param url URL
     * @return 响应结果字符串
     */
    public static String get(String url) {
        // 如果URL为空，返回null
        if (StringUtils.isBlank(url)) {
            return null;
        }

        // 结果字符串
        String result = null;
        // 创建HttpClient
        CloseableHttpClient httpclient = null;
        // 响应对象
        CloseableHttpResponse response = null;
        try {
            httpclient = HttpClients.createDefault();
            // 创建HttpGet请求
            HttpGet httpGet = new HttpGet(url);
            // 设置连接超时和读取超时时间
            httpGet.setConfig(RequestConfig.custom().setSocketTimeout(SOCKET_TIMEOUT).setConnectTimeout(CONNECT_TIMEOUT).build());
            // 执行请求，获取响应
            response = httpclient.execute(httpGet);
            // 获取响应内容并转为字符串
            result = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            LOGGER.warn("请求" + url + "异常", e);
        } finally {
            // 关闭响应和HttpClient
            HttpClientUtils.closeQuietly(response);
            HttpClientUtils.closeQuietly(httpclient);
        }

        // 返回结果字符串
        return result;
    }

    /**
     * GET请求
     *
     * @param url            URL
     * @param connectTimeout 连接超时时间
     * @param socketTimeout  读取超时时间
     * @return 响应结果字符串
     */
    public static String get(String url, int connectTimeout, int socketTimeout) {
        // 如果URL为空，返回null
        if (StringUtils.isBlank(url)) {
            return null;
        }

        // 结果字符串
        String result = null;
        // 创建HttpClient
        CloseableHttpClient httpclient = null;
        // 响应对象
        CloseableHttpResponse response = null;
        try {
            httpclient = HttpClients.createDefault();
            // 创建HttpGet请求
            HttpGet httpGet = new HttpGet(url);
            // 设置连接超时和读取超时时间
            httpGet.setConfig(RequestConfig.custom().setSocketTimeout(socketTimeout).setConnectTimeout(connectTimeout).build());
            // 执行请求，获取响应
            response = httpclient.execute(httpGet);
            // 获取响应内容并转为字符串
            result = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            LOGGER.warn("请求" + url + "异常", e);
        } finally {
            // 关闭响应和HttpClient
            HttpClientUtils.closeQuietly(response);
            HttpClientUtils.closeQuietly(httpclient);
        }

        // 返回结果字符串
        return result;
    }

    /**
     * GET请求
     *
     * @param url    URL
     * @param params 参数
     * @return 响应结果字符串
     */
    public static String get(String url, Map<String, Object> params) {
        // 如果URL为空，返回null
        if (StringUtils.isBlank(url)) {
            return null;
        }

        // 将参数拼接到URL后面
        String paramStr = params.entrySet().stream()
                .map(e -> e.getKey() + "=" + encode(String.valueOf(e.getValue())))
                .collect(Collectors.joining("&"));
        url = url.contains("?") ? url + "&" + paramStr : url + "?" + paramStr;

        // 发起GET请求并返回结果
        return get(url);
    }

    /**
     * 编码
     *
     * @param word 待编码的字符串
     * @return 编码后的字符串
     */
    public static String encode(String word) {
        try {
            return URLEncoder.encode(word, StandardCharsets.UTF_8.name());
        } catch (UnsupportedEncodingException e) {
            LOGGER.warn("URL转义失败，word=" + word, e);
            return StringUtils.EMPTY;
        }
    }

    /**
     * POST请求
     *
     * @param url     URL
     * @param headers 请求头
     * @param body    请求体内容
     * @return 响应结果字符串
     */
    public static String post(String url, Map<String, String> headers, String body) {
        // 如果URL为空，返回null
        if (StringUtils.isBlank(url)) {
            return null;
        }

         // 结果字符串
        String result = null;
        // 创建HttpClient
        CloseableHttpClient httpclient = null;
        // 响应对象
        CloseableHttpResponse response = null;
        try {
            httpclient = HttpClients.createDefault();
            // 创建HttpPost请求
            HttpPost httpPost = new HttpPost(url);

            // 设置请求头
            if (headers != null) {
                for (Entry<String, String> e : headers.entrySet()) {
                    httpPost.addHeader(e.getKey(), e.getValue());
                }
            }

            // 设置连接超时和读取超时时间
            httpPost.setConfig(RequestConfig.custom().setSocketTimeout(SOCKET_TIMEOUT).setConnectTimeout(CONNECT_TIMEOUT).build());
            // 设置请求体
            httpPost.setEntity(new StringEntity(body, StandardCharsets.UTF_8));
            // 执行请求，获取响应
            response = httpclient.execute(httpPost);
            // 获取响应内容并转为字符串
            result = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            LOGGER.warn("请求" + url + "异常", e);
        } finally {
// 关闭响应和HttpClient
            HttpClientUtils.closeQuietly(response);
            HttpClientUtils.closeQuietly(httpclient);
        }

// 返回结果字符串
        return result;
    }

    /**
     * POST请求（JSON）
     *
     * @param url     URL
     * @param headers 请求头
     * @param body    请求体内容
     * @return 响应结果字符串
     */
    public static String postJson(String url, Map<String, String> headers, String body) {
        // 如果headers为空，创建一个新的Map对象
        if (headers == null) {
            headers = Maps.newHashMap();
        }

        // 设置Content-Type为application/json;charset=utf-8
        headers.put("Content-Type", "application/json;charset=utf-8");

        // 发起POST请求并返回结果
        return post(url, headers, body);
    }
}