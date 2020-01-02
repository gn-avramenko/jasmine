/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/
@file:Suppress("unused", "UNCHECKED_CAST")

package com.gridnine.jasmine.web.easyui

import com.gridnine.jasmine.web.core.model.ui.*
import com.gridnine.jasmine.web.core.utils.HtmlUtilsJS
import com.gridnine.jasmine.web.core.utils.ReflectionFactoryJS
import com.gridnine.jasmine.web.easyui.widgets.EasyUiPasswordBoxWidget
import com.gridnine.jasmine.web.easyui.widgets.EasyUiTextBoxWidget

object EasyUiViewBuilder {
    fun generateHtml(viewId: String, uid: String, expandToParent: Boolean, builder: HtmlUtilsJS.Div) {
        val viewDescr = UiMetaRegistryJS.get().views[viewId]
                ?: throw IllegalArgumentException("unable to load description for view $viewId")
        when (viewDescr) {
            is StandardViewDescriptionJS -> {
                when (val layout = viewDescr.layout) {
                    is TableLayoutDescriptionJS -> {
                        builder.table(style = if (expandToParent) "width:100%; height:100%" else null) {
                            tr {
                                layout.columns.forEach { column ->
                                    if ("remaining" == column.width) {
                                        td { }
                                    } else {
                                        td(style = "width:${column.width};height:1px") { }
                                    }
                                }
                            }
                            val rows = arrayListOf<TableTR>()
                            var lastRow = TableTR()
                            layout.widgets.values.forEach {
                                if(it is TableNextRowDescriptionJS){
                                    rows.add(lastRow)
                                    lastRow = TableTR()
                                    return@forEach
                                }
                                lastRow.tds.add(TableTD(it))
                            }
                            rows.add(lastRow)
                            val size = rows.size
                            for ((index, row) in rows.withIndex()) {
                                tr {
                                    val styleAttr = if (index == size - 1 && layout.expandLastRow) "height:100%;padding:5px;vertical-align: top" else "padding:5px"
                                    row.tds.withIndex().forEach { (idx, td) ->
                                        var tdStyleAttr = styleAttr
                                        when (val widget = td.widget) {
                                            is LabelDescriptionJS -> {
                                                tdStyleAttr += when (widget.verticalAlignment?: VerticalAlignmentJS.CENTER) {
                                                    VerticalAlignmentJS.TOP -> ";padding-top:10px;vertical-align: top"
                                                    VerticalAlignmentJS.CENTER -> ";vertical-align: center"
                                                    VerticalAlignmentJS.BOTTOM -> ";vertical-align: bottom"
                                                }
                                                tdStyleAttr += when (widget.horizontalAlignment?: HorizontalAlignmentJS.RIGHT) {
                                                    HorizontalAlignmentJS.LEFT -> ";align: left"
                                                    HorizontalAlignmentJS.CENTER -> ";align: center"
                                                    HorizontalAlignmentJS.RIGHT -> ";align: right"
                                                }
                                            }
                                        }
                                        val widthValue = layout.columns[idx].width
                                        td(style = tdStyleAttr, hSpan = td.widget.hSpan ?: 1) {
                                            when (val widget = td.widget) {
                                                is TableNextColumnDescriptionJS -> ""()
                                                is LabelDescriptionJS ->{
                                                    val textAlight = when (widget.horizontalAlignment?: HorizontalAlignmentJS.RIGHT) {
                                                        HorizontalAlignmentJS.LEFT -> "left"
                                                        HorizontalAlignmentJS.CENTER -> "center"
                                                        HorizontalAlignmentJS.RIGHT -> "right"
                                                    }
                                                    "<div class = \"jasmine-label\" style=\"text-align: ${textAlight};width:$widthValue%;\">${widget.displayName}<div>"()
                                                }
                                                is TextAreaDescriptionJS ->{
                                                    "<input id=\"${td.widget.id}${uid}\" style=\"width:$widthValue%;height:200px\">"()
                                                }
                                                is TableDescriptionJS ->{
                                                    "<table id=\"${td.widget.id}${uid}\"/>"()
                                                }
                                                else -> "<input id=\"${td.widget.id}${uid}\" style=\"width:$widthValue\">"()
                                            }
                                        }
                                    }
                                }
                            }
                            if (!layout.expandLastRow && expandToParent) {
                                tr {
                                    td(style = "height:100%;padding:5px") {
                                        ""()
                                    }
                                }
                            }
                        }
                    }
                    else -> throw IllegalArgumentException("unsupported layout type  ${layout::class.simpleName}")
                }
            }
            else -> throw IllegalArgumentException("unsupported view type  ${viewDescr::class.simpleName}")
        }
    }

