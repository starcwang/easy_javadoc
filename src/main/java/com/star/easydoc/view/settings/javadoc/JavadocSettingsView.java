package com.star.easydoc.view.settings.javadoc;

import javax.swing.*;

import com.intellij.openapi.components.ServiceManager;
import com.star.easydoc.config.EasyDocConfig;
import com.star.easydoc.config.EasyDocConfigComponent;
import gherkin.lexer.Tr;
import org.apache.commons.lang3.BooleanUtils;

/**
 * @author wangchao
 * @date 2022/12/04
 */
public class JavadocSettingsView {

    private EasyDocConfig config = ServiceManager.getService(EasyDocConfigComponent.class).getState();

    private JPanel panel;
    private JTextField authorTextField;
    private JTextField dateFormatTextField;
    private JPanel classPanel;
    private JPanel fieldPanel;
    private JLabel authorLabel;
    private JLabel dataFormatLabel;
    private JRadioButton simpleDocButton;
    private JRadioButton normalDocButton;
    private JLabel fieldDocLabel;
    private JPanel methodPanel;
    private JLabel methodReturnTypeLabel;
    private JRadioButton methodReturnCodeTypeButton;
    private JRadioButton methodReturnLinkTypeButton;
    private JPanel commonPanel;
    private JRadioButton methodReturnDocTypeButton;
    private JRadioButton docFirstRadioButton;
    private JRadioButton onlyTranslateRadioButton;
    private JLabel docPriorityLabel;

    public JavadocSettingsView() {
        simpleDocButton.addChangeListener(e -> {
            JRadioButton button = (JRadioButton)e.getSource();
            if (button.isSelected()) {
                normalDocButton.setSelected(false);
            } else {
                normalDocButton.setSelected(true);
            }
        });

        normalDocButton.addChangeListener(e -> {
            JRadioButton button = (JRadioButton)e.getSource();
            if (button.isSelected()) {
                simpleDocButton.setSelected(false);
            } else {
                simpleDocButton.setSelected(true);
            }
        });

        methodReturnCodeTypeButton.addChangeListener(e -> {
            JRadioButton button = (JRadioButton)e.getSource();
            if (button.isSelected()) {
                methodReturnLinkTypeButton.setSelected(false);
                methodReturnDocTypeButton.setSelected(false);
            }
        });

        methodReturnLinkTypeButton.addChangeListener(e -> {
            JRadioButton button = (JRadioButton)e.getSource();
            if (button.isSelected()) {
                methodReturnCodeTypeButton.setSelected(false);
                methodReturnDocTypeButton.setSelected(false);
            }
        });

        methodReturnDocTypeButton.addChangeListener(e -> {
            JRadioButton button = (JRadioButton)e.getSource();
            if (button.isSelected()) {
                methodReturnCodeTypeButton.setSelected(false);
                methodReturnLinkTypeButton.setSelected(false);
            }
        });

        docFirstRadioButton.addChangeListener(e -> {
            JRadioButton button = (JRadioButton)e.getSource();
            if (button.isSelected()) {
                onlyTranslateRadioButton.setSelected(false);
            } else {
                onlyTranslateRadioButton.setSelected(true);
            }
        });

        onlyTranslateRadioButton.addChangeListener(e -> {
            JRadioButton button = (JRadioButton)e.getSource();
            if (button.isSelected()) {
                docFirstRadioButton.setSelected(false);
            } else {
                docFirstRadioButton.setSelected(true);
            }
        });
    }

    private void createUIComponents() {

    }

    public void refresh() {
        if (BooleanUtils.isTrue(config.getSimpleFieldDoc())) {
            setSimpleDocButton(true);
            setNormalDocButton(false);
        } else {
            setSimpleDocButton(false);
            setNormalDocButton(true);
        }
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
        setAuthorTextField(config.getAuthor());
        setDateFormatTextField(config.getDateFormat());
        setDocPriority(config.getDocPriority());
    }

    public JComponent getComponent() {
        return panel;
    }

    public JTextField getAuthorTextField() {
        return authorTextField;
    }

    public JTextField getDateFormatTextField() {
        return dateFormatTextField;
    }

    public JRadioButton getSimpleDocButton() {
        return simpleDocButton;
    }

    public JRadioButton getNormalDocButton() {
        return normalDocButton;
    }

    public void setSimpleDocButton(boolean b) {
        simpleDocButton.setSelected(b);
    }

    public void setNormalDocButton(boolean b) {
        normalDocButton.setSelected(b);
    }

    public void setAuthorTextField(String author) {
        authorTextField.setText(author);
    }

    public void setDateFormatTextField(String dateFormat) {
        dateFormatTextField.setText(dateFormat);
    }

    public void setMethodReturnCodeTypeButton(boolean selecetd) {
        methodReturnCodeTypeButton.setSelected(selecetd);
    }

    public void setMethodReturnLinkTypeButton(boolean selecetd) {
        methodReturnLinkTypeButton.setSelected(selecetd);
    }

    public void setMethodReturnDocTypeButton(boolean selecetd) {
        methodReturnDocTypeButton.setSelected(selecetd);
    }

    public String getMethodReturnType() {
        if (methodReturnCodeTypeButton.isSelected()) {
            return EasyDocConfig.CODE_RETURN_TYPE;
        } else if (methodReturnLinkTypeButton.isSelected()) {
            return EasyDocConfig.LINK_RETURN_TYPE;
        } else if (methodReturnDocTypeButton.isSelected()) {
            return EasyDocConfig.DOC_RETURN_TYPE;
        }
        return null;
    }

    public String getDocPriority() {
        return docFirstRadioButton.isSelected() ?
            EasyDocConfig.DOC_FIRST : EasyDocConfig.ONLY_TRANSLATE;
    }

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
