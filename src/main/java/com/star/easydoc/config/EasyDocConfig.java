package com.star.easydoc.config;

import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import com.alibaba.fastjson2.annotation.JSONField;

import com.google.common.collect.Maps;
import com.intellij.ide.DataManager;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.star.easydoc.common.Consts;

/**
 * 持久化配置文件
 *
 * @author wangchao
 * @date 2019/08/25
 */
public class EasyDocConfig {

    /** 方法返回值为code类型 */
    public static final String CODE_RETURN_TYPE = "code";
    /** 方法返回值为link类型 */
    public static final String LINK_RETURN_TYPE = "link";
    /** 方法返回值为doc类型 */
    public static final String DOC_RETURN_TYPE = "doc";

    /** 普通模式 */
    public static final String NORMAL_PARAM_TYPE = "普通模式";
    /** 中括号模式 */
    public static final String LINK_PARAM_TYPE = "中括号模式";

    /** 类注释优先 */
    public static final String DOC_FIRST = "docFirst";
    /** 仅翻译 */
    public static final String ONLY_TRANSLATE = "onlyTranslate";

    /** 版本 */
    private String version;

    /**
     * 作者
     */
    private String author = "admin";
    private String kdocAuthor = "admin";
    /**
     * 日期格式
     */
    private String dateFormat = "yyyy/MM/dd";
    private String kdocDateFormat = "yyyy/MM/dd";
    /**
     * 属性是否使用简单模式
     */
    private Boolean simpleFieldDoc = false;
    private Boolean kdocSimpleFieldDoc = false;
    /**
     * 属性是否使用简单模式
     */
    private String methodReturnType = LINK_RETURN_TYPE;
    /** kdoc 参数类型 */
    private String kdocParamType = LINK_PARAM_TYPE;

    /** 注释优先级 */
    private String docPriority = DOC_FIRST;

    /**
     * 翻译方式
     */
    private String translator = Consts.YOUDAO_TRANSLATOR;
    /**
     * 百度app id
     */
    private String appId;
    /**
     * 百度密钥
     */
    private String token;
    /**
     * 腾讯secretKey
     */
    private String secretKey;
    /**
     * 腾讯secretId
     */
    private String secretId;
    /**
     * 阿里云accessKeyId
     */
    private String accessKeyId;
    /**
     * 阿里云accessKeySecret
     */
    private String accessKeySecret;
    /**
     * 有道APP_KEY
     */
    private String youdaoAppKey;
    /**
     * 有道APP_SECRET
     */
    private String youdaoAppSecret;
    /**
     * 谷歌key
     */
    private String googleKey;
    /**
     * 微软key
     */
    private String microsoftKey;
    /**
     * 单词映射
     */
    private SortedMap<String, String> wordMap = new TreeMap<>();

    /** 项目级别单词映射 */
    private SortedMap<String, TreeMap<String, String>> projectWordMap = new TreeMap<>();

    /**
     * 类模板配置
     */
    private TemplateConfig classTemplateConfig = new TemplateConfig();
    private TemplateConfig kdocClassTemplateConfig = new TemplateConfig();
    /**
     * 方法模板配置
     */
    private TemplateConfig methodTemplateConfig = new TemplateConfig();
    private TemplateConfig kdocMethodTemplateConfig = new TemplateConfig();
    /**
     * 属性模板配置
     */
    private TemplateConfig fieldTemplateConfig = new TemplateConfig();
    private TemplateConfig kdocFieldTemplateConfig = new TemplateConfig();

    /** 批量生成是否生成类注释 */
    private Boolean genAllClass;
    /** 批量生成是否生成方法注释 */
    private Boolean genAllMethod;
    /** 批量生成是否生成属性注释 */
    private Boolean genAllField;
    /** 批量生成是否递归内部类 */
    private Boolean genAllInnerClass;

    public void reset() {
        author = "admin";
        kdocAuthor = "admin";
        dateFormat = "yyyy/MM/dd";
        kdocDateFormat = "yyyy/MM/dd";
        simpleFieldDoc = false;
        kdocSimpleFieldDoc = false;
        docPriority = DOC_FIRST;
        kdocParamType = LINK_PARAM_TYPE;
        translator = Consts.YOUDAO_TRANSLATOR;
        appId = null;
        token = null;
        secretKey = null;
        secretId = null;
        accessKeyId = null;
        accessKeySecret = null;
        youdaoAppKey = null;
        youdaoAppSecret = null;
        wordMap = new TreeMap<>();
        projectWordMap = new TreeMap<>();
        classTemplateConfig = new TemplateConfig();
        kdocClassTemplateConfig = new TemplateConfig();
        methodTemplateConfig = new TemplateConfig();
        kdocMethodTemplateConfig = new TemplateConfig();
        fieldTemplateConfig = new TemplateConfig();
        kdocFieldTemplateConfig = new TemplateConfig();
        mergeProject();
    }

