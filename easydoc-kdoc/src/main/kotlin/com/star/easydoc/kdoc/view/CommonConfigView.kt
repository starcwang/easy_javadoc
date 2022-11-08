package com.star.easydoc.kdoc.view

import com.google.common.collect.Lists
import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.diagnostic.Logger
import com.intellij.ui.CollectionListModel
import com.intellij.ui.ListCellRendererWrapper
import com.intellij.ui.ToolbarDecorator
import com.intellij.ui.components.JBList
import com.star.easydoc.common.Consts
import com.star.easydoc.common.config.EasyDocConfig
import com.star.easydoc.common.util.JsonUtil
import com.star.easydoc.kdoc.config.EasyJavadocConfigComponent
import com.star.easydoc.kdoc.view.inner.SupportView
import com.star.easydoc.kdoc.view.inner.WordMapAddView
import com.star.easydoc.service.translator.TranslatorService
import org.apache.commons.io.FileUtils
import org.apache.commons.lang3.BooleanUtils
import java.awt.Desktop
import java.awt.event.ItemEvent
import java.io.File
import java.net.URI
import java.nio.charset.StandardCharsets
import javax.swing.*
import javax.swing.event.ChangeEvent

/**
 * @author wangchao
 * @date 2019/08/25
 */
class CommonConfigView {
    private val translatorService = ServiceManager.getService(TranslatorService::class.java)
    private val config: EasyDocConfig = ServiceManager.getService(EasyJavadocConfigComponent::class.java).state
    private lateinit var panel: JPanel
    private var wordMapPanel: JPanel? = null
    lateinit var authorTextField: JTextField
    lateinit var dateFormatTextField: JTextField
    private lateinit var classPanel: JPanel
    private lateinit var fieldPanel: JPanel
    private lateinit var authorLabel: JLabel
    private lateinit var dataFormatLabel: JLabel
    lateinit var simpleDocButton: JRadioButton
    lateinit var normalDocButton: JRadioButton
    private lateinit var fieldDocLabel: JLabel
    private lateinit var commonPanel: JPanel
    lateinit var translatorBox: JComboBox<*>
    private lateinit var translatorLabel: JLabel
    private lateinit var importButton: JButton
    private lateinit var exportButton: JButton
    lateinit var appIdTextField: JTextField
    lateinit var tokenTextField: JTextField
    private lateinit var resetButton: JButton
    private lateinit var clearButton: JButton
    private lateinit var appIdLabel: JLabel
    private lateinit var tokenLabel: JLabel
    lateinit var secretIdTextField: JTextField
    lateinit var secretKeyTextField: JTextField
    private lateinit var secretIdLabel: JLabel
    private lateinit var secretKeyLabel: JLabel
    private lateinit var starButton: JButton
    private lateinit var payButton: JButton
    private lateinit var methodPanel: JPanel
    private lateinit var methodReturnTypeLabel: JLabel
    private lateinit var methodReturnCodeTypeButton: JRadioButton
    private lateinit var methodReturnLinkTypeButton: JRadioButton
    lateinit var accessKeyIdTextField: JTextField
    lateinit var accessKeySecretTextField: JTextField
    private lateinit var accessKeyIdLabel: JLabel
    private lateinit var accessKeySecretLabel: JLabel
    private lateinit var typeMapList: JBList<Map.Entry<String, String>>

