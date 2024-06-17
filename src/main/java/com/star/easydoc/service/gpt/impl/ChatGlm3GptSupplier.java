package com.star.easydoc.service.gpt.impl;

import com.alibaba.fastjson2.JSON;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.star.easydoc.common.util.HttpUtil;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang.StringUtils;

/**
 * 智谱清言api 文档 https://open.bigmodel.cn/dev/api#glm-3-turbo
 * @author hejiaqi
 * @date 2024/06/17
 */
public class ChatGlm3GptSupplier extends AbstractGptSupplier {

    private static final String URL = "https://open.bigmodel.cn/api/paas/v4/chat/completions";

    /** 超时 */
    private static final int TIMEOUT = 30 * 1000;

    @Override
    public String chat(String content) {

        String token = generateToken(getConfig().getChatGlmApiKey(), 60);

        Map<String, String> headers = Maps.newHashMap();
        headers.put("Authorization", token);
        headers.put("Content-Type", "application/json");

        Message message = new Message();
        message.setContent(content);
        message.setRole("user");
        ChatGlm3Request request = new ChatGlm3Request();
        request.setMessages(Lists.newArrayList(message));

        String response = HttpUtil.postJson(URL, headers, JSON.toJSONString(request), TIMEOUT);

        String result = JSON.parseObject(response).getJSONArray("choices").getJSONObject(0).getJSONObject("message")
            .getString("content");

        return "/**" + StringUtils.substringBeforeLast(StringUtils.substringAfterLast(result, "/**"), "*/") + "*/";
    }

    public static String generateToken(String apikey, int expSeconds) {
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

            return JWT.create()
                .withClaim("api_key", id)
                .withExpiresAt(exp)
                .withClaim("timestamp", currentTimeMillis)
                .withHeader(ImmutableMap.of("alg", "HS256", "sign_type", "SIGN"))
                .sign(algorithm);
        } catch (Exception e) {
            throw new RuntimeException("Error generating JWT token", e);
        }
    }

    private static class ChatGlm3Request {

        /** 所要调用的模型编码 */
        private String model = "glm-3-turbo";
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
