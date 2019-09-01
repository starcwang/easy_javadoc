package com.star.easydoc.util;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import com.intellij.openapi.diagnostic.Logger;
import org.apache.commons.codec.Charsets;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.HttpClientUtils;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

/**
 * @author wangchao
 * @date 2019/09/01
 */
public class HttpUtil {
    private static final Logger LOGGER = Logger.getInstance(HttpUtil.class);
    private static final int CONNECT_TIMEOUT = 300;
    private static final int SOCKET_TIMEOUT = 1000;

    public static String get(String url) {
        if (StringUtils.isBlank(url)) {
            return null;
        }
        String result = null;
        CloseableHttpClient httpclient = null;
        CloseableHttpResponse response = null;
        try {
            httpclient = HttpClients.createDefault();
            HttpGet httpGet = new HttpGet(url);
            httpGet.setConfig(RequestConfig.custom().setSocketTimeout(SOCKET_TIMEOUT).setConnectTimeout(CONNECT_TIMEOUT).build());
            response = httpclient.execute(httpGet);
            result = EntityUtils.toString(response.getEntity());
        } catch (IOException e) {
            LOGGER.warn("请求" + url + "异常", e);
        } finally {
            HttpClientUtils.closeQuietly(response);
            HttpClientUtils.closeQuietly(httpclient);
        }
        return result;
    }

    public static String encode(String word) {
        try {
            return URLEncoder.encode(word, Charsets.UTF_8.name());
        } catch (UnsupportedEncodingException e) {
            LOGGER.warn("url转义失败,word=" + word, e);
            return StringUtils.EMPTY;
        }
    }

}
