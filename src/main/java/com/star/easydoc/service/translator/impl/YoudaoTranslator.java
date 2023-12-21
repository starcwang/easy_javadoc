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
    // 日志记录器。
    private static final Logger LOGGER = Logger.getInstance(YoudaoTranslator.class);

    //有道中译英的URL
    private static final String CH2EN_URL = "http://fanyi.youdao.com/translate?&doctype=json&type=ZH_CN2EN&i=%s";
    //有道英译中的URL
    private static final String EN2CH_URL = "http://fanyi.youdao.com/translate?&doctype=json&type=EN2ZH_CN&i=%s";
    /** 上一次通知时间 */
    private static long lastNotifyTime = 0L;
    /** 通知间隔 */
    private static final long THRESHOLD = 60 * 60 * 1000L;

    //英译中
    @Override
    public String translateEn2Ch(String text) {
        LOGGER.error("有道免费接口已被官方禁用,请申请私人账号,各大厂商基本都免费");
        return "";
    }

    //中译英
    @Override
    public String translateCh2En(String text) {
        LOGGER.error("有道免费接口已被官方禁用,请申请私人账号,各大厂商基本都免费");
        return "";
    }

    /**
     * 通知
     */
    private synchronized void checkNotify() {//检查并通知用户有关翻译接口不可用的信息
        // 如果距离上次通知的时间还未达到阈值，则不进行通知
        if (System.currentTimeMillis() - lastNotifyTime < THRESHOLD) {
            return;
        }
        // 创建百度翻译链接动作
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
        // 创建腾讯翻译链接动作
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
        // 创建阿里翻译链接动作
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
        // 弹出通知框，提醒用户有道翻译接口不可用，并给出解决方案和推荐的其他翻译接口链接
        NotificationUtil.notify("有道翻译暂不可用",
            "请检查网络连接,也有可能由于用户量大,接口被限流,毕竟免费的接口没有保障,请稍后再试试! 推荐申请自己的翻译,私有独享,免费且稳定.",
            aliAction, tencentAction, baiduAction);

        // 更新上次通知的时间为当前时间
        lastNotifyTime = System.currentTimeMillis();
    }

    private static class YoudaoResponse {// 封装有道翻译API响应结果的内部类
        // 响应结果类型，错误码，请求耗时，翻译结果
        private String type;
        private int errorCode;
        private int elapsedTime;
        private List<List<TranslateResult>> translateResult;

        // 提供get和set方法来访问和设置这些属性的值
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

    private static class TranslateResult {// 封装翻译结果的内部类
        // 包含两个属性：翻译结果的源文本，翻译结果的目标文本
        private String src;
        private String tgt;

        // 提供get和set方法来访问和设置这些属性的值
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