    init {
        refreshWordMap()
        setVisible(translatorBox.selectedItem!!)
        simpleDocButton.addChangeListener { e: ChangeEvent ->
            val button = e.source as JRadioButton
            normalDocButton.isSelected = !button.isSelected
        }
        normalDocButton.addChangeListener { e: ChangeEvent ->
            val button = e.source as JRadioButton
            simpleDocButton.isSelected = !button.isSelected
        }
        methodReturnCodeTypeButton.addChangeListener { e: ChangeEvent ->
            val button = e.source as JRadioButton
            methodReturnLinkTypeButton.isSelected = !button.isSelected
        }
        methodReturnLinkTypeButton.addChangeListener { e: ChangeEvent ->
            val button = e.source as JRadioButton
            methodReturnCodeTypeButton.isSelected = !button.isSelected
        }
        importButton.addActionListener {
            val chooser = JFileChooser()
            chooser.fileSelectionMode = JFileChooser.FILES_ONLY
            val res = chooser.showOpenDialog(JLabel())
            if (JFileChooser.APPROVE_OPTION != res) {
                return@addActionListener
            }
            val file = chooser.selectedFile ?: return@addActionListener
            try {
                val json = FileUtils.readFileToString(file, StandardCharsets.UTF_8.name())
                val configuration = JsonUtil.fromJson(json, EasyDocConfig::class.java) ?: throw IllegalArgumentException("文件中内容格式不正确，请确认是否是json格式")
                ServiceManager.getService(EasyJavadocConfigComponent::class.java).loadState(configuration)
                refresh()
            } catch (e: Exception) {
                LOGGER.error("读取文件异常", e)
            }
        }
        exportButton.addActionListener {
            val chooser = JFileChooser()
            chooser.fileSelectionMode = JFileChooser.DIRECTORIES_ONLY
            val res = chooser.showSaveDialog(JLabel())
            if (JFileChooser.APPROVE_OPTION != res) {
                return@addActionListener
            }
            val file = chooser.selectedFile ?: return@addActionListener
            try {
                val targetFile = File(file.absolutePath + "/easy_javadoc.json")
                FileUtils.write(targetFile, JsonUtil.toJson(config), StandardCharsets.UTF_8.name())
            } catch (e: Exception) {
                LOGGER.error("写入文件异常", e)
            }
        }
        resetButton.addActionListener {
            val result = JOptionPane.showConfirmDialog(
                null, "重置将删除所有配置，确认重置?", "确认", JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.INFORMATION_MESSAGE
            )
            if (result == JOptionPane.OK_OPTION) {
                config.reset()
                refresh()
            }
        }
        clearButton.addActionListener {
            val result = JOptionPane.showConfirmDialog(
                null, "确认清空缓存?", "确认", JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.INFORMATION_MESSAGE
            )
            if (result == JOptionPane.OK_OPTION) {
                translatorService.clearCache()
            }
        }
        starButton.addActionListener {
            try {
                val dp = Desktop.getDesktop()
                if (dp.isSupported(Desktop.Action.BROWSE)) {
                    dp.browse(URI.create("https://github.com/starcwang/easy_javadoc"))
                }
            } catch (e: Exception) {
                LOGGER.error("打开链接失败:https://github.com/starcwang/easy_javadoc", e)
            }
        }
        payButton.addActionListener {
            val supportView = SupportView()
            supportView.show()
        }
        translatorBox.addItemListener { e: ItemEvent ->
            val jComboBox = e.source as JComboBox<*>
            setVisible(jComboBox.selectedItem!!)
        }
    }

    private fun setVisible(selectedItem: Any) {
        if (Consts.BAIDU_TRANSLATOR == selectedItem) {
            appIdLabel.isVisible = true
            tokenLabel.isVisible = true
            secretIdLabel.isVisible = false
            secretKeyLabel.isVisible = false
            accessKeyIdLabel.isVisible = false
            accessKeySecretLabel.isVisible = false
            appIdTextField.isVisible = true
            tokenTextField.isVisible = true
            secretIdTextField.isVisible = false
            secretKeyTextField.isVisible = false
            accessKeyIdTextField.isVisible = false
            accessKeySecretTextField.isVisible = false
        } else if (Consts.TENCENT_TRANSLATOR == selectedItem) {
            appIdLabel.isVisible = false
            tokenLabel.isVisible = false
            secretIdLabel.isVisible = true
            secretKeyLabel.isVisible = true
            accessKeyIdLabel.isVisible = false
            accessKeySecretLabel.isVisible = false
            appIdTextField.isVisible = false
            tokenTextField.isVisible = false
            secretIdTextField.isVisible = true
            secretKeyTextField.isVisible = true
            accessKeyIdTextField.isVisible = false
            accessKeySecretTextField.isVisible = false
        } else if (Consts.ALIYUN_TRANSLATOR == selectedItem) {
            appIdLabel.isVisible = false
            tokenLabel.isVisible = false
            secretIdLabel.isVisible = false
            secretKeyLabel.isVisible = false
            accessKeyIdLabel.isVisible = true
            accessKeySecretLabel.isVisible = true
            appIdTextField.isVisible = false
            tokenTextField.isVisible = false
            secretIdTextField.isVisible = false
            secretKeyTextField.isVisible = false
            accessKeyIdTextField.isVisible = true
            accessKeySecretTextField.isVisible = true
        } else {
            appIdLabel.isVisible = false
            tokenLabel.isVisible = false
            secretIdLabel.isVisible = false
            secretKeyLabel.isVisible = false
            accessKeyIdLabel.isVisible = false
            accessKeySecretLabel.isVisible = false
            appIdTextField.isVisible = false
            tokenTextField.isVisible = false
            secretIdTextField.isVisible = false
            secretKeyTextField.isVisible = false
            accessKeyIdTextField.isVisible = false
            accessKeySecretTextField.isVisible = false
        }
    }

