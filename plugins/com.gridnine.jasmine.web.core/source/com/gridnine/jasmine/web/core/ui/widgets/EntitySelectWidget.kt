/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.web.core.ui.widgets

import com.gridnine.jasmine.server.core.model.common.SelectItemJS
import com.gridnine.jasmine.server.core.model.domain.ObjectReferenceJS
import com.gridnine.jasmine.server.core.model.ui.EntitySelectBoxConfigurationJS
import com.gridnine.jasmine.server.standard.model.rest.AutocompleteRequestJS
import com.gridnine.jasmine.web.core.StandardRestClient
import com.gridnine.jasmine.web.core.mainframe.MainFrame
import com.gridnine.jasmine.web.core.ui.*
import com.gridnine.jasmine.web.core.ui.components.*
import kotlin.js.Promise

class EntitySelectWidget(private val parent:WebComponent, configure:EntitySelectWidgetConfiguration.()->Unit):WebComponent {
    private val webSelect:WebSelect
    private val delegate:WebGridLayoutContainer

    init {
        val conf = EntitySelectWidgetConfiguration();
        conf.configure()
        delegate = UiLibraryAdapter.get().createGridLayoutContainer(this){
            width = conf.width
            height = conf.height
            noPadding = true
        }
        delegate.defineColumn("100%")
        delegate.defineColumn("auto")
        delegate.addRow()
        webSelect =UiLibraryAdapter.get().createSelect(delegate){
            width = "100%"
            height = "100%"
            mode = SelectDataType.REMOTE
            showClearIcon = conf.showClearIcon
            editable = true
            multiple = false
            hasDownArrow = false
        }
        webSelect.setLoader { value ->
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

        delegate.addCell(WebGridLayoutCell(webSelect))
        val button = UiLibraryAdapter.get().createLinkButton(delegate){
            icon = "core:link"
        }
        button.setHandler {
            getValue()?.let {
                MainFrame.get().openTab(it)
            }
        }
        delegate.addCell(WebGridLayoutCell(button))
    }

    fun getValue():ObjectReferenceJS?{
        return webSelect.getValues().map { toObjectReference(it) }.firstOrNull()
    }

    fun setValue(value: ObjectReferenceJS?) {
        webSelect.setValues(if (value == null) emptyList() else arrayListOf(toSelectItem(value)))
    }

    override fun getParent(): WebComponent? {
        return parent
    }

    override fun getChildren(): List<WebComponent> {
        return arrayListOf(delegate)
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

    fun configure(config: EntitySelectBoxConfigurationJS?){
        config?.let {
            webSelect.setEnabled(!config.notEditable)
        }
    }

    fun showValidation(value:String?){
        webSelect.showValidation(value)
    }

    fun setReadonly(value:Boolean){
        webSelect.setEnabled(!value)
    }
    private fun toSelectItem(ref:ObjectReferenceJS): SelectItemJS {
        return SelectItemJS("${ref.type}||${ref.uid}", ref.caption?:"???")
    }
    private fun toObjectReference(item: SelectItemJS):ObjectReferenceJS{
        return ObjectReferenceJS(item.id.substringBefore("||"), item.id.substringAfter("||"), item.text)
    }


}

class EntitySelectWidgetConfiguration{
    var width:String? = null
    var height:String? = null
    var showClearIcon = true
    lateinit var handler: AutocompleteHandler
}