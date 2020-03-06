/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/
@file:Suppress("unused", "UNCHECKED_CAST", "UnsafeCastFromDynamic")

package com.gridnine.jasmine.web.easyui.utils

import com.gridnine.jasmine.web.core.model.common.FakeEnumJS
import com.gridnine.jasmine.web.core.model.ui.*
import com.gridnine.jasmine.web.core.model.ui.widgets.*
import com.gridnine.jasmine.web.core.utils.HtmlUtilsJS
import com.gridnine.jasmine.web.core.utils.ReflectionFactoryJS
import com.gridnine.jasmine.web.easyui.widgets.*
import kotlin.js.Date

object EasyUiViewBuilder {
    fun generateHtml(viewId: String, uid: String, expandToParent: Boolean, builder: HtmlUtilsJS.BaseDiv) {
        val viewDescr = UiMetaRegistryJS.get().views[viewId]
                ?: throw IllegalArgumentException("unable to load description for view $viewId")
        when (viewDescr) {
            is StandardViewDescriptionJS -> {
                when (val layout = viewDescr.layout) {
                    is TableLayoutDescriptionJS -> {
                        builder.table(style = if (expandToParent) "width:100%; height:100%" else "width:100%") {
                            tr {
                                layout.columns.forEach { column ->
                                    if ("remaining" == column.width) {
                                        td { }
                                    } else {
                                        td(style = "width:${column.width}px;height:1px") { }
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
                                    var currentIdx = 0
                                    row.tds.forEach { td ->
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
                                        var hasRemaining = false
                                        var width = 0
                                        for(n in 0 until (td.widget.hSpan?:1)){
                                            val columnWidth = layout.columns[currentIdx+n].width
                                            if(columnWidth == "remaining"){
                                                hasRemaining = true
                                            } else {
                                                width+=columnWidth?.toInt()?:0
                                            }
                                        }
                                        val widthValue = if(hasRemaining) "100%" else "${width}px"

                                        td(style = tdStyleAttr, hSpan = td.widget.hSpan ?: 1) {
                                            when (val widget = td.widget) {
                                                is TableNextColumnDescriptionJS -> ""()
                                                is LabelDescriptionJS ->{
                                                    val textAlight = when (widget.horizontalAlignment?: HorizontalAlignmentJS.RIGHT) {
                                                        HorizontalAlignmentJS.LEFT -> "left"
                                                        HorizontalAlignmentJS.CENTER -> "center"
                                                        HorizontalAlignmentJS.RIGHT -> "right"
                                                    }
                                                    "<div class = \"jasmine-label\" style=\"text-align: ${textAlight};width:${widthValue};\">${widget.displayName}<div>"()
                                                }
                                                is TextAreaDescriptionJS ->{
                                                    "<input id=\"${td.widget.id}${uid}\" style=\"width:${widthValue};height:200px\">"()
                                                }
                                                is TableDescriptionJS ->{
                                                    "<table id=\"${td.widget.id}${uid}\"/>"()
                                                }
                                                is TileDescriptionJS ->{
                                                    div (id="${td.widget.id}${uid}", style = "width:100%;padding:5px"){
                                                        generateHtml(widget.compactViewId, "${widget.id}-compact-${uid}", false, this)
                                                    }
                                                }
                                                is NavigatorDescriptionJS ->{
                                                    div (id="${td.widget.id}${uid}", style = "width:100%;height:100%"){
                                                        table (style="width:100%;height:100%"){
                                                            tr (style="height:20px"){
                                                                td{
                                                                    input(id="${td.widget.id}Select${uid}", style = "width:100%")
                                                                }
                                                            }
                                                            tr {
                                                                td{
                                                                    div(id="${td.widget.id}Panel${uid}", style = "width:100%;height:100%"){}
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                                else -> "<input id=\"${td.widget.id}${uid}\" style=\"width:${widthValue}px\">"()

                                            }
                                        }
                                        currentIdx+=td.widget.hSpan?:1
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

    fun<VM:BaseVMEntityJS,VS:BaseVSEntityJS,VV:BaseVVEntityJS, V:BaseView<VM,VS,VV>> createView(viewId: String, uid:String, createProxy:Boolean = false):V {
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
        val wdsIds = arrayListOf<String>()
        widgetsDescriptions.forEach {wd ->
            when(wd){
                is TextboxDescriptionJS -> {
                    view.setValue(wd.id, if (createProxy) ProxyTextBoxWidget() else EasyUiTextBoxWidget(uid, wd))
                    wdsIds.add(wd.id)
                }
                is PasswordBoxDescriptionJS -> {
                    view.setValue(wd.id, if (createProxy) ProxyPasswordBoxWidget() else EasyUiPasswordBoxWidget(uid, wd))
                    wdsIds.add(wd.id)
                }
                is TileDescriptionJS -> {
                    view.setValue(wd.id, EasyUiTileWidget<BaseView<BaseVMEntityJS, BaseVSEntityJS, BaseVVEntityJS>, BaseView<BaseVMEntityJS, BaseVSEntityJS, BaseVVEntityJS>>(uid, wd))
                    wdsIds.add(wd.id)
                }
                is DateboxDescriptionJS -> {
                    view.setValue(wd.id, if (createProxy) ProxyDateBoxWidget() else EasyUiDateBoxWidget(uid, wd))
                    wdsIds.add(wd.id)
                }
                is DateTimeBoxDescriptionJS -> {
                    view.setValue(wd.id, if(createProxy) ProxyDateTimeBoxWidget() else  EasyUiDateTimeBoxWidget(uid, wd))
                    wdsIds.add(wd.id)
                }
                is EnumSelectDescriptionJS -> {
                    view.setValue(wd.id, if(createProxy) ProxyEnumSelectWidget<FakeEnumJS>() else  EasyUiEnumSelectWidget<FakeEnumJS>(uid, wd))
                    wdsIds.add(wd.id)
                }
                is EntitySelectDescriptionJS ->{
                    view.setValue(wd.id, if(createProxy) ProxyEntitySelectWidget() else  EasyUiEntitySelectWidget(uid, wd))
                    wdsIds.add(wd.id)
                }
                is FloatBoxDescriptionJS -> {
                    view.setValue(wd.id, if(createProxy) ProxyFloatBoxWidget() else  EasyUiFloatBoxWidget(uid, wd))
                    wdsIds.add(wd.id)
                }
                is IntegerBoxDescriptionJS ->{
                    view.setValue(wd.id, if(createProxy) ProxyIntBoxWidget() else  EasyUiIntBoxWidget(uid, wd))
                    wdsIds.add(wd.id)
                }
                is SelectDescriptionJS -> {
                    view.setValue(wd.id, if(createProxy) ProxySelectWidget() else  EasyUiSelectWidget(uid, wd))
                    wdsIds.add(wd.id)
                }
                is BooleanBoxDescriptionJS -> {
                    view.setValue(wd.id, if(createProxy) ProxyBooleanBoxWidget() else  EasyUiBooleanBoxWidget(uid, wd))
                    wdsIds.add(wd.id)
                }
                is TableDescriptionJS -> {
                    view.setValue(wd.id, if(createProxy) ProxyTableWidget<BaseVMEntityJS,BaseVSEntityJS,BaseVVEntityJS>() else  EasyUiTableWidget<BaseVMEntityJS,BaseVSEntityJS,BaseVVEntityJS>(uid, wd))
                    wdsIds.add(wd.id)
                }
                is NavigatorDescriptionJS -> {
                    view.setValue(wd.id, if(createProxy) ProxyNavigatorWidget<BaseVMEntityJS,BaseVSEntityJS,BaseVVEntityJS>() else  EasyUiNavigatorWidget<BaseVMEntityJS,BaseVSEntityJS,BaseVVEntityJS>(uid, wd))
                    wdsIds.add(wd.id)
                }
                else -> {}

            }
        }

        wdsIds.forEach {id ->
            val widget = view.getValue(id)
            if(widget is WidgetWithParent){
                widget.parent = view
            }
        }

        var modelUid :String? = null
        view.readData = {model ->
            modelUid = model.uid
            widgetsDescriptions.forEach {
                when (it) {
                    is TextboxDescriptionJS -> {
                        (view.getValue(it.id) as TextBoxWidget).setData(model.getValue(it.id) as String?)
                    }
                    is PasswordBoxDescriptionJS -> {
                        (view.getValue(it.id) as PasswordBoxWidget).setData(model.getValue(it.id) as String?)
                    }
                    is TextAreaDescriptionJS -> {
                        (view.getValue(it.id) as TextAreaWidget).setData(model.getValue(it.id) as String?)
                    }
                    is EnumSelectDescriptionJS -> {
                        (view.getValue(it.id) as EnumSelectWidget<*>).setData(model.getValue(it.id)?.asDynamic())
                    }
                    is SelectDescriptionJS -> {
                        (view.getValue(it.id) as SelectWidget).setData(model.getValue(it.id).asDynamic())
                    }
                    is EntitySelectDescriptionJS -> {
                        (view.getValue(it.id) as EntitySelectWidget).setData(model.getValue(it.id)?.asDynamic())
                    }
                    is FloatBoxDescriptionJS -> {
                        (view.getValue(it.id) as FloatBoxWidget).setData(model.getValue(it.id) as Double?)
                    }
                    is IntegerBoxDescriptionJS -> {
                        (view.getValue(it.id) as IntegerBoxWidget).setData(model.getValue(it.id) as Int?)
                    }
                    is DateboxDescriptionJS -> {
                        (view.getValue(it.id) as DateBoxWidget).setData(model.getValue(it.id) as Date?)
                    }
                    is DateTimeBoxDescriptionJS -> {
                        (view.getValue(it.id) as DateTimeBoxWidget).setData(model.getValue(it.id) as Date?)
                    }
                    is BooleanBoxDescriptionJS -> {
                        (view.getValue(it.id) as BooleanBoxWidget).setData(model.getValue(it.id) as Boolean?)
                    }
                    is TableDescriptionJS -> {
                        (view.getValue(it.id) as TableWidget<BaseVMEntityJS,BaseVSEntityJS,BaseVVEntityJS>).readData(model.getCollection(it.id).asDynamic())
                    }
                    is TileDescriptionJS -> {
                        (view.getValue(it.id) as TileWidget<*,*>).setData(model.getValue(it.id).asDynamic())
                    }
                    is NavigatorDescriptionJS -> {
                        (view.getValue(it.id) as NavigatorWidget<*,*,*>).readData(model.getCollection(it.id).asDynamic())
                    }
                }
            }
        }
        view.configure = {settings ->
            widgetsDescriptions.forEach {
                when (it) {
                    is TextboxDescriptionJS -> {
                        (view.getValue(it.id) as TextBoxWidget).configure(Unit)
                    }
                    is BooleanBoxDescriptionJS -> {
                        (view.getValue(it.id) as BooleanBoxWidget).configure(Unit)
                    }
                    is PasswordBoxDescriptionJS -> {
                        (view.getValue(it.id) as PasswordBoxWidget).configure(Unit)
                    }
                    is TextAreaDescriptionJS -> {
                        (view.getValue(it.id) as TextAreaWidget).configure(Unit)
                    }
                    is EnumSelectDescriptionJS -> {
                        (view.getValue(it.id) as EnumSelectWidget<*>).configure(settings.getValue(it.id).asDynamic())
                    }
                    is SelectDescriptionJS -> {
                        (view.getValue(it.id) as SelectWidget).configure(settings.getValue(it.id).asDynamic())
                    }
                    is EntitySelectDescriptionJS -> {
                        (view.getValue(it.id) as EntitySelectWidget).configure(settings.getValue(it.id).asDynamic())
                    }
                    is FloatBoxDescriptionJS -> {
                        (view.getValue(it.id) as FloatBoxWidget).configure(Unit)
                    }
                    is DateboxDescriptionJS -> {
                        (view.getValue(it.id) as DateBoxWidget).configure(Unit)
                    }
                    is DateTimeBoxDescriptionJS -> {
                        (view.getValue(it.id) as DateTimeBoxWidget).configure(Unit)
                    }
                    is IntegerBoxDescriptionJS -> {
                        (view.getValue(it.id) as IntegerBoxWidget).configure(Unit)
                    }
                    is TableDescriptionJS -> {
                        (view.getValue(it.id) as TableWidget<BaseVMEntityJS,BaseVSEntityJS,BaseVVEntityJS>).configure(settings.getValue(it.id).asDynamic())
                    }
                    is TileDescriptionJS -> {
                        (view.getValue(it.id) as TileWidget<*,*>).configure(settings.getValue(it.id).asDynamic())
                    }
                    is NavigatorDescriptionJS -> {
                        (view.getValue(it.id) as NavigatorWidget<*,*,*>).configure(settings.getCollection(it.id).asDynamic())
                    }
                }
            }
        }
        view.showValidation = {validation ->
            widgetsDescriptions.forEach {
                when (it) {
                    is TextboxDescriptionJS -> {
                        (view.getValue(it.id) as TextBoxWidget).showValidation(validation.getValue(it.id) as String?)
                    }
                    is BooleanBoxDescriptionJS -> {
                        (view.getValue(it.id) as BooleanBoxWidget).showValidation(validation.getValue(it.id) as String?)
                    }
                    is PasswordBoxDescriptionJS -> {
                        (view.getValue(it.id) as PasswordBoxWidget).showValidation(validation.getValue(it.id) as String?)
                    }
                    is TextAreaDescriptionJS -> {
                        (view.getValue(it.id) as TextAreaWidget).showValidation(validation.getValue(it.id) as String?)
                    }
                    is EnumSelectDescriptionJS -> {
                        (view.getValue(it.id) as EnumSelectWidget<*>).showValidation(validation.getValue(it.id) as String?)
                    }
                    is SelectDescriptionJS -> {
                        (view.getValue(it.id) as SelectWidget).showValidation(validation.getValue(it.id).asDynamic())
                    }
                    is EntitySelectDescriptionJS -> {
                        (view.getValue(it.id) as EntitySelectWidget).showValidation(validation.getValue(it.id) as String?)
                    }
                    is FloatBoxDescriptionJS -> {
                        (view.getValue(it.id) as FloatBoxWidget).showValidation(validation.getValue(it.id) as String?)
                    }
                    is IntegerBoxDescriptionJS -> {
                        (view.getValue(it.id) as IntegerBoxWidget).showValidation(validation.getValue(it.id) as String?)
                    }
                    is TableDescriptionJS -> {
                        (view.getValue(it.id) as TableWidget<BaseVMEntityJS,BaseVSEntityJS,BaseVVEntityJS>).showValidation(validation.getValue(it.id)?.asDynamic())
                    }
                    is DateboxDescriptionJS -> {
                        (view.getValue(it.id) as DateBoxWidget).showValidation(validation.getValue(it.id) as String?)
                    }
                    is DateTimeBoxDescriptionJS -> {
                        (view.getValue(it.id) as DateTimeBoxWidget).showValidation(validation.getValue(it.id) as String?)
                    }
                    is TileDescriptionJS -> {
                        (view.getValue(it.id) as TileWidget<*,*>).showValidation(validation.getValue(it.id).asDynamic())
                    }
                    is NavigatorDescriptionJS -> {
                        (view.getValue(it.id) as NavigatorWidget<*,*,*>).showValidation(validation.getCollection(it.id).asDynamic())
                    }
                }
            }
        }
        view.writeData = {model ->
            model.uid = modelUid
            widgetsDescriptions.forEach {
                when (it) {
                    is TextboxDescriptionJS -> {
                        model.setValue(it.id, (view.getValue(it.id) as TextBoxWidget).getData())
                    }
                    is BooleanBoxDescriptionJS -> {
                        model.setValue(it.id, (view.getValue(it.id) as BooleanBoxWidget).getData())
                    }
                    is PasswordBoxDescriptionJS -> {
                        model.setValue(it.id, (view.getValue(it.id) as PasswordBoxWidget).getData())
                    }
                    is TextAreaDescriptionJS -> {
                        model.setValue(it.id, (view.getValue(it.id) as TextAreaWidget).getData())
                    }
                    is EnumSelectDescriptionJS -> {
                        model.setValue(it.id, (view.getValue(it.id) as EnumSelectWidget<*>).getData())
                    }
                    is SelectDescriptionJS -> {
                        model.setValue(it.id, (view.getValue(it.id) as SelectWidget).getData())
                    }
                    is EntitySelectDescriptionJS -> {
                        model.setValue(it.id, (view.getValue(it.id) as EntitySelectWidget).getData())
                    }
                    is FloatBoxDescriptionJS -> {
                        model.setValue(it.id, (view.getValue(it.id) as FloatBoxWidget).getData())
                    }
                    is IntegerBoxDescriptionJS -> {
                        model.setValue(it.id, (view.getValue(it.id) as IntegerBoxWidget).getData())
                    }
                    is DateboxDescriptionJS -> {
                        model.setValue(it.id, (view.getValue(it.id) as DateBoxWidget).getData())
                    }
                    is DateTimeBoxDescriptionJS -> {
                        model.setValue(it.id, (view.getValue(it.id) as DateTimeBoxWidget).getData())
                    }
                    is TableDescriptionJS -> {
                        (view.getValue(it.id) as TableWidget<BaseVMEntityJS,BaseVSEntityJS,BaseVVEntityJS>).writeData(model.getCollection(it.id).asDynamic())
                    }
                    is TileDescriptionJS -> {
                        model.setValue(it.id, (view.getValue(it.id) as TileWidget<*,*>).getData())
                    }
                    is NavigatorDescriptionJS -> {
                        (view.getValue(it.id) as NavigatorWidget<*,*,*>).writeData(model.getCollection(it.id).asDynamic())
                    }
                }
            }
        }
        view.navigate = navigate@{uid  ->
            widgetsDescriptions.forEach {
                when (it) {
                    is TileDescriptionJS -> {
                        val result = (view.getValue(it.id) as TileWidget<*,*>).navigate(uid)
                        if(result){
                            return@navigate true
                        }
                    }
                    is NavigatorDescriptionJS -> {
                        val result = (view.getValue(it.id) as NavigatorWidget<*,*,*>).navigate(uid)
                        if(result){
                            return@navigate true
                        }
                    }
                }
            }
            false
        }
        val interceptors = arrayListOf<ViewInterceptor<VM,VS,VV,V>>()
        viewDescr.interceptors.forEach {
            interceptors.add(ReflectionFactoryJS.get().getFactory(it).invoke() as ViewInterceptor<VM,VS,VV,V>) }
        interceptors.forEach {
            it.onCreate(view)
        }
        return view
    }


    class TableTR(val tds: MutableList<TableTD> = arrayListOf())
    class TableTD(val widget: BaseWidgetDescriptionJS)


}