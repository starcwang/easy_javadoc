package com.star.easydoc.service.translator.impl;

import java.io.IOException;
import java.net.URL;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.SimpleTimeZone;
import java.util.UUID;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.intellij.openapi.diagnostic.Logger;
import com.star.easydoc.service.translator.Translator;
import com.star.easydoc.util.HttpUtil;
import com.star.easydoc.util.JsonUtil;
import org.apache.commons.lang3.StringUtils;

/**
 * 阿里云翻译
 *
 * @author wangchao
 * @date 2019/09/01
 */
public class AliyunTranslator extends AbstractTranslator {

    private static final Logger LOGGER = Logger.getInstance(AliyunTranslator.class);

    private static final String URL = "http://mt.cn-hangzhou.aliyuncs.com/api/translate/web/ecommerce";
    /** akId */
    private String accessKeyId;
    /** akSecret */
    private String accessKeySecret;

    @Override
    protected String translateCh2En(String text) {
        return translate("zh", "en", text);
    }

    @Override
    protected String translateEn2Ch(String text) {
        return translate("en", "zh", text);
    }

    /**
     * 翻译
     *
     * @param sourceLanguage 源语言
     * @param targetLanguage 目标语言
     * @param text 文本
     * @return {@link String}
     */
    private String translate(String sourceLanguage, String targetLanguage, String text) {
        AliyunRequestVO request = new AliyunRequestVO();
        request.setSourceLanguage(sourceLanguage);
        request.setTargetLanguage(targetLanguage);
        request.setSourceText(text);
        try {
            String json = sendPost(URL, JsonUtil.toJson(request), accessKeyId, accessKeySecret);
            AliyunResponseVO response = JsonUtil.fromJson(json, AliyunResponseVO.class);
            return Objects.requireNonNull(response).getData().getTranslated();
        } catch (Exception e) {
            LOGGER.error("请求阿里云翻译接口异常：请检查本地网络是否可连接外网，也有可能被阿里云限流", e);
            return StringUtils.EMPTY;
        }
    }

    /**
     * 计算MD5+BASE64
     */
    private String md5AndBase64(String s) throws NoSuchAlgorithmException {
        if (s == null) {
            return null;
        }
        String encodeStr = "";
        byte[] utfBytes = s.getBytes();
        MessageDigest mdTemp;
        mdTemp = MessageDigest.getInstance("MD5");
        mdTemp.update(utfBytes);
        byte[] md5Bytes = mdTemp.digest();
        Base64.Encoder encoder = Base64.getEncoder();
        encodeStr = encoder.encodeToString(md5Bytes);
        return encodeStr;
    }

    /**
     * 计算 HMAC-SHA1
     */
    private String hmacSha1(String data, String key) throws NoSuchAlgorithmException, InvalidKeyException {
        String result;
        SecretKeySpec signingKey = new SecretKeySpec(key.getBytes(), "HmacSHA1");
        Mac mac = Mac.getInstance("HmacSHA1");
        mac.init(signingKey);
        byte[] rawHmac = mac.doFinal(data.getBytes());
        result = Base64.getEncoder().encodeToString(rawHmac);
        return result;
    }

    /**
     * 获取时间
     */
    private String toGMTString(Date date) {
        SimpleDateFormat df = new SimpleDateFormat("E, dd MMM yyyy HH:mm:ss z", Locale.UK);
        df.setTimeZone(new SimpleTimeZone(0, "GMT"));
        return df.format(date);
    }

