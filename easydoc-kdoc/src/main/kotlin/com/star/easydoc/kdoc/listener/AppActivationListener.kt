package com.star.easydoc.kdoc.listener

import com.intellij.icons.AllIcons
import com.intellij.notification.*
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.application.ApplicationActivationListener
import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.wm.IdeFrame
import com.star.easydoc.kdoc.config.EasyKdocConfigComponent
import com.star.easydoc.kdoc.view.inner.SupportView
import com.star.easydoc.service.translator.TranslatorService
import java.awt.Desktop
import java.net.URI

/**
 * 应用程序激活监听器
 *
 * @author wangchao
 * @date 2022/03/13
 */
class AppActivationListener : ApplicationActivationListener {
    /** 上一次通知时间  */
    @Volatile
    private var lastNoticeTime = 0L

    @Synchronized
    override fun applicationActivated(ideFrame: IdeFrame) {
        support()
        serviceInit()
    }

    /**
     * 支持
     */
    private fun support() {
        if (System.currentTimeMillis() - lastNoticeTime < INTERVAL) {
            return
        }
        val group = NotificationGroup(
            "Easy Kdoc", NotificationDisplayType.BALLOON, true, null,
            AllIcons.General.AddJdk
        )
        val notification = group.createNotification(
            "支持EasyKdoc", "如果这款小而美的插件为您节约了不少时间，请支持一下开发者！",
            NotificationType.INFORMATION, NotificationListener.URL_OPENING_LISTENER
        )

        // 去点star
        notification.addAction(object : NotificationAction("✨ 去点star") {
            override fun actionPerformed(e: AnActionEvent, notification: Notification) {
                try {
                    val dp = Desktop.getDesktop()
                    if (dp.isSupported(Desktop.Action.BROWSE)) {
                        dp.browse(URI.create("https://github.com/starcwang/easy_javadoc"))
                    }
                } catch (ex: Exception) {
                    LOGGER.error("打开链接失败:https://github.com/starcwang/easy_javadoc", ex)
                }
            }
        })

        // 支付
        notification.addAction(object : NotificationAction("☕ 请喝咖啡") {
            override fun actionPerformed(e: AnActionEvent, notification: Notification) {
                val supportView = SupportView()
                supportView.show()
            }
        })
        notification.notify(null)
        lastNoticeTime = System.currentTimeMillis()
    }

    /**
     * 服务初始化
     */
    private fun serviceInit() {
        val config = ServiceManager.getService(EasyKdocConfigComponent::class.java).state
        val translatorService = ServiceManager.getService(TranslatorService::class.java)
        translatorService.init(config)
    }

    override fun applicationDeactivated(ideFrame: IdeFrame) {
        applicationActivated(ideFrame)
    }

    companion object {
        private val LOGGER = Logger.getInstance(AppActivationListener::class.java)

        /** 通知时间间隔  */
        private const val INTERVAL = 7 * 24 * 60 * 60 * 1000L
    }
}