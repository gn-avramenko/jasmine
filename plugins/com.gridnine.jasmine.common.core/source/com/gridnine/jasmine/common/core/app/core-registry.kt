/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.common.core.app

import kotlin.reflect.KClass


open class RegistryItemType<T:Any>(val id:String)

interface RegistryItem<T:Any> {
    fun getType(): RegistryItemType<T>
    fun getId(): String
}


class Registry:Disposable{

    private val registry = hashMapOf<String, MutableMap<String, RegistryItem<*>>>()


    fun register(item: RegistryItem<*>){
        registry.getOrPut(item.getType().id, { hashMapOf()})[item.getId()] = item
    }

    fun<T:Any> allOf(type: RegistryItemType<T>):List<T> = (registry[type.id]?.values?.toList() as List<T>?)?: emptyList()

    fun <T:Any> get(type: RegistryItemType<T>, id:String)= registry[type.id]?.get(id) as T?

    fun <T:Any> get(type: RegistryItemType<T>, cls:KClass<*>)= registry[type.id]?.get(cls.qualifiedName) as T?


    override fun dispose() {
        wrapper.dispose()
    }
    companion object {
        private val wrapper = PublishableWrapper(Registry::class)
        fun get() = wrapper.get()
    }
}