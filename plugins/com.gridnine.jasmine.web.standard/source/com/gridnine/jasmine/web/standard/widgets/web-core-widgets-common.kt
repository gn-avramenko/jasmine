/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.web.standard.widgets

import com.gridnine.jasmine.common.core.meta.DomainMetaRegistryJS
import com.gridnine.jasmine.common.core.model.BaseIndexJS

abstract class BaseWidgetConfiguration{
    var width:String? = null
    var height:String? = null
}

interface AutocompleteHandler{
    fun getIndexClassName():String
    fun getAutocompleteFieldName():String

    companion object{
        fun createMetadataBasedAutocompleteHandler(objectId: String):AutocompleteHandler{
            return object:AutocompleteHandler{

                val indexClassName = DomainMetaRegistryJS.get().indexes.values.find { it.document == objectId }!!.id.substringBeforeLast("JS")
                override fun getIndexClassName(): String {
                    return indexClassName
                }

                override fun getAutocompleteFieldName(): String {
                    return BaseIndexJS.documentField+"Caption"
                }

            }
        }
    }
}