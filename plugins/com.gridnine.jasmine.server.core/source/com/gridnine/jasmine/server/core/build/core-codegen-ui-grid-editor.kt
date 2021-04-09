/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/
package com.gridnine.jasmine.server.core.build

import com.gridnine.jasmine.server.core.app.IApplicationMetadataProvider
import com.gridnine.jasmine.server.core.model.common.Xeption
import com.gridnine.jasmine.server.core.model.domain.*
import com.gridnine.jasmine.server.core.model.ui.*
import java.io.File
import java.lang.StringBuilder


object GridWebEditorGenerator {
    fun generateEditor(description: GridContainerDescription, baseDir: File, projectName: String, generatedFiles: MutableList<File>) {
        val sb = StringBuilder()
        GenUtils.generateHeader(sb, description.id, projectName)
        GenUtils.classBuilder(sb, "class ${GenUtils.getSimpleClassName(description.id)}(private val parent: com.gridnine.jasmine.web.core.ui.WebComponent?):com.gridnine.jasmine.web.core.ui.WebEditor<${description.id}VMJS,${description.id}VSJS,${description.id}VVJS>, com.gridnine.jasmine.web.core.ui.HasDivId"){
            blankLine()
            "private val delegate:com.gridnine.jasmine.web.core.ui.components.WebGridLayoutContainer"()
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
                "delegate = com.gridnine.jasmine.web.core.ui.UiLibraryAdapter.get().createGridLayoutContainer(this){}"()
                description.columns.forEach { column ->
                    when(column.predefinedWidth){
                        PredefinedColumnWidth.STANDARD -> "delegate.defineColumn(com.gridnine.jasmine.web.core.ui.DefaultUIParameters.controlWidthAsString)"()
                        PredefinedColumnWidth.REMAINING -> """delegate.defineColumn("100%")"""()
                        PredefinedColumnWidth.CUSTOM -> """delegate.defineColumn("${column.customWidth}")"""()
                    }
                }
                description.rows.forEach {row ->
                    when(row.predefinedHeight){
                        PredefinedRowHeight.AUTO -> "delegate.addRow()"()
                        PredefinedRowHeight.REMAINING -> "delegate.addRow(\"100%\")"()
                        PredefinedRowHeight.CUSTOM -> "delegate.addRow(${row.customHeight})"()
                    }
                    row.cells.forEach cell@{ cell ->
                        if(cell.widget.widgetType == WidgetType.HIDDEN){
                            return@cell
                        }
                        if(cell.caption  == null){
                            val widget = cell.widget
                            when(widget.widgetType){
                                WidgetType.TEXT_BOX -> {
                                    widget as TextBoxWidgetDescription
                                    "${cell.id}Widget = com.gridnine.jasmine.web.core.ui.widgets.TextBoxWidget(delegate)"{
                                        """width = "100%""""()
                                    }
                                }
                                WidgetType.PASSWORD_BOX -> {
                                    widget as PasswordBoxWidgetDescription
                                    "${cell.id}Widget = com.gridnine.jasmine.web.core.ui.widgets.PasswordBoxWidget(delegate)"{
                                        """width = "100%""""()
                                    }
                                }
                                WidgetType.FLOAT_NUMBER_BOX -> {
                                    widget as FloatNumberBoxWidgetDescription
                                    "${cell.id}Widget = com.gridnine.jasmine.web.core.ui.widgets.FloatNumberBoxWidget(delegate)"{
                                        """width = "100%""""()
                                    }
                                }
                                WidgetType.INTEGER_NUMBER_BOX -> {
                                    widget as IntegerNumberBoxWidgetDescription
                                    "${cell.id}Widget = com.gridnine.jasmine.web.core.ui.widgets.IntegerNumberBoxWidget(delegate)"{
                                        """width = "100%""""()
                                    }
                                }
                                WidgetType.BOOLEAN_BOX -> {
                                    widget as BooleanBoxWidgetDescription
                                    "${cell.id}Widget = com.gridnine.jasmine.web.core.ui.widgets.BooleanBoxWidget(delegate)"{
                                        """width = "100%""""()
                                    }
                                }
                                WidgetType.ENTITY_SELECT_BOX -> {
                                    widget as EntitySelectBoxWidgetDescription
                                    "${cell.id}Widget = com.gridnine.jasmine.web.core.ui.widgets.EntitySelectWidget(delegate)"{
                                        """width = "100%""""()
                                        """handler = com.gridnine.jasmine.web.core.ui.ClientRegistry.get().get(com.gridnine.jasmine.web.core.ui.ObjectHandler.TYPE, "${widget.objectId}JS")!!.getAutocompleteHandler()"""()
                                    }
                                }
                                WidgetType.GENERAL_SELECT_BOX -> {
                                    widget as GeneralSelectBoxWidgetDescription
                                    "${cell.id}Widget = com.gridnine.jasmine.web.core.ui.widgets.GeneralSelectWidget(delegate)"{
                                        """width = "100%""""()
                                    }
                                }
                                WidgetType.ENUM_SELECT_BOX -> {
                                    widget as EnumSelectBoxWidgetDescription
                                    "${cell.id}Widget = com.gridnine.jasmine.web.core.ui.widgets.EnumValueWidget<${widget.enumId}JS>(delegate)"{
                                        """width = "100%""""()
                                        "enumClass = ${widget.enumId}JS::class"()
                                    }
                                }
                                WidgetType.DATE_BOX -> {
                                    widget as DateBoxWidgetDescription
                                    "${cell.id}Widget = com.gridnine.jasmine.web.core.ui.widgets.DateBoxWidget(delegate)"{
                                        """width = "100%""""()
                                    }
                                }
                                WidgetType.DATE_TIME_BOX -> {
                                    widget as DateTimeBoxWidgetDescription
                                    "${cell.id}Widget = com.gridnine.jasmine.web.core.ui.widgets.DateTimeBoxWidget(delegate)"{
                                        """width = "100%""""()
                                    }
                                }
                                WidgetType.HIDDEN -> TODO()
                                WidgetType.TABLE_BOX -> {
                                    widget as TableBoxWidgetDescription
                                    "${cell.id}Widget = com.gridnine.jasmine.web.core.ui.widgets.TableBoxWidget<${widget.id}VMJS,${widget.id}VSJS,${widget.id}VVJS>(delegate)"{
                                        """width = "100%""""()
                                        "showToolsColumn = true"()
                                        "vmFactory = {${widget.id}VMJS()}"()
                                        "vsFactory = {${widget.id}VSJS()}"()
                                        widget.columns.forEach { column ->
                                            val colWidget = column.widget
                                            when(colWidget.widgetType){
                                                WidgetType.TEXT_BOX -> {
                                                    colWidget as TextBoxWidgetDescription
                                                    """column("${column.id}", com.gridnine.jasmine.server.core.model.ui.TextBoxWidgetDescriptionJS(false), com.gridnine.jasmine.server.core.model.l10n.L10nMetaRegistryJS.get().messages["${widget.id}"]?.get("${column.id}")?:"${column.id}", ${column.prefWidth?:"100"})"""()
                                                }
                                                WidgetType.PASSWORD_BOX -> {
                                                    colWidget as PasswordBoxWidgetDescription
                                                    """column("${column.id}", com.gridnine.jasmine.server.core.model.ui.PasswordBoxWidgetDescriptionJS(false), com.gridnine.jasmine.server.core.model.l10n.L10nMetaRegistryJS.get().messages["${widget.id}"]?.get("${column.id}")?:"${column.id}", ${column.prefWidth?:"100"})"""()
                                                }
                                                WidgetType.FLOAT_NUMBER_BOX -> {
                                                    colWidget as FloatNumberBoxWidgetDescription
                                                    """column("${column.id}", com.gridnine.jasmine.server.core.model.ui.FloatNumberBoxWidgetDescriptionJS(false), com.gridnine.jasmine.server.core.model.l10n.L10nMetaRegistryJS.get().messages["${widget.id}"]?.get("${column.id}")?:"${column.id}", ${column.prefWidth?:"100"})"""()
                                                }
                                                WidgetType.INTEGER_NUMBER_BOX -> {
                                                    colWidget as IntegerNumberBoxWidgetDescription
                                                    """column("${column.id}", com.gridnine.jasmine.server.core.model.ui.IntegerNumberBoxWidgetDescriptionJS(false,false), com.gridnine.jasmine.server.core.model.l10n.L10nMetaRegistryJS.get().messages["${widget.id}"]?.get("${column.id}")?:"${column.id}", ${column.prefWidth?:"100"})"""()
                                                }
                                                WidgetType.BOOLEAN_BOX -> {
                                                    colWidget as BooleanBoxWidgetDescription
                                                    """column("${column.id}", com.gridnine.jasmine.server.core.model.ui.BooleanBoxWidgetDescriptionJS(false), com.gridnine.jasmine.server.core.model.l10n.L10nMetaRegistryJS.get().messages["${widget.id}"]?.get("${column.id}")?:"${column.id}", ${column.prefWidth?:"100"})"""()
                                                }
                                                WidgetType.ENTITY_SELECT_BOX ->  {
                                                    colWidget as EntitySelectBoxWidgetDescription
                                                    """column("${column.id}", com.gridnine.jasmine.server.core.model.ui.EntitySelectBoxWidgetDescriptionJS(false, "${colWidget.objectId}JS"), com.gridnine.jasmine.server.core.model.l10n.L10nMetaRegistryJS.get().messages["${widget.id}"]?.get("${column.id}")?:"${column.id}", ${column.prefWidth?:"100"})"""()
                                                }
                                                WidgetType.GENERAL_SELECT_BOX -> {
                                                    colWidget as GeneralSelectBoxWidgetDescription
                                                    """column("${column.id}", com.gridnine.jasmine.server.core.model.ui.GeneralSelectBoxWidgetDescriptionJS(false), com.gridnine.jasmine.server.core.model.l10n.L10nMetaRegistryJS.get().messages["${widget.id}"]?.get("${column.id}")?:"${column.id}", ${column.prefWidth?:"100"})"""()
                                                }
                                                WidgetType.ENUM_SELECT_BOX -> {
                                                    colWidget as EnumSelectBoxWidgetDescription
                                                    """column("${column.id}", com.gridnine.jasmine.server.core.model.ui.EnumSelectBoxWidgetDescriptionJS(false, "${colWidget.enumId}JS"), com.gridnine.jasmine.server.core.model.l10n.L10nMetaRegistryJS.get().messages["${widget.id}"]?.get("${column.id}")?:"${column.id}", ${column.prefWidth?:"100"})"""()
                                                }
                                                WidgetType.DATE_BOX ->  {
                                                    colWidget as DateBoxWidgetDescription
                                                    """column("${column.id}", com.gridnine.jasmine.server.core.model.ui.DateBoxWidgetDescriptionJS(false), com.gridnine.jasmine.server.core.model.l10n.L10nMetaRegistryJS.get().messages["${widget.id}"]?.get("${column.id}")?:"${column.id}", ${column.prefWidth?:"100"})"""()
                                                }
                                                WidgetType.DATE_TIME_BOX -> {
                                                    colWidget as DateTimeBoxWidgetDescription
                                                    """column("${column.id}", com.gridnine.jasmine.server.core.model.ui.DateTimeBoxWidgetDescriptionJS(false), com.gridnine.jasmine.server.core.model.l10n.L10nMetaRegistryJS.get().messages["${widget.id}"]?.get("${column.id}")?:"${column.id}", ${column.prefWidth?:"100"})"""()
                                                }
                                                WidgetType.HIDDEN -> throw  Xeption.forDeveloper("unsupported widget type ${widget.widgetType}" )
                                                WidgetType.TABLE_BOX -> throw Xeption.forDeveloper("table inside table is not supported")
                                            }
                                        }
                                    }
                                }
                            }
                            "delegate.addCell(com.gridnine.jasmine.web.core.ui.components.WebGridLayoutCell(${cell.id}Widget,${cell.colSpan}))"()
                            return@cell
                        }
                        """val ${cell.id}Cell = com.gridnine.jasmine.web.core.ui.widgets.GridCellWidget(delegate, com.gridnine.jasmine.server.core.model.l10n.L10nMetaRegistryJS.get().messages["${description.id}"]?.get("${cell.id}")?:"${cell.id}")"""("par"){
                            val widget = cell.widget
                             when(widget.widgetType){
                                 WidgetType.TEXT_BOX -> {
                                     widget as TextBoxWidgetDescription
                                     "com.gridnine.jasmine.web.core.ui.widgets.TextBoxWidget(par)"{
                                         """width = "100%""""()
                                     }
                                 }
                                 WidgetType.PASSWORD_BOX -> {
                                     widget as PasswordBoxWidgetDescription
                                     "com.gridnine.jasmine.web.core.ui.widgets.PasswordBoxWidget(par)"{
                                         """width = "100%""""()
                                     }
                                 }
                                 WidgetType.FLOAT_NUMBER_BOX -> {
                                     widget as FloatNumberBoxWidgetDescription
                                     "com.gridnine.jasmine.web.core.ui.widgets.FloatNumberBoxWidget(par)"{
                                         """width = "100%""""()
                                     }
                                 }
                                 WidgetType.INTEGER_NUMBER_BOX -> {
                                     widget as IntegerNumberBoxWidgetDescription
                                     "com.gridnine.jasmine.web.core.ui.widgets.IntegerNumberBoxWidget(par)"{
                                         """width = "100%""""()
                                     }
                                 }
                                 WidgetType.BOOLEAN_BOX -> {
                                     widget as BooleanBoxWidgetDescription
                                     "com.gridnine.jasmine.web.core.ui.widgets.BooleanBoxWidget(par)"{
                                         """width = "100%""""()
                                     }
                                 }
                                 WidgetType.ENTITY_SELECT_BOX -> {
                                     widget as EntitySelectBoxWidgetDescription
                                     "com.gridnine.jasmine.web.core.ui.widgets.EntitySelectWidget(par)"{
                                         """width = "100%""""()
                                         """handler = com.gridnine.jasmine.web.core.ui.ClientRegistry.get().get(com.gridnine.jasmine.web.core.ui.ObjectHandler.TYPE, "${widget.objectId}JS")!!.getAutocompleteHandler()"""()
                                     }
                                 }
                                 WidgetType.GENERAL_SELECT_BOX -> {
                                     widget as GeneralSelectBoxWidgetDescription
                                     "com.gridnine.jasmine.web.core.ui.widgets.GeneralSelectWidget(par)"{
                                         """width = "100%""""()
                                     }
                                 }
                                 WidgetType.ENUM_SELECT_BOX -> {
                                     widget as EnumSelectBoxWidgetDescription
                                     "com.gridnine.jasmine.web.core.ui.widgets.EnumValueWidget<${widget.enumId}JS>(par)"{
                                         """width = "100%""""()
                                         "enumClass = ${widget.enumId}JS::class"()
                                     }
                                 }
                                 WidgetType.DATE_BOX -> {
                                     widget as DateBoxWidgetDescription
                                     "com.gridnine.jasmine.web.core.ui.widgets.DateBoxWidget(par)"{
                                         """width = "100%""""()
                                     }
                                 }
                                 WidgetType.DATE_TIME_BOX -> {
                                     widget as DateTimeBoxWidgetDescription
                                     "com.gridnine.jasmine.web.core.ui.widgets.DateTimeBoxWidget(par)"{
                                         """width = "100%""""()
                                     }
                                 }
                                 WidgetType.HIDDEN -> TODO()
                                 WidgetType.TABLE_BOX -> {
                                     widget as TableBoxWidgetDescription
                                     "com.gridnine.jasmine.web.core.ui.widgets.TableBoxWidget<${widget.id}VMJS,${widget.id}VSJS,${widget.id}VVJS>(par)"{
                                         """width = "100%""""()
                                         "showToolsColumn = true"()
                                         "vmFactory = {${widget.id}VMJS()}"()
                                         "vsFactory = {${widget.id}VSJS()}"()
                                         widget.columns.forEach { column ->
                                             val colWidget = column.widget
                                             when(colWidget.widgetType){
                                                 WidgetType.TEXT_BOX -> {
                                                     colWidget as TextBoxWidgetDescription
                                                    """column("${column.id}", com.gridnine.jasmine.server.core.model.ui.TextBoxWidgetDescriptionJS(false), com.gridnine.jasmine.server.core.model.l10n.L10nMetaRegistryJS.get().messages["${widget.id}"]?.get("${column.id}")?:"${column.id}", ${column.prefWidth?:"100"})"""()
                                                 }
                                                 WidgetType.PASSWORD_BOX -> {
                                                     colWidget as PasswordBoxWidgetDescription
                                                     """column("${column.id}", com.gridnine.jasmine.server.core.model.ui.PasswordBoxWidgetDescriptionJS(false), com.gridnine.jasmine.server.core.model.l10n.L10nMetaRegistryJS.get().messages["${widget.id}"]?.get("${column.id}")?:"${column.id}", ${column.prefWidth?:"100"})"""()
                                                 }
                                                 WidgetType.FLOAT_NUMBER_BOX -> {
                                                     colWidget as FloatNumberBoxWidgetDescription
                                                     """column("${column.id}", com.gridnine.jasmine.server.core.model.ui.FloatNumberBoxWidgetDescriptionJS(false), com.gridnine.jasmine.server.core.model.l10n.L10nMetaRegistryJS.get().messages["${widget.id}"]?.get("${column.id}")?:"${column.id}", ${column.prefWidth?:"100"})"""()
                                                 }
                                                 WidgetType.INTEGER_NUMBER_BOX -> {
                                                     colWidget as IntegerNumberBoxWidgetDescription
                                                     """column("${column.id}", com.gridnine.jasmine.server.core.model.ui.IntegerNumberBoxWidgetDescriptionJS(false,false), com.gridnine.jasmine.server.core.model.l10n.L10nMetaRegistryJS.get().messages["${widget.id}"]?.get("${column.id}")?:"${column.id}", ${column.prefWidth?:"100"})"""()
                                                 }
                                                 WidgetType.BOOLEAN_BOX -> {
                                                     colWidget as BooleanBoxWidgetDescription
                                                     """column("${column.id}", com.gridnine.jasmine.server.core.model.ui.BooleanBoxWidgetDescriptionJS(false), com.gridnine.jasmine.server.core.model.l10n.L10nMetaRegistryJS.get().messages["${widget.id}"]?.get("${column.id}")?:"${column.id}", ${column.prefWidth?:"100"})"""()
                                                 }
                                                 WidgetType.ENTITY_SELECT_BOX ->  {
                                                     colWidget as EntitySelectBoxWidgetDescription
                                                     """column("${column.id}", com.gridnine.jasmine.server.core.model.ui.EntitySelectBoxWidgetDescriptionJS(false, "${colWidget.objectId}JS"), com.gridnine.jasmine.server.core.model.l10n.L10nMetaRegistryJS.get().messages["${widget.id}"]?.get("${column.id}")?:"${column.id}", ${column.prefWidth?:"100"})"""()
                                                 }
                                                 WidgetType.GENERAL_SELECT_BOX -> {
                                                     colWidget as GeneralSelectBoxWidgetDescription
                                                     """column("${column.id}", com.gridnine.jasmine.server.core.model.ui.GeneralSelectBoxWidgetDescriptionJS(false), com.gridnine.jasmine.server.core.model.l10n.L10nMetaRegistryJS.get().messages["${widget.id}"]?.get("${column.id}")?:"${column.id}", ${column.prefWidth?:"100"})"""()
                                                 }
                                                 WidgetType.ENUM_SELECT_BOX -> {
                                                     colWidget as EnumSelectBoxWidgetDescription
                                                     """column("${column.id}", com.gridnine.jasmine.server.core.model.ui.EnumSelectBoxWidgetDescriptionJS(false, "${colWidget.enumId}JS"), com.gridnine.jasmine.server.core.model.l10n.L10nMetaRegistryJS.get().messages["${widget.id}"]?.get("${column.id}")?:"${column.id}", ${column.prefWidth?:"100"})"""()
                                                 }
                                                 WidgetType.DATE_BOX ->  {
                                                     colWidget as DateBoxWidgetDescription
                                                     """column("${column.id}", com.gridnine.jasmine.server.core.model.ui.DateBoxWidgetDescriptionJS(false), com.gridnine.jasmine.server.core.model.l10n.L10nMetaRegistryJS.get().messages["${widget.id}"]?.get("${column.id}")?:"${column.id}", ${column.prefWidth?:"100"})"""()
                                                 }
                                                 WidgetType.DATE_TIME_BOX -> {
                                                     colWidget as DateTimeBoxWidgetDescription
                                                     """column("${column.id}", com.gridnine.jasmine.server.core.model.ui.DateTimeBoxWidgetDescriptionJS(false), com.gridnine.jasmine.server.core.model.l10n.L10nMetaRegistryJS.get().messages["${widget.id}"]?.get("${column.id}")?:"${column.id}", ${column.prefWidth?:"100"})"""()
                                                 }
                                                 WidgetType.HIDDEN -> throw  Xeption.forDeveloper("unsupported widget type ${widget.widgetType}" )
                                                 WidgetType.TABLE_BOX -> throw Xeption.forDeveloper("table inside table is not supported")
                                             }
                                         }
                                     }
                                 }
                             }
                        }
                        "delegate.addCell(com.gridnine.jasmine.web.core.ui.components.WebGridLayoutCell(${cell.id}Cell,${cell.colSpan}))"()
                        "${cell.id}Widget = ${cell.id}Cell.widget"()
                    }
                }
            }
            blankLine()
            "override fun readData(vm: ${description.id}VMJS, vs: ${description.id}VSJS)" {
                description.rows.forEach { row ->
                    row.cells.forEach { cell ->
                        when (cell.widget) {
                            is HiddenWidgetDescription -> {
                                "${cell.id}Value = vm.${cell.id}"()
                            }
                            is TableBoxWidgetDescription -> {
                                "${cell.id}Widget.readData(vm.${cell.id}, vs.${cell.id})"()
                            }
                            else -> {
                                "${cell.id}Widget.setValue(vm.${cell.id})"()
                                "vs.${cell.id}?.let{${cell.id}Widget.configure(it)}"()
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
            "override fun getParent(): com.gridnine.jasmine.web.core.ui.WebComponent?" {
                "return parent"()
            }
            blankLine()
            "override fun destroy()"{
                "delegate.destroy()"()
            }
            blankLine()
            "override fun getData(): ${description.id}VMJS" {
                "val result = ${description.id}VMJS()"()
                description.rows.forEach { row ->
                    row.cells.forEach { cell ->
                        val widget = cell.widget
                        when (widget) {
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
            "override fun getChildren(): List<com.gridnine.jasmine.web.core.ui.WebComponent>" {
                "return arrayListOf(delegate)"()
            }
            blankLine()
            "override fun getHtml(): String" {
                "return delegate.getHtml()"()
            }
            blankLine()
            "override fun decorate()" {
                "delegate.decorate()"()
            }
            blankLine()
            "override fun showValidation(validation: ${description.id}VVJS) " {
                description.rows.forEach { row ->
                    row.cells.forEach cell@{ cell ->
                        if(cell.widget.widgetType == WidgetType.HIDDEN){
                            return@cell
                        }
                        "validation.${cell.id}?.let{${cell.id}Widget.showValidation(it)}"()
                    }
                }
            }
            blankLine()
            "override fun getId(): String" {
                "return delegate.getId()"()
            }
        }
        val file = File(baseDir, "source-gen/${GenUtils.getPackageName(description.id).replace(".", File.separator)}/${GenUtils.getSimpleClassName(description.id)}.kt")
        GenUtils.writeContent(file, sb)
        generatedFiles.add(file)
    }

    private fun getWidgetClassName(widget: BaseWidgetDescription): String {
        return when(widget.widgetType){
            WidgetType.TEXT_BOX -> "com.gridnine.jasmine.web.core.ui.widgets.TextBoxWidget"
            WidgetType.PASSWORD_BOX -> "com.gridnine.jasmine.web.core.ui.widgets.PasswordBoxWidget"
            WidgetType.FLOAT_NUMBER_BOX -> "com.gridnine.jasmine.web.core.ui.widgets.FloatNumberBoxWidget"
            WidgetType.INTEGER_NUMBER_BOX -> "com.gridnine.jasmine.web.core.ui.widgets.IntegerNumberBoxWidget"
            WidgetType.BOOLEAN_BOX -> "com.gridnine.jasmine.web.core.ui.widgets.BooleanBoxWidget"
            WidgetType.ENTITY_SELECT_BOX -> "com.gridnine.jasmine.web.core.ui.widgets.EntitySelectWidget"
            WidgetType.GENERAL_SELECT_BOX -> "com.gridnine.jasmine.web.core.ui.widgets.GeneralSelectWidget"
            WidgetType.ENUM_SELECT_BOX -> "com.gridnine.jasmine.web.core.ui.widgets.EnumValueWidget<${(widget as EnumSelectBoxWidgetDescription).enumId}JS>"
            WidgetType.DATE_BOX -> "com.gridnine.jasmine.web.core.ui.widgets.DateBoxWidget"
            WidgetType.DATE_TIME_BOX -> "com.gridnine.jasmine.web.core.ui.widgets.DateTimeBoxWidget"
            WidgetType.TABLE_BOX -> {
                widget as TableBoxWidgetDescription
                "com.gridnine.jasmine.web.core.ui.widgets.TableBoxWidget<${widget.id}VMJS,${widget.id}VSJS,${widget.id}VVJS>"
            }
            WidgetType.HIDDEN -> throw  Xeption.forDeveloper("unsupported widget type ${widget.widgetType}" )
        }
    }

}