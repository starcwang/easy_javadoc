package com.star.easydoc.service.translator.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiMethod;
import com.star.easydoc.common.util.HttpUtil;
import org.apache.commons.lang3.StringUtils;

/**
 * 本地翻译
 *
 * @author Administrator
 * @date 2024/08/03
 */
public class CustomTranslator extends AbstractTranslator {

    private static final Logger LOGGER = Logger.getInstance(CustomTranslator.class);

    @Override
    protected String translateCh2En(String text, PsiElement psiElement) {
        return translate("zh", "en", text, psiElement);
    }

    @Override
    protected String translateEn2Ch(String text, PsiElement psiElement) {
        return translate("en", "zh", text, psiElement);
    }

    private String translate(String from, String to, String query, PsiElement psiElement) {
        String type = "default";
        if (psiElement instanceof PsiClass) {
            type = "class";
        } else if (psiElement instanceof PsiMethod) {
            type = "method";
        } else if (psiElement instanceof PsiField) {
            type = "field";
        }
        String json = null;
        String url = getConfig().getCustomUrl().replace("{from}", from).replace("{to}", to)
            .replace("{query}", HttpUtil.encode(query)).replace("{type}", type);
        try {
            json = HttpUtil.get(url, getConfig().getTimeout());
            JSONObject response = JSON.parseObject(json);
            if (response == null || response.getInteger("code") != 0) {
                LOGGER.error(String.format("custom translate error:url:%s,response:%s", url, json));
                return StringUtils.EMPTY;
            }
            return response.getString("data");
        } catch (Exception e) {
            LOGGER.error(String.format("custom translate error:url:%s,response:%s", url, json), e);
            return StringUtils.EMPTY;
        }
    }

}
