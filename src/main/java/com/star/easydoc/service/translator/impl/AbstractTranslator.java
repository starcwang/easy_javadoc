package com.star.easydoc.service.translator.impl;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.star.easydoc.config.EasyDocConfig;
import com.star.easydoc.service.translator.Translator;

/**
 * @author wangchao
 * @date 2020/08/28
 */
public abstract class AbstractTranslator implements Translator {
    //定义一个抽象类实现Translator接口
    private final Map<String, String> en2chCacheMap = new ConcurrentHashMap<>();
    //用键值对，创建并发哈希映射对象（并发哈希映射是一种线程安全的哈希表，可以同时被多个线程安全地访问和修改），用来存储英译中的翻译结果
    private final Map<String, String> ch2enCacheMap = new ConcurrentHashMap<>();
    //同上，存储中译英的翻译
    /** 配置 */
    private EasyDocConfig config;
    //保存配置信息

    /**
     * 对接口中的英译中方法的重写,如果文本为空，则返回空，
     * 然后从en2chCacheMap中查找是否存在该文本的翻译结果，如果存在则直接返回结果。
     * 如果不存在，则调用抽象方法translateEn2Ch进行具体的翻译操作，并将翻译结果存入缓存中，然后返回结果。
     *
     */
    @Override
    public String en2Ch(String text) {
        if (text == null || text.length() == 0) {
            return "";
        }
        String res = en2chCacheMap.get(text);
        if (res != null && res.length() > 0) {
            return res;
        }
        res = translateEn2Ch(text);
        if (res != null && res.length() > 0) {
            en2chCacheMap.put(text, res);
        }
        return res;
    }


    /**
     * 与上述相同，变为中译英即可
     *
     */
    @Override
    public String ch2En(String text) {
        if (text == null || text.length() == 0) {
            return "";
        }
        String res = ch2enCacheMap.get(text);
        if (res != null && res.length() > 0) {
            return res;
        }
        res = translateCh2En(text);
        if (res != null && res.length() > 0) {
            ch2enCacheMap.put(text, res);
        }
        return res;
    }


    /**
     * 对接口中初始化的方法进行重写，初始化配置信息，并返回当前对象 刘喆
     *
     */
    @Override
    public Translator init(EasyDocConfig config) {
        this.config = config;
        return this;
    }

    /**
     * 对接口中获取配置的的方法进行重写，返回当前配置信息
     */
    @Override
    public EasyDocConfig getConfig() {
        return this.config;
    }

    /**
     * 清除缓存
     */
    /**
     * 对接口中的清除缓存方法进行重写，调用即可删除两个映射的全部数据
     */
    @Override
    public void clearCache() {
        en2chCacheMap.clear();
        ch2enCacheMap.clear();
    }




    /**
     * 声明了两个抽象方法translateCh2En 和 translateEn2Ch，
     * 用于具体的中译英和英译中操作，在后面的类里面会对这两个方法进行多次重写，
     * 以此来完成调用不同翻译软件的翻译功能
     *
     */
    /**
     * 中译英
     *
     * @param text 文本
     * @return {@link String}
     */
    protected abstract String translateCh2En(String text);

    /**
     * 英译中
     *
     * @param text 文本
     * @return {@link String}
     */
    protected abstract String translateEn2Ch(String text);
}
