/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: jasmine
 *****************************************************************/
@file:Suppress("unused")
package com.gridnine.jasmine.server.core.web

import com.gridnine.jasmine.common.core.app.Disposable
import com.gridnine.jasmine.common.core.app.Environment
import com.gridnine.jasmine.common.core.app.PublishableWrapper
import java.net.URL
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

interface WebServer:Disposable{
    override fun dispose() {
        wrapper.dispose()
    }

    companion object {
        private val wrapper = PublishableWrapper(WebServer::class)
        fun get() = wrapper.get()
    }
}