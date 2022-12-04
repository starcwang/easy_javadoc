package com.star.easydoc.view.settings.kdoc

import com.intellij.openapi.options.Configurable
import org.jetbrains.annotations.Nls
import org.jetbrains.annotations.Nls.Capitalization
import javax.swing.JComponent

/**
 *
 * @author wangchao
 * @date 2022/12/04
 */
class KdocSettingsConfigurable : Configurable {

    private val view = KdocSettingsView()

    override fun createComponent(): JComponent {
        return view.component
    }

    override fun isModified(): Boolean {
        return false
    }

    override fun apply() {
        TODO("Not yet implemented")
    }

    @Nls(capitalization = Capitalization.Title)
    override fun getDisplayName(): String {
        return "kdoc"
    }
}