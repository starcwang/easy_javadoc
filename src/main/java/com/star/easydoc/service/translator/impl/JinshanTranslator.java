package com.star.easydoc.service.translator.impl;

import java.util.List;
import java.util.Objects;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.annotation.JSONField;

import com.intellij.psi.PsiElement;
import com.star.easydoc.common.util.HttpUtil;
import org.apache.commons.lang3.StringUtils;

/**
 * 金山翻译
 *
 * @author wangchao
 * @date 2019/09/01
 */
public class JinshanTranslator extends AbstractTranslator {

    private static final String URL = "http://dict-co.iciba.com/api/dictionary.php?key=1E55091D2F202FA617472001B3AF0D39&type=json&w=%s";

    @Override
    public String translateEn2Ch(String text, PsiElement psiElement) {
        try {
            JinshanResponse response = JSON.parseObject(HttpUtil.get(String.format(URL, HttpUtil.encode(text)),
                getConfig().getTimeout()), JinshanResponse.class);
            return Objects.requireNonNull(response).getSymbols().get(0).getParts().get(0).getMeans().get(0);
        } catch (Exception ignore) {
            return StringUtils.EMPTY;
        }
    }

    @Override
    public String translateCh2En(String text, PsiElement psiElement) {
        // TODO: 2020-8-27  
        return null;
    }

    private static class JinshanResponse {

        @JSONField(name = "word_name")
        private String wordName;
        @JSONField(name = "is_CRI")
        private String isCRI;
        private Exchange exchange;
        private List<Symbols> symbols;

        public String getWordName() {
            return wordName;
        }

        public void setWordName(String wordName) {
            this.wordName = wordName;
        }

        public String getIsCRI() {
            return isCRI;
        }

        public void setIsCRI(String isCRI) {
            this.isCRI = isCRI;
        }

        public Exchange getExchange() {
            return exchange;
        }

        public void setExchange(Exchange exchange) {
            this.exchange = exchange;
        }

        public List<Symbols> getSymbols() {
            return symbols;
        }

        public void setSymbols(List<Symbols> symbols) {
            this.symbols = symbols;
        }
    }

    private static class Exchange {

        @JSONField(name = "word_pl")
        private List<String> wordPl;
        @JSONField(name = "word_third")
        private String wordThird;
        @JSONField(name = "word_past")
        private String wordPast;
        @JSONField(name = "word_done")
        private String wordDone;
        @JSONField(name = "word_ing")
        private String wordIng;
        @JSONField(name = "word_er")
        private String wordEr;
        @JSONField(name = "word_est")
        private String wordEst;

        public List<String> getWordPl() {
            return wordPl;
        }

        public void setWordPl(List<String> wordPl) {
            this.wordPl = wordPl;
        }

        public String getWordThird() {
            return wordThird;
        }

        public void setWordThird(String wordThird) {
            this.wordThird = wordThird;
        }

        public String getWordPast() {
            return wordPast;
        }

        public void setWordPast(String wordPast) {
            this.wordPast = wordPast;
        }

        public String getWordDone() {
            return wordDone;
        }

        public void setWordDone(String wordDone) {
            this.wordDone = wordDone;
        }

        public String getWordIng() {
            return wordIng;
        }

        public void setWordIng(String wordIng) {
            this.wordIng = wordIng;
        }

        public String getWordEr() {
            return wordEr;
        }

        public void setWordEr(String wordEr) {
            this.wordEr = wordEr;
        }

        public String getWordEst() {
            return wordEst;
        }

        public void setWordEst(String wordEst) {
            this.wordEst = wordEst;
        }
    }

    private static class Parts {

        private String part;
        private List<String> means;

        public void setPart(String part) {
            this.part = part;
        }

        public String getPart() {
            return part;
        }

        public void setMeans(List<String> means) {
            this.means = means;
        }

        public List<String> getMeans() {
            return means;
        }

    }

    private static class Symbols {

        @JSONField(name = "ph_en")
        private String phEn;
        @JSONField(name = "ph_am")
        private String phAm;
        @JSONField(name = "ph_other")
        private String phOther;
        @JSONField(name = "ph_en_mp3")
        private String phEnMp3;
        @JSONField(name = "ph_am_mp3")
        private String phAmMp3;
        @JSONField(name = "ph_tts_mp3")
        private String phTtsMp3;
        private List<Parts> parts;

        public String getPhEn() {
            return phEn;
        }

        public void setPhEn(String phEn) {
            this.phEn = phEn;
        }

        public String getPhAm() {
            return phAm;
        }

        public void setPhAm(String phAm) {
            this.phAm = phAm;
        }

        public String getPhOther() {
            return phOther;
        }

        public void setPhOther(String phOther) {
            this.phOther = phOther;
        }

        public String getPhEnMp3() {
            return phEnMp3;
        }

        public void setPhEnMp3(String phEnMp3) {
            this.phEnMp3 = phEnMp3;
        }

        public String getPhAmMp3() {
            return phAmMp3;
        }

        public void setPhAmMp3(String phAmMp3) {
            this.phAmMp3 = phAmMp3;
        }

        public String getPhTtsMp3() {
            return phTtsMp3;
        }

        public void setPhTtsMp3(String phTtsMp3) {
            this.phTtsMp3 = phTtsMp3;
        }

        public List<Parts> getParts() {
            return parts;
        }

        public void setParts(List<Parts> parts) {
            this.parts = parts;
        }
    }

}
