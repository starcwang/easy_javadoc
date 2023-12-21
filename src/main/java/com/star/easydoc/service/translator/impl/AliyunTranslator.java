package com.star.easydoc.service.translator.impl;

import java.io.IOException;
import java.net.URL;
import java.security.InvalidKeyException;
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

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.annotation.JSONField;

import com.intellij.openapi.diagnostic.Logger;
import com.star.easydoc.common.util.HttpUtil;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * 阿里云翻译
 *
 * @author wangchao
 * @date 2019/09/01
 */
public class AliyunTranslator extends AbstractTranslator {
    //继承AbstractTranslator类
    private static final Logger LOGGER = Logger.getInstance(AliyunTranslator.class);
    //声明了一个静态常量LOGGER来记录日志
    private static final String URL = "http://mt.cn-hangzhou.aliyuncs.com/api/translate/web/ecommerce";
    //定义一个静态常量来表示阿里云翻译服务请求的URL

    /**
     * 对AbstractTranslator类的翻译方法进行重写，并且调用了translate方法
     *
     */
    @Override
    protected String translateCh2En(String text) {
        return translate("zh", "en", text);
    }

    /**
     * 同上
     */
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
    /**
     * 实际进行翻译的方法，接收源语言、目标语言和文本作为参数。根据参数构建请求对象，并发送HTTP POST请求到阿里云翻译服务API。
     * 通过解析返回的JSON响应，提取翻译结果并返回
     *
     */
    private String translate(String sourceLanguage, String targetLanguage, String text) {
        AliyunRequestVO request = new AliyunRequestVO();//创建一个AliyunRequestVO对象，用于封装请求参数
        request.setSourceLanguage(sourceLanguage);//设置源语言。
        request.setTargetLanguage(targetLanguage);//设置目标语言。
        request.setSourceText(text);//设置待翻译的文本内容。
        String json = null;//将请求参数对象转换为JSON字符串。
        try {
            json = sendPost(URL, JSON.toJSONString(request), getConfig().getAccessKeyId(), getConfig().getAccessKeySecret());
            //调用sendPost方法发送HTTP POST请求到阿里云翻译服务API，传入URL、JSON请求体以及访问密钥ID和密钥密钥作为参数。
            AliyunResponseVO response = JSON.parseObject(json, AliyunResponseVO.class);
            //将返回的JSON响应字符串解析为AliyunResponseVO对象。
            return Objects.requireNonNull(response).getData().getTranslated();//从响应对象中获取翻译结果并返回。
        } catch (Exception e) {
            LOGGER.error("请求阿里云翻译接口异常:请检查本地网络是否可连接外网,也有可能被阿里云限流,response=" + json, e);
            return StringUtils.EMPTY;
            //如果发生异常，将异常信息记录到日志中，包括请求阿里云翻译接口异常、本地网络连接状态和阿里云限流等情况。返回空字符串
        }
    }

    /**
     * 将字符串进行MD5哈希计算，并进行Base64编码。
     * 该方法的主要功能是对字符串进行MD5哈希计算，并使用Base64编码将结果转换为字符串形式。这种方法常用于保护敏感信息的存储或传输，提供一种不可逆的加密方式。
     */
    private String md5AndBase64(String s) {
        if (s == null) {
            return null;
        }
        return Base64.getEncoder().encodeToString(DigestUtils.md5(s));
    }

    /**
     * 使用HMAC-SHA1算法对数据进行签名。
     * 该方法的主要功能是使用HMAC-SHA1算法对给定的数据进行签名，并返回签名结果的Base64编码字符串。HMAC-SHA1是一种常用的消息认证码算法，通过使用秘密密钥对消息进行哈希计算和签名，用于验证消息的完整性和真实性。
     */
    private String hmacSha1(String data, String key) throws NoSuchAlgorithmException, InvalidKeyException {
        String result;
        SecretKeySpec signingKey = new SecretKeySpec(key.getBytes(), "HmacSHA1");
        Mac mac = Mac.getInstance("HmacSHA1");
        mac.init(signingKey);
        byte[] rawHmac = mac.doFinal(data.getBytes());
        return Base64.getEncoder().encodeToString(rawHmac).trim();
        //声明一个字符串变量result用于存储签名结果。
        //创建一个SecretKeySpec对象，使用指定的签名密钥key和算法名称"HmacSHA1"。SecretKeySpec用于表示一个秘密密钥的规范。
        //获取一个Mac实例，使用"HmacSHA1"算法。
        //初始化Mac实例，传入签名密钥。
        //调用Mac实例的doFinal方法，传入要签名的数据data的字节数组形式，返回签名结果的字节数组。
        //使用Java 8中的Base64.getEncoder().encodeToString方法，将签名结果的字节数组进行Base64编码。
        //返回Base64编码后的签名结果字符串，并使用trim方法去除字符串两端的空格。
    }

