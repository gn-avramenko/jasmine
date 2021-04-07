/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.server.standard.activator

import com.gridnine.jasmine.server.core.app.Environment
import com.gridnine.jasmine.server.core.app.IPluginActivator
import com.gridnine.jasmine.server.standard.helpers.ObjectEditorsRegistry
import com.gridnine.jasmine.web.server.components.ServerUiEditorInterceptorsRegistry
import com.gridnine.jasmine.web.server.mainframe.tools.ServerUiAdditionalMenuButton
import com.gridnine.jasmine.web.server.mainframe.tools.ServerUiDeleteListToolButton
import com.gridnine.jasmine.web.server.mainframe.tools.ServerUiSaveObjectEditorButton
import com.gridnine.jasmine.web.server.mainframe.tools.ServerUiShowVersionsMenuItem
import java.util.*

class StandardServerActivator : IPluginActivator{
    override fun configure(config: Properties) {
        Environment.publish(ObjectEditorsRegistry())
        Environment.publish(com.gridnine.jasmine.web.server.common.ServerUiRegistry())
        Environment.publish(ServerUiEditorInterceptorsRegistry())
        com.gridnine.jasmine.web.server.common.ServerUiRegistry.get().register(ServerUiSaveObjectEditorButton())
        com.gridnine.jasmine.web.server.common.ServerUiRegistry.get().register(ServerUiAdditionalMenuButton())
        com.gridnine.jasmine.web.server.common.ServerUiRegistry.get().register(ServerUiShowVersionsMenuItem())
        com.gridnine.jasmine.web.server.common.ServerUiRegistry.get().register(ServerUiDeleteListToolButton())
    }
}