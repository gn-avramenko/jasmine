/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.web.core.ui.widgets

import com.gridnine.jasmine.server.core.model.common.SelectItemJS
import com.gridnine.jasmine.server.core.model.domain.ObjectReferenceJS
import com.gridnine.jasmine.server.standard.model.rest.AutocompleteRequestJS
import com.gridnine.jasmine.web.core.StandardRestClient
import com.gridnine.jasmine.web.core.ui.AutocompleteHandler
import com.gridnine.jasmine.web.core.ui.UiLibraryAdapter
import com.gridnine.jasmine.web.core.ui.WebComponent
import com.gridnine.jasmine.web.core.ui.components.*
import kotlin.js.Promise

class EntityMultiValuesWidget(aParent:WebComponent, configure:EntityMultiValuesWidgetConfiguration.()->Unit):WebComponent {
    private val delegate:WebSelect
    private val parent:WebComponent = aParent
    private val children = arrayListOf<WebComponent>()

    init {
        (parent.getChildren() as MutableList<WebComponent>).add(this)
        val conf = EntityMultiValuesWidgetConfiguration();
        conf.configure()
        delegate =UiLibraryAdapter.get().createSelect(this){
            width = conf.width
            height = conf.height
            mode = SelectDataType.REMOTE
            showClearIcon = conf.showClearIcon
            editable = true
            multiple = true
            hasDownArrow = false
        }
        delegate.setLoader { value ->
            Promise{ resolve, _ ->
                val request = AutocompleteRequestJS()
                request.autocompleteFieldName = conf.handler.getAutocompleteFieldName()
                request.listId = conf.handler.getIndexClassName()
                request.limit = 10
                request.pattern=value
                StandardRestClient.standard_standard_autocomplete(request).then{
                    resolve(it.items.map { toSelectItem(it.document) })
                }
            }
        }
    }

    fun getValues():List<ObjectReferenceJS>{
        return delegate.getValues().map { toObjectReference(it) }
    }

    fun setValues(list: List<ObjectReferenceJS>) {
        delegate.setValues(list.map { toSelectItem(it) })
    }

    override fun getParent(): WebComponent? {
        return parent
    }

    override fun getChildren(): List<WebComponent> {
        return children
    }

    override fun getHtml(): String {
        return delegate.getHtml()
    }

    override fun decorate() {
        delegate.decorate()
    }

    override fun destroy() {
        delegate.destroy()
    }

    private fun toSelectItem(ref:ObjectReferenceJS): SelectItemJS {
        return SelectItemJS("${ref.type}||${ref.uid}", ref.caption?:"???")
    }
    private fun toObjectReference(item: SelectItemJS):ObjectReferenceJS{
        return ObjectReferenceJS(item.id.substringBefore("||"), item.id.substringAfter("||"), item.text)
    }
}

class EntityMultiValuesWidgetConfiguration{
    var width:String? = null
    var height:String? = null
    var showClearIcon = false
    lateinit var handler: AutocompleteHandler
}