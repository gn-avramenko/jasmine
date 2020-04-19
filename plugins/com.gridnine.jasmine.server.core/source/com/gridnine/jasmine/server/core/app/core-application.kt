/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/
@file:Suppress("unused")
package com.gridnine.jasmine.server.core.app
import java.util.*

interface IExtension {

    val plugin: IPlugin

    fun getParameters(paramName: String): List<String>
}


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

interface IApplicationMetadataProvider:Disposable{

    val plugins: List<IPlugin>

    fun getExtensions(extensionPointId: String): List<IExtension>

    override fun dispose() {
        wrapper.dispose()
    }

    companion object {

        private var wrapper = PublishableWrapper(IApplicationMetadataProvider::class)

        fun get() = wrapper.get()
    }
}

