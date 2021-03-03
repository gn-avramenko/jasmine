/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.web.server.activator

import com.gridnine.jasmine.server.core.app.Environment
import com.gridnine.jasmine.server.core.app.IPluginActivator
import com.gridnine.jasmine.web.server.common.ServerUiRegistry
import com.gridnine.jasmine.web.server.mainframe.tools.ServerUiAdditionalMenuButton
import com.gridnine.jasmine.web.server.mainframe.tools.ServerUiDeleteListToolButton
import com.gridnine.jasmine.web.server.mainframe.tools.ServerUiSaveObjectEditorButton
import com.gridnine.jasmine.web.server.mainframe.tools.ServerUiShowVersionsMenuItem
import java.util.*

class ServerUiActivator:IPluginActivator{
    override fun configure(config: Properties) {
        Environment.publish(ServerUiRegistry())
        ServerUiRegistry.get().register(ServerUiSaveObjectEditorButton())
        ServerUiRegistry.get().register(ServerUiAdditionalMenuButton())
        ServerUiRegistry.get().register(ServerUiShowVersionsMenuItem())
        ServerUiRegistry.get().register(ServerUiDeleteListToolButton())
    }
}