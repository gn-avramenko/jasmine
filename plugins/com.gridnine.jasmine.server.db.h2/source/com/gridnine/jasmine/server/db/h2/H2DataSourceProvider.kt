/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.server.db.h2

import com.gridnine.jasmine.common.core.app.ConfigurationProvider
import com.gridnine.jasmine.server.core.storage.C3PoDataSource
import com.gridnine.jasmine.server.core.storage.DataSourceProvider
import com.gridnine.jasmine.server.core.storage.jdbc.JdbcDialect
import com.mchange.v2.c3p0.ComboPooledDataSource
import org.h2.Driver

class H2DataSourceProvider:DataSourceProvider{

    override fun createDataSource(): C3PoDataSource {
        val cpds = ComboPooledDataSource()
        cpds.driverClass = Driver::class.qualifiedName!!
        cpds.jdbcUrl = ConfigurationProvider.get().getProperty("db.h2.connectionUrl")
        cpds.initialPoolSize = 1
        cpds.acquireIncrement = 5
        cpds.minPoolSize = 1
        cpds.maxPoolSize = (ConfigurationProvider.get().getProperty("db.h2.poolSize")?:"5").toInt()
        cpds.user = "sa"
        cpds.password = "sa"
        cpds.isAutoCommitOnClose = false
        return C3PoDataSource(cpds)
    }

    override fun createDialect(): JdbcDialect {
        return H2dbDialect()
    }

    override fun getId(): String {
       return "h2"
    }

}