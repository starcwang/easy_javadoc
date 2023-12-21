package com.star.easydoc.service.translator.impl;

import java.util.List;
import java.util.Objects;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.annotation.JSONField;

import com.intellij.openapi.diagnostic.Logger;
import com.star.easydoc.common.util.HttpUtil;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.RandomStringUtils;

/**
 * 百度翻译
 *
 * @author wangchao
 * @date 2019/09/01
 */
public class BaiduTranslator extends AbstractTranslator {
    //继承AbstractTranslator类
    private static final Logger LOGGER = Logger.getInstance(BaiduTranslator.class);
    //声明了一个静态常量LOGGER来记录日志
    private static final String URL
            = "http://api.fanyi.baidu.com/api/trans/vip/translate?from=auto&to=auto&appid=%s&salt=%s&sign=%s&q=%s";
    //URL: 百度翻译API的请求URL模板，其中包含一些占位符。

    /**
     * 对AbstractTranslator类的翻译方法进行重写，
     * 英译中
     */
    @Override
    public String translateEn2Ch(String text) {
        return get(text);

    }
    /**
     * 对AbstractTranslator类的翻译方法进行重写，
     * 中译英
     */
    @Override
    public String translateCh2En(String text) {
        return get(text);
    }

    private String get(String text) {
        String json = null;//用于存储从翻译API接收到的JSON响应字符串，默认为null。
        String result = "";//用于存储翻译结果，初始为空字符串。
        //最多十次抛出异常
        try {
            for (int i = 0; i < 10; i++) {
                String salt = RandomStringUtils.randomNumeric(16);
                //生成一个长度为16的随机字符串 salt，作为请求参数之一。
                String sign = DigestUtils.md5Hex(getConfig().getAppId() + text + salt + getConfig().getToken());
                //使用 getConfig().getAppId()、text、salt 和 getConfig().getToken() 计算请求签名 sign。这里使用了 DigestUtils.md5Hex 方法对拼接的字符串进行 MD5 哈希计算。
                String eText = HttpUtil.encode(text);
                //对待翻译的文本进行 URL 编码，得到 eText。
                json = HttpUtil.get(String.format(URL, getConfig().getAppId(), salt, sign, eText));
                //使用 String.format 方法将参数插入到 URL 模板中，得到完整的请求URL。
                //使用 HttpUtil.get 方法发送 HTTP GET 请求，将返回的响应字符串赋值给 json。
                BaiduResponse response = JSON.parseObject(json, BaiduResponse.class);
                //使用 FastJSON 的 JSON.parseObject 方法将 json 字符串解析为 BaiduResponse 对象。
                if (response == null || "54003".equals(response.getErrorCode())) {
                    Thread.sleep(500);
                } else {
                    result = Objects.requireNonNull(response).getTransResult().get(0).getDst();
                    break;
                }
                //如果响应对象为空或者错误码为 "54003"（表示请求频率过快），则线程休眠500毫秒后重试。
                //否则，获取翻译结果并将其赋值给 result。这里使用了 Objects.requireNonNull 方法来确保 response 不为 null，并通过 response.getTransResult().get(0).getDst() 获取翻译结果。
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (Exception e) {
            LOGGER.error("请求百度翻译接口异常:请检查本地网络是否可连接外网,也有可能被百度限流,response=" + json, e);
            //捕获到其他异常，记录错误日志
        }
        return result;
    }


    /**
     * 这两个内部类主要用于存储百度翻译API的响应结果。
     * 通过定义这两个内部类，可以方便地进行 JSON 数据的序列化和反序列化，
     * 将接收到的 JSON 响应字符串转换为相应的 Java 对象，并提取出需要的翻译结果和相关信息。
     */
    private static class BaiduResponse {
        @JSONField(name = "error_code")
        private String errorCode;
        @JSONField(name = "error_msg")
        private String errorMsg;
        private String from;
        private String to;
        @JSONField(name = "trans_result")
        private List<TransResult> transResult;
        //errorCode：表示百度翻译API的错误码。
        //errorMsg：表示百度翻译API的错误消息。
        //from：表示翻译源语言。
        //to：表示翻译目标语言。
        //transResult：表示翻译结果的列表，类型为 List<TransResult>

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

        public String getErrorCode() {
            return errorCode;
        }

        public void setErrorCode(String errorCode) {
            this.errorCode = errorCode;
        }

        public String getErrorMsg() {
            return errorMsg;
        }

        public void setErrorMsg(String errorMsg) {
            this.errorMsg = errorMsg;
        }
    }

    private static class TransResult {

        private String src;
        private String dst;
        //src：表示翻译前的原始文本。
        //dst：表示翻译后的目标文本

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
