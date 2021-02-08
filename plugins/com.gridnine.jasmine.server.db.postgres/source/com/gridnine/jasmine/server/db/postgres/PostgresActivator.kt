/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.server.db.postgres

import com.gridnine.jasmine.server.core.app.Environment
import com.gridnine.jasmine.server.core.app.IPluginActivator
import com.gridnine.jasmine.server.core.storage.Database
import com.gridnine.jasmine.server.core.storage.Storage
import com.gridnine.jasmine.server.core.storage.impl.StorageImpl
import com.gridnine.jasmine.server.core.storage.impl.jdbc.JdbcDatabase
import com.gridnine.jasmine.server.core.storage.impl.jdbc.JdbcDialect
import java.io.File
import java.util.*
import javax.sql.DataSource

class PostgresActivator :IPluginActivator{
    override fun configure(config: Properties) {
        val prop = config.getProperty("db-dialect")
        if(prop == null || prop != "postgres"){
            return
        }
        val file = File(Environment.rootFolder, "data/db")
        if (!file.exists()) {
            file.mkdirs()
        }
        Environment.publish(DataSource::class, PostgresDataSource.createDataSource(connectionUrl = config.getProperty("db-connection-url"), user = config.getProperty("db-user"), password = config.getProperty("db-password")));
        Environment.publish(JdbcDialect::class, PostgresDbDialect())
        Environment.publish(Database::class, JdbcDatabase())
        Environment.publish(Storage::class, StorageImpl())
    }
}