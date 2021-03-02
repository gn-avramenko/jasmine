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
    fun getType(): ServerUiRegistryItemType<T>
    fun getId(): String
}



class ServerUiClientRegistry:Disposable{

    private val registry = hashMapOf<String, MutableMap<String, ServerUiRegistryItem<*>>>()


    fun register(item:ServerUiRegistryItem<*>){
        registry.getOrPut(item.getType().id, { hashMapOf()})[item.getId()] = item
    }

    fun<T:Any> allOf(type: ServerUiRegistryItemType<T>):List<T> = (registry[type.id]?.values?.toList() as List<T>?)?: emptyList()

    fun <T:Any> get(type:ServerUiRegistryItemType<T>, id:String)= registry[type.id]?.get(id) as T?

    fun <T:Any> get(type:ServerUiRegistryItemType<T>, cls:KClass<*>)= registry[type.id]?.get(cls.qualifiedName) as T?


    override fun dispose() {
        wrapper.dispose()
    }
    companion object {
        private val wrapper = PublishableWrapper(ServerUiClientRegistry::class)
        fun get() = wrapper.get()
    }
}