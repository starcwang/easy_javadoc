package com.star.easydoc.service.gpt.impl;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson2.JSON;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.star.easydoc.common.util.HttpUtil;

/**
 * 智谱清言api 文档 https://open.bigmodel.cn/dev/api#glm-4
 * @author wangchao
 * @date 2024/03/05
 */
public class ChatGlmGptSupplier extends AbstractGptSupplier {

    private static final String URL = "https://open.bigmodel.cn/api/paas/v4/chat/completions";

    private String APP_KEY = "628ae29270b54d55e3a6e650ccff48a5.Hol6aMPA7ppF1lJx";

    @Override
    public String chat(String text) {

        String token = generateToken(APP_KEY, 3600);

        Map<String, String> headers = Maps.newHashMap();
        headers.put("Authorization:", token);
        headers.put("Content-Type", "application/json");

        Message message = new Message();
        message.setContent(text);
        message.setRole("user");
        ChatGlmRequest request = new ChatGlmRequest();
        request.setMessages(Lists.newArrayList(message));
        // todo debug
        String result = HttpUtil.postJson(URL, headers, JSON.toJSONString(request));

        System.out.println("Result: " + result);
        return result;
    }

    public String translateEn2Ch(String text) {

        String token = generateToken(APP_KEY, 3600);

        Map<String, String> headers = ImmutableMap.of("Authorization:", token,
            "Content-Type", "application/json");
        System.out.println("Generated JWT: " + token);

        String result = HttpUtil.postJson(URL, headers,
            "{\"model\":\"glm-4\",\"messages\":[{\"role\": \"user\", \"content\": \"你好,你会下中国象棋吗\"}]}");

        System.out.println("Result: " + result);
        return "";
    }

    public String translateCh2En(String text) {
        return "";
    }

    public String generateToken(String apikey, int expSeconds) {
        try {
            String[] parts = apikey.split("\\.");
            if (parts.length != 2) {
                throw new IllegalArgumentException("invalid apikey");
            }
            String id = parts[0];
            String secret = parts[1];

            long currentTimeMillis = System.currentTimeMillis();
            Date exp = new Date(currentTimeMillis + expSeconds * 1000L);

            Algorithm algorithm = Algorithm.HMAC256(secret);

            String token = JWT.create()
                .withClaim("api_key", id)
                .withExpiresAt(exp)
                .withClaim("timestamp", currentTimeMillis)
                .withHeader(ImmutableMap.of("alg", "HS256", "sign_type", "SIGN"))
                .sign(algorithm);

            return token;
        } catch (Exception e) {
            throw new RuntimeException("Error generating JWT token", e);
        }
    }

    private static class ChatGlmRequest {

        /** 所要调用的模型编码 */
        private String model = "glm-4";
        /** 当前对话信息列表 */
        private List<Message> messages;
        /** 是否开启流式模式 */
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

    public static class Message {
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
