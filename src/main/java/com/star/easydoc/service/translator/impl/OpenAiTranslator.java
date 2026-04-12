package com.star.easydoc.service.translator.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.intellij.openapi.diagnostic.Logger;
import com.star.easydoc.common.util.HttpUtil;
import com.star.easydoc.service.translator.impl.AbstractTranslator;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;

/**
 * OpenAI 翻译器（复用 OpenAI 配置，走翻译流程）
 * 使用 Chat Completions，将输入作为 user 消息，并使用 system 提示强约束只返回纯译文。
 */
public class OpenAiTranslator extends AbstractTranslator {

    private static final Logger LOGGER = Logger.getInstance(OpenAiTranslator.class);
    private static final String DEFAULT_BASE = "https://api.openai.com/v1";
    private static final int TIMEOUT = 30 * 1000;

    @Override
    protected String translateCh2En(String text) {
        return translate(text, true);
    }

    @Override
    protected String translateEn2Ch(String text) {
        return translate(text, false);
    }

    private String translate(String text, boolean zh2en) {
        String apiKey = getConfig().getOpenAiApiKey();
        String model = getConfig().getOpenAiModel();
        if (StringUtils.isBlank(apiKey) || StringUtils.isBlank(model)) {
            return StringUtils.EMPTY;
        }
        Map<String, String> headers = Maps.newHashMap();
        headers.put("Authorization", "Bearer " + apiKey);
        headers.put("Content-Type", "application/json");

        // system + user
        Message system = new Message();
        system.setRole("system");
        system.setContent(zh2en ?
            "You are a translation engine. Translate Chinese to concise English. Return translation only without explanations." :
            "You are a translation engine. Translate English to concise Chinese. Return translation only without explanations.");
        Message user = new Message();
        user.setRole("user");
        user.setContent(text);

        OpenAiRequest request = new OpenAiRequest();
        request.setModel(model);
        request.setMessages(Lists.newArrayList(system, user));
        request.setStream(false);
        request.setTemperature(0.0);

        String base = StringUtils.isNotBlank(getConfig().getOpenAiBaseUrl()) ? getConfig().getOpenAiBaseUrl() : DEFAULT_BASE;
        String url = base.endsWith("/") ? (base + "chat/completions") : (base + "/chat/completions");

        String json = null;
        try {
            json = HttpUtil.postJson(url, headers, JSON.toJSONString(request), TIMEOUT);
            JSONObject obj = JSON.parseObject(json);
            if (obj == null) {
                return StringUtils.EMPTY;
            }
            String result = obj.getJSONArray("choices").getJSONObject(0).getJSONObject("message").getString("content");
            return StringUtils.defaultString(result).trim();
        } catch (Exception e) {
            LOGGER.error("openai translate error:url:" + url + ",response:" + json, e);
            return StringUtils.EMPTY;
        }
    }

    // --- request payloads ---
    private static class OpenAiRequest {
        private String model;
        private List<Message> messages;
        private Boolean stream;
        private Double temperature;

        public String getModel() { return model; }
        public void setModel(String model) { this.model = model; }
        public List<Message> getMessages() { return messages; }
        public void setMessages(List<Message> messages) { this.messages = messages; }
        public Boolean getStream() { return stream; }
        public void setStream(Boolean stream) { this.stream = stream; }
        public Double getTemperature() { return temperature; }
        public void setTemperature(Double temperature) { this.temperature = temperature; }
    }

    private static class Message {
        private String role;
        private String content;
        public String getRole() { return role; }
        public void setRole(String role) { this.role = role; }
        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }
    }
}

