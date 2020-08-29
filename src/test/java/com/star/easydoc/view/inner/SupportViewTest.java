package com.star.easydoc.view.inner;

import java.awt.*;

import javax.swing.*;

import org.junit.Test;

/**
 * @author wangchao
 * @date 2020/08/29
 */
public class SupportViewTest {

    @Test
    public void testCreateCenterPanel() throws Exception {
        // 确保一个漂亮的外观风格
        JFrame.setDefaultLookAndFeelDecorated(true);

        // 创建及设置窗口
        JFrame frame = new JFrame("HelloWorldSwing");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // 添加 "Hello World" 标签
        ImageIcon imageIcon = new ImageIcon(this.getClass().getResource("/alipay.jpg"));
        double scale = 400d / imageIcon.getIconHeight();
        imageIcon.setImage(imageIcon.getImage().getScaledInstance((int)(imageIcon.getIconWidth() * scale), 400, Image.SCALE_DEFAULT));


        JLabel label = new JLabel(imageIcon);
        label.setVisible(true);

        frame.getContentPane().add(label);
        // 显示窗口
        frame.pack();
        frame.setVisible(true);
        Thread.sleep(10000);
    }
}