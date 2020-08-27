package com.star.easydoc.service.translator.impl;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import com.intellij.openapi.diagnostic.Logger;
import com.star.easydoc.util.HttpUtil;
import com.star.easydoc.util.JsonUtil;
import org.apache.commons.lang3.StringUtils;

/**
 * 有道翻译
 *
 * @author wangchao
 * @date 2019/09/01
 */
public class YoudaoTranslator extends AbstractTranslator {
    private static final Logger LOGGER = Logger.getInstance(YoudaoTranslator.class);

    private static final String CH2EN_URL = "http://fanyi.youdao.com/translate?&doctype=json&type=ZH_CN2EN&i=%s";
    private static final String EN2CH_URL = "http://fanyi.youdao.com/translate?&doctype=json&type=EN2ZH_CN&i=%s";

    @Override
    public String translateEn2Ch(String text) {
        try {
            YoudaoResponse response = JsonUtil.fromJson(HttpUtil.get(String.format(EN2CH_URL, HttpUtil.encode(text))), YoudaoResponse.class);
            return Objects.requireNonNull(response).getTranslateResult().stream()
                .map(translateResults -> translateResults.stream().map(TranslateResult::getTgt).collect(Collectors.joining(" ")))
                .collect(Collectors.joining("\n"));
        } catch (Exception e) {
            LOGGER.error("请求有道翻译接口异常", e);
            return StringUtils.EMPTY;
        }
    }

    @Override
    public String translateCh2En(String text) {
        try {
            YoudaoResponse response = JsonUtil.fromJson(HttpUtil.get(String.format(CH2EN_URL, HttpUtil.encode(text))), YoudaoResponse.class);
            return Objects.requireNonNull(response).getTranslateResult().stream()
                .map(translateResults -> translateResults.stream().map(TranslateResult::getTgt).collect(Collectors.joining(" ")))
                .collect(Collectors.joining("\n"));
        } catch (Exception e) {
            LOGGER.error("请求有道翻译接口异常", e);
            return StringUtils.EMPTY;
        }
    }

    public static class YoudaoResponse {

        private String type;
        private int errorCode;
        private int elapsedTime;
        private List<List<TranslateResult>> translateResult;

        public void setType(String type) {
            this.type = type;
        }

        public String getType() {
            return type;
        }

        public void setErrorCode(int errorCode) {
            this.errorCode = errorCode;
        }

        public int getErrorCode() {
            return errorCode;
        }

        public void setElapsedTime(int elapsedTime) {
            this.elapsedTime = elapsedTime;
        }

        public int getElapsedTime() {
            return elapsedTime;
        }

        public void setTranslateResult(List<List<TranslateResult>> translateResult) {
            this.translateResult = translateResult;
        }

        public List<List<TranslateResult>> getTranslateResult() {
            return translateResult;
        }

    }

    public static class TranslateResult {

        private String src;
        private String tgt;

        public void setSrc(String src) {
            this.src = src;
        }

        public String getSrc() {
            return src;
        }

        public void setTgt(String tgt) {
            this.tgt = tgt;
        }

        public String getTgt() {
            return tgt;
        }

    }
}
