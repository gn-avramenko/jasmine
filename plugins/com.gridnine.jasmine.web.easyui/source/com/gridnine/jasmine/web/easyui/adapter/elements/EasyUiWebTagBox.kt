/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.web.easyui.adapter.elements

import com.gridnine.jasmine.web.core.ui.WebComponent
import com.gridnine.jasmine.web.core.ui.components.*
import com.gridnine.jasmine.web.core.utils.MiscUtilsJS
import com.gridnine.jasmine.web.easyui.adapter.jQuery
import kotlin.js.Promise

class EasyUiWebTagBox(private val parent:WebComponent?, configure: WebTagBoxConfiguration.()->Unit) :WebTagBox{

    private var initialized = false

    private val uid = MiscUtilsJS.createUUID()

    private var width:String? = null
    private var height:String? = null
    private val mode:ComboboxMode
    private var jq:dynamic = null
    private var loader:((String?) -> Promise<List<SelectItemJS>>)? = null
    private val allItems = arrayListOf<SelectItemJS>()
    private val selectedValues = arrayListOf<String>()
    private var editable = false
    private var showClearIcon = false
    private var hasDownArrow = true
    private var limitToList = true
    init {
        (parent?.getChildren() as MutableList<WebComponent>?)?.add(this)
        val configuration = WebTagBoxConfiguration()
        configuration.configure()
        width = configuration.width
        height = configuration.height
        mode = configuration.mode
        editable = configuration.editable
        showClearIcon = configuration.showClearIcon
        limitToList = configuration.limitToList
        hasDownArrow = configuration.hasDownArrow
    }

    override fun getHtml(): String {
        return "<input id=\"tagBox${uid}\" style=\"${if(width != null) "width:$width" else ""};${if(height != null) "height:$height" else ""}\"/>"
    }

    override fun setLoader(value: (String?) -> Promise<List<SelectItemJS>>) {
        loader = value
    }

    override fun setPossibleValues(items: List<SelectItemJS>) {
        allItems.clear()
        allItems.addAll(items)
        if(initialized){
            jq.tagbox("loadData", items.toTypedArray())
        }
    }

    override fun getValues(): List<String> {
        if(!initialized){
            return selectedValues
        }
        return (jq.tagbox("getValues") as Array<String>).toList()
    }

    override fun setValues(items: List<String>) {
        if(!initialized){
            selectedValues.clear()
            selectedValues.addAll(items)
            return
        }
        jq.tagbox("setValues", items.toTypedArray())
    }

    override fun getParent(): WebComponent? {
        return parent
    }

    override fun getChildren(): List<WebComponent> {
        return emptyList()
    }


    override fun decorate() {
        jq = jQuery("#tagBox$uid")
        val options = object{
            val valueField = "id"
            val textField = "text"
            val panelHeight = "auto"
            val limitToList = this@EasyUiWebTagBox.limitToList
            val editable = this@EasyUiWebTagBox.editable
            val mode = if(this@EasyUiWebTagBox.mode == ComboboxMode.LOCAL) "local" else "remote"
            val hasDownArrow = this@EasyUiWebTagBox.hasDownArrow
            val onChange = { newValue: Array<String>, _: String? ->
                if(showClearIcon) {
                    jq.tagbox("getIcon", 0).css("visibility", if (newValue.isEmpty()) "hidden" else "visible");
                }
            }
        }.asDynamic()
        if(showClearIcon){
            options.icons = arrayOf(object{
                val iconCls = "icon-clear"
                val handler = {_:dynamic ->
                    jq.tagbox("setValues", arrayOfNulls<String>(0))
                }
            })
        }
        if(mode == ComboboxMode.REMOTE){
           options.loader = { param: dynamic, success: dynamic, _: dynamic ->
                   if(!initialized){
                       success(arrayOfNulls<Any>(0))
                   } else{
                       this@EasyUiWebTagBox.loader!!.invoke(param.q).then {
                           success(it.toTypedArray())
                       }
                   }
           }
//           options.keyHandler = jQuery.extend(object{}, jQuery.fn.tagbox.defaults.keyHandler, object{
//                val enter = { e:dynamic ->
//                   console.log(e)
//                }
//                val query = {
//                    this@EasyUiWebTagBox.loader!!.invoke("").then {
//                        jq.tagbox("loadData", it.toTypedArray())
//                        //jq.tagbox("showPanel")
//                    }
//                }
//            })
        }
        jq.tagbox(options)
        initialized = true
        if(mode == ComboboxMode.LOCAL){
           setPossibleValues(ArrayList(allItems))
        }
        setValues(selectedValues)
        if(showClearIcon && selectedValues.isEmpty()) {
            jq.tagbox("getIcon", 0).css("visibility", "hidden");
        }
    }

}