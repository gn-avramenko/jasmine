/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.server.db.h2

import com.gridnine.jasmine.common.core.app.IPluginActivator
import com.gridnine.jasmine.server.core.storage.StorageRegistry
import java.util.*

class H2Activator : IPluginActivator {
    override fun configure(config: Properties) {
        StorageRegistry.get().register(H2DataSourceProvider())
    }
}