    fun<VM:BaseVMEntityJS,VS:BaseVSEntityJS,VV:BaseVVEntityJS, V:BaseView<VM,VS,VV>> createView(viewId: String, uid:String):V {
        val view = ReflectionFactoryJS.get().getFactory(viewId).invoke() as V
        val viewDescr = UiMetaRegistryJS.get().views[viewId]
                ?: throw IllegalArgumentException("unable to load description for view $viewId")
        val widgetsDescriptions = arrayListOf<BaseWidgetDescriptionJS>()
        when (viewDescr) {
            is StandardViewDescriptionJS -> {
                when (val layout = viewDescr.layout) {
                    is TableLayoutDescriptionJS -> layout.widgets.values.forEach { widgetsDescriptions.add(it) }
                    else -> throw IllegalArgumentException("unsupported layout type  ${layout::class.simpleName}")
                }
            }
            else -> throw IllegalArgumentException("unsupported view type  ${viewDescr::class.simpleName}")
        }
        widgetsDescriptions.forEach {wd ->
            when(wd){
                is TextboxDescriptionJS -> view.setValue(wd.id, EasyUiTextBoxWidget(uid, wd))
                is PasswordBoxDescriptionJS -> view.setValue(wd.id, EasyUiPasswordBoxWidget(uid, wd))
                else -> {}
            }
        }
        view.readData = {model,settings ->
            widgetsDescriptions.forEach {
                when (it) {
                    is TextboxDescriptionJS -> {
                        (view.getValue(it.id) as TextBoxWidget).configure(Unit)
                        (view.getValue(it.id) as TextBoxWidget).setData(model.getValue(it.id) as String?)
                    }
                    is PasswordBoxDescriptionJS -> {
                        (view.getValue(it.id) as PasswordBoxWidget).configure(Unit)
                        (view.getValue(it.id) as PasswordBoxWidget).setData(model.getValue(it.id) as String?)
                    }
                    is TextAreaDescriptionJS -> {
                        (view.getValue(it.id) as TextAreaWidget).configure(Unit)
                        (view.getValue(it.id) as TextAreaWidget).setData(model.getValue(it.id) as String?)
                    }
                    is EnumSelectDescriptionJS -> {
                        (view.getValue(it.id) as EnumSelectWidget<*>).configure(settings.getValue(it.id).asDynamic())
                        (view.getValue(it.id) as EnumSelectWidget<*>).setData(model.getValue(it.id)?.asDynamic())
                    }
                    is EntityAutocompleteDescriptionJS -> {
                        (view.getValue(it.id) as EntityAutocompletetWidget).configure(settings.getValue(it.id).asDynamic())
                        (view.getValue(it.id) as EntityAutocompletetWidget).setData(model.getValue(it.id)?.asDynamic())
                    }
                    is FloatBoxDescriptionJS -> {
                        (view.getValue(it.id) as FloatBoxWidget).configure(Unit)
                        (view.getValue(it.id) as FloatBoxWidget).setData(model.getValue(it.id) as Double?)
                    }
                    is IntegerBoxDescriptionJS -> {
                        (view.getValue(it.id) as IntegerBoxWidget).configure(Unit)
                        (view.getValue(it.id) as IntegerBoxWidget).setData(model.getValue(it.id) as Int?)
                    }
                    is TableDescriptionJS -> {
                        (view.getValue(it.id) as TableWidget<BaseVMEntityJS,BaseVSEntityJS,BaseVVEntityJS>).configure(settings.getValue(it.id).asDynamic())
                        (view.getValue(it.id) as TableWidget<BaseVMEntityJS,BaseVSEntityJS,BaseVVEntityJS>).readData(model.getCollection(it.id).asDynamic())
                    }
                }
            }
        }
        return view
    }


    class TableTR(val tds: MutableList<TableTD> = arrayListOf())
    class TableTD(val widget: BaseWidgetDescriptionJS)


}