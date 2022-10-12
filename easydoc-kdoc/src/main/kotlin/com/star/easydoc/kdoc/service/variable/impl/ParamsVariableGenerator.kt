package com.star.easydoc.kdoc.service.variable.impl

import com.google.common.collect.Lists
import com.intellij.openapi.components.ServiceManager
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiMethod
import com.intellij.psi.PsiParameter
import com.intellij.psi.impl.source.PsiMethodImpl
import com.intellij.psi.javadoc.PsiDocTag
import com.star.easydoc.service.translator.TranslatorService
import org.apache.commons.lang.StringUtils
import java.util.*
import java.util.stream.Collectors

/**
 * @author [wangchao](mailto:wangchao.star@gmail.com)
 * @version 1.0.0
 * @since 2019-12-07 23:18:00
 */
class ParamsVariableGenerator : AbstractVariableGenerator() {
    private val translatorService = ServiceManager.getService(TranslatorService::class.java)
    override fun generate(element: PsiElement): String {
        if (element !is PsiMethod) {
            return ""
        }
        val paramNameList = element.parameterList.parameters.mapNotNull { obj: PsiParameter -> obj.name }
        if (paramNameList.isEmpty()) {
            return ""
        }
        val paramGroupList: MutableList<ParamGroup> = ArrayList()
        val docComment = (element as PsiMethodImpl).docComment
        // {"paramName":PsiDocTag}
        var psiDocTagMap: Map<String?, PsiDocTag?> = HashMap()
        if (docComment != null) {
            val paramsDocArray = docComment.findTagsByName("param")
            psiDocTagMap = Arrays.stream(paramsDocArray).collect(Collectors.toMap({ q: PsiDocTag -> q.dataElements[0].text }, { tag: PsiDocTag? -> tag }))
        }
        for (paramName in paramNameList) {
            val psiDocTag = psiDocTagMap[paramName]
            if (psiDocTag == null) {
                // 不存在则插入一个需要翻译的
                paramGroupList.add(ParamGroup(paramName, translatorService.translate(paramName)))
                continue
            }
            val eleParamDesc = psiDocTag.dataElements[1]
            val desc = eleParamDesc.text
            if (StringUtils.isNotEmpty(desc)) {
                // 如果已经存在注释则直接返回
                paramGroupList.add(ParamGroup(paramName, desc))
            } else {
                // 不存在注释则翻译
                paramGroupList.add(ParamGroup(paramName, translatorService.translate(paramName)))
            }
        }
        val perLine: MutableList<String> = Lists.newArrayList()
        for (i in paramGroupList.indices) {
            val paramGroup = paramGroupList[i]
            if (i == 0) {
                perLine.add("@param " + paramGroup.param + " " + paramGroup.desc)
            } else {
                perLine.add("* @param " + paramGroup.param + " " + paramGroup.desc)
            }
        }
        return java.lang.String.join("\n", perLine)
    }

    /**
     * 参数名注释组合
     */
    internal class ParamGroup(var param: String, var desc: String)
}