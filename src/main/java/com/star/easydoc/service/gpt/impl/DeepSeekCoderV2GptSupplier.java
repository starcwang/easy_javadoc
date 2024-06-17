package com.star.easydoc.service.gpt.impl;

import com.alibaba.fastjson2.JSON;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.star.easydoc.common.util.HttpUtil;
import org.apache.commons.lang.StringUtils;

import java.util.List;
import java.util.Map;

/**
 * https://platform.deepseek.com/api-docs/zh-cn/
 * @author hejiaqi
 * @date 2024/06/17
 */
public class DeepSeekCoderV2GptSupplier extends AbstractGptSupplier {

    private static final String URL = "https://api.deepseek.com/chat/completions";

    /** 超时 */
    private static final int TIMEOUT = 30 * 1000;

    @Override
    public String chat(String content) {

        String auth = generateAuth(getConfig().getChatGlmApiKey());

        Map<String, String> headers = Maps.newHashMap();
        headers.put("Authorization", auth);
        headers.put("Content-Type", "application/json");

        Message message = new Message();
        message.setContent(content);
        message.setRole("user");
        DeepSeekCoderV2Request request = new DeepSeekCoderV2Request();
        request.setMessages(Lists.newArrayList(message));

        String response = HttpUtil.postJson(URL, headers, JSON.toJSONString(request), TIMEOUT);

        String result = JSON.parseObject(response).getJSONArray("choices").getJSONObject(0).getJSONObject("message")
            .getString("content");

        return "/**" + StringUtils.substringBeforeLast(StringUtils.substringAfterLast(result, "/**"), "*/") + "*/";
    }

    public static String generateAuth(String apikey) {
        return "Bearer " + apikey;
    }

    private static class DeepSeekCoderV2Request {

        /** 所要调用的模型编码 */
        private String model = "deepseek-coder";
        /** 当前对话信息列表 */
        private List<Message> messages;
        /** 是否开启流式模式 */
        private Boolean stream = false;

        private Float temperature = 0.0f;

        public Float getTemperature() {
            return temperature;
        }

        public void setTemperature(Float temperature) {
            this.temperature = temperature;
        }

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
        /** 消息的角色信息，system，assistant，user */
        private String role;
        /** 消息内容 */
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
