package com.star.easydoc.service.translator.impl;

import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.annotation.JSONField;

import com.intellij.openapi.diagnostic.Logger;
import com.star.easydoc.common.util.HttpUtil;
import org.apache.commons.lang3.StringUtils;

/**
 * 腾讯翻译
 *
 * @author wangchao
 * @date 2020/08/26
 */
public class TencentTranslator extends AbstractTranslator {
    // 日志记录器。它的类型是TencentTranslator类的实例，通过调用Logger.getInstance方法获取。
    // 通常，日志记录器用于在应用程序中记录消息、警告和错误等信息，以便进行调试和故障排除。
    // 在这里，日志记录器被命名为TencentTranslator，可能是用于记录与腾讯翻译服务相关的信息。
    private static final Logger LOGGER = Logger.getInstance(TencentTranslator.class);

    // 该方法接受一个英文文本作为参数，并返回一个中文翻译后的结果。
    // get方法可能是一个用于向腾讯翻译服务发送请求并获取翻译结果的方法。
    @Override
    public String translateEn2Ch(String text) {
        TencentResponse response = get(text, "zh");
        return response == null ? StringUtils.EMPTY : response.getTargetText();
    }

    // 该方法接受一个中文文本作为参数，并返回一个英文翻译后的结果。
    // get方法可能是一个用于向腾讯翻译服务发送请求并获取翻译结果的方法。
    @Override
    public String translateCh2En(String text) {
        TencentResponse response = get(text, "en");
        return response == null ? StringUtils.EMPTY : response.getTargetText();
    }

    // 腾讯翻译API的核心实现
    private TencentResponse get(String text, String target) {
        TencentResponse response = null;
        String json = null;
        try {
            // 循环了10次还没有成功，则会返回null
            for (int i = 0; i < 10; i++) {
                // 将以下参数存放到Map
                SortedMap<String, Object> params = new TreeMap<>();
                // 生成随机数Nonce
                params.put("Nonce", new SecureRandom().nextInt(java.lang.Integer.MAX_VALUE));
                // 获取当前时间戳Timestamp
                params.put("Timestamp", System.currentTimeMillis() / 1000);
                params.put("Region", "ap-beijing");
                params.put("SecretId", getConfig().getSecretId());
                params.put("Action", "TextTranslate");
                params.put("Version", "2018-03-21");
                params.put("SourceText", text);
                params.put("Source", "auto");
                params.put("Target", target);
                params.put("ProjectId", 0);

                // 构建待签名字符串str2sign
                String str2sign = getStringToSign("GET", "tmt.tencentcloudapi.com", params);
                // 对str2sign进行签名
                String signature = sign(str2sign, getConfig().getSecretKey(), "HmacSHA1");
                // 签名结果添加到params中
                params.put("Signature", signature);
                // 使用HttpUtil工具类发送GET请求，将params作为请求参数
                json = HttpUtil.get("https://tmt.tencentcloudapi.com", params);
                // 将返回的json数据解析为TencentResult对象
                TencentResult result = JSON.parseObject(json, TencentResult.class);
                // 获取其中的response字段作为翻译结果
                response = result == null ? null : result.getResponse();
                // 判断response和response中的error以及error中的code，满足条件调用Thread.sleep(500)，让程序暂停500毫秒后再进行下一次尝试
                if (response == null || (response.getError() != null && "RequestLimitExceeded".equals(
                    response.getError().getCode()))) {
                    Thread.sleep(500);
                } else {
                    // 成功则跳出循环返回response
                    break;
                }
            }
        } catch (Exception e) {
            LOGGER.error("请求腾讯翻译接口异常,response=" + json, e);
        }
        return response;
    }

    // 对字符串s进行HmacSHA1签名的实现
    private static String sign(String s, String key, String method) throws Exception {
        // 获取到指定算法（HmacSHA1）的Mac对象
        Mac mac = Mac.getInstance(method);
        // 将key转换为适合于指定算法的密钥规范。密钥规范使用key.getBytes(StandardCharsets.UTF_8)获取key的字节数组，并指定了使用mac.getAlgorithm()返回的算法。
        SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), mac.getAlgorithm());
        // 初始化Mac对象，并传入密钥规范
        mac.init(secretKeySpec);
        // 对字符串s进行签名，得到签名结果的字节数组hash
        byte[] hash = mac.doFinal(s.getBytes(StandardCharsets.UTF_8));
        // 将签名结果进行Base64编码，并以字符串形式返回。
        return Base64.getEncoder().encodeToString(hash);
    }

    // 生成待签名字符串的方法
    private static String getStringToSign(String method, String endpoint, SortedMap<String, Object> params) {
        StringBuilder s2s = new StringBuilder();
        // 将请求方法 method、请求终端 endpoint 和 "/?" 拼接到 s2s 中
        s2s.append(method).append(endpoint).append("/?");
        // 遍历参数列表 params 的每个键值对，将键和对应的值使用等号连接，并在末尾添加"&"符号，然后追加到 s2s 中
        for (Entry<String, Object> e : params.entrySet()) {
            s2s.append(e.getKey()).append("=").append(params.get(e.getKey()).toString()).append("&");
        }
        // 返回 s2s 去掉最后一个字符即去掉末尾多余的"&"符号之后的字符串
        return s2s.substring(0, s2s.length() - 1);
    }

    // 静态内部类，用于存储腾讯云翻译接口的响应结果
    private static class TencentResult {
        // 腾讯翻译接口的返回结果中，包含了一个名为 "Response" 的字段
        // 该字段对应的值是一个 TencentResponse 对象，其中包含了翻译结果的相关信息，如请求ID、源语言、目标语言、目标文本以及错误信息等。
        @JSONField(name = "Response")
        private TencentResponse response;

        // 提供get和set方法，用于获取和设置响应结果中的 TencentResponse 对象
        public TencentResponse getResponse() {
            return response;
        }

        public void setResponse(TencentResponse response) {
            this.response = response;
        }
    }

    // 用于解析腾讯翻译API返回结果的内部类TencentResponse
    private static class TencentResponse {
        // 包含请求ID，源语言代码，目标语言代码，翻译后的文本内容，错误信息
        @JSONField(name = "RequestId")
        private String requestId;
        @JSONField(name = "Source")
        private String source;
        @JSONField(name = "Target")
        private String target;
        @JSONField(name = "TargetText")
        private String targetText;
        @JSONField(name = "Error")
        private TencentError error;

        // 提供get和set方法来访问和设置这些字段的值
        public String getRequestId() {
            return requestId;
        }

        public void setRequestId(String requestId) {
            this.requestId = requestId;
        }

        public String getSource() {
            return source;
        }

        public void setSource(String source) {
            this.source = source;
        }

        public String getTarget() {
            return target;
        }

        public void setTarget(String target) {
            this.target = target;
        }

        public String getTargetText() {
            return targetText;
        }

        public void setTargetText(String targetText) {
            this.targetText = targetText;
        }

        public TencentError getError() {
            return error;
        }

        public void setError(TencentError error) {
            this.error = error;
        }
    }

    // 用于封装腾讯云 API 返回的错误信息
    private static class TencentError {
        // 包含两个属性code和message，分别表示错误码和错误信息
        @JSONField(name = "Code")
        private String code;
        @JSONField(name = "Message")
        private String message;

        // 提供get和set方法来访问和设置错误信息
        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }
}
