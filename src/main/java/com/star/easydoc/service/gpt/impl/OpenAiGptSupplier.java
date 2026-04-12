package com.star.easydoc.service.gpt.impl;

import com.alibaba.fastjson2.JSON;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.star.easydoc.common.util.HttpUtil;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;

/**
 * OpenAI Chat Completions API
 * https://api.openai.com/v1/chat/completions
 */
public class OpenAiGptSupplier extends AbstractGptSupplier {

    private static final String DEFAULT_BASE = "https://api.openai.com/v1";
    /** 超时 */
    private static final int TIMEOUT = 30 * 1000;

    @Override
    public String chat(String content) {
        String apiKey = getConfig().getOpenAiApiKey();
        String model = getConfig().getOpenAiModel();
        if (StringUtils.isBlank(apiKey) || StringUtils.isBlank(model)) {
            return StringUtils.EMPTY;
        }

        Map<String, String> headers = Maps.newHashMap();
        headers.put("Authorization", "Bearer " + apiKey);
        headers.put("Content-Type", "application/json");

        Message message = new Message();
        message.setRole("user");
        message.setContent(content);

        OpenAiRequest request = new OpenAiRequest();
        request.setModel(model);
        request.setMessages(Lists.newArrayList(message));
        request.setStream(false);

        String base = StringUtils.isNotBlank(getConfig().getOpenAiBaseUrl()) ? getConfig().getOpenAiBaseUrl() : DEFAULT_BASE;
        String url = base.endsWith("/") ? (base + "chat/completions") : (base + "/chat/completions");
        String response = HttpUtil.postJson(url, headers, JSON.toJSONString(request), TIMEOUT);
        String result = JSON.parseObject(response)
                .getJSONArray("choices")
                .getJSONObject(0)
                .getJSONObject("message")
                .getString("content");

        // 复用 ChatGLM 的返回处理逻辑，提取 /** ... */
        return "/**" + org.apache.commons.lang.StringUtils.substringBeforeLast(
                org.apache.commons.lang.StringUtils.substringAfterLast(result, "/**"), "*/") + "*/";
    }

    // --- request/response payloads ---
    private static class OpenAiRequest {
        private String model;
        private List<Message> messages;
        private Boolean stream;

        public String getModel() {
            return model;
        }

        public void setModel(String model) {
            this.model = model;
        }

        public List<Message> getMessages() {
            return messages;
        }

        public void setMessages(List<Message> messages) {
            this.messages = messages;
        }

        public Boolean getStream() {
            return stream;
        }

        public void setStream(Boolean stream) {
            this.stream = stream;
        }
    }

    private static class Message {
        private String role;
        private String content;

        public String getRole() {
            return role;
        }

        public void setRole(String role) {
            this.role = role;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }
    }
}