    /**
     * 获取时间
     * 将给定的日期转换为GMT格式的字符串。
     * 使用了三个SimpleDateFormat对象来定义不同部分的日期格式，并将时区设置为GMT。然后，它将日期的各个部分格式化为字符串，并将它们拼接在一起形成最终的GMT格式字符串。
     */
    private String toGMTString(Date date) {
        SimpleDateFormat df1 = new SimpleDateFormat("E, dd ", Locale.UK);
        SimpleDateFormat df2 = new SimpleDateFormat("MMM", Locale.UK);
        SimpleDateFormat df3 = new SimpleDateFormat(" yyyy HH:mm:ss z", Locale.UK);
        df1.setTimeZone(new SimpleTimeZone(0, "GMT"));
        df2.setTimeZone(new SimpleTimeZone(0, "GMT"));
        df3.setTimeZone(new SimpleTimeZone(0, "GMT"));
        String month = df2.format(date);
        if (month.length() > 3) {
            month = month.substring(0, 3);
        }
        return df1.format(date) + month + df3.format(date);
        //创建三个SimpleDateFormat对象：df1、df2、df3，用于定义日期格式。
        //df1用于获取日期的星期和日期部分，格式为"E, dd"（例如：Mon, 01）。
        //df2用于获取日期的月份部分，格式为"MMM"（例如：Jan）。
        //df3用于获取日期的年份、时间和时区部分，格式为" yyyy HH:mm:ss z"（例如：2023 12:00:00 GMT）。
        //为三个SimpleDateFormat对象设置时区为GMT（格林尼治标准时间）。
        //使用df2格式化日期对象，将月份转换为缩写形式（如果超过3个字符）。
        //返回拼接了日期的星期和日期部分、月份部分和年份、时间和时区部分的字符串
    }

    /**
     * 发送POST请求
     * 该方法的主要功能是发送HTTP POST请求，并在请求头中添加身份验证信息和签名信息。
     * 它使用了MD5和Base64对请求体进行加密，使用HMAC-SHA1算法生成签名，并将相关的请求头参数设置到headers中。
     * 最后，它调用HttpUtil.post方法发送请求，并返回请求的结果。
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

        //创建一个URL对象，使用传入的url参数。
        //定义HTTP请求头参数的各个变量，包括请求方法method、接受的内容类型accept、请求的内容类型contentType、请求路径path、当前时间的GMT格式date和请求的主机名host。
        //对请求体body进行MD5和Base64加密，生成加密后的字符串bodyMd5。
        //生成一个随机的UUID作为签名的唯一标识uuid。
        //构建待签名的字符串stringToSign，包括请求方法、接受的内容类型、加密后的请求体、请求的内容类型、当前时间、签名方法、签名的唯一标识、版本号和请求路径。
        //使用HMAC-SHA1算法，将待签名的字符串进行签名，得到签名结果signature。
        //构建授权头部信息authHeader，包括身份验证ID和签名，格式为"acs AK_ID:signature"。
        //创建一个HashMap对象headers，用于存储请求头参数。
        //将通用的请求属性添加到headers中，包括接受的内容类型、请求的内容类型、加密后的请求体、当前时间、请求的主机名、授权头部信息、签名的唯一标识、签名方法和版本号。
        //调用HttpUtil.post方法发送HTTP POST请求，传入URL、请求头参数和请求体，返回请求的结果。
    }

    /**
     * 阿里云翻译请求参数
     * 定义了一些内部类，用于存储阿里云翻译API请求和返回结果的字段信息。
     * 这些内部类的定义提供了一个结构化的方式来存储阿里云翻译API请求和返回结果中的各个字段信息，方便在代码中进行访问和处理。
     */
    private static class AliyunRequestVO {
        /** 格式类型 */
        @JSONField(name = "FormatType")
        private String formatType = "text";
        /** 源语言 */
        @JSONField(name = "SourceLanguage")
        private String sourceLanguage;
        /** 目标语言 */
        @JSONField(name = "TargetLanguage")
        private String targetLanguage;
        /** 文本 */
        @JSONField(name = "SourceText")
        private String sourceText;
        /** 场景 */
        @JSONField(name = "Scene")
        private String scene = "general";
        //formatType：文本格式类型，默认为"text"。
        //sourceLanguage：源语言。
        //targetLanguage：目标语言。
        //sourceText：待翻译的文本。
        //scene：翻译场景，默认为"general"。

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
        @JSONField(name = "Code")
        private String code;
        /** 请求id */
        @JSONField(name = "RequestId")
        private String requestId;
        /** 数据 */
        @JSONField(name = "Data")
        private AliyunResponseDataVO data;
        //code：API返回的状态码。
        //requestId：API请求的唯一标识符。
        //data：翻译结果的数据。

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
        @JSONField(name = "WordCount")
        private String wordCount;
        /** 翻译 */
        @JSONField(name = "Translated")
        private String translated;
        //wordCount：翻译文本中的字数。
        //translated：翻译后的文本结果。

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
