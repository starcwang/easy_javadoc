package com.star.easydoc.service.translator.impl;

import java.awt.*;
import java.net.URI;
import java.util.List;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationAction;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.diagnostic.Logger;
import com.star.easydoc.common.util.NotificationUtil;
import org.jetbrains.annotations.NotNull;

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
    /** 上一次通知时间 */
    private static long lastNotifyTime = 0L;
    /** 通知间隔 */
    private static final long THRESHOLD = 60 * 60 * 1000L;

    @Override
    public String translateEn2Ch(String text) {
        LOGGER.error("有道免费接口已被官方禁用,请申请私人账号,各大厂商基本都免费");
        return "";
    }

    @Override
    public String translateCh2En(String text) {
        LOGGER.error("有道免费接口已被官方禁用,请申请私人账号,各大厂商基本都免费");
        return "";
    }

    /**
     * 通知
     */
    private synchronized void checkNotify() {
        if (System.currentTimeMillis() - lastNotifyTime < THRESHOLD) {
            return;
        }
        AnAction baiduAction = new NotificationAction("百度翻译") {
            @Override
            public void actionPerformed(@NotNull AnActionEvent e, @NotNull Notification notification) {
                try {
                    Desktop dp = Desktop.getDesktop();
                    if (dp.isSupported(Desktop.Action.BROWSE)) {
                        dp.browse(URI.create("https://api.fanyi.baidu.com/doc/21"));
                    }
                } catch (Exception ex) {
                    LOGGER.error("打开链接失败:https://api.fanyi.baidu.com/doc/21", ex);
                }
            }
        };
        AnAction tencentAction = new NotificationAction("腾讯翻译") {
            @Override
            public void actionPerformed(@NotNull AnActionEvent e, @NotNull Notification notification) {
                try {
                    Desktop dp = Desktop.getDesktop();
                    if (dp.isSupported(Desktop.Action.BROWSE)) {
                        dp.browse(URI.create("https://cloud.tencent.com/document/product/551/7372"));
                    }
                } catch (Exception ex) {
                    LOGGER.error("打开链接失败:https://cloud.tencent.com/document/product/551/7372", ex);
                }
            }
        };
        AnAction aliAction = new NotificationAction("阿里翻译") {
            @Override
            public void actionPerformed(@NotNull AnActionEvent e, @NotNull Notification notification) {
                try {
                    Desktop dp = Desktop.getDesktop();
                    if (dp.isSupported(Desktop.Action.BROWSE)) {
                        dp.browse(URI.create("https://www.aliyun.com/product/ai/alimt"));
                    }
                } catch (Exception ex) {
                    LOGGER.error("打开链接失败:https://www.aliyun.com/product/ai/alimt", ex);
                }
            }
        };
        NotificationUtil.notify("有道翻译暂不可用",
            "请检查网络连接,也有可能由于用户量大,接口被限流,毕竟免费的接口没有保障,请稍后再试试! 推荐申请自己的翻译,私有独享,免费且稳定.",
            aliAction, tencentAction, baiduAction);

        lastNotifyTime = System.currentTimeMillis();
    }

    private static class YoudaoResponse {

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

    private static class TranslateResult {

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
