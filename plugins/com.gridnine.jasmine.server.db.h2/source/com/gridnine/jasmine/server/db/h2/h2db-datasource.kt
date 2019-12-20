/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.server.db.h2

import com.gridnine.jasmine.server.core.app.Disposable
import com.mchange.v2.c3p0.ComboPooledDataSource
import java.sql.Driver

fun createH2DataSource(connectionUrl:String, poolSize:Int = 5):ComboPooledDataSource{
    val cpds = ComboPooledDataSource()
    cpds.driverClass = Driver::class.qualifiedName!!
    cpds.jdbcUrl = connectionUrl
    cpds.initialPoolSize = 1
    cpds.minPoolSize = 1
    cpds.maxPoolSize = poolSize
    return cpds
}

class H2DataSourceDisposable(val dataSource:ComboPooledDataSource):Disposable{
    override fun dispose() {
        try {
            dataSource.close()
        }catch (e:Exception){
            //noops
        }
    }
}