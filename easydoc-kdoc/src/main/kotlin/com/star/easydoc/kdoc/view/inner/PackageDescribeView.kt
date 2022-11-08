package com.star.easydoc.kdoc.view.inner

import com.intellij.openapi.ui.DialogWrapper
import com.intellij.psi.PsiPackage
import java.util.*
import javax.swing.JComponent
import javax.swing.JPanel
import javax.swing.JTable
import javax.swing.table.DefaultTableModel

class PackageDescribeView(private val packMap: Map<PsiPackage, String>) : DialogWrapper(false) {
    private lateinit var panel1: JPanel
    private lateinit var packageInfoTable: JTable
    private lateinit var packIndexMap: MutableMap<Int, PsiPackage>

    init {
        createMap(packMap)
        init()
        title = "包信息生成"
    }

    //        packageInfoTable.get
    val finalMap: Map<PsiPackage?, String>
        get() {
            //        packageInfoTable.get
            val editor = packageInfoTable.cellEditor
            editor?.stopCellEditing()
            val finalPackMap: MutableMap<PsiPackage?, String> = HashMap()
            for (index in packIndexMap.keys) {
                val psiPackage = packIndexMap[index]
                val value = packageInfoTable.getValueAt(index, 1) as String
                finalPackMap[psiPackage] = value
            }
            return finalPackMap
        }

    fun createMap(packMap: Map<PsiPackage, String>) {
        val list: List<Map.Entry<PsiPackage, String>> = ArrayList<Map.Entry<PsiPackage, String>>(packMap.entries)
        val objs = Array(list.size) { arrayOfNulls<String>(2) }
        packIndexMap = HashMap()
        for (i in list.indices) {
            val aPackage: PsiPackage = list[i].key
            objs[i][0] = aPackage.qualifiedName
            objs[i][1] = list[i].value
            packIndexMap[i] = aPackage
        }
        val innerModel: DefaultTableModel = object : DefaultTableModel(objs, arrayOf("包名称", "注释")) {
            override fun isCellEditable(row: Int, column: Int): Boolean {
                return column == 1
            }
        }
        packageInfoTable.model = innerModel
        //        packageInfoTable.colum(0)
    }

    override fun createCenterPanel(): JComponent {
        return panel1
    }
}