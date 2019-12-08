package com.star.easydoc.service.translator.impl;

import java.util.List;
import java.util.Objects;

import com.star.easydoc.service.translator.Translator;
import com.star.easydoc.util.HttpUtil;
import com.star.easydoc.util.JsonUtil;
import org.apache.commons.lang3.StringUtils;

/**
 * 有道翻译
 *
 * @author wangchao
 * @date 2019/09/01
 */
public abstract class YoudaoTranslator implements Translator {

    @Override
    public String translate(String text) {
        try {
            YoudaoResponse response = JsonUtil.fromJson(HttpUtil.get(String.format(getUrl(), HttpUtil.encode(text))), YoudaoResponse.class);
            return Objects.requireNonNull(response).getTranslateResult().get(0).get(0).getTgt();
        } catch (Exception ignore) {
            return StringUtils.EMPTY;
        }
    }

    /**
     * 得到Url
     *
     * @return {@link java.lang.String}
     */
    protected abstract String getUrl();

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
