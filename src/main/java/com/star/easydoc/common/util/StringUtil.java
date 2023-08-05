package com.star.easydoc.common.util;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 字符串工具类
 *
 * @author wangchao
 * @date 2022/01/01
 */
public class StringUtil {

    /** 私有构造方法 */
    private StringUtil() {}

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

    /**
     * 英文单词分割
     *
     * @param word 名称
     * @return 分割后内容
     */
    public static List<String> split(String word) {
        word = word.replaceAll("(?<=[^A-Z])[A-Z][^A-Z]", "_$0");
        word = word.replaceAll("[A-Z]{2,}", "_$0");
        word = word.replaceAll("_+", "_");
        return Arrays.stream(word.split("_")).map(String::toLowerCase).collect(Collectors.toList());
    }

    /**
     * 统计字符串结尾为c的数量
     *
     * @param str 字符串
     * @param c 字符
     * @return int
     */
    public static int endCount(String str, char c) {
        // 计算空行数
        if (str == null || str.length() == 0) {
            return 0;
        }
        char[] chars = str.toCharArray();
        int count = 0;
        for (int i = chars.length - 1; i >= 0; i--) {
            if (chars[i] == c) {
                count++;
            } else {
                break;
            }
        }
        return count;
    }

}
