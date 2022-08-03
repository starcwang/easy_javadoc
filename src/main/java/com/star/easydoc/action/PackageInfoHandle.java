package com.star.easydoc.action;

import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiInvalidElementAccessException;
import com.intellij.psi.PsiPackage;
import com.intellij.util.IncorrectOperationException;
import com.star.easydoc.service.DocGeneratorService;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 * 创建类, 可能有更便捷的方式创建实体类. 有待优化
 */
public class PackageInfoHandle {
    public static final String INFO_FILE_NAME = "package-info.java";
    public static final String PACKAGE_INFO_DESCRIBE = "PACKAGE_INFO_DESCRIBE";
    private static DocGeneratorService docGeneratorService = ServiceManager.getService(DocGeneratorService.class);

    public static void handle(PsiPackage psiPackage, String comment) {
        try {
            Project project = psiPackage.getProject();
            PsiDirectory psiDirectory = psiPackage.getDirectories()[0];
            //是文件夹，文件夹处理
            VirtualFile virtualFile = psiDirectory.getVirtualFile().findChild(PackageInfoHandle.INFO_FILE_NAME);
            if (virtualFile == null) {
                //文件不存在则创建文件
                String result = docGeneratorService.generate(psiPackage).replace("${" + PACKAGE_INFO_DESCRIBE + "}", comment) +
                    "package " + psiPackage.getQualifiedName() + ";\n";
                WriteCommandAction.writeCommandAction(project).run(() -> {
                    try {
                        PsiFile file = psiDirectory.createFile(PackageInfoHandle.INFO_FILE_NAME);
                        VirtualFile fileVirtualFile = file.getVirtualFile();
                        fileVirtualFile.setBinaryContent(result.getBytes(StandardCharsets.UTF_8));
                    } catch (PsiInvalidElementAccessException | IOException | IncorrectOperationException ignored) {
                        ignored.printStackTrace();
                    }
                });
                //处理完就结束了
                return;
            } else {
                //                virtualFile.getCharset()
                try (InputStream inputStream = virtualFile.getInputStream()) {
                    int available = inputStream.available();
                    byte[] bytes = new byte[available];
                    inputStream.read(bytes);
                    String val = new String(bytes, virtualFile.getCharset());
                    int index = val.indexOf("/**");
                    StringBuffer buffer = new StringBuffer();
                    if (index >= 0) {
                        if (index > 0) {
                            buffer.append(val, 0, index);
                        }
                        buffer.append("/**\n");
                        buffer.append(" * ").append(comment);
                        buffer.append("\n");
                        buffer.append(" * ");
                        buffer.append(val, index + 3, val.length());
                    } else {
                        //没有注释生成就行了
                        buffer.append(docGeneratorService.generate(psiPackage).replace("${" + PACKAGE_INFO_DESCRIBE + "}", comment));
                        buffer.append(val);
                    }
                    String finalVal = buffer.toString();
                    WriteCommandAction.writeCommandAction(project).run(() -> {
                        try {
                            virtualFile.setBinaryContent(finalVal.getBytes(StandardCharsets.UTF_8));
                        } catch (PsiInvalidElementAccessException | IOException | IncorrectOperationException ignored) {
                            ignored.printStackTrace();
                        }
                    });
                }
                //存在package-info，逻辑需要处理,先干脆不动算了，即然存在，应该会写注释的~~
                //                System.out.println("6666");
            }
        } catch (Exception e) {
            //由于外面stream执行，错误处理直接吞掉，保证后续也可以执行
            e.printStackTrace();
        }
        VirtualFileManager.getInstance().refreshWithoutFileWatcher(true);

    }

}
