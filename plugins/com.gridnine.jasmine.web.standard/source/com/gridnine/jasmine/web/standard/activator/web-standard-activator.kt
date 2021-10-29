/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.web.standard.activator

import com.gridnine.jasmine.web.core.common.ActivatorJS
import com.gridnine.jasmine.web.core.common.EnvironmentJS
import com.gridnine.jasmine.web.core.common.RegistryJS
import com.gridnine.jasmine.web.core.remote.CoroutineExceptionHandler
import com.gridnine.jasmine.web.core.remote.WebCoreMetaRegistriesUpdater
import com.gridnine.jasmine.web.standard.DomainReflectionUtilsJS
import com.gridnine.jasmine.web.standard.RestReflectionUtilsJS
import com.gridnine.jasmine.web.standard.UiReflectionUtilsJS
import com.gridnine.jasmine.web.standard.WebMessagesInitializerJS
import com.gridnine.jasmine.web.standard.editor.ObjectEditorMainFrameTabHandler
import com.gridnine.jasmine.web.standard.editor.ObjectVersionViewerMainFrameTabHandler
import com.gridnine.jasmine.web.standard.editor.WebEditorInterceptorsRegistry
import com.gridnine.jasmine.web.standard.list.WebListMainFrameTabHandler
import com.gridnine.jasmine.web.standard.mainframe.WebActionsHandler
import com.gridnine.jasmine.web.standard.mainframe.WebOptionsHandler
import com.gridnine.jasmine.web.standard.workspaceEditor.DateDynamicValueEditorHandler
import com.gridnine.jasmine.web.standard.workspaceEditor.WorkspaceEditorTabHandler
import com.gridnine.jasmine.web.standard.workspaceEditor.WorkspaceListItemVariantHandler
import kotlinx.browser.window


const val pluginId = "com.gridnine.jasmine.web.standard"

fun standardMain(){
    RegistryJS.get().register(WebStandardActivator())
}

class WebStandardActivator : ActivatorJS{
    override suspend fun activate() {
        EnvironmentJS.publish(CoroutineExceptionHandler::class, StandardCoroutineExceptionHandler())
        EnvironmentJS.publish(WebActionsHandler())
        EnvironmentJS.publish(WebOptionsHandler())
        EnvironmentJS.publish(WebEditorInterceptorsRegistry())
        WebCoreMetaRegistriesUpdater.updateMetaRegistries(pluginId)
        DomainReflectionUtilsJS.registerWebDomainClasses()
        RestReflectionUtilsJS.registerWebRestClasses()
        UiReflectionUtilsJS.registerWebUiClasses()
        WebMessagesInitializerJS.initialize()
        RegistryJS.get().register(WebListMainFrameTabHandler())
        RegistryJS.get().register(ObjectEditorMainFrameTabHandler())
        RegistryJS.get().register(ObjectVersionViewerMainFrameTabHandler())
        RegistryJS.get().register(WorkspaceEditorTabHandler())
        RegistryJS.get().register(WorkspaceListItemVariantHandler())
        RegistryJS.get().register(DateDynamicValueEditorHandler())
        console.log("web standard activated")
    }

    override fun getId(): String {
        return pluginId
    }
}
