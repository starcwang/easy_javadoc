package com.star.easydoc.service.translator.impl;

import java.util.List;
import java.util.Objects;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.annotation.JSONField;

import com.star.easydoc.common.util.HttpUtil;
import org.apache.commons.lang3.StringUtils;

/**
 * 金山翻译
 *
 * @author wangchao
 * @date 2019/09/01
 */
public class JinshanTranslator extends AbstractTranslator {
    //继承AbstractTranslator类
    private static final String URL = "http://dict-co.iciba.com/api/dictionary.php?key=1E55091D2F202FA617472001B3AF0D39&type=json&w=%s";
    //定义金山翻译的URL

    /**
     *
     * 对AbstractTranslator类的翻译方法进行重写
     */
    @Override
    public String translateEn2Ch(String text) {
        try {
            JinshanResponse response = JSON.parseObject(HttpUtil.get(String.format(URL, HttpUtil.encode(text))), JinshanResponse.class);
            //使用String.format方法将英文文本插入到URL字符串中，生成完整的请求URL。这个URL将作为参数传递给HttpUtil.get方法，执行HTTP GET请求，发送给金山翻译API。
            //HttpUtil.get方法返回的是一个JSON字符串，包含了翻译结果的信息。代码使用JSON.parseObject方法将JSON字符串解析成JinshanResponse对象。这个对象包含了翻译结果的各个字段。
            return Objects.requireNonNull(response).getSymbols().get(0).getParts().get(0).getMeans().get(0);
            //通过response.getSymbols().get(0)获取翻译结果中的第一个符号对象。然后，通过getParts().get(0)获取该符号对象中的第一个部分对象。最后，使用getMeans().get(0)获取部分对象中的第一个翻译结果字符串。
        } catch (Exception ignore) {
            return StringUtils.EMPTY;
            //如果翻译结果为空或解析过程中出现问题，方法将返回一个空字符串，即StringUtils.EMPTY。
        }
    }

    @Override
    public String translateCh2En(String text) {
        // TODO: 2020-8-27
        return null;
    }//同上

    /**
     * 定义了一些内部类，用于存储解析金山翻译API返回结果时所需的字段信息。
     * 这些内部类的定义提供了一个结构化的方式来存储金山翻译API返回结果中的各个字段信息，方便在代码中进行访问和处理。
     */
    private static class JinshanResponse {

        @JSONField(name = "word_name")
        private String wordName;
        @JSONField(name = "is_CRI")
        private String isCRI;
        private Exchange exchange;
        private List<Symbols> symbols;
        //wordName：翻译的单词或短语名称。
        //isCRI：是否为常用词。
        //exchange：单词的各种形式的交换信息，如复数形式、过去式等。
        //symbols：翻译结果的符号列表。

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
        //wordPl：单词的复数形式列表。
        //wordThird：单词的第三人称单数形式。
        //wordPast：单词的过去式形式。
        //wordDone：单词的完成式形式。
        //wordIng：单词的进行时形式。
        //wordEr：单词的比较级形式。
        //wordEst：单词的最高级形式。

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
        //part：单词或短语的词性。
        //means：单词或短语的翻译结果列表。

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
        //phEn：英式发音。
        //phAm：美式发音。
        //phOther：其他类型的发音。
        //phEnMp3：英式发音的MP3文件链接。
        //phAmMp3：美式发音的MP3文件链接。
        //phTtsMp3：TTS发音的MP3文件链接。
        //parts：单词或短语的各个部分及其翻译结果列表。

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
