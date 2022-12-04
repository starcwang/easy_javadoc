package com.star.easydoc.service;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiPackage;
import com.star.easydoc.javadoc.service.JavaDocGeneratorServiceImpl;

/**
 * 创建类, 可能有更便捷的方式创建实体类. 有待优化
 */
public class PackageInfoService {
    public static final String INFO_FILE_NAME = "package-info.java";
    public static final String PACKAGE_INFO_DESCRIBE = "PACKAGE_INFO_DESCRIBE";
    private static JavaDocGeneratorServiceImpl docGeneratorService = ServiceManager.getService(JavaDocGeneratorServiceImpl.class);

    public void handle(PsiPackage psiPackage, String comment) {
        try {
            Project project = psiPackage.getProject();
            PsiDirectory psiDirectory = psiPackage.getDirectories()[0];
            //是文件夹，文件夹处理
            VirtualFile virtualFile = psiDirectory.getVirtualFile().findChild(PackageInfoService.INFO_FILE_NAME);
            if (virtualFile == null) {
                //文件不存在则创建文件
                String result = docGeneratorService.generate(psiPackage).replace("${" + PACKAGE_INFO_DESCRIBE + "}", comment) +
                    "package " + psiPackage.getQualifiedName() + ";\n";
                WriteCommandAction.writeCommandAction(project).run(() -> {
                    try {
                        PsiFile file = psiDirectory.createFile(PackageInfoService.INFO_FILE_NAME);
                        VirtualFile fileVirtualFile = file.getVirtualFile();
                        fileVirtualFile.setBinaryContent(result.getBytes(StandardCharsets.UTF_8));
                    } catch (Exception ignore) {
                    }
                });
                //处理完就结束了
                return;
            } else {
                try (InputStream inputStream = virtualFile.getInputStream()) {
                    int available = inputStream.available();
                    byte[] bytes = new byte[available];
                    inputStream.read(bytes);
                    String val = new String(bytes, virtualFile.getCharset());
                    int index = val.indexOf("/**");
                    StringBuilder builder = new StringBuilder();
                    if (index >= 0) {
                        if (index > 0) {
                            builder.append(val, 0, index);
                        }
                        builder.append("/**\n");
                        builder.append(" * ").append(comment);
                        builder.append("\n");
                        builder.append(" * ");
                        builder.append(val, index + 3, val.length());
                    } else {
                        //没有注释生成就行了
                        builder.append(docGeneratorService.generate(psiPackage).replace("${" + PACKAGE_INFO_DESCRIBE + "}", comment));
                        builder.append(val);
                    }
                    String finalVal = builder.toString();
                    WriteCommandAction.writeCommandAction(project).run(() -> {
                        try {
                            virtualFile.setBinaryContent(finalVal.getBytes(StandardCharsets.UTF_8));
                        } catch (Exception ignore) {
                        }
                    });
                }
            }
        } catch (Exception ignore) {
        }
        VirtualFileManager.getInstance().refreshWithoutFileWatcher(true);
    }

}
