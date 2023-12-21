package com.star.easydoc.view.settings.javadoc;

import javax.swing.*;

import com.intellij.openapi.components.ServiceManager;
import com.star.easydoc.config.EasyDocConfig;
import com.star.easydoc.config.EasyDocConfigComponent;
import gherkin.lexer.Tr;
import org.apache.commons.lang3.BooleanUtils;

//用于构建一个用于设置Javadoc配置的界面，包括选择文档类型、设置字段文档、设置方法返回类型等选项。
// 声明一个名为JavadocSettingsView的公共类
public class JavadocSettingsView {

    // 从ServiceManager获取EasyDocConfigComponent的实例，并获取其状态，赋值给config变量
    private EasyDocConfig config = ServiceManager.getService(EasyDocConfigComponent.class).getState();

    // 声明一个JPanel类型的成员变量panel
    private JPanel panel;
    // 声明一个JTextField类型的成员变量authorTextField
    private JTextField authorTextField;
    // 声明一个JTextField类型的成员变量dateFormatTextField
    private JTextField dateFormatTextField;
    // 声明一个JPanel类型的成员变量classPanel
    private JPanel classPanel;
    // 声明一个JPanel类型的成员变量fieldPanel
    private JPanel fieldPanel;
    // 声明一个JLabel类型的成员变量authorLabel
    private JLabel authorLabel;
    // 声明一个JLabel类型的成员变量dataFormatLabel
    private JLabel dataFormatLabel;
    // 声明一个JRadioButton类型的成员变量simpleDocButton
    private JRadioButton simpleDocButton;
    // 声明一个JRadioButton类型的成员变量normalDocButton
    private JRadioButton normalDocButton;
    // 声明一个JLabel类型的成员变量fieldDocLabel
    private JLabel fieldDocLabel;
    // 声明一个JPanel类型的成员变量methodPanel
    private JPanel methodPanel;
    // 声明一个JLabel类型的成员变量methodReturnTypeLabel
    private JLabel methodReturnTypeLabel;
    // 声明一个JRadioButton类型的成员变量methodReturnCodeTypeButton
    private JRadioButton methodReturnCodeTypeButton;
    // 声明一个JRadioButton类型的成员变量methodReturnLinkTypeButton
    private JRadioButton methodReturnLinkTypeButton;
    // 声明一个JPanel类型的成员变量commonPanel
    private JPanel commonPanel;
    // 声明一个JRadioButton类型的成员变量methodReturnDocTypeButton
    private JRadioButton methodReturnDocTypeButton;
    // 声明一个JRadioButton类型的成员变量docFirstRadioButton
    private JRadioButton docFirstRadioButton;
    // 声明一个JRadioButton类型的成员变量onlyTranslateRadioButton
    private JRadioButton onlyTranslateRadioButton;
    // 声明一个JLabel类型的成员变量docPriorityLabel
    private JLabel docPriorityLabel;
  //simpleDocButton、normalDocButton、methodReturnCodeTypeButton、methodReturnLinkTypeButton、methodReturnDocTypeButton、docFirstRadioButton 和 onlyTranslateRadioButton 这些似乎都是 JRadioButton 对象，被添加了 ChangeListener。
 //当这些按钮的状态（是否被选中）改变时，会触发相应的 ChangeListener。
  //
  //当 simpleDocButton 或 normalDocButton 被选中时，另一个按钮会被取消选中。
  //当 methodReturnCodeTypeButton、methodReturnLinkTypeButton 或 methodReturnDocTypeButton 被选中时，其他两个按钮都会被取消选中。
  //当 docFirstRadioButton 被选中时，onlyTranslateRadioButton 会被取消选中；反之亦然。
    public JavadocSettingsView() {
        simpleDocButton.addChangeListener(e -> {
            JRadioButton button = (JRadioButton) e.getSource();
            if (button.isSelected()) {
                normalDocButton.setSelected(false);
            } else {
                normalDocButton.setSelected(true);
            }
        });

        normalDocButton.addChangeListener(e -> {
            JRadioButton button = (JRadioButton) e.getSource();
            if (button.isSelected()) {
                simpleDocButton.setSelected(false);
            } else {
                simpleDocButton.setSelected(true);
            }
        });

        methodReturnCodeTypeButton.addChangeListener(e -> {
            JRadioButton button = (JRadioButton) e.getSource();
            if (button.isSelected()) {
                methodReturnLinkTypeButton.setSelected(false);
                methodReturnDocTypeButton.setSelected(false);
            }
        });

        methodReturnLinkTypeButton.addChangeListener(e -> {
            JRadioButton button = (JRadioButton) e.getSource();
            if (button.isSelected()) {
                methodReturnCodeTypeButton.setSelected(false);
                methodReturnDocTypeButton.setSelected(false);
            }
        });

        methodReturnDocTypeButton.addChangeListener(e -> {
            JRadioButton button = (JRadioButton) e.getSource();
            if (button.isSelected()) {
                methodReturnCodeTypeButton.setSelected(false);
                methodReturnLinkTypeButton.setSelected(false);
            }
        });

        docFirstRadioButton.addChangeListener(e -> {
            JRadioButton button = (JRadioButton) e.getSource();
            if (button.isSelected()) {
                onlyTranslateRadioButton.setSelected(false);
            } else {
                onlyTranslateRadioButton.setSelected(true);
            }
        });

        onlyTranslateRadioButton.addChangeListener(e -> {
            JRadioButton button = (JRadioButton) e.getSource();
            if (button.isSelected()) {
                docFirstRadioButton.setSelected(false);
            } else {
                docFirstRadioButton.setSelected(true);
            }
        });
    }

