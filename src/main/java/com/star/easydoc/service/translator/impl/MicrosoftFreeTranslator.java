package com.star.easydoc.service.translator.impl;

import java.util.Base64;
import java.util.Map;
import java.util.Objects;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;

import com.google.common.collect.Maps;
import com.intellij.openapi.diagnostic.Logger;
import com.star.easydoc.common.util.HttpUtil;
import org.apache.commons.lang3.StringUtils;

/**
 * 微软免费翻译
 *
 * @author wangchao
 * @date 2023/09/13
 */
public class MicrosoftFreeTranslator extends AbstractTranslator {
    //继承AbstractTranslator类
    /** 日志 */
    private static final Logger LOGGER = Logger.getInstance(MicrosoftFreeTranslator.class);
    //声明了一个静态常量LOGGER来记录日志
    /** 令牌 */
    private String token = null;

    /** 令牌过期时间 */
    private Long exp = null;
    //定义了令牌 token 和令牌过期时间 exp，初始值为 null。
    /** 重试次数 */
    private static final int RETRY_TIMES = 10;
    //定义了重试次数 RETRY_TIMES，值为 10。
    /** 锁 */
    private static final Object LOCK = new Object();
    //定义了锁对象 LOCK，用于同步获取令牌。
    /** 令牌url */
    /**
     * x下面三个定义了令牌 URL TOKEN_URL，英译中 URL EN2CH_URL 和中译英 URL CH2EN_URL。
     */
    private static final String TOKEN_URL = "https://edge.microsoft.com/translate/auth";
    /** 英译中url */
    private static final String EN2CH_URL
            = "https://api.cognitive.microsofttranslator.com/translate?api-version=3.0&textType=plain&from=en&to=zh-Hans";

    /** 中译英url */
    private static final String CH2EN_URL
            = "https://api.cognitive.microsofttranslator.com/translate?api-version=3.0&textType=plain&from=zh-Hans&to=en";

    /**
     * 刷新令牌
     * 该方法的作用是刷新令牌，即获取新的令牌并更新相关的令牌信息。
     * 确保在使用翻译功能之前，存在有效的令牌，并且令牌的过期时间未到。如果令牌无效或过期，则通过重新获取令牌的方式来刷新令牌，以保证后续的翻译操作能够正常进行。
     */
    private void refreshToken() {
        String thisToken = null;
        //声明一个变量 thisToken，用于存储获取到的令牌。
        for (int i = 1; i <= RETRY_TIMES; i++) {
            try {
                thisToken = HttpUtil.get(TOKEN_URL, 1000, 3000);
                if (thisToken != null && !thisToken.isEmpty()) {
                    break;
                }
            } catch (Exception e) {
                LOGGER.warn("获取微软翻译token失败,重试第" + i + "次");
            }
        }
        //在每次循环中，通过调用 HttpUtil.get() 方法发送 HTTP GET 请求到 TOKEN_URL，并设置连接超时时间为 1000 毫秒，读取超时时间为 3000 毫秒。
        //如果成功获取到令牌 thisToken，并且令牌不为 null 或空字符串，则跳出循环。
        //如果在获取令牌时发生异常，捕获异常并使用日志记录器 LOGGER 输出警告信息，指示获取令牌失败，并记录重试次数。
        if (thisToken == null || thisToken.isEmpty()) {
            throw new RuntimeException("重试" + RETRY_TIMES + "次后获取微软token仍失败");
        }
        //在循环结束后，检查 thisToken 是否为 null 或空字符串，如果是，则抛出运行时异常，表示重试多次后仍然无法获取到令牌。
        Long thisExp = JSON.parseObject(Base64.getUrlDecoder().decode(thisToken.split("\\.")[1])).getLong("exp");
        //解析令牌中的过期时间 thisExp，通过将令牌字符串使用 Base64 解码后，获取其中的第二部分，并将其转换为 JSON 对象，然后从中提取出过期时间。
        token = thisToken;
        exp = thisExp;
        //将获取到的令牌 thisToken 和过期时间 thisExp 分别赋值给成员变量 token 和 exp。
    }

