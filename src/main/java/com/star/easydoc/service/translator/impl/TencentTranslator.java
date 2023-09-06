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
    private static final Logger LOGGER = Logger.getInstance(TencentTranslator.class);

    @Override
    public String translateEn2Ch(String text) {
        TencentResponse response = get(text, "zh");
        return response == null ? StringUtils.EMPTY : response.getTargetText();
    }

    @Override
    public String translateCh2En(String text) {
        TencentResponse response = get(text, "en");
        return response == null ? StringUtils.EMPTY : response.getTargetText();
    }

    private TencentResponse get(String text, String target) {
        TencentResponse response = null;
        String json = null;
        try {
            for (int i = 0; i < 10; i++) {
                SortedMap<String, Object> params = new TreeMap<>();
                params.put("Nonce", new SecureRandom().nextInt(java.lang.Integer.MAX_VALUE));
                params.put("Timestamp", System.currentTimeMillis() / 1000);
                params.put("Region", "ap-beijing");
                params.put("SecretId", getConfig().getSecretId());
                params.put("Action", "TextTranslate");
                params.put("Version", "2018-03-21");
                params.put("SourceText", text);
                params.put("Source", "auto");
                params.put("Target", target);
                params.put("ProjectId", 0);

                String str2sign = getStringToSign("GET", "tmt.tencentcloudapi.com", params);
                String signature = sign(str2sign, getConfig().getSecretKey(), "HmacSHA1");
                params.put("Signature", signature);
                json = HttpUtil.get("https://tmt.tencentcloudapi.com", params);
                TencentResult result = JSON.parseObject(json, TencentResult.class);
                response = result == null ? null : result.getResponse();
                if (response == null || (response.getError() != null && "RequestLimitExceeded".equals(
                    response.getError().getCode()))) {
                    Thread.sleep(500);
                } else {
                    break;
                }
            }
        } catch (Exception e) {
            LOGGER.error("请求腾讯翻译接口异常,response=" + json, e);
        }
        return response;
    }

    private static String sign(String s, String key, String method) throws Exception {
        Mac mac = Mac.getInstance(method);
        SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), mac.getAlgorithm());
        mac.init(secretKeySpec);
        byte[] hash = mac.doFinal(s.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(hash);
    }

    private static String getStringToSign(String method, String endpoint, SortedMap<String, Object> params) {
        StringBuilder s2s = new StringBuilder();
        s2s.append(method).append(endpoint).append("/?");
        for (Entry<String, Object> e : params.entrySet()) {
            s2s.append(e.getKey()).append("=").append(params.get(e.getKey()).toString()).append("&");
        }
        return s2s.substring(0, s2s.length() - 1);
    }

    private static class TencentResult {
        @JSONField(name = "Response")
        private TencentResponse response;

        public TencentResponse getResponse() {
            return response;
        }

        public void setResponse(TencentResponse response) {
            this.response = response;
        }
    }

    private static class TencentResponse {
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
        @JSONField(name = "Code")
        private String code;
        @JSONField(name = "Message")
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
