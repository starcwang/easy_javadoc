package com.star.easydoc.service.translator.impl;

import com.intellij.openapi.components.ServiceManager;
import com.intellij.psi.PsiElement;
import com.star.easydoc.service.gpt.GptService;

/**
 * 通用大模型(OpenAI格式)翻译
 *
 * @author nanusl
 * @date 2026/08/05
 */
public class OpenAiTranslator extends AbstractTranslator {

    private GptService gptService = ServiceManager.getService(GptService.class);

    private static final String DEFAULT_PROMPT = "Translate from {form} to {to}: {query}";

    @Override
    protected String translateCh2En(String text, PsiElement psiElement) {
        return translate("zh", "en", text, psiElement);
    }

    @Override
    protected String translateEn2Ch(String text, PsiElement psiElement) {
        return translate("en", "zh", text, psiElement);
    }

    private String translate(String from, String to, String query, PsiElement psiElement) {
        return gptService.chat(DEFAULT_PROMPT
                .replace("{form}", from)
                .replace("{to}", to)
                .replace("{query}", query));

    }
}
