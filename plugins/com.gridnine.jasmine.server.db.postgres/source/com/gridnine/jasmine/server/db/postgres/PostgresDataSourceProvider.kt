/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.server.db.postgres

import com.gridnine.jasmine.common.core.app.ConfigurationProvider
import com.gridnine.jasmine.server.core.storage.C3PoDataSource
import com.gridnine.jasmine.server.core.storage.DataSourceProvider
import com.gridnine.jasmine.server.core.storage.jdbc.JdbcDialect
import com.mchange.v2.c3p0.ComboPooledDataSource
import org.postgresql.Driver

class PostgresDataSourceProvider():DataSourceProvider{


    override fun createDataSource(): C3PoDataSource {
        val cpds = ComboPooledDataSource()
        cpds.driverClass = Driver::class.qualifiedName!!
        cpds.jdbcUrl = ConfigurationProvider.get().getProperty("db.postgres.connectionUrl")
        cpds.initialPoolSize = 1
        cpds.acquireIncrement = 5
        cpds.minPoolSize = 1
        cpds.maxPoolSize = (ConfigurationProvider.get().getProperty("db.postgres.poolSize")?:"5").toInt()
        cpds.user = ConfigurationProvider.get().getProperty("db.postgres.user")
        cpds.password = ConfigurationProvider.get().getProperty("db.postgres.password")
        cpds.isAutoCommitOnClose = false
        return C3PoDataSource(cpds)
    }

    override fun createDialect(): JdbcDialect {
        return PostgresDbDialect()
    }

    override fun getId(): String {
        return "postgres"
    }
}


