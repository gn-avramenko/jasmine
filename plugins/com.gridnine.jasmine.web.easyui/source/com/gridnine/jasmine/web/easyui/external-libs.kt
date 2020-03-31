/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/
@file:Suppress("unused", "UNCHECKED_CAST", "DEPRECATION")

package com.gridnine.jasmine.web.easyui

import com.gridnine.jasmine.web.easyui.widgets.table.EasyUiEntityTableColumnEditor
import com.gridnine.jasmine.web.easyui.widgets.table.EasyUiEnumTableColumnEditor


external interface JQuery {
    fun data(prop:String):dynamic
    fun remove()
    fun empty()
    fun html(htmlString: String): JQuery
    fun layout():Any?
    fun layout(options:Any):Any?
    fun tabs(options:Any)
    fun tabs(method:String):dynamic
    fun tabs(method:String, arg:Any?):dynamic
    fun jtabs(options:Any)
    fun jtabs(method:String):dynamic
    fun jtabs(method:String, arg:Any?):dynamic
    fun accordion(options:Any):Any?
    fun accordion(method:String):Any?
    fun accordion(method:String, arg:Any?):Any?
    fun on(event:String, callback:(event:dynamic) -> Unit)
    fun searchbox(method:String):Any
    fun searchbox(options:Any):dynamic
    fun datagrid(method: String, vararg args:Any?): Any?
    fun datagrid(options:Any)
    fun datalist(options:Any)
    fun tree(options:Any)
    fun height(value:Int)
    fun height():Int
    fun tree(method:String, args:Any):dynamic
    fun linkbutton(options:Any)
    fun linkbutton(method:String, vararg args:Any)
    fun dialog(options:Any)
    fun dialog(method:String)
    fun menu(options:Any)
    fun menu(method:String, arg:Any)
    fun panel(options:Any):JQuery
    fun combobox(options:Any)
    fun combobox(method:String):Any?
    fun combobox(method: String , arg:Any?)
    fun tagbox(options:Any):JQuery
    fun tagbox(method: String, vararg  args:Any?):JQuery
    fun textbox(method:String):Any?
    fun textbox(options:Any)
    fun textbox(method: String , arg:Any?)
    fun passwordbox(method:String):Any?
    fun passwordbox(options:Any)
    fun passwordbox(method: String , arg:Any?)
    fun numberbox(options:Any)
    fun numberbox(method: String , arg:Any?)
    fun numberbox(method: String ):Any?
    fun addClass(cls:String)
    fun removeClass(cls:String)
    fun removeClass()
    fun click(handler: () -> Unit)
    fun focus()
    val length:Int
    fun append(content:String)
    fun switchbutton(options: Any):JQuery
    fun switchbutton(method:String, vararg args:Any?):Any?
    fun datebox(options: Any):JQuery
    fun datebox(method:String, vararg args:Any?):Any?
    fun datetimebox(options: Any):JQuery
    fun datetimebox(method:String, vararg args:Any?):Any?
    fun hide()
    fun show()
}


external interface JQueryStatic {
    @nativeInvoke
    operator fun invoke(selector: String): JQuery
}

external var debugger: dynamic = definedExternally
external var jQuery: JQueryStatic = definedExternally
external fun createDatagrid(id:String, columns:dynamic, loader:dynamic, onDblClickRow:dynamic):Unit = definedExternally
external fun createTable(id:String, columns:dynamic, loader:dynamic, onClickRow: dynamic):Unit = definedExternally
external fun createSearchBox(id:String, prompt:String, searcher:dynamic): JQuery = definedExternally

external fun confirm(question:String, handler:() ->Unit):Unit = definedExternally
external fun showMessage(title:String?, message:String ,timeout: Int)
external fun showError(title:String?, message:String, stacktrace:String?)
external fun openFileJS(accept:String?, resolve:(file:dynamic) ->Unit)


external fun extendDatagridEditors(enumEditor:EasyUiEnumTableColumnEditor,entityEditor:EasyUiEntityTableColumnEditor): Unit
