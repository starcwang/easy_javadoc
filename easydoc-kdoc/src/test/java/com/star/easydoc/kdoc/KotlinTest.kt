package com.star.easydoc.kdoc

import com.google.common.collect.Maps
import org.junit.Test


/**
 *
 * @author wangchao
 * @date 2022/10/12
 */
internal class KotlinTest {

    @Test
    fun testCommon() {
        val variableMap: MutableMap<String, String> = Maps.newHashMap()
        variableMap["a"] = "asjjj"
        variableMap.put("b", "fdsfds")
        println(variableMap)
    }
}