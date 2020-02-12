/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: jasmine
 *****************************************************************/
@file:Suppress("unused")

package com.gridnine.jasmine.server.core.utils

import java.util.concurrent.ConcurrentHashMap
import kotlin.reflect.KClass
import kotlin.reflect.full.primaryConstructor

object ReflectionUtils{

    private val cache = ConcurrentHashMap<String, KClass<*>?>()

    fun<T:Any> newInstance(className: String):T{
        return getClass<T>(className).primaryConstructor?.call()?:throw IllegalStateException("no primary constructor in class $className")
    }

    @Suppress("UNCHECKED_CAST")
    fun<T:Any> getClass(className:String):KClass<T>{
        var cleanClassName = className
        if(className.indexOf("<") != -1){
            cleanClassName = className.substringBefore("<")
        }
        return cache.getOrPut(cleanClassName, {
             Class.forName(cleanClassName).kotlin
        }) as KClass<T>
    }

    fun safeGetEnum(className:String, element:String):Enum<*>?{
        val cls = getClass<Any>(className)
        cls.java.enumConstants.forEach {
            if((it as Enum<*>).name == element){
                return  it
            }
        }
        return null
    }

}