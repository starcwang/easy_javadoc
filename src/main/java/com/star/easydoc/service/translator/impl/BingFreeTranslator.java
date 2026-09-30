package com.star.easydoc.service.translator.impl;

import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;

import com.google.common.collect.Maps;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.psi.PsiElement;
import com.star.easydoc.common.util.HttpUtil;
import org.apache.commons.lang3.StringUtils;

/**
 * 必应免费翻译
 *
 * @author wangchao
 * @date 2026/09/30
 */
public class BingFreeTranslator extends AbstractTranslator {

    /** 日志 */
    private static final Logger LOGGER = Logger.getInstance(BingFreeTranslator.class);

    /** 重试次数 */
    private static final int RETRY_TIMES = 3;

    /** 锁 */
    private static final Object LOCK = new Object();

    /** 翻译页url，会自动重定向到就近域名（如cn.bing.com） */
    private static final String TRANSLATOR_PAGE_URL = "https://www.bing.com/translator";

    /** 翻译接口路径 */
    private static final String TRANSLATE_API_PATH = "/ttranslatev3";

    /** 浏览器UA */
    private static final String USER_AGENT = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 "
        + "(KHTML, like Gecko) Chrome/131.0.0.0 Safari/537.36";

    /** 页面最终域名 */
    private static final Pattern CANONICAL_PATTERN = Pattern.compile("<link rel=\"canonical\" href=\"(https?://[^/]+)");

    /** 防滥用参数 params_AbusePreventionHelper = [key,"token",ttl毫秒] */
    private static final Pattern ABUSE_PREVENTION_PATTERN = Pattern.compile(
        "params_AbusePreventionHelper\\s*=\\s*\\[\\s*(\\d+)\\s*,\\s*\"([^\"]+)\"\\s*,\\s*(\\d+)\\s*]");

    /** 页面会话IG */
    private static final Pattern IG_PATTERN = Pattern.compile("IG\\s*:\\s*\"([A-Fa-f0-9]+)\"");

    /** 页面会话IID */
    private static final Pattern IID_PATTERN = Pattern.compile("data-iid\\s*=\\s*\"([^\"]+)\"");

    /** 凭证过期安全余量（毫秒） */
    private static final long EXPIRY_SAFETY_MARGIN_MS = 60_000L;

    /** 接口域名 */
    private String baseUrl = "https://www.bing.com";

    /** 防滥用key */
    private String key = null;

    /** 防滥用token */
    private String token = null;

    /** 页面会话IG */
    private String ig = null;

    /** 页面会话IID */
    private String iid = null;

    /** 凭证过期时间戳（毫秒） */
    private long expireAt = 0L;

    /**
     * 刷新凭证：抓取翻译页并解析防滥用参数
     */
    private void refreshAuth() {
        for (int i = 1; i <= RETRY_TIMES; i++) {
            try {
                // 翻译页较大，读超时至少5秒
                String html = HttpUtil.get(TRANSLATOR_PAGE_URL, Math.max(getConfig().getTimeout(), 5000));
                if (parseAuth(html)) {
                    return;
                }
            } catch (Exception e) {
                LOGGER.warn("get bing translator page failed,retrying " + i);
            }
        }
        throw new RuntimeException("it still fails after " + RETRY_TIMES + " retries");
    }

    /**
     * 从翻译页解析凭证
     *
     * @param html 页面
     * @return 是否解析成功
     */
    private boolean parseAuth(String html) {
        if (StringUtils.isBlank(html)) {
            return false;
        }
        Matcher abuse = ABUSE_PREVENTION_PATTERN.matcher(html);
        Matcher igMatcher = IG_PATTERN.matcher(html);
        Matcher iidMatcher = IID_PATTERN.matcher(html);
        if (!abuse.find() || !igMatcher.find() || !iidMatcher.find()) {
            return false;
        }
        Matcher canonical = CANONICAL_PATTERN.matcher(html);
        if (canonical.find()) {
            baseUrl = canonical.group(1);
        }
        key = abuse.group(1);
        token = abuse.group(2);
        expireAt = System.currentTimeMillis() + Long.parseLong(abuse.group(3)) - EXPIRY_SAFETY_MARGIN_MS;
        ig = igMatcher.group(1);
        iid = iidMatcher.group(1);
        return true;
    }

    /**
     * 获取凭证，过期自动刷新
     */
    private void getAuth() {
        if (token != null && System.currentTimeMillis() < expireAt) {
            return;
        }
        synchronized (LOCK) {
            if (token != null && System.currentTimeMillis() < expireAt) {
                return;
            }
            refreshAuth();
        }
    }

    @Override
    protected String translateCh2En(String text, PsiElement psiElement) {
        return translate("zh-Hans", "en", text);
    }

    @Override
    protected String translateEn2Ch(String text, PsiElement psiElement) {
        return translate("en", "zh-Hans", text);
    }

    private String translate(String fromLang, String toLang, String text) {
        String json = null;
        try {
            getAuth();
            String url = baseUrl + TRANSLATE_API_PATH + "?isVertical=1&IG=" + ig + "&IID=" + iid;
            Map<String, String> headers = Maps.newHashMap();
            headers.put("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");
            headers.put("User-Agent", USER_AGENT);
            headers.put("Referer", baseUrl + "/translator");
            String body = "text=" + HttpUtil.encode(text) + "&fromLang=" + fromLang + "&to=" + toLang
                + "&token=" + HttpUtil.encode(token) + "&key=" + key;
            json = HttpUtil.post(url, headers, body, getConfig().getTimeout());
            JSONArray response = JSON.parseArray(json);
            return Objects.requireNonNull(response).getJSONObject(0).getJSONArray("translations").getJSONObject(0)
                .getString("text");
        } catch (Exception e) {
            // 凭证可能提前失效，重置后下次调用自动刷新
            token = null;
            LOGGER.error("bing free translate error: please check your network,response=" + json, e);
            return StringUtils.EMPTY;
        }
    }
}
