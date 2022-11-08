package com.star.easydoc.kdoc.view.inner

import com.intellij.openapi.ui.DialogWrapper
import java.awt.Image
import javax.swing.ImageIcon
import javax.swing.JComponent
import javax.swing.JLabel
import javax.swing.JPanel

/**
 * @author wangchao
 * @date 2020/08/29
 */
class SupportView : DialogWrapper(false) {
    private lateinit var alipayLabel: JLabel
    private lateinit var wechatLabel: JLabel
    private lateinit var panel: JPanel

    init {
        init()
        title = "感谢大佬！"
    }

    override fun createCenterPanel(): JComponent {
        return panel
    }

    private fun createUIComponents() {
        val alipayIcon = ImageIcon(this.javaClass.getResource("/alipay.jpg"))
        var scale = 400.0 / alipayIcon.iconHeight
        alipayIcon.image = alipayIcon.image.getScaledInstance((alipayIcon.iconWidth * scale).toInt(), 400, Image.SCALE_DEFAULT)
        alipayLabel = JLabel(alipayIcon)
        alipayLabel.isVisible = true
        val wechatIcon = ImageIcon(this.javaClass.getResource("/wechat.png"))
        scale = 400.0 / wechatIcon.iconHeight
        wechatIcon.image = wechatIcon.image.getScaledInstance((wechatIcon.iconWidth * scale).toInt(), 400, Image.SCALE_DEFAULT)
        wechatLabel = JLabel(wechatIcon)
        wechatLabel.isVisible = true
    }
}