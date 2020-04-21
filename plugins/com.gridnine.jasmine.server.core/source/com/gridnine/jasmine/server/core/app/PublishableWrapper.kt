/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.server.core.app

import com.gridnine.jasmine.server.core.model.common.Xeption
import kotlin.reflect.KClass


class PublishableWrapper<E : Any>(private val cls: KClass<E>) {
    @Volatile
    private var instance: E? = null

    @Volatile
    private var disposed = false

    fun get(): E {
        if(Environment.test){
            return Environment.getPublished(cls)
        }
        if (disposed) {
            throw Xeption.forDeveloper("object ${cls.qualifiedName} disposed")
        }
        if (instance != null) {
            return instance!!
        }
        synchronized(this) {
            if (instance != null) {
                return instance!!
            }
            instance = Environment.getPublished(cls)
            return instance!!
        }
    }

    fun dispose() {
        disposed = true
        instance = null
    }
}