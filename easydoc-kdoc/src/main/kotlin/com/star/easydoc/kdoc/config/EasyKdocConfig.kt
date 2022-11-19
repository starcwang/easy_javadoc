package com.star.easydoc.kdoc.config

import com.star.easydoc.common.config.EasyDocConfig

/**
 *
 * @author wangchao
 * @date 2022/11/19
 */
class EasyKdocConfig: EasyDocConfig() {
    /**
     * 作者
     */
    var paramType = "中括号模式"

    /** 普通模式 */
    val NORMAL_PARAM_TYPE = "普通模式"
    /** 中括号模式 */
    val LINK_PARAM_TYPE = "中括号模式"

}