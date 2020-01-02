/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/
@file:Suppress("unused")

package com.gridnine.jasmine.server.sandbox.activator

import com.gridnine.jasmine.server.core.app.IPluginActivator
import com.gridnine.jasmine.server.core.web.WebApplication
import com.gridnine.jasmine.server.core.web.WebServerConfig
import java.lang.IllegalArgumentException
import java.util.*

class SandboxActivator:IPluginActivator{
    override fun configure(config: Properties) {
        val easyuiApp = WebApplication("/sandbox/easyui", javaClass.classLoader.getResource("sb_easyui")
                ?:throw IllegalArgumentException("unable to load resource sb_easyui"),
                javaClass.classLoader)
        WebServerConfig.get().addApplication(easyuiApp)
        val webCoreApp = WebApplication("/web-core", javaClass.classLoader.getResource("webapp-core")
                ?:throw IllegalArgumentException("unable to load resource webapp-core"),
                javaClass.classLoader)
        WebServerConfig.get().addApplication(webCoreApp)
        val sandboxApp = WebApplication("/web-sandbox", javaClass.classLoader.getResource("web-sandbox")
                ?:throw IllegalArgumentException("unable to load resource web-sandbox"),
                javaClass.classLoader)
        WebServerConfig.get().addApplication(sandboxApp)
        val easyuiWebapp = WebApplication("/web-easyui-libs", javaClass.classLoader.getResource("easyui-libs")
                ?:throw IllegalArgumentException("unable to load resource easyui-libs"),
                javaClass.classLoader)
        WebServerConfig.get().addApplication(easyuiWebapp)

        val easyuiScriptWebapp = WebApplication("/web-easyui-script", javaClass.classLoader.getResource("easyui-script")
                ?:throw IllegalArgumentException("unable to load resource easyui-script"),
                javaClass.classLoader)
        WebServerConfig.get().addApplication(easyuiScriptWebapp)
    }
}