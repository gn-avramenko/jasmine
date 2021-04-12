/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.server.db.h2

import com.gridnine.jasmine.common.core.app.Disposable
import com.mchange.v2.c3p0.ComboPooledDataSource
import org.h2.Driver
import javax.sql.DataSource

class H2DataSource(private val delegate:ComboPooledDataSource):DataSource by delegate,Disposable {

    override fun dispose() {
        delegate.close()
    }

    companion object{
        fun createDataSource(connectionUrl:String, poolSize:Int = 5):H2DataSource{
            val cpds = ComboPooledDataSource()
            cpds.driverClass = Driver::class.qualifiedName!!
            cpds.jdbcUrl = connectionUrl
            cpds.initialPoolSize = 1
            cpds.acquireIncrement = 5
            cpds.minPoolSize = 1
            cpds.maxPoolSize = poolSize
            cpds.user = "sa"
            cpds.password = "sa"
            cpds.isAutoCommitOnClose = false

            return H2DataSource(cpds)
        }
    }
}

