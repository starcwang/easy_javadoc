package com.star.easydoc.common.util;

import com.intellij.icons.AllIcons.General;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationDisplayType;
import com.intellij.notification.NotificationGroup;
import com.intellij.notification.NotificationListener;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.actionSystem.AnAction;
import org.apache.commons.lang3.ArrayUtils;

/**
 * 通知工具类
 *
 * @author wangchao
 * @date 2022/11/06
 */
public class NotificationUtil {

    /** 私有构造 */
    private NotificationUtil() {}

    /**
     * 通知
     *
     * @param title 标题
     * @param content 内容
     * @param actions 动作
     */
    public static void notify(String title, String content, AnAction... actions) {
        // 创建一个通知组，指定名称为"Easy Javadoc"，显示类型为BALLOON，允许多个通知一起显示，传入null作为project，使用默认的icon
        NotificationGroup group = new NotificationGroup("Easy Javadoc", NotificationDisplayType.BALLOON, true, null,
                General.AddJdk);
        // 创建一个通知，包括标题、内容、通知类型为INFORMATION，以及打开URL的通知监听器
        Notification notification = group.createNotification(
                title, content,
                NotificationType.INFORMATION, NotificationListener.URL_OPENING_LISTENER);

        // 如果传入的actions不为空，则遍历所有的action，将其添加到notification中
        if (ArrayUtils.isNotEmpty(actions)) {
            for (AnAction action : actions) {
                notification.addAction(action);
            }
        }

        // 发送通知
        notification.notify(null);
    }
}
