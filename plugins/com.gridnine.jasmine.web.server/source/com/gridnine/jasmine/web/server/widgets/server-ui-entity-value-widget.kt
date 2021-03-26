/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.web.server.widgets

import com.gridnine.jasmine.server.core.model.common.BaseIdentity
import com.gridnine.jasmine.server.core.model.common.SelectItem
import com.gridnine.jasmine.server.core.model.domain.ObjectReference
import com.gridnine.jasmine.server.core.model.ui.EntitySelectBoxConfiguration
import com.gridnine.jasmine.server.core.model.ui.EnumSelectBoxConfiguration
import com.gridnine.jasmine.server.core.reflection.ReflectionFactory
import com.gridnine.jasmine.web.server.components.*

class ServerUiEntityValueWidget<D:BaseIdentity>(config:ServerUiEntityValueWidgetConfiguration):BaseServerUiNodeWrapper<ServerUiSelect>() {

    init {
        _node = ServerUiLibraryAdapter.get().createSelect(ServerUiSelectConfiguration{
            width = config.width
            height = config.height
            mode = ServerUiSelectDataType.REMOTE
            showClearIcon = false
            editable = true
            multiple = false
        })
        _node.setLoaderParams(restAutocompleteUrl, 10, arrayListOf(
                Pair("autocompleteFieldName", config.handler.getAutocompleteFieldName()),
                Pair("listId",  config.handler.getIndexClassName())
        ))
    }

    fun getValue():ObjectReference<D>?{
        return _node.getValues().firstOrNull()?.let { ObjectReference<D>(ReflectionFactory.get().getClass(it.id.substringBefore("||")), it.id.substringAfter("||"), it.text) }
    }

    fun setValue(value: ObjectReference<D>?) {
        if(value == null){
            _node.setValues(emptyList())
            return
        }
        _node.setValues(arrayListOf(SelectItem("${value.type}||${value.uid}", value.caption!!)))
    }

    fun setReadonly(value:Boolean){
        _node.setEnabled(!value)
    }

    fun showValidation(value: String?) {
        _node.showValidation(value)
    }
    fun configure(config: EntitySelectBoxConfiguration?) {
        _node.setEnabled(config?.notEditable != false)
    }
}

class ServerUiEntityValueWidgetConfiguration(){
    constructor(config:ServerUiEntityValueWidgetConfiguration.()->Unit):this(){
        config.invoke(this)
    }
    var width:String? = null
    var height:String? = null
    lateinit var handler: ServerUiAutocompleteHandler
}