    public void mergeProject() {
        Project[] projects = ProjectManager.getInstance().getOpenProjects();
        for (Project project : projects) {
            projectWordMap.computeIfAbsent(project.getName(), k -> Maps.newTreeMap());
        }
    }

    /**
     * 模板配置
     */
    public static class TemplateConfig {
        /**
         * 是否默认的
         */
        private Boolean isDefault;
        /**
         * 模板
         */
        private String template;
        /**
         * 自定义映射
         */
        private Map<String, CustomValue> customMap;

        public TemplateConfig() {
            isDefault = true;
            template = "";
            customMap = new TreeMap<>();
        }

        public Boolean getIsDefault() {
            return isDefault;
        }

        public void setIsDefault(Boolean isDefault) {
            this.isDefault = isDefault;
        }

        public String getTemplate() {
            return template;
        }

        public void setTemplate(String template) {
            this.template = template;
        }

        public Map<String, CustomValue> getCustomMap() {
            return customMap;
        }

        public void setCustomMap(Map<String, CustomValue> customMap) {
            this.customMap = customMap;
        }
    }

    /**
     * 自定义值
     */
    public static class CustomValue {
        /**
         * 类型
         */
        private VariableType type;
        /**
         * 值
         */
        private String value;

        public CustomValue() {
        }

        public CustomValue(VariableType type, String value) {
            this.type = type;
            this.value = value;
        }

        public VariableType getType() {
            return type;
        }

        public void setType(VariableType type) {
            this.type = type;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }

    /**
     * 变量类型
     */
    public enum VariableType {
        /**
         * 固定值
         */
        STRING("固定值"),
        /**
         * groovy脚本
         */
        GROOVY("Groovy脚本");

        private final String desc;

        VariableType(String desc) {
            this.desc = desc;
        }

        public String getDesc() {
            return desc;
        }

        public static VariableType fromDesc(String desc) {
            for (VariableType value : values()) {
                if (value.desc.equals(desc)) {
                    return value;
                }
            }
            return null;
        }
    }

    public SortedMap<String, TreeMap<String, String>> getProjectWordMap() {
        if (projectWordMap == null) {
            projectWordMap = Maps.newTreeMap();
        }
        return projectWordMap;
    }

    public void setProjectWordMap(SortedMap<String, TreeMap<String, String>> projectWordMap) {
        this.projectWordMap = projectWordMap;
    }

    public String getKdocAuthor() {
        return kdocAuthor;
    }

    public void setKdocAuthor(String kdocAuthor) {
        this.kdocAuthor = kdocAuthor;
    }

    public String getKdocDateFormat() {
        return kdocDateFormat;
    }

    public void setKdocDateFormat(String kdocDateFormat) {
        this.kdocDateFormat = kdocDateFormat;
    }

    public Boolean getKdocSimpleFieldDoc() {
        return kdocSimpleFieldDoc;
    }

    public void setKdocSimpleFieldDoc(Boolean kdocSimpleFieldDoc) {
        this.kdocSimpleFieldDoc = kdocSimpleFieldDoc;
    }

    public TemplateConfig getKdocClassTemplateConfig() {
        return kdocClassTemplateConfig;
    }

    public void setKdocClassTemplateConfig(TemplateConfig kdocClassTemplateConfig) {
        this.kdocClassTemplateConfig = kdocClassTemplateConfig;
    }

    public TemplateConfig getKdocMethodTemplateConfig() {
        return kdocMethodTemplateConfig;
    }

    public void setKdocMethodTemplateConfig(TemplateConfig kdocMethodTemplateConfig) {
        this.kdocMethodTemplateConfig = kdocMethodTemplateConfig;
    }

    public TemplateConfig getKdocFieldTemplateConfig() {
        return kdocFieldTemplateConfig;
    }

    public void setKdocFieldTemplateConfig(TemplateConfig kdocFieldTemplateConfig) {
        this.kdocFieldTemplateConfig = kdocFieldTemplateConfig;
    }

    public String getKdocParamType() {
        return kdocParamType;
    }

    public void setKdocParamType(String kdocParamType) {
        this.kdocParamType = kdocParamType;
    }

    public String getTranslator() {
        return translator;
    }

    public void setTranslator(String translator) {
        this.translator = translator;
    }

    public Boolean getSimpleFieldDoc() {
        return simpleFieldDoc;
    }