    private fun createUIComponents() {
        typeMapList = JBList<Map.Entry<String, String>>(CollectionListModel<Map.Entry<String, String>>(Lists.newArrayList<Map.Entry<String, String>>()))
        typeMapList.selectionMode = ListSelectionModel.SINGLE_SELECTION
        typeMapList.cellRenderer = object : ListCellRendererWrapper<Map.Entry<String, String>>() {
            override fun customize(list: JList<*>, value: Map.Entry<String, String>, index: Int, selected: Boolean, hasFocus: Boolean) {
                setText(value.key + " -> " + value.value)
            }
        }
        typeMapList.setEmptyText("请添加单词映射")
        typeMapList.selectedIndex = 0
        val toolbarDecorator = ToolbarDecorator.createDecorator(typeMapList)
        toolbarDecorator.setAddAction {
            val wordMapAddView = WordMapAddView()
            if (wordMapAddView.showAndGet()) {
                config.wordMap[wordMapAddView.mapping.key] = wordMapAddView.mapping.value
                refreshWordMap()
            }
        }
        toolbarDecorator.setRemoveAction {
            val typeMap: MutableMap<String, String> = config.wordMap
            typeMap.remove(typeMapList.selectedValue.key)
            refreshWordMap()
        }
        wordMapPanel = toolbarDecorator.createPanel()
    }

    fun refresh() {
        if (BooleanUtils.isTrue(config.simpleFieldDoc)) {
            setSimpleDocButton(true)
            setNormalDocButton(false)
        } else {
            setSimpleDocButton(false)
            setNormalDocButton(true)
        }
        if (EasyDocConfig.CODE_RETURN_TYPE == config.methodReturnType) {
            setMethodReturnCodeTypeButton(true)
            setMethodReturnLinkTypeButton(false)
        } else if (EasyDocConfig.LINK_RETURN_TYPE == config.methodReturnType) {
            setMethodReturnCodeTypeButton(false)
            setMethodReturnLinkTypeButton(true)
        }
        setAuthorTextField(config.author)
        setDateFormatTextField(config.dateFormat)
        setTranslatorBox(config.translator)
        setAppIdTextField(config.appId)
        setTokenTextField(config.token)
        setSecretIdTextField(config.secretId)
        setSecretKeyTextField(config.secretKey)
        setAccessKeyIdTextField(config.accessKeyId)
        setAccessKeySecretTextField(config.accessKeySecret)
        refreshWordMap()
    }

    private fun refreshWordMap() {
        if (config.wordMap != null) {
            typeMapList.model = CollectionListModel<Map.Entry<String, String>>(Lists.newArrayList<Map.Entry<String, String>>(config.wordMap.entries))
        }
    }

    val component: JComponent
        get() = panel

    fun setSimpleDocButton(b: Boolean) {
        simpleDocButton.isSelected = b
    }

    fun setNormalDocButton(b: Boolean) {
        normalDocButton.isSelected = b
    }

    fun setAuthorTextField(author: String) {
        authorTextField.text = author
    }

    fun setTranslatorBox(translator: String) {
        translatorBox.selectedItem = translator
    }

    fun setDateFormatTextField(dateFormat: String) {
        dateFormatTextField.text = dateFormat
    }

    fun setAppIdTextField(appId: String) {
        appIdTextField.text = appId
    }

    fun setTokenTextField(token: String) {
        tokenTextField.text = token
    }

    fun setSecretIdTextField(secretId: String) {
        secretIdTextField.text = secretId
    }

    fun setSecretKeyTextField(secretKey: String) {
        secretKeyTextField.text = secretKey
    }

    fun setAccessKeyIdTextField(accessKeyId: String) {
        accessKeyIdTextField.text = accessKeyId
    }

    fun setAccessKeySecretTextField(accessKeySecret: String) {
        accessKeySecretTextField.text = accessKeySecret
    }

    fun setMethodReturnCodeTypeButton(selecetd: Boolean) {
        methodReturnCodeTypeButton.isSelected = selecetd
    }

    fun setMethodReturnLinkTypeButton(selecetd: Boolean) {
        methodReturnLinkTypeButton.isSelected = selecetd
    }

    val methodReturnType: String
        get() = if (methodReturnCodeTypeButton.isSelected) EasyDocConfig.CODE_RETURN_TYPE else EasyDocConfig.LINK_RETURN_TYPE

    companion object {
        private val LOGGER = Logger.getInstance(CommonConfigView::class.java)
    }
}