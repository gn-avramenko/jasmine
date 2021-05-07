/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.web.standard.activator

import com.gridnine.jasmine.web.core.common.ActivatorJS
import com.gridnine.jasmine.web.core.common.EnvironmentJS
import com.gridnine.jasmine.web.core.common.RegistryJS
import com.gridnine.jasmine.web.core.remote.WebCoreMetaRegistriesUpdater
import com.gridnine.jasmine.web.standard.DomainReflectionUtilsJS
import com.gridnine.jasmine.web.standard.RestReflectionUtilsJS
import com.gridnine.jasmine.web.standard.list.WebListMainFrameTabHandler
import com.gridnine.jasmine.web.standard.mainframe.WebActionsHandler


const val pluginId = "com.gridnine.jasmine.web.standard"

fun main(){
    RegistryJS.get().register(WebStandardActivator())
}

class WebStandardActivator : ActivatorJS{
    override suspend fun activate() {
        EnvironmentJS.publish(WebActionsHandler())
        DomainReflectionUtilsJS.registerWebDomainClasses()
        RestReflectionUtilsJS.registerWebRestClasses()
        WebCoreMetaRegistriesUpdater.updateMetaRegistries(pluginId)
        RegistryJS.get().register(WebListMainFrameTabHandler())
        console.log("web standard activated")
    }

    override fun getId(): String {
        return pluginId
    }
}
