package com.star.easydoc.common.util;

import java.util.Optional;

import com.intellij.dvcs.repo.Repository;
import com.intellij.dvcs.repo.VcsRepositoryManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectCoreUtil;

/**
 * 版本控制工具类
 *
 * @author wangchao
 * @date 2023/05/09
 */
public class VcsUtil {

    /** 私有构造 */
    private VcsUtil() {}

    /**
     * 获取当前分支
     * 当前分支可能为空
     *
     * @param project 项目
     * @return {@link String}
     */
    public static String getCurrentBranch(Project project) {
        VcsRepositoryManager manager = VcsRepositoryManager.getInstance(project);
        Optional<Repository> firstRepository = manager.getRepositories().stream().findFirst();
        if (!firstRepository.isPresent()) {
            return "";
        }
        Repository repository = firstRepository.get();
        return repository.getCurrentBranchName();
    }

}
