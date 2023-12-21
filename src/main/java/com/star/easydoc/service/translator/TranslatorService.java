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
    // 用于翻译文本的 Java 类
    // 支持多种翻译引擎，包括百度、阿里云、腾讯、有道、金山、谷歌等
    // 支持自定义单词映射，可以将自定义的单词翻译成指定的中文
    // 支持英译中和中译英两种翻译模式
    // 可以与 IntelliJ IDEA 集成，支持对 Java 类注释进行翻译
public class TranslatorService {

    // 配置
    private EasyDocConfig config;
    private Map<String, Translator> translatorMap;
    private static final Object LOCK = new Object();

    /**
     * 初始化
     *
     * @param config 配置
     */
    public void init(EasyDocConfig config) {// 使用双重检查锁定来保证初始化只会被执行一次。
        // 检查翻译器映射表和配置对象是否已经被初始化，如果是，则直接返回
        if (translatorMap != null && this.config != null) {
            return;
        }
        // 使用 synchronized 块进行双重检查锁定
        synchronized (LOCK) {
            // 再次检查翻译器映射表和配置对象是否已经被初始化，如果是，则直接返回
            if (translatorMap != null && this.config != null) {
                return;
            }
            // 如果翻译器映射表和配置对象都未被初始化，则创建一个 ImmutableMap 实例，并向其中添加各个翻译器对象
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
            // 将配置对象赋值给成员变量 this.config
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
            // 如果某个单词不存在自定义映射，则调用getFromOthers方法获取其他翻译服务的翻译结果，如果其他服务也无法翻译，则保留原单词。
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
            // 使用StringUtils.join方法将单词拼接成完整的句子，并调用getFromOthers方法获取其他翻译服务的翻译结果。
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

        // 根据传入的类名className，通过JavaPsiFacade从项目中查找对应的PsiClass。如果无法找到，也直接调用通用的translate方法翻译源文本
        if (StringUtils.isNotBlank(className)) {
            PsiClass aClass = JavaPsiFacade.getInstance(project).findClass(className,
                GlobalSearchScope.projectScope(project));
            if (aClass == null) {
                return translate(source);
            }
            // 如果找到了对应的PsiClass，则获取它的文档注释PsiDocComment。如果不存在文档注释，则也直接调用通用的translate方法翻译源文本。
            PsiDocComment docComment = aClass.getDocComment();
            if (docComment == null) {
                return translate(source);
            }
            // 如果存在文档注释，则获取其中的描述元素descriptionElements
            PsiElement[] descriptionElements = docComment.getDescriptionElements();
            for (PsiElement element : descriptionElements) {
                // 对于每个描述元素，如果它是PsiDocTokenImpl类型，则获取它的文本并去除多余的空格和换行符
                if (element instanceof PsiDocTokenImpl) {
                    String doc = element.getText().replaceAll("[ \\n\\t*]+", "");
                    // 如果文本不为空，则返回该文本作为翻译结果。
                    if (StringUtils.isNotBlank(doc)) {
                        return doc;
                    }
                }
            }
            // 如果所有的描述元素都没有非空的文本，则也直接调用通用的translate方法翻译源文本
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
        // 根据当前配置中选择的翻译服务获取对应的Translator对象，并保存在translatorMap中
        Translator translator = translatorMap.get(config.getTranslator());
        // 如果无法获取Translator对象，则返回空字符串
        if (Objects.isNull(translator)) {
            return StringUtils.EMPTY;
        }
        // 将源文本中的换行符替换为空格，并调用Translator对象的en2Ch方法进行英译中翻译，返回翻译结果
        return translator.en2Ch(source.replace("\n", " "));
    }

    /**
     * 中译英
     *
     * @param source 源中文
     * @return {@link String}
     */
    public String translateCh2En(String source) {
        // 判断源字符串是否为空或空字符串
        if (StringUtils.isBlank(source)) {
            return "";
        }
        // 根据当前配置中选择的翻译服务获取对应的Translator对象，并调用其ch2En方法进行中译英翻译，得到翻译结果ch
        String ch = translatorMap.get(config.getTranslator()).ch2En(source);
        // 用空格将翻译结果ch分割为单个单词
        String[] chs = StringUtils.split(ch);
        // 过滤掉停用词，去除其中的标点符号，得到单词列表chList
        List<String> chList = chs == null ? Lists.newArrayList() : Lists.newArrayList(chs);
        chList = chList.stream()
            .filter(c -> !Consts.STOP_WORDS.contains(c.toLowerCase()))
            .map(str -> str.replaceAll("[,.'\\-+;:`~]+", ""))
            .collect(Collectors.toList());

        // 如果chList为空，直接返回空字符串
        if (CollectionUtil.isEmpty(chList)) {
            return "";
        }
        // 如果只有一个单词，则直接返回该单词
        if (chList.size() == 1) {
            return chList.get(0);
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < chList.size(); i++) {
            // 对于chList中每个单词，如果是停用词或空字符串，则跳过
            if (StringUtils.isBlank(chList.get(i))) {
                continue;
            }
            if (Consts.STOP_WORDS.contains(chList.get(i).toLowerCase())) {
                continue;
            }
            // 如果是第一个单词，则将其转换成小写并添加到sb中，否则将其首字母大写后添加到sb中
            if (i == 0) {
                sb.append(chList.get(i).toLowerCase());
            } else {
                String lowCh = chList.get(i).toLowerCase();
                sb.append(StringUtils.substring(lowCh, 0, 1).toUpperCase()).append(StringUtils.substring(lowCh, 1));
            }
        }
        // 返回sb中的字符串
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

    private String getFromCustom(String word) {//从自定义词典中查询指定的单词，返回其对应的翻译结果
        Map<String, String> map = config.getWordMapWithProject();
        // 如果自定义词典中包含该单词，则返回其对应的翻译结果，类型为 String。
        //如果自定义词典中不包含该单词，则返回 null
        return ObjectUtils.firstNonNull(map.get(word), map.get(word.toLowerCase()));
    }

    private String getFromOthers(String word) {// 对指定单词进行翻译
        Translator translator = translatorMap.get(config.getTranslator());
        // 翻译结果为空，则返回空字符串
        if (Objects.isNull(translator)) {
            return StringUtils.EMPTY;
        }
        String res = translator.en2Ch(word);
        // 翻译结果不为空，则替换“的”为空字符串并返回
        if (res != null) {
            res = res.replace("的", "");
        }
        return res;
    }

    // 清空缓存
    public void clearCache() {
        translatorMap.values().forEach(Translator::clearCache);
    }
}
