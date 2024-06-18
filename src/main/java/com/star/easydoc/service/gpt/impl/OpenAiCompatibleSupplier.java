package com.star.easydoc.service.gpt.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.star.easydoc.common.util.HttpUtil;

import java.util.List;
import java.util.Map;

import com.star.easydoc.common.util.NotificationUtil;
import org.apache.commons.lang.StringUtils;

/**
 * opanai 兼容
 * https://openai.xiniushu.com/
 * https://platform.openai.com/docs/introduction
 * @author hejiaqi
 * @date 2024/06/17
 */
public class OpenAiCompatibleSupplier extends AbstractGptSupplier {

    /** 超时 */
    private static final int TIMEOUT = 30 * 1000;

    @Override
    public String chat(String content) {

        String auth = generateAuth(getConfig().getOpenaiApiKey());
        String url = getConfig().getOpenaiCustomUrl();
        String model = getConfig().getAiModel();


        Map<String, String> headers = Maps.newHashMap();
        headers.put("Authorization", auth);
        headers.put("Content-Type", "application/json");
        headers.put("Accept", "application/json");

        Message message = new Message();
        message.setContent(content);
        message.setRole("user");
        ChatGlm3Request request = new ChatGlm3Request();
        request.setModel(model);
        request.setMessages(Lists.newArrayList(message));

        String response = HttpUtil.postJson(url, headers, JSON.toJSONString(request), TIMEOUT);

        String result = "/** 生成失败,请稍后重试或更换模型/翻译方式 */";
        try {
            JSONObject respJson = JSON.parseObject(response);
            if (respJson.containsKey("choices")){
                result = respJson.getJSONArray("choices").getJSONObject(0).getJSONObject("message")
                        .getString("content");
            } else {
                // 尝试兼容本地 ollama
                result = respJson.getJSONArray("message").getJSONObject(0).getString("content");
            }
        }catch (Exception ignoreAndNotify){
            NotificationUtil.notify("ai返回值" , response);
        }
        return "/**" + StringUtils.substringBeforeLast(StringUtils.substringAfterLast(result, "/**"), "*/") + "*/";
    }

    public static String generateAuth(String apikey) {
        return "Bearer " + apikey;
    }

    private static class ChatGlm3Request {

        /** 所要调用的模型编码 */
        private String model;
        /** 当前对话信息列表 */
        private List<Message> messages;
        /** 是否开启流式模式 */
        private Boolean stream = false;
        /** 采样温度 */
        private Float temperature = 0.8f;

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
