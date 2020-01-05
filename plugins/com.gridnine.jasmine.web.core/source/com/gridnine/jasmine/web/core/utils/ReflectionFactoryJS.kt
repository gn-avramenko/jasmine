/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/
@file:Suppress("unused", "UNCHECKED_CAST")
package com.gridnine.jasmine.web.core.utils

import com.gridnine.jasmine.web.core.application.EnvironmentJS
import kotlin.reflect.KClass


class ReflectionFactoryJS{
    companion object{
        fun get(): ReflectionFactoryJS = EnvironmentJS.getPublished(ReflectionFactoryJS::class)
    }
    private val factories:MutableMap<String, () -> Any> = hashMapOf()
    private val enumFactories:MutableMap<String, (String) -> Any> = hashMapOf()
    private val qualifiedNames:MutableMap<KClass<*>, String> = hashMapOf()
    fun registerClass(className:String, factory: () ->Any){
        factories[className] = factory
    }

    fun registerQualifiedName(cls: KClass<*>, className:String){
        qualifiedNames[cls] = className
    }


    fun registerEnum(className:String, factory: (String) ->Any){
        enumFactories[className] = factory
    }


    fun getFactory(className:String):(() ->Any){
        return factories[className]?:throw IllegalArgumentException("no factory registered for class $className")
    }


    fun getQualifiedClassName(cls:KClass<*>): String{
        return qualifiedNames[cls]?:throw IllegalArgumentException("$cls is not registered")
    }

    fun <E:Enum<E>> getEnum(enumClassName:String, itemName:String):E{
        return enumFactories[enumClassName]?.invoke(itemName) as E??:throw IllegalArgumentException("no enum factories is registered for  $enumClassName")
    }
}