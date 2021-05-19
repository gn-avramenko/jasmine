/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.web.reports.activator

import com.gridnine.jasmine.web.core.common.ActivatorJS
import com.gridnine.jasmine.web.core.common.RegistryJS
import com.gridnine.jasmine.web.core.remote.WebCoreMetaRegistriesUpdater
import com.gridnine.jasmine.web.reports.DomainReflectionUtilsJS
import com.gridnine.jasmine.web.reports.RestReflectionUtilsJS
import com.gridnine.jasmine.web.reports.list.WebReportsListMainFrameTabHandler
import com.gridnine.jasmine.web.reports.workspaceEditor.WorkspaceReportsItemVariantHandler


const val pluginId = "com.gridnine.jasmine.web.reports"

fun main(){
    RegistryJS.get().register(WebReportsActivator())
}

class WebReportsActivator : ActivatorJS{
    override suspend fun activate() {
        RestReflectionUtilsJS.registerWebRestClasses()
        DomainReflectionUtilsJS.registerWebDomainClasses()
        WebCoreMetaRegistriesUpdater.updateMetaRegistries(pluginId)
        RegistryJS.get().register(WebReportsListMainFrameTabHandler())
        RegistryJS.get().register(WorkspaceReportsItemVariantHandler())
        console.log("web reports activated")
    }

    override fun getId(): String {
        return pluginId
    }
}
