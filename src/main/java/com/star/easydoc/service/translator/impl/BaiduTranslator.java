package com.star.easydoc.service.translator.impl;

import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.intellij.openapi.components.ServiceManager;
import com.star.easydoc.config.EasyJavadocConfigComponent;
import com.star.easydoc.model.EasyJavadocConfiguration;
import com.star.easydoc.service.translator.Translator;
import com.star.easydoc.util.HttpUtil;
import com.star.easydoc.util.JsonUtil;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * 百度翻译
 *
 * @author wangchao
 * @date 2019/09/01
 */
public class BaiduTranslator implements Translator {

    private static final String URL = "http://api.fanyi.baidu.com/api/trans/vip/translate?from=auto&to=auto&appid=%s&salt=%s&sign=%s&q=%s";
    private EasyJavadocConfiguration config = ServiceManager.getService(EasyJavadocConfigComponent.class).getState();

    @Override
    public String en2Ch(String text) {
        try {
            String salt = RandomStringUtils.randomNumeric(16);
            String sign = DigestUtils.md5Hex(config.getAppId() + text + salt + config.getToken());
            String eText = HttpUtil.encode(text);
            BaiduResponse response = JsonUtil.fromJson(HttpUtil.get(String.format(URL, config.getAppId(), salt, sign, eText)), BaiduResponse.class);
            return Objects.requireNonNull(response).getTransResult().get(0).getDst();
        } catch (Exception ignore) {
            return StringUtils.EMPTY;
        }
    }

    @Override
    public String ch2En(String text) {
        try {
            String salt = RandomStringUtils.randomNumeric(16);
            String sign = DigestUtils.md5Hex(config.getAppId() + text + salt + config.getToken());
            String eText = HttpUtil.encode(text);
            BaiduResponse response = JsonUtil.fromJson(HttpUtil.get(String.format(URL, config.getAppId(), salt, sign, eText)), BaiduResponse.class);
            return Objects.requireNonNull(response).getTransResult().get(0).getDst();
        } catch (Exception ignore) {
            return StringUtils.EMPTY;
        }
    }

    public static class BaiduResponse {

        private String from;
        private String to;
        @JsonProperty("trans_result")
        private List<TransResult> transResult;

        public void setFrom(String from) {
            this.from = from;
        }

        public String getFrom() {
            return from;
        }

        public void setTo(String to) {
            this.to = to;
        }

        public String getTo() {
            return to;
        }

        public void setTransResult(List<TransResult> transResult) {
            this.transResult = transResult;
        }

        public List<TransResult> getTransResult() {
            return transResult;
        }

    }

    public static class TransResult {

        private String src;
        private String dst;

        public void setSrc(String src) {
            this.src = src;
        }

        public String getSrc() {
            return src;
        }

        public void setDst(String dst) {
            this.dst = dst;
        }

        public String getDst() {
            return dst;
        }

    }
}
