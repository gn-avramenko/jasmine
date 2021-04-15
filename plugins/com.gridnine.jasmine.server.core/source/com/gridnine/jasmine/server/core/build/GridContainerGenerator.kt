/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/
package com.gridnine.jasmine.server.core.build

import com.gridnine.jasmine.common.core.build.GenUtils
import com.gridnine.jasmine.common.core.meta.*
import com.gridnine.jasmine.common.core.model.Xeption
import com.gridnine.jasmine.server.core.ui.common.BaseNodeWrapper
import com.gridnine.jasmine.server.core.ui.common.ViewEditor
import com.gridnine.jasmine.server.core.ui.common.ViewEditorInterceptorsRegistry
import com.gridnine.jasmine.server.core.ui.components.GridLayoutCell
import com.gridnine.jasmine.server.core.ui.components.GridLayoutColumnConfiguration
import com.gridnine.jasmine.server.core.ui.components.GridLayoutContainer
import com.gridnine.jasmine.server.core.ui.components.UiLibraryAdapter
import com.gridnine.jasmine.server.core.ui.widgets.*
import java.io.File


internal object GridContainerGenerator {
    fun generateEditor(description: GridContainerDescription, baseDir: File, projectName: String, generatedFiles: MutableList<File>) {
        val sb = StringBuilder()
        GenUtils.generateHeader(sb, description.id, projectName)
        GenUtils.classBuilder(sb, "class ${GenUtils.getSimpleClassName(description.id)}:${ViewEditor::class.qualifiedName}<${description.id}VM,${description.id}VS,${description.id}VV>, ${BaseNodeWrapper::class.qualifiedName}<${GridLayoutContainer::class.qualifiedName}>()"){
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
                val columns = if(description.columns.isEmpty()){
                    val result = arrayListOf<GridContainerColumnDescription>()
                    for (n in 1..(description.columnsCount?:1)){
                        result.add(GridContainerColumnDescription(PredefinedColumnWidth.STANDARD, null))
                    }
                    result
                } else description.columns
                val fixedWidth = columns.map {
                    when(it.predefinedWidth){
                        PredefinedColumnWidth.STANDARD -> 300
                        PredefinedColumnWidth.REMAINING -> null
                        PredefinedColumnWidth.CUSTOM -> if(it.customWidth?.contains("px") == true) it.customWidth!!.substringBeforeLast("px").toInt() else null
                    }
                }.reduce{ c1, c2 -> if(c1 == null || c2 == null) null else c1+c2}
                "_node = ${UiLibraryAdapter::class.qualifiedName}.get().createGridLayoutContainer"{
                    if(fixedWidth != null){
                        """width="${fixedWidth}px""""()
                    }
                    columns.forEach { column ->
                        when(column.predefinedWidth){
                            PredefinedColumnWidth.STANDARD ->  """${GridLayoutColumnConfiguration::class.qualifiedName}("300px")"""()
                            PredefinedColumnWidth.REMAINING ->  """${GridLayoutColumnConfiguration::class.qualifiedName}("100%")"""()
                            PredefinedColumnWidth.CUSTOM ->  """${GridLayoutColumnConfiguration::class.qualifiedName}("${column.customWidth}")"""()
                        }
                    }
                }

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
                                    """handler = ${AutocompleteHandler::class.qualifiedName}.createMetadataBasedAutocompleteHandler("${widget.objectId}")"""()
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
                                                """column("${column.id}", ${TextBoxWidgetDescription::class.qualifiedName}(false), ${L10nMetaRegistry::class.qualifiedName}.get().bundles["${widget.id}"]?.messages?.get("${column.id}")?.getDisplayName()?:"${column.id}", ${column.prefWidth?:"100"})"""()
                                            }
                                            WidgetType.PASSWORD_BOX -> {
                                                colWidget as PasswordBoxWidgetDescription
                                                """column("${column.id}", ${PasswordBoxWidgetDescription::class.qualifiedName}(false), ${L10nMetaRegistry::class.qualifiedName}.get().bundles["${widget.id}"]?.messages?.get("${column.id}")?.getDisplayName()?:"${column.id}", ${column.prefWidth?:"100"})"""()
                                            }
                                            WidgetType.BIG_DECIMAL_NUMBER_BOX -> {
                                                colWidget as BigDecimalNumberBoxWidgetDescription
                                                """column("${column.id}",  ${BigDecimalNumberBoxWidgetDescription::class.qualifiedName}(false), ${L10nMetaRegistry::class.qualifiedName}.get().bundles["${widget.id}"]?.messages?.get("${column.id}")?.getDisplayName()?:"${column.id}", ${column.prefWidth?:"100"})"""()
                                            }
                                            WidgetType.INTEGER_NUMBER_BOX -> {
                                                colWidget as IntegerNumberBoxWidgetDescription
                                                """column("${column.id}", ${IntegerNumberBoxWidgetDescription::class.qualifiedName}(false,false), ${L10nMetaRegistry::class.qualifiedName}.get().bundles["${widget.id}"]?.messages?.get("${column.id}")?.getDisplayName()?:"${column.id}", ${column.prefWidth?:"100"})"""()
                                            }
                                            WidgetType.BOOLEAN_BOX -> {
                                                colWidget as BooleanBoxWidgetDescription
                                                """column("${column.id}", ${BooleanBoxWidgetDescription::class.qualifiedName}(false), ${L10nMetaRegistry::class.qualifiedName}.get().bundles["${widget.id}"]?.messages?.get("${column.id}")?.getDisplayName()?:"${column.id}", ${column.prefWidth?:"100"})"""()
                                            }
                                            WidgetType.ENTITY_SELECT_BOX ->  {
                                                colWidget as EntitySelectBoxWidgetDescription
                                                """column("${column.id}", ${EntitySelectBoxWidgetDescription::class.qualifiedName}(false, "${colWidget.objectId}"), ${L10nMetaRegistry::class.qualifiedName}.get().bundles["${widget.id}"]?.messages?.get("${column.id}")?.getDisplayName()?:"${column.id}", ${column.prefWidth?:"100"})"""()
                                            }
                                            WidgetType.GENERAL_SELECT_BOX -> {
                                                colWidget as GeneralSelectBoxWidgetDescription
                                                """column("${column.id}", ${GeneralSelectBoxWidgetDescription::class.qualifiedName}(false), ${L10nMetaRegistry::class.qualifiedName}.get().bundles["${widget.id}"]?.messages?.get("${column.id}")?.getDisplayName()?:"${column.id}", ${column.prefWidth?:"100"})"""()
                                            }
                                            WidgetType.ENUM_SELECT_BOX -> {
                                                colWidget as EnumSelectBoxWidgetDescription
                                                """column("${column.id}", ${EnumSelectBoxWidgetDescription::class.qualifiedName}(false, "${colWidget.enumId}"), ${L10nMetaRegistry::class.qualifiedName}.get().bundles["${widget.id}"]?.messages?.get("${column.id}")?.getDisplayName()?:"${column.id}", ${column.prefWidth?:"100"})"""()
                                            }
                                            WidgetType.DATE_BOX ->  {
                                                colWidget as DateBoxWidgetDescription
                                                """column("${column.id}", ${DateBoxWidgetDescription::class.qualifiedName}(false), ${L10nMetaRegistry::class.qualifiedName}.get().bundles["${widget.id}"]?.messages?.get("${column.id}")?.getDisplayName()?:"${column.id}", ${column.prefWidth?:"100"})"""()
                                            }
                                            WidgetType.DATE_TIME_BOX -> {
                                                colWidget as DateTimeBoxWidgetDescription
                                                """column("${column.id}", ${DateTimeBoxWidgetDescription::class.qualifiedName}(false), ${L10nMetaRegistry::class.qualifiedName}.get().bundles["${widget.id}"]?.messages?.get("${column.id}")?.getDisplayName()?:"${column.id}", ${column.prefWidth?:"100"})"""()
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
                            """_node.addCell(${GridLayoutCell::class.qualifiedName}(${GridCellWidget::class.qualifiedName}( ${L10nMetaRegistry::class.qualifiedName}.get().bundles["${description.id}"]?.messages?.get("${cell.id}")?.getDisplayName()?:"${cell.id}", ${cell.id}Widget),${cell.colSpan}))"""()
                        } else {
                            """_node.addCell(${GridLayoutCell::class.qualifiedName}(${cell.id}Widget,${cell.colSpan}))"""()
                        }
                    }
                }
                """${ViewEditorInterceptorsRegistry::class.qualifiedName}.get().getInterceptors(this)?.forEach{it.onInit(this)}"""()
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
            "override fun navigate(id: String): Boolean" {
                "return false"()
            }
        }
        val file = File(baseDir, "source-gen/${GenUtils.getPackageName(description.id).replace(".", File.separator)}/${GenUtils.getSimpleClassName(description.id)}.kt")
        GenUtils.writeContent(file, sb)
        generatedFiles.add(file)
    }

    private fun getWidgetClassName(widget: BaseWidgetDescription): String {
        return when(widget.widgetType){
            WidgetType.TEXT_BOX -> "${TextBoxWidget::class.qualifiedName}"
            WidgetType.PASSWORD_BOX -> "${PasswordBoxWidget::class.qualifiedName}"
            WidgetType.BIG_DECIMAL_NUMBER_BOX -> "${BigDecimalBoxWidget::class.qualifiedName}"
            WidgetType.INTEGER_NUMBER_BOX -> "${IntBoxWidget::class.qualifiedName}"
            WidgetType.BOOLEAN_BOX -> "${BooleanBoxWidget::class.qualifiedName}"
            WidgetType.ENTITY_SELECT_BOX -> "${EntityValueWidget::class.qualifiedName}<${(widget as EntitySelectBoxWidgetDescription).objectId}>"
            WidgetType.GENERAL_SELECT_BOX -> "${GeneralSelectBoxValueWidget::class.qualifiedName}"
            WidgetType.ENUM_SELECT_BOX -> "${EnumBoxValueWidget::class.qualifiedName}<${(widget as EnumSelectBoxWidgetDescription).enumId}>"
            WidgetType.DATE_BOX -> "${DateBoxWidget::class.qualifiedName}"
            WidgetType.DATE_TIME_BOX -> "${DateTimeBoxWidget::class.qualifiedName}"
            WidgetType.TABLE_BOX -> {
                widget as TableBoxWidgetDescription
                "${TableWidget::class.qualifiedName}<${widget.id}VM,${widget.id}VS,${widget.id}VV>"
            }
            WidgetType.HIDDEN -> throw  Xeption.forDeveloper("unsupported widget type ${widget.widgetType}" )
        }
    }

}