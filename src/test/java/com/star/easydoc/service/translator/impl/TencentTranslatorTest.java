package com.star.easydoc.service.translator.impl;

import com.tencentcloudapi.common.Credential;
import com.tencentcloudapi.common.exception.TencentCloudSDKException;
import com.tencentcloudapi.common.profile.ClientProfile;
import com.tencentcloudapi.common.profile.HttpProfile;
import com.tencentcloudapi.tmt.v20180321.TmtClient;
import com.tencentcloudapi.tmt.v20180321.models.TextTranslateRequest;
import com.tencentcloudapi.tmt.v20180321.models.TextTranslateResponse;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author wangchao
 * @date 2020/08/27
 */
public class TencentTranslatorTest {

    @Test
    public void en2Ch() {
        TencentTranslator tencentTranslator = new TencentTranslator();
        tencentTranslator.en2Ch("");
    }
}