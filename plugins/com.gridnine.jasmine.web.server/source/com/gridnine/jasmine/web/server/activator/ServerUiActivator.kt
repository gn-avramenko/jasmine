/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.web.server.activator

import com.gridnine.jasmine.server.core.app.Environment
import com.gridnine.jasmine.server.core.app.IPluginActivator
import com.gridnine.jasmine.web.server.registry.ServerUiClientRegistry
import java.util.*

class ServerUiActivator:IPluginActivator{
    override fun configure(config: Properties) {
        Environment.publish(ServerUiClientRegistry())
    }
}