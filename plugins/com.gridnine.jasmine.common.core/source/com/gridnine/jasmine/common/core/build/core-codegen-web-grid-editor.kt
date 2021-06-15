/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/
@file:Suppress("UNCHECKED_CAST")

package com.gridnine.jasmine.common.core.build

import com.gridnine.jasmine.common.core.meta.*
import com.gridnine.jasmine.common.core.model.Xeption
import java.io.File
import java.lang.StringBuilder


object GridWebEditorGenerator {
    fun generateEditor(description: GridContainerDescription, baseDir: File, projectName: String, generatedFiles: MutableList<File>, context: MutableMap<String, Any>) {
        val webMapping = context[PluginAssociationsGenerator.WEB_MAP_KEY] as HashMap<String, String>
        val sb = StringBuilder()
        GenUtils.generateHeader(sb, description.id, projectName)
        GenUtils.classBuilder(sb, "class ${GenUtils.getSimpleClassName(description.id)}:com.gridnine.jasmine.web.standard.editor.WebEditor<${description.id}VMJS,${description.id}VSJS,${description.id}VVJS>, com.gridnine.jasmine.web.core.ui.components.BaseWebNodeWrapper<com.gridnine.jasmine.web.standard.widgets.WebGridLayoutWidget>()") {
            blankLine()
            blankLine()
            description.rows.forEach { row ->
                row.cells.forEach { cell ->
                    if (cell.widget.widgetType == WidgetType.HIDDEN) {
                        val widget = cell.widget as HiddenWidgetDescription
                        if (widget.nonNullable) {
                            "lateinit var ${cell.id}Value: ${widget.objectId}"()
                        } else {
                            "var ${cell.id}Value: ${widget.objectId}? = null"()
                        }
                    } else {
                        cell.widget.let { widget ->
                            "val ${cell.id}Widget: ${getWidgetClassName(widget)}"()
                            blankLine()
                        }
                    }
                }
            }
            "init"{
                description.rows.forEach { row ->
                    row.cells.forEach cell@{ cell ->
                        if (cell.widget.widgetType == WidgetType.HIDDEN) {
                            return@cell
                        }
                        val widget = cell.widget
                        when (widget.widgetType) {
                            WidgetType.ENTITY_SELECT_BOX -> {
                                widget as EntitySelectBoxWidgetDescription
                                "${cell.id}Widget = ${getWidgetClassName(widget)}"{
                                    """width = "100%""""()
                                    """handler = com.gridnine.jasmine.web.standard.widgets.AutocompleteHandler.createMetadataBasedAutocompleteHandler("${widget.objectId}JS")"""()
                                }
                            }
                            WidgetType.ENUM_SELECT_BOX -> {
                                widget as EnumSelectBoxWidgetDescription
                                "${cell.id}Widget =  ${getWidgetClassName(widget)}"{
                                    """width = "100%""""()
                                    "enumClass = ${widget.enumId}JS::class"()
                                }
                            }
                            WidgetType.TABLE_BOX -> {
                                widget as TableBoxWidgetDescription
                                "${cell.id}Widget = ${getWidgetClassName(widget)}"{
                                    """width = "100%""""()
                                    "showToolsColumn = true"()
                                    "vmFactory = {${widget.id}VMJS()}"()
                                    webMapping["web-messages-${widget.id}"] = baseDir.name
                                    widget.columns.forEach { column ->
                                        val colWidget = column.widget
                                        when (colWidget.widgetType) {
                                            WidgetType.TEXT_BOX -> {
                                                colWidget as TextBoxWidgetDescription
                                                """column("${column.id}", com.gridnine.jasmine.web.standard.widgets.TextBoxWidgetDescriptionJS(false), com.gridnine.jasmine.common.core.meta.L10nMetaRegistryJS.get().messages["${widget.id}"]?.get("${column.id}")?:"${column.id}", ${column.prefWidth ?: "100"})"""()
                                            }
                                            WidgetType.PASSWORD_BOX -> {
                                                colWidget as PasswordBoxWidgetDescription
                                                """column("${column.id}", com.gridnine.jasmine.web.standard.widgets.PasswordBoxWidgetDescriptionJS(false), com.gridnine.jasmine.common.core.meta.L10nMetaRegistryJS.get().messages["${widget.id}"]?.get("${column.id}")?:"${column.id}", ${column.prefWidth ?: "100"})"""()
                                            }
                                            WidgetType.BIG_DECIMAL_NUMBER_BOX -> {
                                                colWidget as BigDecimalNumberBoxWidgetDescription
                                                """column("${column.id}",  com.gridnine.jasmine.web.standard.widgets.BigDecimalNumberBoxWidgetDescriptionJS(false), com.gridnine.jasmine.common.core.meta.L10nMetaRegistryJS.get().messages["${widget.id}"]?.get("${column.id}")?:"${column.id}", ${column.prefWidth ?: "100"})"""()
                                            }
                                            WidgetType.INTEGER_NUMBER_BOX -> {
                                                colWidget as IntegerNumberBoxWidgetDescription
                                                """column("${column.id}", com.gridnine.jasmine.web.standard.widgets.IntegerNumberBoxWidgetDescriptionJS(false,false), com.gridnine.jasmine.common.core.meta.L10nMetaRegistryJS.get().messages["${widget.id}"]?.get("${column.id}")?:"${column.id}", ${column.prefWidth ?: "100"})"""()
                                            }
                                            WidgetType.BOOLEAN_BOX -> {
                                                colWidget as BooleanBoxWidgetDescription
                                                """column("${column.id}", com.gridnine.jasmine.web.standard.widgets.BooleanBoxWidgetDescriptionJS(false), com.gridnine.jasmine.common.core.meta.L10nMetaRegistryJS.get().messages["${widget.id}"]?.get("${column.id}")?:"${column.id}", ${column.prefWidth ?: "100"})"""()
                                            }
                                            WidgetType.ENTITY_SELECT_BOX -> {
                                                colWidget as EntitySelectBoxWidgetDescription
                                                """column("${column.id}", com.gridnine.jasmine.web.standard.widgets.EntitySelectBoxWidgetDescriptionJS(false, "${colWidget.objectId}JS"), com.gridnine.jasmine.common.core.meta.L10nMetaRegistryJS.get().messages["${widget.id}"]?.get("${column.id}")?:"${column.id}", ${column.prefWidth ?: "100"})"""()
                                            }
                                            WidgetType.GENERAL_SELECT_BOX -> {
                                                colWidget as GeneralSelectBoxWidgetDescription
                                                """column("${column.id}", com.gridnine.jasmine.web.standard.widgets.GeneralSelectBoxWidgetDescriptionJS(false), com.gridnine.jasmine.common.core.meta.L10nMetaRegistryJS.get().messages["${widget.id}"]?.get("${column.id}")?:"${column.id}", ${column.prefWidth ?: "100"})"""()
                                            }
                                            WidgetType.ENUM_SELECT_BOX -> {
                                                colWidget as EnumSelectBoxWidgetDescription
                                                """column("${column.id}", com.gridnine.jasmine.web.standard.widgets.EnumSelectBoxWidgetDescriptionJS(false, "${colWidget.enumId}JS"), com.gridnine.jasmine.common.core.meta.L10nMetaRegistryJS.get().messages["${widget.id}"]?.get("${column.id}")?:"${column.id}", ${column.prefWidth ?: "100"})"""()
                                            }
                                            WidgetType.DATE_BOX -> {
                                                colWidget as DateBoxWidgetDescription
                                                """column("${column.id}", com.gridnine.jasmine.web.standard.widgets.DateBoxWidgetDescriptionJS(false), com.gridnine.jasmine.common.core.meta.L10nMetaRegistryJS.get().messages["${widget.id}"]?.get("${column.id}")?:"${column.id}", ${column.prefWidth ?: "100"})"""()
                                            }
                                            WidgetType.DATE_TIME_BOX -> {
                                                colWidget as DateTimeBoxWidgetDescription
                                                """column("${column.id}", com.gridnine.jasmine.web.standard.widgets.DateTimeBoxWidgetDescriptionJS(false), com.gridnine.jasmine.common.core.meta.L10nMetaRegistryJS.get().messages["${widget.id}"]?.get("${column.id}")?:"${column.id}", ${column.prefWidth ?: "100"})"""()
                                            }
                                            WidgetType.HIDDEN -> throw  Xeption.forDeveloper("unsupported widget type ${widget.widgetType}")
                                            WidgetType.TABLE_BOX -> throw Xeption.forDeveloper("table inside table is not supported")
                                        }
                                    }
                                }
                            }
                            else -> {
                                "${cell.id}Widget =  ${getWidgetClassName(widget)}"{
                                    """width = "100%""""()
                                }
                            }
                        }
                    }
                }
                val columns = if (description.columns.isEmpty()) {
                    val result = arrayListOf<GridContainerColumnDescription>()
                    for (n in 1..(description.columnsCount ?: 1)) {
                        result.add(GridContainerColumnDescription(PredefinedColumnWidth.STANDARD, null))
                    }
                    result
                } else description.columns
                val fixedWidth = columns.map {
                    when (it.predefinedWidth) {
                        PredefinedColumnWidth.STANDARD -> 200
                        PredefinedColumnWidth.REMAINING -> null
                        PredefinedColumnWidth.CUSTOM -> if (it.customWidth?.contains("px") == true) it.customWidth.substringBeforeLast("px").toInt() else null
                    }
                }.reduce { c1, c2 -> if (c1 == null || c2 == null) null else c1 + c2 }
                "_node = com.gridnine.jasmine.web.standard.widgets.WebGridLayoutWidget"{
                    if (fixedWidth != null) {
                        """width="${fixedWidth}px""""()
                    }
                }
                "_node.setColumnsWidths(${columns.joinToString (","){
                    when (it.predefinedWidth) {
                        PredefinedColumnWidth.STANDARD -> "com.gridnine.jasmine.web.core.ui.components.DefaultUIParameters.controlWidthAsString"
                        PredefinedColumnWidth.REMAINING -> "\"100%\""
                        PredefinedColumnWidth.CUSTOM -> """"${it.customWidth}""""
                    }
                }})"()
                description.rows.forEach { row ->
                    val height = when (row.predefinedHeight) {
                        PredefinedRowHeight.AUTO -> null
                        PredefinedRowHeight.REMAINING -> "100%"
                        PredefinedRowHeight.CUSTOM -> row.customHeight
                    }
                    """_node.addRow(${if(height != null) """$height""" else "null"}, 
                        ${row.cells.filter { cell ->cell.widget.widgetType != WidgetType.HIDDEN }.joinToString(",\r\n") {cell ->
                        if (cell.caption != null) {
                            """com.gridnine.jasmine.web.standard.widgets.WebGridLayoutWidgetCell(com.gridnine.jasmine.web.standard.widgets.WebGridCellWidget(com.gridnine.jasmine.common.core.meta.L10nMetaRegistryJS.get().messages["${description.id}"]?.get("${cell.id}")?:"${cell.id}", ${cell.id}Widget),${cell.colSpan})"""
                        } else {
                            """com.gridnine.jasmine.web.standard.widgets.WebGridLayoutWidgetCell(${cell.id}Widget,${cell.colSpan})"""
                        } 
                    }}
                        )""".trimMargin()()
                }
            }
            blankLine()
            "override fun readData(vm: ${description.id}VMJS, vs: ${description.id}VSJS?)" {
                description.rows.forEach { row ->
                    row.cells.forEach { cell ->
                        when (cell.widget) {
                            is HiddenWidgetDescription -> {
                                "${cell.id}Value = vm.${cell.id}"()
                            }
                            is TableBoxWidgetDescription -> {
                                "${cell.id}Widget.readData(vm.${cell.id}, vs?.${cell.id})"()
                            }
                            else -> {
                                "${cell.id}Widget.setValue(vm.${cell.id})"()
                                "vs?.${cell.id}?.let{${cell.id}Widget.configure(it)}"()
                            }
                        }
                    }
                }
            }
            blankLine()
            "override fun setReadonly(value: Boolean)" {
                description.rows.forEach { row ->
                    row.cells.forEach cell@{ cell ->
                        if (cell.widget.widgetType == WidgetType.HIDDEN) {
                            return@cell
                        }
                        "${cell.id}Widget.setReadonly(value)"()
                    }
                }
            }
            blankLine()
            "override fun getData(): ${description.id}VMJS" {
                "val result = ${description.id}VMJS()"()
                description.rows.forEach { row ->
                    row.cells.forEach { cell ->
                        when (val widget = cell.widget) {
                            is HiddenWidgetDescription -> {
                                "result.${cell.id} = ${cell.id}Value"()
                            }
                            is IntegerNumberBoxWidgetDescription -> {
                                "result.${cell.id} = ${cell.id}Widget.getValue()${if (widget.nonNullable) "!!" else ""}"()
                            }
                            is TableBoxWidgetDescription -> {
                                "result.${cell.id}.addAll(${cell.id}Widget.getData())"()
                            }
                            else -> {
                                "result.${cell.id} =${cell.id}Widget.getValue()"()
                            }
                        }
                    }
                }
                "return result"()
            }

            blankLine()
            "override fun showValidation(vv: ${description.id}VVJS?) " {
                description.rows.forEach { row ->
                    row.cells.forEach cell@{ cell ->
                        if (cell.widget.widgetType == WidgetType.HIDDEN) {
                            return@cell
                        }
                        "vv?.${cell.id}?.let{${cell.id}Widget.showValidation(it)}"()
                    }
                }
            }
        }
        val file = File(baseDir, "source-gen/${GenUtils.getPackageName(description.id).replace(".", File.separator)}/${GenUtils.getSimpleClassName(description.id)}.kt")
        GenUtils.writeContent(file, sb)
        generatedFiles.add(file)
    }

    private fun getWidgetClassName(widget: BaseWidgetDescription): String {
        return when (widget.widgetType) {
            WidgetType.TEXT_BOX -> "com.gridnine.jasmine.web.standard.widgets.TextBoxWidget"
            WidgetType.PASSWORD_BOX -> "com.gridnine.jasmine.web.standard.widgets.PasswordBoxWidget"
            WidgetType.BIG_DECIMAL_NUMBER_BOX -> "com.gridnine.jasmine.web.standard.widgets.FloatNumberBoxWidget"
            WidgetType.INTEGER_NUMBER_BOX -> "com.gridnine.jasmine.web.standard.widgets.IntegerNumberBoxWidget"
            WidgetType.BOOLEAN_BOX -> "com.gridnine.jasmine.web.standard.widgets.BooleanBoxWidget"
            WidgetType.ENTITY_SELECT_BOX -> "com.gridnine.jasmine.web.standard.widgets.EntitySelectWidget"
            WidgetType.GENERAL_SELECT_BOX -> "com.gridnine.jasmine.web.standard.widgets.GeneralSelectWidget"
            WidgetType.ENUM_SELECT_BOX -> "com.gridnine.jasmine.web.standard.widgets.EnumValueWidget<${(widget as EnumSelectBoxWidgetDescription).enumId}JS>"
            WidgetType.DATE_BOX -> "com.gridnine.jasmine.web.standard.widgets.DateBoxWidget"
            WidgetType.DATE_TIME_BOX -> "com.gridnine.jasmine.web.standard.widgets.DateTimeBoxWidget"
            WidgetType.TABLE_BOX -> {
                widget as TableBoxWidgetDescription
                "com.gridnine.jasmine.web.standard.widgets.TableBoxWidget<${widget.id}VMJS,${widget.id}VSJS,${widget.id}VVJS>"
            }
            WidgetType.HIDDEN -> throw  Xeption.forDeveloper("unsupported widget type ${widget.widgetType}")
        }
    }

}