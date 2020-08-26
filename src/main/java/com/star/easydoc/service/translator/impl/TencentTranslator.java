package com.star.easydoc.service.translator.impl;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.intellij.openapi.components.ServiceManager;
import com.star.easydoc.config.EasyJavadocConfigComponent;
import com.star.easydoc.model.EasyJavadocConfiguration;
import com.star.easydoc.service.translator.Translator;
import com.star.easydoc.util.JsonUtil;
import com.tencentcloudapi.common.Credential;
import com.tencentcloudapi.common.exception.TencentCloudSDKException;
import com.tencentcloudapi.common.profile.ClientProfile;
import com.tencentcloudapi.common.profile.HttpProfile;
import com.tencentcloudapi.tmt.v20180321.TmtClient;
import com.tencentcloudapi.tmt.v20180321.models.TextTranslateRequest;
import com.tencentcloudapi.tmt.v20180321.models.TextTranslateResponse;

/**
 * 腾讯翻译
 *
 * @author wangchao
 * @date 2020/08/26
 */
public class TencentTranslator implements Translator {
    private EasyJavadocConfiguration config = ServiceManager.getService(EasyJavadocConfigComponent.class).getState();

    @Override
    public String en2Ch(String text) {
        try{
            Credential cred = new Credential(config.getAppId(), config.getToken());

            HttpProfile httpProfile = new HttpProfile();
            httpProfile.setEndpoint("tmt.tencentcloudapi.com");

            ClientProfile clientProfile = new ClientProfile();
            clientProfile.setHttpProfile(httpProfile);

            TmtClient client = new TmtClient(cred, "ap-beijing", clientProfile);

            String params = JsonUtil.toJson(new TencentRequest(text, "zh"));
            TextTranslateRequest req = TextTranslateRequest.fromJsonString(params, TextTranslateRequest.class);

            TextTranslateResponse resp = client.TextTranslate(req);
            return resp.getTargetText();
        } catch (TencentCloudSDKException e) {
            System.out.println(e.toString());
        }
        return "";
    }

    @Override
    public String ch2En(String text) {
        try{
            Credential cred = new Credential(config.getAppId(), config.getToken());

            HttpProfile httpProfile = new HttpProfile();
            httpProfile.setEndpoint("tmt.tencentcloudapi.com");

            ClientProfile clientProfile = new ClientProfile();
            clientProfile.setHttpProfile(httpProfile);

            TmtClient client = new TmtClient(cred, "ap-beijing", clientProfile);

            String params = JsonUtil.toJson(new TencentRequest(text, "en"));
            TextTranslateRequest req = TextTranslateRequest.fromJsonString(params, TextTranslateRequest.class);

            TextTranslateResponse resp = client.TextTranslate(req);
            return resp.getTargetText();
        } catch (TencentCloudSDKException e) {
            System.out.println(e.toString());
        }
        return "";
    }

    private static class TencentRequest {

        @JsonProperty("SourceText")
        private String sourceText;
        @JsonProperty("Source")
        private String source = "auto";
        @JsonProperty("Target")
        private String target;
        @JsonProperty("ProjectId")
        private Integer projectId = 0;

        public TencentRequest(String sourceText, String target) {
            this.target = target;
            this.sourceText = sourceText;
        }

        public TencentRequest() {}

        public String getSourceText() {
            return sourceText;
        }

        public void setSourceText(String sourceText) {
            this.sourceText = sourceText;
        }

        public String getSource() {
            return source;
        }

        public void setSource(String source) {
            this.source = source;
        }

        public String getTarget() {
            return target;
        }

        public void setTarget(String target) {
            this.target = target;
        }

        public Integer getProjectId() {
            return projectId;
        }

        public void setProjectId(Integer projectId) {
            this.projectId = projectId;
        }
    }
}
