/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.web.core.remote

import com.gridnine.jasmine.web.core.common.ActivatorJS
import com.gridnine.jasmine.web.core.common.EnvironmentJS
import com.gridnine.jasmine.web.core.common.RegistryJS
import kotlinx.browser.document
import kotlinx.coroutines.await
import kotlinx.coroutines.delay
import kotlin.js.Promise

internal enum class PluginStatus{
    LOADING,
    LOADED
}
@Suppress("UnsafeCastFromDynamic")
class WebPluginsHandler{

    private val pluginsStatuses = hashMapOf<String, PluginStatus>()

    suspend fun loadPluginForClass(className:String){
        val res = RpcManager.get().postDynamic("core_core_getPluginUrl","""{
         "className":"$className"   
        }""".trimIndent())
        val url = res.url as String
        val pluginId = res.pluginId as String
        loadPlugin(pluginId, url)
    }

    private suspend fun loadPlugin(pluginId: String, url: String) {
        if(pluginsStatuses[pluginId] == PluginStatus.LOADED){
            return
        }
        if(pluginsStatuses[pluginId] == PluginStatus.LOADING){
            while(pluginsStatuses[pluginId] != PluginStatus.LOADED){
                delay(100)
            }
            return
        }
        pluginsStatuses[pluginId] = PluginStatus.LOADING
        Promise<Unit>{ resolve, _ ->
            val script = document.createElement("script")
            script.asDynamic().onload = {

                resolve(Unit)
            }
            script.asDynamic().src = url
            document.asDynamic().head.appendChild(script)
        }.await()
        RegistryJS.get().get(ActivatorJS.TYPE, pluginId)!!.activate()
        pluginsStatuses[pluginId] = PluginStatus.LOADED

    }

    companion object{
        fun get() = EnvironmentJS.getPublished(WebPluginsHandler::class)
    }
}