/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.web.server.widgets

import com.gridnine.jasmine.server.core.model.domain.BaseIndex
import com.gridnine.jasmine.server.core.model.domain.DomainMetaRegistry

interface ServerUiAutocompleteHandler{
    fun getIndexClassName():String
    fun getAutocompleteFieldName():String

    companion object{
        fun createMetadataBasedAutocompleteHandler(objectId: String):ServerUiAutocompleteHandler{
            return object:ServerUiAutocompleteHandler{

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