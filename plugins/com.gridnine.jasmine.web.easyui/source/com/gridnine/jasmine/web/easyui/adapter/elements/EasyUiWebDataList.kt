/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.web.easyui.adapter.elements

import com.gridnine.jasmine.web.core.ui.WebComponent
import com.gridnine.jasmine.web.core.ui.components.WebDataList
import com.gridnine.jasmine.web.core.ui.components.WebDataListConfiguration
import com.gridnine.jasmine.web.core.utils.MiscUtilsJS
import com.gridnine.jasmine.web.easyui.adapter.jQuery

class EasyUiWebDataList<E:Any>(private val parent:WebComponent?, configure: WebDataListConfiguration.()->Unit) :WebDataList<E>{

    private var initialized = false

    private val fit:Boolean

    private val showLines:Boolean

    private val width:String?
    private val height:String?

    private val children = arrayListOf<WebComponent>()

    private val uid = MiscUtilsJS.createUUID()

    private lateinit var valueGetter: (E) -> String?
    private var formatter: ((E, Int) -> String?)? = null

    private val data = arrayListOf<E>()

    private var jq:dynamic = null

    private var selectionAllowed = true

    private var clickListener:((E) ->Unit)? = null

    init {
        val configuration = WebDataListConfiguration()
        configuration.configure()
        fit = configuration.fit
        showLines = configuration.showLines
        width = configuration.width
        height = configuration.height
    }


    override fun getHtml(): String {
       return "<div id=\"dataList${uid}\" style=\"${if(width != null) "width:$width" else ""};${if(height != null) "height:$height" else ""}\"></div>"
    }

    private fun getSelector() = "#dataList$uid"

    override fun decorate() {
        jq = jQuery(getSelector())
        jq.datalist(object{
            val fit = this@EasyUiWebDataList.fit
            val lines = showLines
            val valueField = "_valueField"
            val textField = "_textField"
            val textFormatter = { _:dynamic,row:ListRowWrapper<E>,index:Int ->
                formatter?.invoke(row.data, index)?:row._textField
            }
            val onBeforeSelect = {_:dynamic,_:dynamic ->
                selectionAllowed
            }
            val onClickRow = {_:Int,row:ListRowWrapper<E> ->
                clickListener?.let { it.invoke(row.data) }
            }
        })
        reloadData()
        initialized = true
    }

    override fun destroy() {
        //noops
    }


    override fun getParent(): WebComponent? {
        return parent
    }

    override fun getChildren(): MutableList<WebComponent> {
        return children
    }

    override fun setValueGetter(value: (E) -> String?) {
        valueGetter = value
    }

    override fun setData(data: List<E>) {
        this.data.clear()
        this.data.addAll(data)
        if(initialized){
            reloadData()
        }
    }

    private fun reloadData() {
        val result = arrayOfNulls<Any>(data.size)
        data.withIndex().forEach { (index, value) ->
            val elm = ListRowWrapper<E>()
            elm._textField = valueGetter.invoke(value)
            elm._valueField = elm._textField
            elm.data = value
            result[index] = elm
        }
        jq.datalist("loadData", result)
    }

    override fun setFormatter(value: (E, Int) -> String?) {
        formatter = value
    }

    override fun setClickListener(listener: ((E) -> Unit)?) {
        clickListener = listener
    }

    override fun setSelectionAllowed(value: Boolean) {
        selectionAllowed = value
    }
}

class ListRowWrapper<E:Any>{
    var _valueField:String? = null
    var _textField:String? = null
    lateinit var data:E
}