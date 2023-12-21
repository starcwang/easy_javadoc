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
    // 日志记录器
    private static final Logger LOGGER = Logger.getInstance(WriterService.class);

    // 写Javadoc，可以指定空行的数量，并对文档注释进行格式化
    /**
     * 将 Javadoc 注释写入指定的代码元素。
     * @param project      当前项目对象
     * @param psiElement   需要添加注释的代码元素
     * @param comment      要写入的 Javadoc 注释
     * @param emptyLineNum 需要在 Javadoc 注释前添加的空行数
     */
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

    // 在编辑器中写入指定文本，会在当前光标位置插入文本并选中
    /**
     * 将指定的文本写入到编辑器中。
     * @param project 当前项目对象
     * @param editor 编辑器对象
     * @param text 要写入的文本
     **/
    public void write(Project project, Editor editor, String text) {
        // 判断传入的project、editor和text是否为空
        if (project == null || editor == null || StringUtils.isBlank(text)) {
            return;
        }
        try {
            WriteCommandAction.writeCommandAction(project).run( // 执行写入操作，确保在 IntelliJ IDEA 的 undo/redo 历史记录中能够正确记录该操作
                (ThrowableRunnable<Throwable>)() -> {// 使用ThrowableRunnable来捕获可能抛出的异常
                    // 获取当前选择的文本起始位置（start）
                    int start = editor.getSelectionModel().getSelectionStart();
                    // 将文本插入到编辑器的光标位置处
                    EditorModificationUtil.insertStringAtCaret(editor, text);
                    // 设置选择区域，以便在插入文本后将插入的文本选中
                    editor.getSelectionModel().setSelection(start, start + text.length());
                });
        } catch (Throwable throwable) {
            // 记录日志
            LOGGER.error("写入错误", throwable);
        }
    }

    //在 Kotlin 代码中写入 KDoc 文档注释，同样也会进行格式化
    public void writeKdoc(Project project, KtElement ktElement, KDoc comment) {
        try {
            WriteCommandAction.writeCommandAction(project).run( // 执行写入操作
                    // 使用ThrowableRunnable来捕获可能抛出的异常
                (ThrowableRunnable<Throwable>)() -> {
                    //判断ktElement所属的文件是否为空，如果为空则直接返回
                    if (ktElement.getContainingFile() == null) {
                        return;
                    }

                    // 写入文档注释
                    // 如果ktElement是KtDeclaration类型的声明，则获取其原始的文档注释kDoc对象
                    if (ktElement instanceof KtDeclaration) {
                        KDoc kDoc = ((KtDeclaration)ktElement).getDocComment();
                        if (kDoc == null) {// 如果kDoc为空，则将新的注释（comment）添加到ktElement的第一个子节点之前
                            ktElement.getNode().addChild(comment.getNode(), ktElement.getFirstChild().getNode());
                        } else {
                            kDoc.replace(comment);
                        }
                    }

                    // 格式化文档注释
                    // 使用CodeStyleManager对文档注释进行格式化
                    CodeStyleManager codeStyleManager = CodeStyleManager.getInstance(ktElement.getProject());
                    // 获取ktElement的第一个子节点
                    PsiElement javadocElement = ktElement.getFirstChild();
                    // 获取其起始偏移量（startOffset）和结束偏移量（endOffset）
                    int startOffset = javadocElement.getTextOffset();
                    int endOffset = javadocElement.getTextOffset() + javadocElement.getText().length();
                    // 调用reformatText方法对该范围内的代码进行重新格式化
                    codeStyleManager.reformatText(ktElement.getContainingFile(), startOffset, endOffset + 1);
                }
            );
        } catch (Throwable throwable) {
            // 记录日志
            LOGGER.error("写入错误", throwable);
        }
    }

}
