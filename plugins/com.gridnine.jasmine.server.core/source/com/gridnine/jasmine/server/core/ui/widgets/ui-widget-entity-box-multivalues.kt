/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.server.core.ui.widgets

import com.gridnine.jasmine.common.core.model.BaseIdentity
import com.gridnine.jasmine.common.core.model.ObjectReference
import com.gridnine.jasmine.common.core.model.SelectItem
import com.gridnine.jasmine.common.core.reflection.ReflectionFactory
import com.gridnine.jasmine.server.core.ui.common.BaseNodeWrapper
import com.gridnine.jasmine.server.core.ui.common.BaseWidgetConfiguration
import com.gridnine.jasmine.server.core.ui.components.Select
import com.gridnine.jasmine.server.core.ui.components.SelectDataType
import com.gridnine.jasmine.server.core.ui.components.UiLibraryAdapter

class EntityMultiValuesWidget<D:BaseIdentity>(configure: EntityMultiValuesWidgetConfiguration.()->Unit):BaseNodeWrapper<Select>() {

    init {
        val config = EntityMultiValuesWidgetConfiguration()
        config.configure()
        _node = UiLibraryAdapter.get().createSelect{
            width = config.width
            height = config.height
            mode = SelectDataType.REMOTE
            showClearIcon = false
            editable = true
            multiple = true
        }
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

class EntityMultiValuesWidgetConfiguration:BaseWidgetConfiguration(){
    lateinit var handler: AutocompleteHandler
}