package com.star.easydoc.service.translator.impl;

import java.nio.charset.StandardCharsets;
import java.util.Map.Entry;
import java.util.Random;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.diagnostic.Logger;
import com.star.easydoc.config.EasyJavadocConfigComponent;
import com.star.easydoc.model.EasyJavadocConfiguration;
import com.star.easydoc.util.HttpUtil;
import com.star.easydoc.util.JsonUtil;

/**
 * 腾讯翻译
 *
 * @author wangchao
 * @date 2020/08/26
 */
public class TencentTranslator extends AbstractTranslator {
    private static final Logger LOGGER = Logger.getInstance(TencentTranslator.class);
    private EasyJavadocConfiguration config = ServiceManager.getService(EasyJavadocConfigComponent.class).getState();

    @Override
    public String translateEn2Ch(String text) {
        try {
            TencentResponse response = get(text, "zh");
            return response.getTargetText();
        } catch (Exception e) {
            LOGGER.error("请求腾讯翻译接口异常", e);
        }
        return "";
    }

    @Override
    public String translateCh2En(String text) {
        try {
            TencentResponse response = get(text, "en");
            return response.getTargetText();
        } catch (Exception e) {
            LOGGER.error("请求腾讯翻译接口异常", e);
        }
        return "";
    }

    public TencentResponse get(String text, String target) throws Exception {
        TencentResponse response = null;
        for (int i = 0; i < 10; i++) {
            SortedMap<String, Object> params = new TreeMap<>();
            params.put("Nonce", new Random().nextInt(java.lang.Integer.MAX_VALUE));
            params.put("Timestamp", System.currentTimeMillis() / 1000);
            params.put("Region", "ap-beijing");
            params.put("SecretId", config.getSecretId());
            params.put("Action", "TextTranslate");
            params.put("Version", "2018-03-21");
            params.put("SourceText", text);
            params.put("Source", "auto");
            params.put("Target", target);
            params.put("ProjectId", 0);

            String str2sign = getStringToSign("GET", "tmt.tencentcloudapi.com", params);
            String signature = sign(str2sign, config.getSecretKey(), "HmacSHA1");
            params.put("Signature", signature);
            TencentResult result = JsonUtil
                .fromJson(HttpUtil.get("https://tmt.tencentcloudapi.com", params), TencentResult.class);
            response = result == null ? null : result.getResponse();
            if (response == null || (response.getError() != null && "RequestLimitExceeded".equals(response.getError().getCode()))) {
                Thread.sleep(500);
            } else {
                break;
            }
        }
        return response;
    }

    public static String sign(String s, String key, String method) throws Exception {
        Mac mac = Mac.getInstance(method);
        SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), mac.getAlgorithm());
        mac.init(secretKeySpec);
        byte[] hash = mac.doFinal(s.getBytes(StandardCharsets.UTF_8));
        return DatatypeConverter.printBase64Binary(hash);
    }

    public static String getStringToSign(String method, String endpoint, SortedMap<String, Object> params) {
        StringBuilder s2s = new StringBuilder();
        s2s.append(method).append(endpoint).append("/?");
        for (Entry<String, Object> e : params.entrySet()) {
            s2s.append(e.getKey()).append("=").append(params.get(e.getKey()).toString()).append("&");
        }
        return s2s.substring(0, s2s.length() - 1);
    }

    private static class TencentResult {
        @JsonProperty("Response")
        private TencentResponse response;

        public TencentResponse getResponse() {
            return response;
        }

        public void setResponse(TencentResponse response) {
            this.response = response;
        }
    }

    private static class TencentResponse {
        @JsonProperty("RequestId")
        private String requestId;
        @JsonProperty("Source")
        private String source;
        @JsonProperty("Target")
        private String target;
        @JsonProperty("TargetText")
        private String targetText;
        @JsonProperty("Error")
        private TencentError error;

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

    private static class TencentError {
        @JsonProperty("Code")
        private String code;
        @JsonProperty("Message")
        private String message;

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
