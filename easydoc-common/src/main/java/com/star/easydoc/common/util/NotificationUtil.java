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
        NotificationGroup group = new NotificationGroup("Easy Javadoc", NotificationDisplayType.BALLOON, true, null,
            General.AddJdk);
        Notification notification = group.createNotification(
            title, content,
            NotificationType.INFORMATION, NotificationListener.URL_OPENING_LISTENER);

        if (ArrayUtils.isNotEmpty(actions)) {
            for (AnAction action : actions) {
                notification.addAction(action);
            }
        }

        notification.notify(null);
    }
}
