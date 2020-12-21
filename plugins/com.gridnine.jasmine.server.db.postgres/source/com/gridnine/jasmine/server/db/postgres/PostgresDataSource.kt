/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.server.db.postgres

import com.gridnine.jasmine.server.core.app.Disposable
import com.mchange.v2.c3p0.ComboPooledDataSource
import org.postgresql.Driver
import javax.sql.DataSource

class PostgresDataSource(private val delegate:ComboPooledDataSource):DataSource by delegate,Disposable {
    override fun dispose() {
        delegate.close()
    }

    companion object{
        fun createDataSource(connectionUrl:String, poolSize:Int = 5, user:String, password:String):PostgresDataSource{
            val cpds = ComboPooledDataSource()
            cpds.driverClass = Driver::class.qualifiedName!!
            cpds.jdbcUrl = connectionUrl
            cpds.initialPoolSize = 1
            cpds.acquireIncrement = 5
            cpds.minPoolSize = 1
            cpds.maxPoolSize = poolSize
            cpds.user = user
            cpds.password = password
            cpds.isAutoCommitOnClose = false

            return PostgresDataSource(cpds)
        }
    }
}


