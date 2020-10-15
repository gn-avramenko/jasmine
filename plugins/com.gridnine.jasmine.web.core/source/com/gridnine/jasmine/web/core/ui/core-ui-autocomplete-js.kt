/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/
@file:Suppress("unused", "UnsafeCastFromDynamic", "UNCHECKED_CAST")

package com.gridnine.jasmine.web.core.ui

import com.gridnine.jasmine.server.core.model.domain.BaseIndexJS
import com.gridnine.jasmine.server.core.model.domain.DomainMetaRegistryJS

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
                    return BaseIndexJS.document+"Caption"
                }

            }
        }
    }
}
