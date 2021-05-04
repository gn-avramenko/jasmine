/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.web.standard.activator

import com.gridnine.jasmine.web.core.common.ActivatorJS
import com.gridnine.jasmine.web.core.common.RegistryJS
import com.gridnine.jasmine.web.core.remote.WebCoreMetaRegistriesUpdater
import com.gridnine.jasmine.web.standard.DomainReflectionUtilsJS
import com.gridnine.jasmine.web.standard.RestReflectionUtilsJS


const val pluginId = "com.gridnine.jasmine.web.standard"

fun main(){
    RegistryJS.get().register(WebStandardActivator())
}

class WebStandardActivator : ActivatorJS{
    override suspend fun activate() {
        DomainReflectionUtilsJS.registerWebDomainClasses()
        RestReflectionUtilsJS.registerWebRestClasses()
        WebCoreMetaRegistriesUpdater.updateMetaRegistries(pluginId)
        console.log("web standard activated")
    }

    override fun getId(): String {
        return pluginId
    }
}