    public void setSimpleFieldDoc(Boolean simpleFieldDoc) {
        this.simpleFieldDoc = simpleFieldDoc;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getDateFormat() {
        return dateFormat;
    }

    public void setDateFormat(String dateFormat) {
        this.dateFormat = dateFormat;
    }

    public SortedMap<String, String> getWordMap() {
        if (wordMap == null) {
            wordMap = Maps.newTreeMap();
        }
        return wordMap;
    }

    @JSONField(serialize = false, deserialize = false)
    public Map<String, String> getWordMapWithProject() {
        Map<String, String> map = Maps.newHashMap();
        if (wordMap != null) {
            map.putAll(wordMap);
        }
        Project project = DataManager.getInstance()
                .getDataContextFromFocus()
                .getResultSync()
                .getData(CommonDataKeys.PROJECT);
        if (project != null) {
            Map<String, String> projectMap = projectWordMap.get(project.getName());
            if (projectMap != null) {
                map.putAll(projectMap);
            }
        }
        return map;
    }

    public void setWordMap(SortedMap<String, String> wordMap) {
        this.wordMap = wordMap;
    }

    public TemplateConfig getClassTemplateConfig() {
        if (classTemplateConfig == null) {
            classTemplateConfig = new TemplateConfig();
        }
        return classTemplateConfig;
    }

    public void setClassTemplateConfig(TemplateConfig classTemplateConfig) {
        this.classTemplateConfig = classTemplateConfig;
    }

    public TemplateConfig getMethodTemplateConfig() {
        if (methodTemplateConfig == null) {
            methodTemplateConfig = new TemplateConfig();
        }
        return methodTemplateConfig;
    }

    public void setMethodTemplateConfig(TemplateConfig methodTemplateConfig) {
        this.methodTemplateConfig = methodTemplateConfig;
    }

    public TemplateConfig getFieldTemplateConfig() {
        if (fieldTemplateConfig == null) {
            fieldTemplateConfig = new TemplateConfig();
        }
        return fieldTemplateConfig;
    }

    public void setFieldTemplateConfig(TemplateConfig fieldTemplateConfig) {
        this.fieldTemplateConfig = fieldTemplateConfig;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public String getSecretId() {
        return secretId;
    }

    public void setSecretId(String secretId) {
        this.secretId = secretId;
    }

    public String getAccessKeyId() {
        return accessKeyId;
    }

    public void setAccessKeyId(String accessKeyId) {
        this.accessKeyId = accessKeyId;
    }

    public String getAccessKeySecret() {
        return accessKeySecret;
    }

    public void setAccessKeySecret(String accessKeySecret) {
        this.accessKeySecret = accessKeySecret;
    }

    public String getYoudaoAppKey() {
        return youdaoAppKey;
    }

    public void setYoudaoAppKey(String youdaoAppKey) {
        this.youdaoAppKey = youdaoAppKey;
    }

    public String getYoudaoAppSecret() {
        return youdaoAppSecret;
    }

    public void setYoudaoAppSecret(String youdaoAppSecret) {
        this.youdaoAppSecret = youdaoAppSecret;
    }

    public String getMethodReturnType() {
        return methodReturnType;
    }

    public void setMethodReturnType(String methodReturnType) {
        this.methodReturnType = methodReturnType;
    }

    @JSONField(serialize = false, deserialize = false)
    public boolean isCodeMethodReturnType() {
        return CODE_RETURN_TYPE.equals(methodReturnType);
    }

    @JSONField(serialize = false, deserialize = false)
    public boolean isLinkMethodReturnType() {
        return LINK_RETURN_TYPE.equals(methodReturnType);
    }

    @JSONField(serialize = false, deserialize = false)
    public boolean isDocMethodReturnType() {
        return DOC_RETURN_TYPE.equals(methodReturnType);
    }

    public Boolean getGenAllClass() {
        return genAllClass;
    }

    public void setGenAllClass(Boolean genAllClass) {
        this.genAllClass = genAllClass;
    }

    public Boolean getGenAllMethod() {
        return genAllMethod;
    }

    public void setGenAllMethod(Boolean genAllMethod) {
        this.genAllMethod = genAllMethod;
    }

    public Boolean getGenAllField() {
        return genAllField;
    }

    public void setGenAllField(Boolean genAllField) {
        this.genAllField = genAllField;
    }

    public Boolean getGenAllInnerClass() {
        return genAllInnerClass;
    }

    public void setGenAllInnerClass(Boolean genAllInnerClass) {
        this.genAllInnerClass = genAllInnerClass;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getGoogleKey() {
        return googleKey;
    }
    

    public void setGoogleKey(String googleKey) {
        this.googleKey = googleKey;
    }

    public String getMicrosoftKey() {
        return microsoftKey;
    }

    public void setMicrosoftKey(String microsoftKey) {
        this.microsoftKey = microsoftKey;
    }

    public String getDocPriority() {
        return docPriority;
    }

    public void setDocPriority(String docPriority) {
        this.docPriority = docPriority;
    }
}
