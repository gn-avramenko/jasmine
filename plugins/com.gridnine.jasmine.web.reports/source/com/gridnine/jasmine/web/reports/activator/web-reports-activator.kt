/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.web.reports.activator

import com.gridnine.jasmine.web.core.common.ActivatorJS
import com.gridnine.jasmine.web.core.common.RegistryJS
import com.gridnine.jasmine.web.core.remote.WebCoreMetaRegistriesUpdater
import com.gridnine.jasmine.web.reports.DomainReflectionUtilsJS
import com.gridnine.jasmine.web.reports.MiscReflectionUtilsJS
import com.gridnine.jasmine.web.reports.RestReflectionUtilsJS
import com.gridnine.jasmine.web.reports.UiReflectionUtilsJS
import com.gridnine.jasmine.web.reports.editor.ReportDescriptionMainFrameTabHandler
import com.gridnine.jasmine.web.reports.list.WebReportsListMainFrameTabHandler
import com.gridnine.jasmine.web.reports.workspaceEditor.WorkspaceReportsItemVariantHandler
import kotlinx.browser.window


const val pluginId = "com.gridnine.jasmine.web.reports"

fun main(){
    if(window.asDynamic().builtByWebpack != true){
        reportsMain()
    }
}

fun reportsMain(){
    RegistryJS.get().register(WebReportsActivator())
}

class WebReportsActivator : ActivatorJS{
    override suspend fun activate() {
        RestReflectionUtilsJS.registerWebRestClasses()
        DomainReflectionUtilsJS.registerWebDomainClasses()
        MiscReflectionUtilsJS.registerWebMiscClasses()
        UiReflectionUtilsJS.registerWebUiClasses()
        WebCoreMetaRegistriesUpdater.updateMetaRegistries(pluginId)
        RegistryJS.get().register(WebReportsListMainFrameTabHandler())
        RegistryJS.get().register(WorkspaceReportsItemVariantHandler())
        RegistryJS.get().register(ReportDescriptionMainFrameTabHandler())
        console.log("web reports activated")
    }

    override fun getId(): String {
        return pluginId
    }
}
