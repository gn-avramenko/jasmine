/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.web.server.common

import com.gridnine.jasmine.server.core.app.Disposable
import com.gridnine.jasmine.server.core.app.PublishableWrapper
import kotlin.reflect.KClass


open class ServerUiRegistryItemType<T:Any>(val id:String)

interface ServerUiRegistryItem<T:Any> {
    fun getType(): com.gridnine.jasmine.web.server.common.ServerUiRegistryItemType<T>
    fun getId(): String
}



class ServerUiRegistry:Disposable{

    private val registry = hashMapOf<String, MutableMap<String, com.gridnine.jasmine.web.server.common.ServerUiRegistryItem<*>>>()


    fun register(item: com.gridnine.jasmine.web.server.common.ServerUiRegistryItem<*>){
        registry.getOrPut(item.getType().id, { hashMapOf()})[item.getId()] = item
    }

    fun<T:Any> allOf(type: com.gridnine.jasmine.web.server.common.ServerUiRegistryItemType<T>):List<T> = (registry[type.id]?.values?.toList() as List<T>?)?: emptyList()

    fun <T:Any> get(type: com.gridnine.jasmine.web.server.common.ServerUiRegistryItemType<T>, id:String)= registry[type.id]?.get(id) as T?

    fun <T:Any> get(type: com.gridnine.jasmine.web.server.common.ServerUiRegistryItemType<T>, cls:KClass<*>)= registry[type.id]?.get(cls.qualifiedName) as T?


    override fun dispose() {
        com.gridnine.jasmine.web.server.common.ServerUiRegistry.Companion.wrapper.dispose()
    }
    companion object {
        private val wrapper = PublishableWrapper(com.gridnine.jasmine.web.server.common.ServerUiRegistry::class)
        fun get() = com.gridnine.jasmine.web.server.common.ServerUiRegistry.Companion.wrapper.get()
    }
}