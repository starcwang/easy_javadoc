package com.star.easydoc.common.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.apache.commons.lang3.RandomUtils;

/**
 * 随机数工具类
 *
 * @author wangchao
 * @date 2022/01/01
 */
public class RandomUtil extends RandomUtils {

    /**
     * 生成一个随机double
     *
     * @param scale 保留几位小数
     * @return double
     */
    public static double nextDoubleInThousand(int scale) {
        return BigDecimal.valueOf(nextDouble(0, 1000)).setScale(scale, RoundingMode.HALF_UP).doubleValue();
    }

}
