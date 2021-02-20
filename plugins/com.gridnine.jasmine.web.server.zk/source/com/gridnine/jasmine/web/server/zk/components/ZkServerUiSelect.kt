/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.web.server.zk.components

import com.gridnine.jasmine.server.core.model.common.SelectItem
import com.gridnine.jasmine.web.server.components.ServerUiComponent
import com.gridnine.jasmine.web.server.components.ServerUiSelect
import com.gridnine.jasmine.web.server.components.ServerUiSelectConfiguration
import com.gridnine.jasmine.web.server.components.ServerUiSelectDataType
import com.gridnine.jasmine.zk.select2.Select2
import com.gridnine.jasmine.zk.select2.Select2DataSourceType
import com.gridnine.jasmine.zk.select2.Select2Option
import com.gridnine.jasmine.zk.select2.Select2QueryParameter
import org.zkoss.zk.ui.HtmlBasedComponent

class ZkServerUiSelect(private val config : ServerUiSelectConfiguration) : ServerUiSelect, ZkServerUiComponent(){

    private var url:String? = null

    private var limit:Int? = null

    private var parameters: List<Pair<String, String?>>? = null

    private var component:Select2? = null

    private var initValues= arrayListOf<SelectItem>()

    private var possibleValues= arrayListOf<SelectItem>()

    private var validation:String? = null

    private var enabled = true

    private var changeListener: ((List<SelectItem>) -> Unit)? = null

    private var changeListenerAdded = false

    override fun setLoaderParams(url: String, limit: Int, parameters: List<Pair<String, String?>>) {
        this.url = url
        this.limit = limit
        this.parameters = parameters
        if(component != null){
            setLoaderParametersInternal()
        }
    }

    private fun setLoaderParametersInternal() {
        TODO("Not yet implemented")
    }

    override fun getValues(): List<SelectItem> {
        if(component != null){
            return component!!.selectedValues.map { SelectItem(it.id, it.text) }
        }
        return initValues
    }

    override fun setValues(values: List<SelectItem>) {
        initValues.clear()
        initValues.addAll(values)
        if(component != null){
            component!!.selectedValues = values.map { Select2Option(it.id, it.text) }
        }
    }

    override fun setPossibleValues(values: List<SelectItem>) {
        possibleValues.clear()
        possibleValues.addAll(values)
        if(component != null){
            component!!.configuration.possibleValues = values.map { Select2Option(it.id, it.text) }
        }
    }

    override fun showValidation(value: String?) {
        validation = value
        if(component != null){
            component!!.validation = validation
        }
    }

    override fun setEnabled(value: Boolean) {
        enabled = value
        if(component != null){
            component!!.isEnabled = true
        }
    }

    override fun setChangeListener(value: ((List<SelectItem>) -> Unit)?) {
        changeListener = value
        if(component!= null){
            if(!changeListenerAdded){
                component!!.addChangeListener{
                    changeListener?.invoke(it.selectedValues.map { SelectItem(it.id, it.text) })
                }
                changeListenerAdded = true
            }
        }
    }

    override fun getComponent(): HtmlBasedComponent {
        if(component != null){
            return component!!
        }
        component = Select2()
        component!!.configuration.possibleValues = possibleValues.map { Select2Option(it.id, it.text) }
        component!!.configuration.isShowClearIcon = config.showClearIcon
        component!!.configuration.isMultiple = config.multiple
        component!!.configuration.dataSourceType  = if(config.mode == ServerUiSelectDataType.REMOTE) Select2DataSourceType.REMOTE else Select2DataSourceType.LOCAL
        component!!.configuration.baseUrl = url
        component!!.configuration.queryParameters= parameters?.map {
            val result = Select2QueryParameter()
            result.key = it.first
            result.value = it.second
            result
        }?: arrayListOf()
        component!!.configuration.isEditable = config.editable
        if(config.width == "100%"){
            component!!.hflex = "1"
        } else if(config.width != null){
            component!!.width = config.width
        }
        if(config.height == "100%"){
            component!!.vflex = "1"
        }else if(config.height != null) {
            component!!.height = config.height
        }
        component!!.selectedValues = initValues.map { Select2Option(it.id, it.text) }
        if(config.mode == ServerUiSelectDataType.REMOTE){
            setLoaderParametersInternal()
        }
        return component!!
    }

    override fun getParent(): ServerUiComponent? {
        return parent
    }


}