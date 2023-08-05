package com.star.easydoc.service;

import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorModificationUtil;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiJavaDocumentedElement;
import com.intellij.psi.codeStyle.CodeStyleManager;
import com.intellij.psi.impl.source.tree.PsiWhiteSpaceImpl;
import com.intellij.psi.javadoc.PsiDocComment;
import com.intellij.util.ThrowableRunnable;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.kotlin.kdoc.psi.api.KDoc;
import org.jetbrains.kotlin.psi.KtDeclaration;
import org.jetbrains.kotlin.psi.KtElement;

/**
 * @author wangchao
 * @date 2019/08/25
 */
public class WriterService {
    private static final Logger LOGGER = Logger.getInstance(WriterService.class);

    public void writeJavadoc(Project project, PsiElement psiElement, PsiDocComment comment, int emptyLineNum) {
        try {
            WriteCommandAction.writeCommandAction(project).run(
                (ThrowableRunnable<Throwable>)() -> {
                    if (psiElement.getContainingFile() == null) {
                        return;
                    }

                    // 写入文档注释
                    if (psiElement instanceof PsiJavaDocumentedElement) {
                        PsiDocComment psiDocComment = ((PsiJavaDocumentedElement)psiElement).getDocComment();
                        if (psiDocComment == null) {
                            psiElement.getNode().addChild(comment.getNode(), psiElement.getFirstChild().getNode());
                        } else {
                            psiDocComment.replace(comment);
                        }
                    }

                    // 格式化文档注释
                    CodeStyleManager codeStyleManager = CodeStyleManager.getInstance(psiElement.getProject());
                    PsiElement javadocElement = psiElement.getFirstChild();
                    int startOffset = javadocElement.getTextOffset();
                    int endOffset = javadocElement.getTextOffset() + javadocElement.getText().length();
                    codeStyleManager.reformatText(psiElement.getContainingFile(), startOffset, endOffset + 1);

                    // 添加空行
                    if (emptyLineNum > 0) {
                        PsiElement whiteSpaceElement = psiElement.getChildren()[1];
                        if (whiteSpaceElement instanceof PsiWhiteSpaceImpl) {
                            // 修改whiteSpace
                            String space = StringUtils.repeat("\n", emptyLineNum + 1);
                            String exists = StringUtils.stripStart(whiteSpaceElement.getText(), "\n");
                            ((PsiWhiteSpaceImpl)whiteSpaceElement).replaceWithText(space + exists);
                        }
                    }
                });
        } catch (Throwable throwable) {
            LOGGER.error("写入错误", throwable);
        }
    }

    public void write(Project project, Editor editor, String text) {
        if (project == null || editor == null || StringUtils.isBlank(text)) {
            return;
        }
        try {
            WriteCommandAction.writeCommandAction(project).run(
                (ThrowableRunnable<Throwable>)() -> {
                    int start = editor.getSelectionModel().getSelectionStart();
                    EditorModificationUtil.insertStringAtCaret(editor, text);
                    editor.getSelectionModel().setSelection(start, start + text.length());
                });
        } catch (Throwable throwable) {
            LOGGER.error("写入错误", throwable);
        }
    }

    public void writeKdoc(Project project, KtElement ktElement, KDoc comment) {
        try {
            WriteCommandAction.writeCommandAction(project).run(
                (ThrowableRunnable<Throwable>)() -> {
                    if (ktElement.getContainingFile() == null) {
                        return;
                    }

                    // 写入文档注释
                    if (ktElement instanceof KtDeclaration) {
                        KDoc kDoc = ((KtDeclaration)ktElement).getDocComment();
                        if (kDoc == null) {
                            ktElement.getNode().addChild(comment.getNode(), ktElement.getFirstChild().getNode());
                        } else {
                            kDoc.replace(comment);
                        }
                    }

                    // 格式化文档注释
                    CodeStyleManager codeStyleManager = CodeStyleManager.getInstance(ktElement.getProject());
                    PsiElement javadocElement = ktElement.getFirstChild();
                    int startOffset = javadocElement.getTextOffset();
                    int endOffset = javadocElement.getTextOffset() + javadocElement.getText().length();
                    codeStyleManager.reformatText(ktElement.getContainingFile(), startOffset, endOffset + 1);
                }
            );
        } catch (Throwable throwable) {
            LOGGER.error("写入错误", throwable);
        }
    }

}
