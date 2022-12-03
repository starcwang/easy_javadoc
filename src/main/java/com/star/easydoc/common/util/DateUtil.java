package com.star.easydoc.common.util;

import java.time.format.DateTimeFormatter;

/**
 * 日期工具类
 *
 * @author wangchao
 * @date 2022/01/01
 */
public class DateUtil {

    /** 阿里巴巴日期模板 */
    public static final String ALIBABA_DATE_TEMPLATE = "yyyy/MM/dd";
    /** 日期时间模板 */
    public static final String DATE_TIME_TEMPLATE = "yyyy-MM-dd HH:mm:ss";
    /** 日期时间格式化 */
    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(DATE_TIME_TEMPLATE);
    /** 日期时间正则表达式 */
    public static final String DATE_TIME_REGEX = "\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}";
    /** 日期正则表达式 */
    public static final String DATE_REGEX = "\\d{4}-\\d{2}-\\d{2}";

}