    /**
     * 获取令牌
     * 该方法的作用是获取令牌，如果存在有效的令牌则直接返回，否则会刷新令牌并返回新的令牌。
     * 获取一个有效的令牌，以确保在进行翻译操作时可以使用有效的令牌。
     * 通过双重检查锁定的方式，保证了在多线程环境下仅有一个线程可以刷新令牌，而其他线程会等待刷新完成后继续执行获取令牌的操作。这样可以避免多个线程同时刷新令牌，提高了并发安全性。
     */
    private String getToken() {
        if (token != null && exp != null && exp * 1000L > System.currentTimeMillis()) {
            return token;
        }
        synchronized (LOCK) {
            if (token != null && exp != null && exp * 1000L > System.currentTimeMillis()) {
                return token;
            }
            refreshToken();
        }
        return token;
        //首先，通过一系列条件判断来检查当前令牌的有效性：
        //检查 token 和 exp 是否都不为 null，以及当前时间是否在令牌过期时间之前 (exp * 1000L > System.currentTimeMillis())。
        //如果满足这些条件，则说明当前令牌有效，直接返回 token。
        //如果令牌无效，进入同步代码块，使用 LOCK 对象进行同步操作，确保只有一个线程可以进入此代码块。
        //在同步代码块内部，再次进行令牌有效性的判断，以防止其他线程在等待期间已经刷新了令牌。
        //如果令牌仍然无效，则调用 refreshToken() 方法刷新令牌。
        //在同步代码块结束后，无论是令牌有效还是刷新了新的令牌，都会返回当前的 token。
    }

    /**
     *
     * 父类方法的重写，并调用translate方法
     */
    @Override
    protected String translateCh2En(String text) {
        return translate(CH2EN_URL, text);
    }

    @Override
    protected String translateEn2Ch(String text) {
        return translate(EN2CH_URL, text);
    }

    /**
     *
     * 该方法的作用是发送翻译请求并解析响应，提取出翻译结果。
     * 在请求过程中，首先构建请求体 JSON 数据，然后设置请求头中的授权信息，最后通过 HttpUtil.postJson() 方法发送 HTTP POST 请求。
     * 在异常处理中，记录错误信息，并返回一个空字符串表示翻译失败。在解析响应时，提取出翻译结果并返回。
     */
    private String translate(String url, String text) {
        String json = null;
        try {
            JSONObject textObject = new JSONObject();
            textObject.put("Text", text);
            JSONArray body = new JSONArray();
            body.add(textObject);
            Map<String, String> headers = Maps.newHashMap();
            headers.put("Authorization", "Bearer " + getToken());
            json = HttpUtil.postJson(url, headers, JSON.toJSONString(body));
            JSONArray response = JSON.parseArray(json);
            //创建一个 JSONObject 对象 textObject，并将待翻译的文本放入其中。
            //创建一个 JSONArray 对象 body，并将 textObject 放入其中。
            //创建一个 Map 对象 headers，用于存储请求头信息。
            //在 headers 中添加 Authorization 请求头，其值为 "Bearer " 加上通过 getToken() 方法获取到的令牌。
            //使用 HttpUtil.postJson() 方法发送 HTTP POST 请求，将 url、headers、以及 body 的 JSON 字符串作为参数传递。
            //将响应的 JSON 字符串存储到 json 变量中。
            return Objects.requireNonNull(response).getJSONObject(0).getJSONArray("translations").getJSONObject(0).getString("text");
            //将 json 字符串解析为 JSON 数组 response。
            //获取数组中的第一个元素（索引为 0），并将其转换为 JSON 对象。
            //从 JSON 对象中获取名为 "translations" 的 JSON 数组，再获取其中的第一个元素（索引为 0）。
            //从 JSON 对象中获取名为 "text" 的字符串，即翻译结果
        } catch (Exception e) {
            LOGGER.error("请求微软免费翻译接口异常:请检查本地网络是否可连接外网,response=" + json, e);
            return StringUtils.EMPTY;
        }
    }
}
