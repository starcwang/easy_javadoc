package com.star.easydoc.service.translator;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.intellij.openapi.project.Project;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.impl.source.javadoc.PsiDocTokenImpl;
import com.intellij.psi.javadoc.PsiDocComment;
import com.intellij.psi.search.GlobalSearchScope;
import com.star.easydoc.common.Consts;
import com.star.easydoc.common.util.CollectionUtil;
import com.star.easydoc.common.util.StringUtil;
import com.star.easydoc.config.EasyDocConfig;
import com.star.easydoc.service.translator.impl.AliyunTranslator;
import com.star.easydoc.service.translator.impl.BaiduTranslator;
import com.star.easydoc.service.translator.impl.GoogleTranslator;
import com.star.easydoc.service.translator.impl.JinshanTranslator;
import com.star.easydoc.service.translator.impl.MicrosoftFreeTranslator;
import com.star.easydoc.service.translator.impl.MicrosoftTranslator;
import com.star.easydoc.service.translator.impl.SimpleSplitterTranslator;
import com.star.easydoc.service.translator.impl.TencentTranslator;
import com.star.easydoc.service.translator.impl.YoudaoAiTranslator;
import com.star.easydoc.service.translator.impl.YoudaoTranslator;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * @author wangchao
 * @date 2019/08/25
 */
public class TranslatorService {

    private EasyDocConfig config;
    private Map<String, Translator> translatorMap;
    private static final Object LOCK = new Object();

    /**
     * 初始化
     *
     * @param config 配置
     */
    public void init(EasyDocConfig config) {
        if (translatorMap != null && this.config != null) {
            return;
        }
        synchronized (LOCK) {
            if (translatorMap != null && this.config != null) {
                return;
            }
            translatorMap = ImmutableMap.<String, Translator>builder()
                .put(Consts.BAIDU_TRANSLATOR, new BaiduTranslator().init(config))
                .put(Consts.ALIYUN_TRANSLATOR, new AliyunTranslator().init(config))
                .put(Consts.TENCENT_TRANSLATOR, new TencentTranslator().init(config))
                .put(Consts.JINSHAN_TRANSLATOR, new JinshanTranslator().init(config))
                .put(Consts.YOUDAO_AI_TRANSLATOR, new YoudaoAiTranslator().init(config))
                .put(Consts.YOUDAO_TRANSLATOR, new YoudaoTranslator().init(config))
                .put(Consts.MICROSOFT_TRANSLATOR, new MicrosoftTranslator().init(config))
                .put(Consts.MICROSOFT_FREE_TRANSLATOR, new MicrosoftFreeTranslator().init(config))
                .put(Consts.GOOGLE_TRANSLATOR, new GoogleTranslator().init(config))
                .put(Consts.SIMPLE_SPLITTER, new SimpleSplitterTranslator().init(config))
                .build();
            this.config = config;
        }
    }

    /**
     * 英译中
     *
     * @param source 源
     * @return {@link String}
     */
    public String translate(String source) {
        // 如果自定义了完整的映射，直接使用完整的映射返回
        String custom = getFromCustom(source);
        if (StringUtils.isNotBlank(custom)) {
            return custom;
        }

        List<String> words = StringUtil.split(source);
        if (hasCustomWord(words)) {
            // 有自定义单词，使用默认模式，单个单词翻译
            StringBuilder sb = new StringBuilder();
            for (String word : words) {
                String res = getFromCustom(word);
                if (StringUtils.isBlank(res)) {
                    res = getFromOthers(word);
                }
                if (StringUtils.isBlank(res)) {
                    res = word;
                }
                sb.append(res);
            }
            return sb.toString();
        } else {
            // 没有自定义单词，使用整句翻译，翻译更准确
            return getFromOthers(StringUtils.join(words, StringUtils.SPACE));
        }
    }

    /**
     * 英译中
     *
     * @param source 源
     * @return {@link String}
     */
    public String translateWithClass(String source, String className, Project project) {
        // 开关判断
        if (EasyDocConfig.ONLY_TRANSLATE.equals(config.getDocPriority())) {
            return translate(source);
        }

        if (StringUtils.isNotBlank(className)) {
            PsiClass aClass = JavaPsiFacade.getInstance(project).findClass(className,
                GlobalSearchScope.projectScope(project));
            if (aClass == null) {
                return translate(source);
            }
            PsiDocComment docComment = aClass.getDocComment();
            if (docComment == null) {
                return translate(source);
            }
            PsiElement[] descriptionElements = docComment.getDescriptionElements();
            for (PsiElement element : descriptionElements) {
                if (element instanceof PsiDocTokenImpl) {
                    String doc = element.getText().replaceAll("[ \\n\\t*]+", "");
                    if (StringUtils.isNotBlank(doc)) {
                        return doc;
                    }
                }
            }
            return translate(source);
        } else {
            return translate(source);
        }
    }

    /**
     * 自动翻译
     *
     * @param source 源
     * @return {@link String}
     */
    public String autoTranslate(String source) {
        Translator translator = translatorMap.get(config.getTranslator());
        if (Objects.isNull(translator)) {
            return StringUtils.EMPTY;
        }
        return translator.en2Ch(source.replace("\n", " "));
    }

    /**
     * 中译英
     *
     * @param source 源中文
     * @return {@link String}
     */
    public String translateCh2En(String source) {
        if (StringUtils.isBlank(source)) {
            return "";
        }
        String ch = translatorMap.get(config.getTranslator()).ch2En(source);
        String[] chs = StringUtils.split(ch);
        List<String> chList = chs == null ? Lists.newArrayList() : Lists.newArrayList(chs);
        chList = chList.stream()
            .filter(c -> !Consts.STOP_WORDS.contains(c.toLowerCase()))
            .map(str -> str.replaceAll("[,.'\\-+;:`~]+", ""))
            .collect(Collectors.toList());

        if (CollectionUtil.isEmpty(chList)) {
            return "";
        }
        if (chList.size() == 1) {
            return chList.get(0);
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < chList.size(); i++) {
            if (StringUtils.isBlank(chList.get(i))) {
                continue;
            }
            if (Consts.STOP_WORDS.contains(chList.get(i).toLowerCase())) {
                continue;
            }
            if (i == 0) {
                sb.append(chList.get(i).toLowerCase());
            } else {
                String lowCh = chList.get(i).toLowerCase();
                sb.append(StringUtils.substring(lowCh, 0, 1).toUpperCase()).append(StringUtils.substring(lowCh, 1));
            }
        }
        return sb.toString();
    }

    /**
     * 是否有自定义单词
     *
     * @param words 单词
     * @return boolean
     */
    private boolean hasCustomWord(List<String> words) {
        return CollectionUtil.containsAny(config.getWordMapWithProject().keySet(), words);
    }

    private String getFromCustom(String word) {
        Map<String, String> map = config.getWordMapWithProject();
        return ObjectUtils.firstNonNull(map.get(word), map.get(word.toLowerCase()));
    }

    private String getFromOthers(String word) {
        Translator translator = translatorMap.get(config.getTranslator());
        if (Objects.isNull(translator)) {
            return StringUtils.EMPTY;
        }
        String res = translator.en2Ch(word);
        if (res != null) {
            res = res.replace("的", "");
        }
        return res;
    }

    public void clearCache() {
        translatorMap.values().forEach(Translator::clearCache);
    }
}
