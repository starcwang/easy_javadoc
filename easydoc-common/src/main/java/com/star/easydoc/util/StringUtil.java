package com.star.easydoc.util;

import java.util.List;

/**
 * 字符串工具类
 *
 * @author wangchao
 * @date 2022/01/01
 */
public class StringUtil {

    /**
     * 检查传入字符串是否以给定的任意前缀开始
     *
     * @param text 文本
     * @param prefixList 前缀列表
     * @return boolean
     */
    public static boolean anyStartWith(String text, List<String> prefixList) {
        for (String prefix : prefixList) {
            if (text.startsWith(prefix)) {
                return true;
            }
        }
        return false;
    }

}
