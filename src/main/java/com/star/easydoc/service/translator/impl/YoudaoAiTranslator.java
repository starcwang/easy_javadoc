package com.star.easydoc.service.translator.impl;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.alibaba.fastjson2.JSON;

import com.google.common.collect.Maps;
import com.intellij.openapi.diagnostic.Logger;
import com.star.easydoc.common.util.HttpUtil;
import org.apache.commons.lang3.StringUtils;

/**
 * 有道智云翻译
 *
 * @author wangchao
 * @date 2019/09/01
 */
public class YoudaoAiTranslator extends AbstractTranslator {
    // 日志记录器。
    private static final Logger LOGGER = Logger.getInstance(YoudaoAiTranslator.class);

    // 有道智云翻译接口的URL地址
    private static final String YOUDAO_URL = "https://openapi.youdao.com/api";

    // 英译中
    @Override
    public String translateEn2Ch(String text) {
        return translate(text, "en", "zh-CHS");
    }

    // 中译英
    @Override
    public String translateCh2En(String text) {
        return translate(text, "zh-CHS", "en");
    }

    // 通用翻译方法，可指定源语言和目标语言
    private String translate(String text, String from, String to) {
        // 创建一个参数Map对象，存放以下参数
        Map<String, Object> params = Maps.newHashMap();
        // 生成一个时间戳作为salt值
        String salt = String.valueOf(System.currentTimeMillis());
        // 存放源语言、目标语言、签名类型
        params.put("from", from);
        params.put("to", to);
        params.put("signType", "v3");
        String curtime = String.valueOf(System.currentTimeMillis() / 1000);
        params.put("curtime", curtime);
        // 根据配置获取有道智云的appKey和appSecret，并组合成一个待加密的字符串signStr
        String signStr = getConfig().getYoudaoAppKey() + truncate(text) + salt + curtime + getConfig().getYoudaoAppSecret();
        // 通过调用getDigest()方法对signStr进行SHA-256加密得到签名sign
        String sign = getDigest(signStr);
        // 将appKey、待翻译的文本、salt、签名等参数都放入参数Map中
        params.put("appKey", getConfig().getYoudaoAppKey());
        params.put("q", text);
        params.put("salt", salt);
        params.put("sign", sign);
        String json = null;
        try {
            // 使用HttpUtil类发起GET请求，将参数Map作为查询参数传递给有道智云翻译API的URL
            json = HttpUtil.get(YOUDAO_URL, params);
            // 将响应JSON字符串解析为YoudaoAiResponse对象
            YoudaoAiResponse response = JSON.parseObject(json, YoudaoAiResponse.class);
            // 返回翻译结果的第一个元素。
            return Objects.requireNonNull(response).getTranslation().get(0);
        } catch (Exception e) {
            // 如果请求过程中发生异常，会记录日志并返回一个空字符串
            LOGGER.error("请求有道智云接口异常,返回为空,response=" + json, e);
            return StringUtils.EMPTY;
        }
    }

    /**
     * 生成加密字段
     */
    public static String getDigest(String string) {// 使用SHA-256算法生成加密字段
        // 检查输入的字符串是否为空，如果为空则返回 null
        if (string == null) {
            return null;
        }
        // 将输入的字符串按 UTF-8 编码转换为字节数组 btInput
        char[] hexDigits = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
        byte[] btInput = string.getBytes(StandardCharsets.UTF_8);
        try {
            // 使用 MessageDigest.getInstance("SHA-256") 获取 SHA-256 算法的实例 mdInst
            MessageDigest mdInst = MessageDigest.getInstance("SHA-256");
            // 更新摘要以处理输入的字节
            mdInst.update(btInput);
            // 获得摘要的字节数组 md
            byte[] md = mdInst.digest();
            // 将每个字节转换为十六进制表示的字符，并将这些字符拼接成一个字符串，最终返回 SHA-256 摘要的十六进制表示字符串
            int j = md.length;
            char[] str = new char[j * 2];
            int k = 0;
            for (byte byte0 : md) {
                str[k++] = hexDigits[byte0 >>> 4 & 0xf];
                str[k++] = hexDigits[byte0 & 0xf];
            }
            return new String(str);
        } catch (NoSuchAlgorithmException e) {
            return null;
        }
    }

    // 对输入的字符串进行截断处理
    public static String truncate(String q) {
        // 检查输入的字符串是否为null，如果是，则返回nul
        if (q == null) {
            return null;
        }
        // 获取输入字符串的长度，并判断如果长度小于或等于20，则直接返回原始字符串。
        // 如果长度大于20，那么它会将字符串截断为前10个字符、长度、以及后10个字符拼接在一起，然后返回这个新的字符串。
        int len = q.length();
        return len <= 20 ? q : (q.substring(0, 10) + len + q.substring(len - 10, len));
    }

    // 用于表示有道翻译API的响应结果类
    private static class YoudaoAiResponse {
        // 表示错误码
        private String errorCode;
        // 表示翻译结果
        private List<String> translation;

        //提供get和set方法用于获取和设置errorCode和translation成员变量的值
        public String getErrorCode() {
            return errorCode;
        }

        public void setErrorCode(String errorCode) {
            this.errorCode = errorCode;
        }

        public List<String> getTranslation() {
            return translation;
        }

        public void setTranslation(List<String> translation) {
            this.translation = translation;
        }
    }

}
