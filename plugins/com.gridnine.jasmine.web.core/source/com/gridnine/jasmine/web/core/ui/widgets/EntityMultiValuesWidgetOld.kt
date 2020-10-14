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
import com.gridnine.jasmine.web.core.ui.components.ComboboxMode
import com.gridnine.jasmine.web.core.ui.components.SelectItemJS
import com.gridnine.jasmine.web.core.ui.components.WebTagBox
import com.gridnine.jasmine.web.core.ui.components.WebTagBoxConfiguration
import kotlin.js.Promise

class EntityMultiValuesWidgetOld(parent:WebComponent, configure:EntityMultiValuesWidgetConfigurationOld.()->Unit
                                    , private val delegate: WebTagBox = UiLibraryAdapter.get().createTagBox(parent, convertConfiguration(configure)) ):WebComponent by delegate{
    private val handler:AutocompleteHandler

    private var ignoreSearchRequest = false

    private val selectedValues = arrayListOf<SelectItemJS>()
    fun getValues():List<ObjectReferenceJS> {
        return delegate.getValues().map { toReference(it) }
    }

    fun setValues(values: List<ObjectReferenceJS>) {
        selectedValues.clear()
        selectedValues.addAll(values.map { toSelectItem(it)})
        ignoreSearchRequest = true
        //delegate.setPossibleValues(selectedValues)
        delegate.setValues(selectedValues.map { it.id })
        ignoreSearchRequest = false
    }

    init {
        ignoreSearchRequest = true
        val conf = EntityMultiValuesWidgetConfigurationOld();
        conf.configure()
        handler = conf.handler
        delegate.setLoader { value ->
           Promise{ resolve, _ ->
               if(ignoreSearchRequest){
                   resolve(selectedValues)
                   return@Promise
               }
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
        ignoreSearchRequest = false
    }


    companion object{
        fun  convertConfiguration(configure: EntityMultiValuesWidgetConfigurationOld.() -> Unit): WebTagBoxConfiguration.() -> Unit {
            val conf = EntityMultiValuesWidgetConfigurationOld();
            conf.configure()
            return {
                width = conf.width
                height = conf.height
                mode = ComboboxMode.REMOTE
                showClearIcon = conf.showClearIcon
                editable = true
                hasDownArrow = false
                limitToList = true
            }
        }

        private fun toSelectItem(ref:ObjectReferenceJS):SelectItemJS{
            return SelectItemJS("${ref.type}||${ref.uid}||${ref.caption}", ref.caption?:"???")
        }

        private fun toReference(item:String):ObjectReferenceJS{
            val comps = item.split("||")
            return ObjectReferenceJS(comps[0],comps[1],comps[2])
        }
    }
}

class EntityMultiValuesWidgetConfigurationOld{
    var width:String? = null
    var height:String? = null
    var showClearIcon = false
    lateinit var handler: AutocompleteHandler
}