package com.star.easydoc.service.gpt.impl;

import java.util.List;
import java.util.Map;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.star.easydoc.common.util.HttpUtil;
import org.apache.commons.lang.StringUtils;

/**
 * 通用大模型（OpenAI格式）
 * 支持所有兼容OpenAI API格式的大模型接口，如OpenAI、DeepSeek、通义千问、Moonshot等
 *
 * @author wangchao
 * @date 2024/04/12
 */
public class OpenAiGptSupplier extends AbstractGptSupplier {

    /** 超时 */
    private static final int TIMEOUT = 60 * 1000;

    @Override
    public String chat(String content) {
        String apiUrl = getConfig().getOpenAiApiUrl();
        if (StringUtils.isBlank(apiUrl)) {
            throw new RuntimeException("通用大模型API地址不能为空");
        }
        String apiKey = getConfig().getOpenAiApiKey();
        if (StringUtils.isBlank(apiKey)) {
            throw new RuntimeException("通用大模型API密钥不能为空");
        }
        String model = getConfig().getOpenAiModel();
        if (StringUtils.isBlank(model)) {
            model = "gpt-4o";
        }

        // 拼接完整URL
        String url = apiUrl.endsWith("/") ? apiUrl + "chat/completions" : apiUrl + "/chat/completions";

        Map<String, String> headers = Maps.newHashMap();
        headers.put("Authorization", "Bearer " + apiKey);
        headers.put("Content-Type", "application/json");

        Message message = new Message();
        message.setContent(content);
        message.setRole("user");

        OpenAiRequest request = new OpenAiRequest();
        request.setModel(model);
        request.setMessages(Lists.newArrayList(message));

        String response = HttpUtil.postJson(url, headers, JSON.toJSONString(request), TIMEOUT);

        JSONObject responseObject = JSON.parseObject(response);
        if (responseObject == null) {
            throw new RuntimeException("通用大模型响应为空");
        }

        // 检查错误信息
        JSONObject error = responseObject.getJSONObject("error");
        if (error != null) {
            String errorMsg = error.getString("message");
            throw new RuntimeException("通用大模型API错误: " + errorMsg);
        }

        JSONArray choices = responseObject.getJSONArray("choices");
        if (choices == null || choices.isEmpty()) {
            throw new RuntimeException("通用大模型响应缺少'choices'字段，原始响应: " + response);
        }

        String result = choices.getJSONObject(0).getJSONObject("message").getString("content");

        // 提取javadoc注释部分
        if (result.contains("/**") && result.contains("*/")) {
            return "/**" + StringUtils.substringBeforeLast(StringUtils.substringAfterLast(result, "/**"), "*/") + "*/";
        }
        return result;
    }

    private static class OpenAiRequest {
        private String model;
        private List<Message> messages;
        private Boolean stream = false;

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
