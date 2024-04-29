package com.star.easydoc.javadoc.service.generator.impl;

import java.util.List;

import com.google.common.collect.Lists;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementFactory;
import com.intellij.psi.PsiJavaDocumentedElement;
import com.intellij.psi.javadoc.PsiDocComment;
import com.intellij.psi.javadoc.PsiDocTag;
import com.star.easydoc.config.EasyDocConfig;
import com.star.easydoc.javadoc.service.generator.DocGenerator;

/**
 * @author wangchao
 * @date 2024/03/11
 */
public abstract class AbstractDocGenerator implements DocGenerator {

    /**
     * 合并
     *
     * @param psiElement PSI元件
     * @param targetDoc 目标文档
     * @return {@link String}
     */
    protected String merge(PsiJavaDocumentedElement psiElement, String targetDoc) {
        PsiDocComment sDoc = psiElement.getDocComment();

        // 强制覆盖直接返回
        if (sDoc == null || EasyDocConfig.COVER_MODE_FORCE.equals(getConfig().getCoverMode())) {
            return targetDoc;
        }

        // 和已有的做merge
        List<String> docList = Lists.newArrayList();
        PsiElementFactory factory = PsiElementFactory.SERVICE.getInstance(psiElement.getProject());
        PsiDocComment tDoc = factory.createDocCommentFromText(targetDoc);
        PsiDocTag[] sTags = sDoc.getTags();
        for (PsiElement child : tDoc.getChildren()) {
            if (!(child instanceof PsiDocTag)) {
                docList.add(child.getText());
                continue;
            }
            PsiDocTag docTag = (PsiDocTag)child;
            if (docTag.getName().equals("param") || docTag.getName().equals("throws")
                || docTag.getName().equals("exception")) {
                docList.add(child.getText());
                continue;
            }
            boolean append = true;
            for (PsiDocTag sTag : sTags) {
                if (sTag.getName().equals(docTag.getName())) {
                    docList.add(sTag.getText());
                    append = false;
                    break;
                }
            }
            if (append) {
                docList.add(docTag.getText());
            }
        }
        return String.join("\n", docList);
    }

    /**
     * 获取配置
     *
     * @return {@code EasyDocConfig }
     */
    protected abstract EasyDocConfig getConfig();
}
