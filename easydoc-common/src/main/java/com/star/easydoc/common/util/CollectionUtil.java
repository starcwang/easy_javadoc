package com.star.easydoc.common.util;

import java.util.Collection;

/**
 * 集合工具
 *
 * @author wangchao
 * @date 2019/12/14
 */
public class CollectionUtil {

    private CollectionUtil() {}

    /**
     * 是否空
     *
     * @param collection 集合
     * @return boolean
     */
    public static boolean isEmpty(Collection<?> collection) {
        return collection == null || collection.isEmpty();
    }

    /**
     * 是否不空
     *
     * @param collection 集合
     * @return boolean
     */
    public static boolean isNotEmpty(Collection<?> collection) {
        return collection != null && !collection.isEmpty();
    }

    /**
     * 包含任何
     *
     * @param coll1 coll1
     * @param coll2 coll2
     * @return boolean
     */
    public static boolean containsAny(final Collection<?> coll1, final Collection<?> coll2) {
        if (coll1.size() < coll2.size()) {
            for (final Object aColl1 : coll1) {
                if (coll2.contains(aColl1)) {
                    return true;
                }
            }
        } else {
            for (final Object aColl2 : coll2) {
                if (coll1.contains(aColl2)) {
                    return true;
                }
            }
        }
        return false;
    }
}
