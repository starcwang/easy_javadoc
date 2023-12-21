package com.star.easydoc.common.util;

/**
 * 语言工具
 * Language utility class
 *
 * @author wangchao
 * @version 1.0.0
 * @date 2020/02/12
 * @since 2019-12-08 03:16:00
 */
public class LanguageUtil {

    private LanguageUtil() {}

    // 检查字符是否是中文
    /**
     * Check if a character is Chinese
     *
     * @param c the character to be checked
     * @return true if the character is Chinese, false otherwise
     */
    private static boolean isChinese(char c) {
        Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
        // 判断Unicode块是否为CJK统一表意文字
        return ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
                // 判断Unicode块是否为CJK兼容性表意文字
                || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
                // 判断Unicode块是否为CJK统一表意文字扩展A
                || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
                // 判断Unicode块是否为CJK统一表意文字扩展B
                || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_B
                // 判断Unicode块是否为CJK符号和标点
                || ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
                // 判断Unicode块是否为半角及全角字符
                || ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS
                // 判断Unicode块是否为常规标点符号
                || ub == Character.UnicodeBlock.GENERAL_PUNCTUATION;
    }

    // 检查字符串中是否含有中文字符
    /**
     * Check if a string contains Chinese characters
     *
     * @param str the string to be checked
     * @return true if the string contains Chinese characters, false otherwise
     */
    public static boolean isChinese(String str) {
        char[] ch = str.toCharArray();
        for (char c : ch) {
            if (isChinese(c)) {
                return true;
            }
        }
        return false;
    }

    // 检查字符串是否全部为中文字符
    /**
     * Check if a string consists of only Chinese characters
     *
     * @param str the string to be checked
     * @return true if the string consists of only Chinese characters, false otherwise
     */
    public static boolean isAllChinese(String str) {
        char[] ch = str.toCharArray();
        for (char c : ch) {
            if (!isChinese(c)) {
                return false;
            }
        }
        return true;
    }
}
