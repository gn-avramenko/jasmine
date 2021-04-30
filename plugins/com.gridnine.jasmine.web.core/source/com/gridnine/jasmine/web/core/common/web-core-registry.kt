/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

@file:Suppress("UNCHECKED_CAST")

package com.gridnine.jasmine.web.core.common

@Suppress("unused")
open class RegistryItemTypeJS<T:Any>(val id:String)

interface RegistryItemJS<T:Any> {
    fun getType(): RegistryItemTypeJS<T>
    fun getId(): String
}


class RegistryJS{
    private val registry = hashMapOf<String, LinkedHashMap<String, RegistryItemJS<*>>>()

    fun register(item:RegistryItemJS<*>){
        registry.getOrPut(item.getType().id, { linkedMapOf() })[item.getId()] = item
    }

    fun<T:Any> allOf(type: RegistryItemTypeJS<T>):List<T> = (registry[type.id]?.values?.toList() as List<T>?)?: emptyList()

    fun <T:Any> get(type:RegistryItemTypeJS<T>, id:String)= registry[type.id]?.get(id) as T?

    companion object{
        fun get() = EnvironmentJS.getPublished(RegistryJS::class)
    }
}