    // 定义一个私有方法，用于创建UI组件。在这个例子中，该方法为空，可能是因为具体的UI组件创建逻辑在其他地方。
    private void createUIComponents() {

    }

    // 定义一个公共方法，用于刷新UI组件的状态。
    public void refresh() {
        // 检查config中的simpleFieldDoc字段是否为true。
        // 如果为true，则设置SimpleDocButton为选中状态，NormalDocButton为未选中状态；
        // 如果为false，则设置SimpleDocButton为未选中状态，NormalDocButton为选中状态。
        if (BooleanUtils.isTrue(config.getSimpleFieldDoc())) {
            setSimpleDocButton(true);
            setNormalDocButton(false);
        } else {
            setSimpleDocButton(false);
            setNormalDocButton(true);
        }

        // 检查config中的getMethodReturnType字段是否等于CODE_RETURN_TYPE。
        // 如果等于，则设置MethodReturnCodeTypeButton为选中状态，MethodReturnLinkTypeButton和MethodReturnDocTypeButton为未选中状态；
        // 如果不等于，则继续检查是否等于LINK_RETURN_TYPE或DOC_RETURN_TYPE，并根据检查结果设置相应的按钮状态。
        if (EasyDocConfig.CODE_RETURN_TYPE.equals(config.getMethodReturnType())) {
            setMethodReturnCodeTypeButton(true);
            setMethodReturnLinkTypeButton(false);
            setMethodReturnDocTypeButton(false);
        } else if (EasyDocConfig.LINK_RETURN_TYPE.equals(config.getMethodReturnType())) {
            setMethodReturnCodeTypeButton(false);
            setMethodReturnLinkTypeButton(true);
            setMethodReturnDocTypeButton(false);
        } else if (EasyDocConfig.DOC_RETURN_TYPE.equals(config.getMethodReturnType())) {
            setMethodReturnCodeTypeButton(false);
            setMethodReturnLinkTypeButton(false);
            setMethodReturnDocTypeButton(true);
        }

        // 设置AuthorTextField的值为config中的author字段；
        setAuthorTextField(config.getAuthor());

        // 设置DateFormatTextField的值为config中的dateFormat字段；
        setDateFormatTextField(config.getDateFormat());

        // 根据config中的docPriority字段的值设置DocPriority的选中状态；
        setDocPriority(config.getDocPriority());
    }

    // 定义一个名为getComponent的公共方法，该方法返回一个JComponent类型的panel组件
    public JComponent getComponent() {
        return panel;
    }

    // 定义一个名为getAuthorTextField的公共方法，该方法返回一个JTextField类型的authorTextField组件
    public JTextField getAuthorTextField() {
        return authorTextField;
    }

    // 定义一个名为getDateFormatTextField的公共方法，该方法返回一个JTextField类型的dateFormatTextField组件
    public JTextField getDateFormatTextField() {
        return dateFormatTextField;
    }

