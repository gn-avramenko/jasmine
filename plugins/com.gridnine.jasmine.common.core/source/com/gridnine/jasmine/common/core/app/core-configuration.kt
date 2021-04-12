/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/
@file:Suppress("unused")
package com.gridnine.jasmine.common.core.app

import com.gridnine.jasmine.common.core.model.Xeption
import org.slf4j.LoggerFactory
import java.io.File
import kotlin.reflect.KClass

interface Disposable {

    fun dispose()
}


object Environment {
    internal val log = LoggerFactory.getLogger(Environment::class.java)

    lateinit var rootFolder: File
        private set

    lateinit var tempFolder: File
        private set

    var test = false

    private val publishedObjects = LinkedHashMap<KClass<*>, PublishedEntry<*>>()

    fun configure(rootFolder:File) {
        Environment.rootFolder = rootFolder
        log.debug("root folder is ${rootFolder.absolutePath}")
        tempFolder = File(rootFolder,"temp")
        if(!tempFolder.exists()){
            tempFolder.mkdirs()
        }
        log.debug("temp folder is ${tempFolder.absolutePath}")
        log.info("configured")
    }


    fun dispose() {
        val list = ArrayList(publishedObjects.keys)
        list.reverse()
        list.forEach{ unpublish(it) }
    }


    fun <T:Any> publish(obj: T) {
        val cls = obj::class
        if (publishedObjects.containsKey(cls)) {
            throw Xeption.forDeveloper("object of class ${obj::class.qualifiedName} is already published")
        }
        publishedObjects[obj::class] = PublishedEntry(obj)
        log.info("published $obj")
    }

    fun <T:Any> publish(cls: KClass<in T>, obj: T) {
        if (publishedObjects.containsKey(cls)) {
            throw Xeption.forDeveloper("object of class ${cls.qualifiedName} is already published")
        }
        publishedObjects[cls] = PublishedEntry(obj)
        log.info("published $obj, class = ${cls.qualifiedName}")
    }

    fun unpublish(cls: KClass<*>) {
        val entry= publishedObjects[cls]
        if(entry != null){
            entry.dispose()
            publishedObjects.remove(cls)
        }
    }

    fun isPublished(cls: KClass<*>): Boolean {
        return publishedObjects.containsKey(cls)
    }

    @Suppress("UNCHECKED_CAST")
    fun <T : Any> getPublished(cls: KClass<T>): T {
        val result = publishedObjects[cls]?: throw Xeption.forDeveloper("object of class ${cls.qualifiedName} is not not published") //$NON-NLS-1$
        return result.obj as T
    }

    private class PublishedEntry<T> constructor(val obj: T) {

        fun dispose() {
            if (obj is Disposable) {
                try {
                    obj.dispose()
                } catch (t: Throwable) {
                    log.error("failed disposing $obj", t) //$NON-NLS-1$
                }

            }
            log.info("disposed $obj") //$NON-NLS-1$
        }
    }
}


interface ConfigurationProvider {

    fun getProperty(propertyName:String):String?

    companion object {

        fun get(): ConfigurationProvider {
            return Environment.getPublished(ConfigurationProvider::class)
        }
    }
}