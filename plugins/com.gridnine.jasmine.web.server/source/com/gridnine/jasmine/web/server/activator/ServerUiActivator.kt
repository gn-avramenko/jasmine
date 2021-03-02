/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.web.server.activator

import com.gridnine.jasmine.server.core.app.Environment
import com.gridnine.jasmine.server.core.app.IPluginActivator
import com.gridnine.jasmine.web.server.common.ServerUiClientRegistry
import com.gridnine.jasmine.web.server.mainframe.tools.ServerUiAdditionalMenuButton
import com.gridnine.jasmine.web.server.mainframe.tools.ServerUiSaveObjectEditorButton
import com.gridnine.jasmine.web.server.mainframe.tools.ServerUiShowVersionsMenuItem
import java.util.*

class ServerUiActivator:IPluginActivator{
    override fun configure(config: Properties) {
        Environment.publish(ServerUiClientRegistry())
        ServerUiClientRegistry.get().register(ServerUiSaveObjectEditorButton())
        ServerUiClientRegistry.get().register(ServerUiAdditionalMenuButton())
        ServerUiClientRegistry.get().register(ServerUiShowVersionsMenuItem())
    }
}