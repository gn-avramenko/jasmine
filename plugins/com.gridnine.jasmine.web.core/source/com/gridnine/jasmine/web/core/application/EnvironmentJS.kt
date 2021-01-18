/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/
@file:Suppress("unused", "UNCHECKED_CAST")
package com.gridnine.jasmine.web.core.application

import kotlin.reflect.KClass

object EnvironmentJS{

    var test = false

    private val publishedObjects = linkedMapOf<KClass<*>, Any>()

    fun<T:Any> publish(obj:T){
        val cls = obj::class
        if (publishedObjects.containsKey(cls)) {
            throw IllegalArgumentException("object of ${obj::class} already published")
        }
        publishedObjects[obj::class] = obj
        console.info("published ${obj::class.simpleName}") //$NON-NLS-1$
    }

    fun<T:Any> publish(cls:KClass<in T>, obj:T){
        if (publishedObjects.containsKey(cls)) {
            throw IllegalArgumentException("object of  $cls already published")
        }
        publishedObjects[cls] = obj
        console.info("published ${obj::class.simpleName} of class $cls")
    }

    fun<T:Any> unpublish(cls: KClass<T>) {
        publishedObjects.remove(cls)
        console.info("unpublished $cls")
    }

    fun <T:Any>isPublished(cls: KClass<T>): Boolean {
        return publishedObjects.containsKey(cls)
    }

    fun <T : Any> getPublished(cls: KClass<T>): T {
        return publishedObjects[cls] as T? ?: throw IllegalArgumentException("object of  $cls not published") //$NON-NLS-1$
    }

    fun dispose(){
        publishedObjects.clear()
    }
}