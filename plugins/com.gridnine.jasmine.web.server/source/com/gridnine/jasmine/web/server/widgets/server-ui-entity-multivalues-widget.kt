/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.web.server.widgets

import com.gridnine.jasmine.server.core.model.common.BaseIdentity
import com.gridnine.jasmine.server.core.model.common.SelectItem
import com.gridnine.jasmine.server.core.model.domain.ObjectReference
import com.gridnine.jasmine.server.core.reflection.ReflectionFactory
import com.gridnine.jasmine.web.server.components.*

class ServerUiEntityMultiValuesWidget<D:BaseIdentity>(configure:ServerUiEntityMultiValuesWidgetConfiguration.()->Unit):BaseServerUiNodeWrapper<ServerUiSelect>() {


    init {
        val config = ServerUiEntityMultiValuesWidgetConfiguration()
        config.configure()
        _node = ServerUiLibraryAdapter.get().createSelect(ServerUiSelectConfiguration{
            width = config.width
            height = config.height
            mode = ServerUiSelectDataType.REMOTE
            showClearIcon = false
            editable = true
            multiple = true
        })
        _node.setLoaderParams(restAutocompleteUrl, 10, arrayListOf(
                Pair("autocompleteFieldName", config.handler.getAutocompleteFieldName()),
                Pair("listId",  config.handler.getIndexClassName())
        ))
    }

    fun getValues():List<ObjectReference<D>>{
        return _node.getValues().map { ObjectReference<D>(ReflectionFactory.get().getClass(it.id.substringBefore("||")), it.id.substringAfter("||"), it.text) }
    }

    fun setValues(list: List<ObjectReference<D>>) {
        _node.setValues(list.map { SelectItem("${it.type.qualifiedName}||${it.uid}", it.caption!!) })
    }

}

class ServerUiEntityMultiValuesWidgetConfiguration(){
    var width:String? = null
    var height:String? = null
    lateinit var handler: ServerUiAutocompleteHandler
}