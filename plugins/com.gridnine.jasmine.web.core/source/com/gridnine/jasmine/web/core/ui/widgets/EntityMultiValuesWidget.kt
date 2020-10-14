/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.web.core.ui.widgets

import com.gridnine.jasmine.server.core.model.domain.ObjectReferenceJS
import com.gridnine.jasmine.server.standard.model.rest.AutocompleteRequestJS
import com.gridnine.jasmine.web.core.StandardRestClient
import com.gridnine.jasmine.web.core.ui.AutocompleteHandler
import com.gridnine.jasmine.web.core.ui.UiLibraryAdapter
import com.gridnine.jasmine.web.core.ui.WebComponent
import com.gridnine.jasmine.web.core.ui.components.*
import kotlin.js.Promise

class EntityMultiValuesWidget(parent:WebComponent, configure:EntityMultiValuesWidgetConfiguration.()->Unit
                                    , private val delegate: WebSelect = UiLibraryAdapter.get().createSelect(parent, convertConfiguration(configure)) ):WebComponent by delegate{
    private val handler:AutocompleteHandler

    init {
        val conf = EntityMultiValuesWidgetConfiguration();
        conf.configure()
        handler = conf.handler
        delegate.setLoader { value ->
            Promise{ resolve, _ ->
                val request = AutocompleteRequestJS()
                request.autocompleteFieldName = handler.getAutocompleteFieldName()
                request.listId = handler.getIndexClassName()
                request.limit = 10
                request.pattern=value
                StandardRestClient.standard_standard_autocomplete(request).then{
                    resolve(it.items.map { toSelectItem(it.document) })
                }
            }
        }
    }

    companion object{
        fun  convertConfiguration(configure: EntityMultiValuesWidgetConfiguration.() -> Unit): WebSelectConfiguration.() -> Unit {
            val conf = EntityMultiValuesWidgetConfiguration();
            conf.configure()
            return {
                width = conf.width
                height = conf.height
                mode = ComboboxMode.REMOTE
                showClearIcon = conf.showClearIcon
                editable = true
                multiple = true
                hasDownArrow = false
            }
        }
        private fun toSelectItem(ref:ObjectReferenceJS):SelectItemJS{
            return SelectItemJS("${ref.type}||${ref.uid}", ref.caption?:"???")
        }
    }
}

class EntityMultiValuesWidgetConfiguration{
    var width:String? = null
    var height:String? = null
    var showClearIcon = false
    lateinit var handler: AutocompleteHandler
}