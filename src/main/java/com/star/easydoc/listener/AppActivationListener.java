package com.star.easydoc.listener;

import java.awt.*;
import java.net.URI;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationAction;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.application.ApplicationActivationListener;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.wm.IdeFrame;
import com.star.easydoc.common.util.NotificationUtil;
import com.star.easydoc.config.EasyDocConfig;
import com.star.easydoc.config.EasyDocConfigComponent;
import com.star.easydoc.service.gpt.GptService;
import com.star.easydoc.service.translator.TranslatorService;
import com.star.easydoc.view.inner.SupportView;
import org.jetbrains.annotations.NotNull;

/**
 * 应用程序激活监听器
 *
 * @author wangchao
 * @date 2022/03/13
 */
public class AppActivationListener implements ApplicationActivationListener {
    private static final Logger LOGGER = Logger.getInstance(AppActivationListener.class);

    /** 锁判断 */
    private volatile boolean isActivate = false;

    /** 锁 */
    private static final Object LOCK = new Object();

    @Override
    public synchronized void applicationActivated(@NotNull IdeFrame ideFrame) {
        activate();
    }

    /**
     * 激活
     */
    public void activate() {
        synchronized (LOCK) {
            if (isActivate) {
                return;
            }
            support();
            serviceInit();
            isActivate = true;
        }
    }

    /**
     * 支持
     */
    private void support() {
        AnAction starAction = new NotificationAction("⭐ 去点star") {
            @Override
            public void actionPerformed(@NotNull AnActionEvent e, @NotNull Notification notification) {
                try {
                    Desktop dp = Desktop.getDesktop();
                    if (dp.isSupported(Desktop.Action.BROWSE)) {
                        dp.browse(URI.create("https://github.com/starcwang/easy_javadoc"));
                    }
                } catch (Exception ex) {
                    LOGGER.error("open url failed:https://github.com/starcwang/easy_javadoc", ex);
                }
            }
        };

        AnAction reviewsAction = new NotificationAction("\uD83D\uDC4D 五星好评") {
            @Override
            public void actionPerformed(@NotNull AnActionEvent e, @NotNull Notification notification) {
                try {
                    Desktop dp = Desktop.getDesktop();
                    if (dp.isSupported(Desktop.Action.BROWSE)) {
                        dp.browse(URI.create("https://plugins.jetbrains.com/plugin/12977-easy-javadoc/reviews"));
                    }
                } catch (Exception ex) {
                    LOGGER.error("open url failed:https://plugins.jetbrains.com/plugin/12977-easy-javadoc/reviews", ex);
                }
            }
        };

        AnAction payAction = new NotificationAction("\uD83C\uDF57 加个鸡腿") {
            @Override
            public void actionPerformed(@NotNull AnActionEvent e, @NotNull Notification notification) {
                SupportView supportView = new SupportView();
                supportView.show();
            }
        };

        NotificationUtil.notify("关于EasyJavadoc快捷键",
            "EasyJavadoc默认的快捷键是command(ctrl)+\\,和新版IDEA的AI Assistant插件冲突,请自行修改快捷键");
        NotificationUtil.notify("支持EasyJavadoc", "如果这款小而美的插件为您节约了不少时间，请支持一下开发者！",
            starAction, reviewsAction, payAction);
    }

    /**
     * 服务初始化
     */
    private void serviceInit() {
        EasyDocConfig config = ServiceManager.getService(EasyDocConfigComponent.class).getState();

        TranslatorService translatorService = ServiceManager.getService(TranslatorService.class);
        translatorService.init(config);
        GptService gptService = ServiceManager.getService(GptService.class);
        gptService.init(config);
    }

    @Override
    public void applicationDeactivated(@NotNull IdeFrame ideFrame) {
        applicationActivated(ideFrame);
    }
}