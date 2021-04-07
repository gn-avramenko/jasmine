/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/
package com.gridnine.jasmine.server.core.build

import com.gridnine.jasmine.server.core.model.common.Xeption
import com.gridnine.jasmine.server.core.model.ui.*
import java.io.File


object ServerUiGridWebEditorGenerator {
    fun generateEditor(description: GridContainerDescription, baseDir: File, projectName: String, generatedFiles: MutableList<File>) {
        val sb = StringBuilder()
        GenUtils.generateHeader(sb, description.id, projectName)
        GenUtils.classBuilder(sb, "class ${GenUtils.getSimpleClassName(description.id)}:com.gridnine.jasmine.web.server.components.ServerUiViewEditor<${description.id}VM,${description.id}VS,${description.id}VV>, com.gridnine.jasmine.web.server.components.BaseServerUiNodeWrapper<com.gridnine.jasmine.web.server.components.ServerUiGridLayoutContainer>()"){
            blankLine()
            description.rows.forEach { row ->
                row.cells.forEach { cell ->
                    if(cell.widget.widgetType == WidgetType.HIDDEN){
                        val widget = cell.widget as HiddenWidgetDescription
                        if(widget.nonNullable){
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
                val columns = if(description.columns.isEmpty()) arrayListOf(GridContainerColumnDescription(PredefinedColumnWidth.STANDARD, null)) else description.columns
                val fixedWidth = columns.map {
                    when(it.predefinedWidth){
                        PredefinedColumnWidth.STANDARD -> 300
                        PredefinedColumnWidth.REMAINING -> null
                        PredefinedColumnWidth.CUSTOM -> if(it.customWidth?.contains("px") == true) it.customWidth.substringBeforeLast("px").toInt() else null
                    }
                }.reduce{ c1, c2 -> if(c1 == null || c2 == null) null else c1+c2}
                "_node = com.gridnine.jasmine.web.server.components.ServerUiLibraryAdapter.get().createGridLayoutContainer(com.gridnine.jasmine.web.server.components.ServerUiGridLayoutContainerConfiguration"{
                    if(fixedWidth != null){
                        """width="${fixedWidth}px""""()
                    }
                    columns.forEach { column ->
                        when(column.predefinedWidth){
                            PredefinedColumnWidth.STANDARD ->  """columns.add(com.gridnine.jasmine.web.server.components.ServerUiGridLayoutColumnConfiguration("300px"))"""()
                            PredefinedColumnWidth.REMAINING ->  """columns.add(com.gridnine.jasmine.web.server.components.ServerUiGridLayoutColumnConfiguration("100%"))"""()
                            PredefinedColumnWidth.CUSTOM ->  """columns.add(com.gridnine.jasmine.web.server.components.ServerUiGridLayoutColumnConfiguration("${column.customWidth}"))"""()
                        }
                    }
                }
                sb.append(")")
                description.rows.forEach {row ->
                    when(row.predefinedHeight){
                        PredefinedRowHeight.AUTO -> "_node.addRow()"()
                        PredefinedRowHeight.REMAINING -> "_node.addRow(\"100%\")"()
                        PredefinedRowHeight.CUSTOM -> "_node.addRow(${row.customHeight})"()
                    }
                    row.cells.forEach cell@{ cell ->
                        if(cell.widget.widgetType == WidgetType.HIDDEN){
                            return@cell
                        }
                        val widget = cell.widget
                        when(widget.widgetType) {
                            WidgetType.ENTITY_SELECT_BOX -> {
                                widget as EntitySelectBoxWidgetDescription
                                "${cell.id}Widget = ${getWidgetClassName(widget)}"{
                                    """width = "100%""""()
                                    """handler = com.gridnine.jasmine.web.server.widgets.ServerUiAutocompleteHandler.createMetadataBasedAutocompleteHandler("${widget.objectId}")"""()
                                }
                            }
                            WidgetType.ENUM_SELECT_BOX -> {
                                widget as EnumSelectBoxWidgetDescription
                                "${cell.id}Widget =  ${getWidgetClassName(widget)}"{
                                    """width = "100%""""()
                                    "enumClass = ${widget.enumId}::class"()
                                }
                            }
                            WidgetType.TABLE_BOX -> {
                                widget as TableBoxWidgetDescription
                                "${cell.id}Widget = ${getWidgetClassName(widget)}"{
                                    """width = "100%""""()
                                    "showToolsColumn = true"()
                                    "vmFactory = {${widget.id}VM()}"()
                                    widget.columns.forEach { column ->
                                        val colWidget = column.widget
                                        when(colWidget.widgetType){
                                            WidgetType.TEXT_BOX -> {
                                                colWidget as TextBoxWidgetDescription
                                                """column("${column.id}", com.gridnine.jasmine.server.core.model.ui.TextBoxWidgetDescription(false), com.gridnine.jasmine.server.core.model.l10n.L10nMetaRegistry.get().webMessages["${widget.id}"]?.messages?.get("${column.id}")?.getDisplayName()?:"${column.id}", ${column.prefWidth?:"100"})"""()
                                            }
                                            WidgetType.PASSWORD_BOX -> {
                                                colWidget as PasswordBoxWidgetDescription
                                                """column("${column.id}", com.gridnine.jasmine.server.core.model.ui.PasswordBoxWidgetDescription(false), com.gridnine.jasmine.server.core.model.l10n.L10nMetaRegistry.get().webMessages["${widget.id}"]?.messages?.get("${column.id}")?.getDisplayName()?:"${column.id}", ${column.prefWidth?:"100"})"""()
                                            }
                                            WidgetType.FLOAT_NUMBER_BOX -> {
                                                colWidget as FloatNumberBoxWidgetDescription
                                                """column("${column.id}", com.gridnine.jasmine.server.core.model.ui.FloatNumberBoxWidgetDescription(false), com.gridnine.jasmine.server.core.model.l10n.L10nMetaRegistry.get().webMessages["${widget.id}"]?.messages?.get("${column.id}")?.getDisplayName()?:"${column.id}", ${column.prefWidth?:"100"})"""()
                                            }
                                            WidgetType.INTEGER_NUMBER_BOX -> {
                                                colWidget as IntegerNumberBoxWidgetDescription
                                                """column("${column.id}", com.gridnine.jasmine.server.core.model.ui.IntegerNumberBoxWidgetDescription(false,false), com.gridnine.jasmine.server.core.model.l10n.L10nMetaRegistry.get().webMessages["${widget.id}"]?.messages?.get("${column.id}")?.getDisplayName()?:"${column.id}", ${column.prefWidth?:"100"})"""()
                                            }
                                            WidgetType.BOOLEAN_BOX -> {
                                                colWidget as BooleanBoxWidgetDescription
                                                """column("${column.id}", com.gridnine.jasmine.server.core.model.ui.BooleanBoxWidgetDescription(false), com.gridnine.jasmine.server.core.model.l10n.L10nMetaRegistry.get().webMessages["${widget.id}"]?.messages?.get("${column.id}")?.getDisplayName()?:"${column.id}", ${column.prefWidth?:"100"})"""()
                                            }
                                            WidgetType.ENTITY_SELECT_BOX ->  {
                                                colWidget as EntitySelectBoxWidgetDescription
                                                """column("${column.id}", com.gridnine.jasmine.server.core.model.ui.EntitySelectBoxWidgetDescription(false, "${colWidget.objectId}"), com.gridnine.jasmine.server.core.model.l10n.L10nMetaRegistry.get().webMessages["${widget.id}"]?.messages?.get("${column.id}")?.getDisplayName()?:"${column.id}", ${column.prefWidth?:"100"})"""()
                                            }
                                            WidgetType.GENERAL_SELECT_BOX -> {
                                                colWidget as GeneralSelectBoxWidgetDescription
                                                """column("${column.id}", com.gridnine.jasmine.server.core.model.ui.GeneralSelectBoxWidgetDescription(false), com.gridnine.jasmine.server.core.model.l10n.L10nMetaRegistry.get().webMessages["${widget.id}"]?.messages?.get("${column.id}")?.getDisplayName()?:"${column.id}", ${column.prefWidth?:"100"})"""()
                                            }
                                            WidgetType.ENUM_SELECT_BOX -> {
                                                colWidget as EnumSelectBoxWidgetDescription
                                                """column("${column.id}", com.gridnine.jasmine.server.core.model.ui.EnumSelectBoxWidgetDescription(false, "${colWidget.enumId}"), com.gridnine.jasmine.server.core.model.l10n.L10nMetaRegistry.get().webMessages["${widget.id}"]?.messages?.get("${column.id}")?.getDisplayName()?:"${column.id}", ${column.prefWidth?:"100"})"""()
                                            }
                                            WidgetType.DATE_BOX ->  {
                                                colWidget as DateBoxWidgetDescription
                                                """column("${column.id}", com.gridnine.jasmine.server.core.model.ui.DateBoxWidgetDescription(false), com.gridnine.jasmine.server.core.model.l10n.L10nMetaRegistry.get().webMessages["${widget.id}"]?.messages?.get("${column.id}")?.getDisplayName()?:"${column.id}", ${column.prefWidth?:"100"})"""()
                                            }
                                            WidgetType.DATE_TIME_BOX -> {
                                                colWidget as DateTimeBoxWidgetDescription
                                                """column("${column.id}", com.gridnine.jasmine.server.core.model.ui.DateTimeBoxWidgetDescription(false), com.gridnine.jasmine.server.core.model.l10n.L10nMetaRegistry.get().webMessages["${widget.id}"]?.messages?.get("${column.id}")?.getDisplayName()?:"${column.id}", ${column.prefWidth?:"100"})"""()
                                            }
                                            WidgetType.HIDDEN -> throw  Xeption.forDeveloper("unsupported widget type ${widget.widgetType}" )
                                            WidgetType.TABLE_BOX -> throw Xeption.forDeveloper("table inside table is not supported")
                                        }
                                    }
                                }
                            }
                            else ->{
                                "${cell.id}Widget =  ${getWidgetClassName(widget)}"{
                                    """width = "100%""""()
                                }
                            }
                        }
                        if(cell.caption  != null){
                            """_node.addCell(com.gridnine.jasmine.web.server.components.ServerUiGridLayoutCell(com.gridnine.jasmine.web.server.widgets.ServerUiGridCellWidget(com.gridnine.jasmine.server.core.model.l10n.L10nMetaRegistry.get().webMessages["${description.id}"]?.messages?.get("${cell.id}")?.getDisplayName()?:"${cell.id}", ${cell.id}Widget),${cell.colSpan}))"""()
                        } else {
                            """_node.addCell(com.gridnine.jasmine.web.server.components.ServerUiGridLayoutCell(${cell.id}Widget,${cell.colSpan}))"""()
                        }
                    }
                }
                """com.gridnine.jasmine.web.server.components.ServerUiEditorInterceptorsRegistry.get().getInterceptors(this)?.forEach{it.onInit(this)}"""()
            }
            blankLine()
            "override fun setData(vm: ${description.id}VM, vs: ${description.id}VS?)" {
                description.rows.forEach { row ->
                    row.cells.forEach { cell ->
                        when (cell.widget) {
                            is HiddenWidgetDescription -> {
                                "${cell.id}Value = vm.${cell.id}"()
                            }
                            is TableBoxWidgetDescription -> {
                                "${cell.id}Widget.setData(vm.${cell.id}, vs?.${cell.id})"()
                            }
                            else -> {
                                "${cell.id}Widget.setValue(vm.${cell.id})"()
                                "${cell.id}Widget.configure(vs?.${cell.id})"()
                            }
                        }
                    }
                }
            }
            blankLine()
            "override fun setReadonly(value: Boolean)" {
                description.rows.forEach { row ->
                    row.cells.forEach cell@{ cell ->
                        if(cell.widget.widgetType == WidgetType.HIDDEN){
                            return@cell
                        }
                        "${cell.id}Widget.setReadonly(value)"()
                    }
                }
            }
            blankLine()
            "override fun getData(): ${description.id}VM" {
                "val result = ${description.id}VM()"()
                description.rows.forEach { row ->
                    row.cells.forEach { cell ->
                        when (val widget = cell.widget) {
                            is HiddenWidgetDescription -> {
                                "result.${cell.id} = ${cell.id}Value"()
                            }
                            is IntegerNumberBoxWidgetDescription -> {
                                "result.${cell.id} = ${cell.id}Widget.getValue()${if(widget.nonNullable)"!!" else ""}"()
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
            "override fun showValidation(validation: ${description.id}VV?) " {
                description.rows.forEach { row ->
                    row.cells.forEach cell@{ cell ->
                        if(cell.widget.widgetType == WidgetType.HIDDEN){
                            return@cell
                        }
                        "${cell.id}Widget.showValidation(validation?.${cell.id})"()
                    }
                }
            }
            blankLine()
            "override fun navigate(key: String): Boolean" {
                "return false"()
            }
        }
        val file = File(baseDir, "source-gen/${GenUtils.getPackageName(description.id).replace(".", File.separator)}/${GenUtils.getSimpleClassName(description.id)}.kt")
        GenUtils.writeContent(file, sb)
        generatedFiles.add(file)
    }

    private fun getWidgetClassName(widget: BaseWidgetDescription): String {
        return when(widget.widgetType){
            WidgetType.TEXT_BOX -> "com.gridnine.jasmine.web.server.widgets.ServerUiTextBoxWidget"
            WidgetType.PASSWORD_BOX -> "com.gridnine.jasmine.web.server.widgets.ServerUiPasswordBoxWidget"
            WidgetType.FLOAT_NUMBER_BOX -> "com.gridnine.jasmine.web.server.widgets.ServerUiBigDecimalBoxWidget"
            WidgetType.INTEGER_NUMBER_BOX -> "com.gridnine.jasmine.web.server.widgets.ServerUiIntBoxWidget"
            WidgetType.BOOLEAN_BOX -> "com.gridnine.jasmine.web.server.widgets.ServerUiBooleanBoxWidget"
            WidgetType.ENTITY_SELECT_BOX -> "com.gridnine.jasmine.web.server.widgets.ServerUiEntityValueWidget<${(widget as EntitySelectBoxWidgetDescription).objectId}>"
            WidgetType.GENERAL_SELECT_BOX -> "com.gridnine.jasmine.web.server.widgets.ServerUiGeneralSelectValueWidget"
            WidgetType.ENUM_SELECT_BOX -> "com.gridnine.jasmine.web.server.widgets.ServerUiEnumValueWidget<${(widget as EnumSelectBoxWidgetDescription).enumId}>"
            WidgetType.DATE_BOX -> "com.gridnine.jasmine.web.server.widgets.ServerUiDateBoxWidget"
            WidgetType.DATE_TIME_BOX -> "com.gridnine.jasmine.web.server.widgets.ServerUiDateTimeBoxWidget"
            WidgetType.TABLE_BOX -> {
                widget as TableBoxWidgetDescription
                "com.gridnine.jasmine.web.server.widgets.ServerUiTableWidget<${widget.id}VM,${widget.id}VS,${widget.id}VV>"
            }
            WidgetType.HIDDEN -> throw  Xeption.forDeveloper("unsupported widget type ${widget.widgetType}" )
        }
    }

}