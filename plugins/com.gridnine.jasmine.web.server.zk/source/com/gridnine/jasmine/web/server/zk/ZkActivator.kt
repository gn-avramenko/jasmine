/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.web.server.zk

import com.gridnine.jasmine.server.core.app.Environment
import com.gridnine.jasmine.server.core.app.IPluginActivator
import com.gridnine.jasmine.web.server.components.ServerUiLibraryAdapter
import com.gridnine.jasmine.web.server.zk.components.ZkServerUiLibraryAdapter
import java.util.*

class ZkActivator : IPluginActivator {
    override fun configure(config: Properties) {
        Environment.publish(ServerUiLibraryAdapter::class, ZkServerUiLibraryAdapter())
    }
}