/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: jasmine
 *****************************************************************/
@file:Suppress("unused")
package com.gridnine.jasmine.server.core.web

import com.gridnine.jasmine.server.core.app.Disposable
import com.gridnine.jasmine.server.core.app.Environment
import java.net.URL
import java.util.logging.Filter
import kotlin.reflect.KClass

class WebApplication (val path:String, val docBase:URL, val classLoader: ClassLoader)

class WebAppFilter(val name:String, val cls:KClass<*>)
class WebServerConfig {

    private val applications = ArrayList<WebApplication>()

    val globalFilters = arrayListOf<WebAppFilter>()

    fun getApplications(): List<WebApplication> {
        return applications
    }

    fun addApplication(app: WebApplication) {
        applications.add(app)
    }

    companion object {

        fun get(): WebServerConfig {
            return Environment.getPublished(WebServerConfig::class)
        }
    }

}

interface WebServer : Disposable