/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: jasmine
 *****************************************************************/
@file:Suppress("unused")
package com.gridnine.jasmine.server.core.web

import com.gridnine.jasmine.server.core.app.Disposable
import com.gridnine.jasmine.server.core.app.Environment
import java.net.URL

class WebApplication (val path:String, val docBase:URL, val classLoader: ClassLoader)

class WebServerConfig {

    private val applications = ArrayList<WebApplication>()

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