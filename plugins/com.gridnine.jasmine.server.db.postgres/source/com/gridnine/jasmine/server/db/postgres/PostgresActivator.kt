/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.server.db.postgres

import com.gridnine.jasmine.common.core.app.IPluginActivator
import com.gridnine.jasmine.server.core.storage.StorageRegistry
import java.util.*

class PostgresActivator : IPluginActivator {
    override fun configure(config: Properties) {
        StorageRegistry.get().register(PostgresDataSourceProvider())
    }
}