    /**
     * 发送POST请求
     */
    private String sendPost(String url, String body, String akId, String akSecret)
        throws IOException, NoSuchAlgorithmException, InvalidKeyException {
        URL realUrl = new URL(url);
        // http header 参数
        String method = "POST";
        String accept = "application/json";
        String contentType = "application/json;charset=utf-8";
        String path = realUrl.getFile();
        String date = toGMTString(new Date());
        String host = realUrl.getHost();
        // 1.对body做MD5+BASE64加密
        String bodyMd5 = md5AndBase64(body);
        String uuid = UUID.randomUUID().toString();
        String stringToSign = method + "\n" + accept + "\n" + bodyMd5 + "\n" + contentType + "\n" + date + "\n"
            + "x-acs-signature-method:HMAC-SHA1\n"
            + "x-acs-signature-nonce:" + uuid + "\n"
            + "x-acs-version:2019-01-02\n"
            + path;
        // 2.计算 HMAC-SHA1
        String signature = hmacSha1(stringToSign, akSecret);
        // 3.得到 authorization header
        String authHeader = "acs " + akId + ":" + signature;

        Map<String, String> headers = new HashMap<>();
        // 设置通用的请求属性
        headers.put("Accept", accept);
        headers.put("Content-Type", contentType);
        headers.put("Content-MD5", bodyMd5);
        headers.put("Date", date);
        headers.put("Host", host);
        headers.put("Authorization", authHeader);
        headers.put("x-acs-signature-nonce", uuid);
        headers.put("x-acs-signature-method", "HMAC-SHA1");
        headers.put("x-acs-version", "2019-01-02");  // 版本可选

        return HttpUtil.post(url, headers, body);
    }

    @Override
    public Translator init(Map<String, String> config) {
        this.accessKeyId = config.get("accessKeyId");
        this.accessKeySecret = config.get("accessKeySecret");
        return this;
    }

    /**
     * 阿里云翻译请求参数
     */
    private static class AliyunRequestVO {
        /** 格式类型 */
        @JsonProperty("FormatType")
        private String formatType = "text";
        /** 源语言 */
        @JsonProperty("SourceLanguage")
        private String sourceLanguage;
        /** 目标语言 */
        @JsonProperty("TargetLanguage")
        private String targetLanguage;
        /** 文本 */
        @JsonProperty("SourceText")
        private String sourceText;
        /** 场景 */
        @JsonProperty("Scene")
        private String scene = "general";

        public String getFormatType() {
            return formatType;
        }

        public void setFormatType(String formatType) {
            this.formatType = formatType;
        }

        public String getSourceLanguage() {
            return sourceLanguage;
        }

        public void setSourceLanguage(String sourceLanguage) {
            this.sourceLanguage = sourceLanguage;
        }

        public String getTargetLanguage() {
            return targetLanguage;
        }

        public void setTargetLanguage(String targetLanguage) {
            this.targetLanguage = targetLanguage;
        }

        public String getSourceText() {
            return sourceText;
        }

        public void setSourceText(String sourceText) {
            this.sourceText = sourceText;
        }

        public String getScene() {
            return scene;
        }

        public void setScene(String scene) {
            this.scene = scene;
        }
    }

    /**
     * 阿里云翻译返回结果
     */
    private static class AliyunResponseVO {
        /** 代码 */
        @JsonProperty("Code")
        private String code;
        /** 请求id */
        @JsonProperty("RequestId")
        private String requestId;
        /** 数据 */
        @JsonProperty("Data")
        private AliyunResponseDataVO data;

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getRequestId() {
            return requestId;
        }

        public void setRequestId(String requestId) {
            this.requestId = requestId;
        }

        public AliyunResponseDataVO getData() {
            return data;
        }

        public void setData(AliyunResponseDataVO data) {
            this.data = data;
        }
    }

    /**
     * 阿里云翻译返回数据结果
     */
    private static class AliyunResponseDataVO {
        /** 字数 */
        @JsonProperty("WordCount")
        private String wordCount;
        /** 翻译 */
        @JsonProperty("Translated")
        private String translated;

        public String getWordCount() {
            return wordCount;
        }

        public void setWordCount(String wordCount) {
            this.wordCount = wordCount;
        }

        public String getTranslated() {
            return translated;
        }

        public void setTranslated(String translated) {
            this.translated = translated;
        }
    }
}
