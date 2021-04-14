/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.server.core.ui.widgets

import com.gridnine.jasmine.common.core.meta.DomainMetaRegistry
import com.gridnine.jasmine.common.core.model.BaseIndex

interface AutocompleteHandler{
    fun getIndexClassName():String
    fun getAutocompleteFieldName():String

    companion object{
        fun createMetadataBasedAutocompleteHandler(objectId: String): AutocompleteHandler {
            return object: AutocompleteHandler {

                override fun getIndexClassName(): String {
                    return DomainMetaRegistry.get().indexes.values.find { it.document == objectId }!!.id
                }

                override fun getAutocompleteFieldName(): String {
                    return BaseIndex.documentField+"Caption"
                }

            }
        }
    }
}

lateinit var restAutocompleteUrl:String