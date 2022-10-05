package com.star.easydoc.common.util;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

import com.intellij.openapi.diagnostic.Logger;

/**
 * bean工具
 *
 * @author wangchao
 * @date 2019/12/14
 */
public class BeanUtil {
    /** 日志记录器 */
    private static final Logger LOGGER = Logger.getInstance(BeanUtil.class);

    private BeanUtil() {}

    /**
     * 复制属性
     *
     * @param source 源
     * @param target 目标
     */
    public static <T> void copyProperties(T source, T target) {
        try {
            Field[] sourceFields = source.getClass().getDeclaredFields();
            Field[] targetFields = target.getClass().getDeclaredFields();

            Map<String, Field> nameFieldMap = Arrays.stream(sourceFields).collect(Collectors.toMap(Field::getName, f -> f));
            for (Field targetField : targetFields) {
                Field sourceField = nameFieldMap.get(targetField.getName());
                sourceField.setAccessible(true);
                targetField.setAccessible(true);
                targetField.set(target, sourceField.get(source));
            }
        } catch (Exception e) {
            LOGGER.error("拷贝属性异常", e);
        }
    }
}
