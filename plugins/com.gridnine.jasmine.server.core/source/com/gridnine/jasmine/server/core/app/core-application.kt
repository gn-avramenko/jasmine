/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/
@file:Suppress("unused")
package com.gridnine.jasmine.server.core.app
import java.util.*

interface IExtension {

    val classLoader: ClassLoader

    fun getParameters(paramName: String): List<String>
}

@SuppressWarnings("unused")
interface IPluginActivator {

    fun configure(config: Properties){
        //noops
    }


    fun activate(){
        //noops
    }
}

interface IPlugin{
    val pluginId:String
    val classLoader:ClassLoader
}

interface IApplicationMetadataProvider {

    val plugins: List<IPlugin>

    fun getExtensions(extensionPointId: String): List<IExtension>

    companion object {

        fun get(): IApplicationMetadataProvider {
            return Environment.getPublished(IApplicationMetadataProvider::class)
        }
    }
}

interface IApplication