/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.web.standard.mainframe

import com.gridnine.jasmine.common.core.meta.UiMetaRegistryJS
import com.gridnine.jasmine.common.core.model.SelectItemJS
import com.gridnine.jasmine.common.standard.model.rest.*
import com.gridnine.jasmine.web.core.common.EnvironmentJS
import com.gridnine.jasmine.web.core.remote.WebPluginsHandler
import com.gridnine.jasmine.web.standard.StandardRestClient

class WebOptionsHandler{

    private val cache = hashMapOf<String, List<SelectItemJS>>()

    suspend fun getOptionsFor(group:String):List<SelectItemJS>{
        return cache.getOrPut(group){
            StandardRestClient.standard_standard_getOptions(GetOptionsRequestJS().apply {
                groupId = group
            }).options.map { SelectItemJS(it.id, it.text) }

        }
    }

    suspend fun ensureOptionLoaded(groupId: String, optionId:String){
        if(UiMetaRegistryJS.get().optionsGroups[groupId]?.options?.any{it.id == optionId} == true){
            return
        }
        WebPluginsHandler.get().loadPluginForId("options-$groupId-$optionId")
    }

    companion object{
        fun get() = EnvironmentJS.getPublished(WebOptionsHandler::class)
    }
}