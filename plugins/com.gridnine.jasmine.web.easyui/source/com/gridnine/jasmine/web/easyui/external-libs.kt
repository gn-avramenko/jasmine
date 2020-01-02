/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/
@file:Suppress("unused", "UNCHECKED_CAST")

package com.gridnine.jasmine.web.easyui


external interface JQuery {
    fun remove()
    fun html(htmlString: String): JQuery
    fun layout():Any?
    fun tabs(options:Any)
    fun tabs(method:String):dynamic
    fun tabs(method:String, arg:Any?):dynamic
    fun accordion():Any?
    fun accordion(method:String):Any?
    fun accordion(method:String, arg:Any?):Any?
    fun on(event:String, callback:(event:dynamic) -> Unit)
    fun searchbox(method:String):Any
    fun datagrid(method: String): Any?
    fun datagrid(options:Any)
    fun linkbutton(options:Any)
    fun dialog(options:Any)
    fun combobox(options:Any)
    fun combobox(method:String):Any?
    fun combobox(method: String , arg:Any?)
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
    fun click(handler: () -> Unit)
    val length:Int
    fun append(content:String)
}


external interface JQueryStatic {
    @nativeInvoke
    operator fun invoke(selector: String): JQuery
}


external var jQuery: JQueryStatic = definedExternally
external fun createDatagrid(id:String, columns:dynamic, loader:dynamic, onDblClickRow:dynamic):Unit = definedExternally
external fun createTable(id:String, columns:dynamic, loader:dynamic, onClickRow: dynamic):Unit = definedExternally
external fun createDatalist(id:String,  onClickRow:dynamic):Unit = definedExternally
external fun createSearchBox(id:String, prompt:String, searcher:dynamic): JQuery = definedExternally
external fun confirm(question:String, handler:() ->Unit):Unit = definedExternally
external fun showMessage(title:String?, message:String ,timeout: Int)
external fun showError(title:String?, message:String, stacktrace:String?)
external fun openFileJS(accept:String?, resolve:(file:dynamic) ->Unit)