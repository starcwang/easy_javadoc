package com.star.easydoc.view.inner;

import java.awt.*;

import javax.swing.*;

import com.intellij.openapi.ui.DialogWrapper;
import org.jetbrains.annotations.Nullable;

/**
 * SupportView 类是一个自定义的对话框，用于显示支持信息。
 * 继承自 DialogWrapper 类，实现了一个简单的对话框窗口。
 * 提供了支付宝和微信的二维码图片，并通过 JLabel 组件将图片显示在对话框中心面板上。
 *
 * @author wangchao
 * @date 2020/08/29
 */
public class SupportView extends DialogWrapper {

    private JLabel alipayLabel;
    private JLabel wechatLabel;
    private JPanel panel;

    /**
     * 构造函数，初始化 SupportView 实例。
     * 设置对话框为非模态对话框，并设置对话框标题为 "感谢大佬！"。
     */
    public SupportView() {
        super(false);
        init();
        setTitle("感谢大佬！");
    }

    /**
     * 创建对话框的中心面板，返回一个 JComponent 对象。
     * 将 panel（面板）作为中心面板。
     *
     * @return 中心面板
     */
    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        return panel;
    }

    /**
     * 在中心面板上创建 UI 组件。
     * 通过 ImageIcon 加载支付宝和微信的二维码图片，并通过 JLabel 将图片显示在对话框中心面板中。
     * 将二维码图片缩放为适合的大小，并设置可见性为 true。
     */
    private void createUIComponents() {
        ImageIcon alipayIcon = new ImageIcon(this.getClass().getResource("/alipay.jpg"));
        double scale = 400d / alipayIcon.getIconHeight();
        alipayIcon.setImage(alipayIcon.getImage().getScaledInstance((int)(alipayIcon.getIconWidth() * scale), 400, Image.SCALE_DEFAULT));
        alipayLabel = new JLabel(alipayIcon);
        alipayLabel.setVisible(true);

        ImageIcon wechatIcon = new ImageIcon(this.getClass().getResource("/wechat.png"));
        scale = 400d / wechatIcon.getIconHeight();
        wechatIcon.setImage(wechatIcon.getImage().getScaledInstance((int)(wechatIcon.getIconWidth() * scale), 400, Image.SCALE_DEFAULT));
        wechatLabel = new JLabel(wechatIcon);
        wechatLabel.setVisible(true);
    }
}