    // 定义一个名为getSimpleDocButton的公共方法，该方法返回一个JRadioButton类型的simpleDocButton组件
    public JRadioButton getSimpleDocButton() {
        return simpleDocButton;
    }

    // 定义一个名为getNormalDocButton的公共方法，该方法返回一个JRadioButton类型的normalDocButton组件
    public JRadioButton getNormalDocButton() {
        return normalDocButton;
    }

    // 定义一个名为setSimpleDocButton的公共方法，该方法接收一个boolean类型的参数b，并将simpleDocButton设置为选中状态（如果b为true），否则取消选中状态（如果b为false）
    public void setSimpleDocButton(boolean b) {
        simpleDocButton.setSelected(b);
    }

    // 定义一个名为setNormalDocButton的公共方法，该方法接收一个boolean类型的参数b，并将normalDocButton设置为选中状态（如果b为true），否则取消选中状态（如果b为false）
    public void setNormalDocButton(boolean b) {
        normalDocButton.setSelected(b);
    }

    // 定义一个名为setAuthorTextField的公共方法，该方法接收一个String类型的参数author，并将authorTextField设置为相应文本（如果author不为null或空字符串）
    public void setAuthorTextField(String author) {
        authorTextField.setText(author);
    }

    // 定义一个名为setDateFormatTextField的公共方法，该方法接收一个String类型的参数dateFormat，并将dateFormatTextField设置为相应文本（如果dateFormat不为null或空字符串）
    public void setDateFormatTextField(String dateFormat) {
        dateFormatTextField.setText(dateFormat);
    }

    // 设置方法返回码类型按钮的状态，如果selecetd为true，则选中该按钮，否则取消选中
    public void setMethodReturnCodeTypeButton(boolean selecetd) {
        methodReturnCodeTypeButton.setSelected(selecetd);
    }

    // 设置方法返回链接类型按钮的状态，如果selecetd为true，则选中该按钮，否则取消选中
    public void setMethodReturnLinkTypeButton(boolean selecetd) {
        methodReturnLinkTypeButton.setSelected(selecetd);
    }

    // 设置方法返回文档类型按钮的状态，如果selecetd为true，则选中该按钮，否则取消选中
    public void setMethodReturnDocTypeButton(boolean selecetd) {
        methodReturnDocTypeButton.setSelected(selecetd);
    }

    // 获取方法的返回类型，根据选中的按钮返回相应的字符串
    public String getMethodReturnType() {
        if (methodReturnCodeTypeButton.isSelected()) {
            return EasyDocConfig.CODE_RETURN_TYPE; // 如果选中了方法返回码类型按钮，返回EasyDocConfig.CODE_RETURN_TYPE
        } else if (methodReturnLinkTypeButton.isSelected()) {
            return EasyDocConfig.LINK_RETURN_TYPE; // 如果选中了方法返回链接类型按钮，返回EasyDocConfig.LINK_RETURN_TYPE
        } else if (methodReturnDocTypeButton.isSelected()) {
            return EasyDocConfig.DOC_RETURN_TYPE; // 如果选中了方法返回文档类型按钮，返回EasyDocConfig.DOC_RETURN_TYPE
        }
        return null; // 如果没有任何按钮被选中，返回null
    }

    // 获取文档优先级，如果选中了docFirstRadioButton，则返回EasyDocConfig.DOC_FIRST，否则返回EasyDocConfig.ONLY_TRANSLATE
    public String getDocPriority() {
        return docFirstRadioButton.isSelected() ?
                EasyDocConfig.DOC_FIRST : EasyDocConfig.ONLY_TRANSLATE;
    }

    // 设置文档优先级，如果docPriority等于EasyDocConfig.DOC_FIRST，则选中docFirstRadioButton并取消选中onlyTranslateRadioButton，否则选中onlyTranslateRadioButton并取消选中docFirstRadioButton
    public void setDocPriority(String docPriority) {
        if (EasyDocConfig.DOC_FIRST.equals(docPriority)) {
            docFirstRadioButton.setSelected(true);
            onlyTranslateRadioButton.setSelected(false);
        } else {
            docFirstRadioButton.setSelected(false);
            onlyTranslateRadioButton.setSelected(true);
        }
    }
}
