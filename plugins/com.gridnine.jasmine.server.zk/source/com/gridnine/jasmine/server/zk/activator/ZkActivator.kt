/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.server.zk.activator

import com.gridnine.jasmine.common.core.app.Environment
import com.gridnine.jasmine.common.core.app.IPluginActivator
import com.gridnine.jasmine.server.core.ui.components.UiLibraryAdapter
import com.gridnine.jasmine.server.zk.ui.components.ZkUiLibraryAdapter
import java.util.*

class ZkActivator : IPluginActivator {
    override fun configure(config: Properties) {
        Environment.publish(UiLibraryAdapter::class, ZkUiLibraryAdapter())
    }
}