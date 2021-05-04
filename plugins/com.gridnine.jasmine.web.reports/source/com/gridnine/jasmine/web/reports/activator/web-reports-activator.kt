/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.web.reports.activator

import com.gridnine.jasmine.web.core.common.ActivatorJS
import com.gridnine.jasmine.web.core.common.RegistryJS
import com.gridnine.jasmine.web.core.remote.WebCoreMetaRegistriesUpdater
import com.gridnine.jasmine.web.reports.RestReflectionUtilsJS


const val pluginId = "com.gridnine.jasmine.web.reports"

fun main(){
    RegistryJS.get().register(WebReportsActivator())
}

class WebReportsActivator : ActivatorJS{
    override suspend fun activate() {
        RestReflectionUtilsJS.registerWebRestClasses()
        WebCoreMetaRegistriesUpdater.updateMetaRegistries(pluginId)
        console.log("web reports activated")
    }

    override fun getId(): String {
        return pluginId
    }
}
