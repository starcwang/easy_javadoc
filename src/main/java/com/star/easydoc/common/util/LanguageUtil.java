package com.star.easydoc.common.util;

/**
 * 语言工具
 *
 * @author wangchao
 * @version 1.0.0
 * @date 2020/02/12
 * @since 2019-12-08 03:16:00
 */
public class LanguageUtil {

    /** 私有构造 */
    private LanguageUtil() {}

    /**
     * 是否是中文
     *
     * @param c c
     * @return boolean
     */
    private static boolean isChinese(char c) {
        Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
        return ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
            || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
            || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
            || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_B
            || ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
            || ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS
            || ub == Character.UnicodeBlock.GENERAL_PUNCTUATION;
    }

    /**
     * 是否含有中文
     *
     * @param str str
     * @return boolean
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

    /**
     * 是否是中文字符串
     *
     * @param str str
     * @return boolean
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
