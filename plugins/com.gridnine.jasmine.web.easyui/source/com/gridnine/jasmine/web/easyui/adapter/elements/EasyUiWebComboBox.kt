/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.web.easyui.adapter.elements

import com.gridnine.jasmine.web.core.ui.WebComponent
import com.gridnine.jasmine.web.core.ui.components.ComboboxMode
import com.gridnine.jasmine.web.core.ui.components.SelectItemJS
import com.gridnine.jasmine.web.core.ui.components.WebComboBox
import com.gridnine.jasmine.web.core.ui.components.WebComboBoxConfiguration
import com.gridnine.jasmine.web.core.utils.MiscUtilsJS
import com.gridnine.jasmine.web.easyui.adapter.jQuery

class EasyUiWebComboBox(private val parent:WebComponent?, configure: WebComboBoxConfiguration.()->Unit) :WebComboBox{

    private var initialized = false

    private val uid = MiscUtilsJS.createUUID()

    private var width:String? = null
    private var height:String? = null
    private val mode:ComboboxMode
    private var jq:dynamic = null
    private var loader:((String?) -> List<SelectItemJS>)? = null
    private val allItems = arrayListOf<SelectItemJS>()
    private val selectedValues = arrayListOf<String>()
    private var ignoreSearchRequest = false
    private var editable = false
    init {
        (parent?.getChildren() as MutableList<WebComponent>?)?.add(this)
        val configuration = WebComboBoxConfiguration()
        configuration.configure()
        width = configuration.width
        height = configuration.height
        mode = configuration.mode
        editable= configuration.editable
    }

    override fun getHtml(): String {
        return "<input id=\"comboBox${uid}\" style=\"${if(width != null) "width:$width" else ""};${if(height != null) "height:$height" else ""}\"/>"
    }

    override fun setLoader(value: (String?) -> List<SelectItemJS>) {
        loader = value
    }

    override fun setPossibleValues(items: List<SelectItemJS>) {
        allItems.clear()
        allItems.addAll(items)
        if(initialized){
            jq.combobox("loadData", items.toTypedArray())
        }
    }

    override fun getValues(): List<String> {
        if(!initialized){
            return selectedValues
        }
        return (jq.combobox("getValues") as Array<String>).toList()
    }

    override fun setValues(items: List<String>) {
        if(!initialized){
            selectedValues.clear()
            selectedValues.addAll(items)
            return
        }
        jq.combobox("setValues", items.toTypedArray())
    }

    override fun getParent(): WebComponent? {
        return parent
    }

    override fun getChildren(): List<WebComponent> {
        return emptyList()
    }


    override fun decorate() {
        jq = jQuery("#comboBox$uid")
        val options = object{
            val valueField = "id"
            val textField = "text"
            val panelHeight = "auto"
            val editable = this@EasyUiWebComboBox.editable
            val mode = if(this@EasyUiWebComboBox.mode == ComboboxMode.LOCAL) "local" else "remote"
        }.asDynamic()
        if(mode == ComboboxMode.REMOTE){
           options.loader = loader@{ param: dynamic, success: dynamic, _: dynamic ->
               if (ignoreSearchRequest) {
                   success(allItems.toTypedArray())
                   return@loader true
               }
               if (this@EasyUiWebComboBox.loader == null) {
                   success(allItems.toTypedArray())
               } else {
                   val result = this@EasyUiWebComboBox.loader!!.invoke(param.q)
                   success(result.toTypedArray())
               }
               return@loader true
           }
        }
        jq.combobox(options)
        initialized = true
        if(mode == ComboboxMode.LOCAL){
           setPossibleValues(ArrayList(allItems))
        }
        setValues(selectedValues)
    }

}