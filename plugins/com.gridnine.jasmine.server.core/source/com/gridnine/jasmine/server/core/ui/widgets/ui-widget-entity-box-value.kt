/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.server.core.ui.widgets

import com.gridnine.jasmine.common.core.model.BaseIdentity
import com.gridnine.jasmine.common.core.model.EntitySelectBoxConfiguration
import com.gridnine.jasmine.common.core.model.ObjectReference
import com.gridnine.jasmine.common.core.model.SelectItem
import com.gridnine.jasmine.common.core.reflection.ReflectionFactory
import com.gridnine.jasmine.server.core.ui.common.BaseNodeWrapper
import com.gridnine.jasmine.server.core.ui.common.BaseWidgetConfiguration
import com.gridnine.jasmine.server.core.ui.components.Select
import com.gridnine.jasmine.server.core.ui.components.SelectDataType
import com.gridnine.jasmine.server.core.ui.components.UiLibraryAdapter

class EntityValueWidget<D:BaseIdentity>(configure: EntityValueWidgetConfiguration.()->Unit):BaseNodeWrapper<Select>() {

    init {
        val config = EntityValueWidgetConfiguration()
        config.configure()
        _node = UiLibraryAdapter.get().createSelect{
            width = config.width
            height = config.height
            mode = SelectDataType.REMOTE
            showClearIcon = config.showClearIcon
            editable = true
            multiple = false
        }
        _node.setLoaderParams(restAutocompleteUrl, 10, arrayListOf(
                Pair("autocompleteFieldName", config.handler.getAutocompleteFieldName()),
                Pair("listId",  config.handler.getIndexClassName())
        ))
    }

    fun getValue():ObjectReference<D>?{
        return _node.getValues().firstOrNull()?.let { ObjectReference(ReflectionFactory.get().getClass(it.id.substringBefore("||")), it.id.substringAfter("||"), it.text) }
    }

    fun setValue(value: ObjectReference<D>?) {
        if(value == null){
            _node.setValues(emptyList())
            return
        }
        _node.setValues(arrayListOf(SelectItem("${value.type.qualifiedName}||${value.uid}", value.caption!!)))
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

class EntityValueWidgetConfiguration:BaseWidgetConfiguration() {
    var showClearIcon = false
    lateinit var handler: AutocompleteHandler
}