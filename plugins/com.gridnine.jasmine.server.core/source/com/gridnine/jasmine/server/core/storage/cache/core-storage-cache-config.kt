/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/
@file:Suppress("unused")

package com.gridnine.jasmine.server.core.storage.cache

import com.gridnine.jasmine.server.core.app.Environment
import com.gridnine.jasmine.server.core.model.domain.BaseAsset
import com.gridnine.jasmine.server.core.model.domain.BaseDocument
import com.gridnine.jasmine.server.core.model.domain.BaseIndex
import com.gridnine.jasmine.server.core.model.domain.DomainMetaRegistry
import com.gridnine.jasmine.server.core.utils.ReflectionUtils
import kotlin.reflect.KClass


class CacheConfiguration {

    companion object{
        fun get():CacheConfiguration{
            return Environment.getPublished(CacheConfiguration::class)
        }
    }

    private val cachedEntities = HashSet<String>()
    private val cachedProperties = HashMap<String, MutableSet<String>>()

    fun registerCachedEntity(cls: KClass<*>) {
        cachedEntities.add(cls.qualifiedName!!)
    }

    fun registerCachedProperty(cls: KClass<*>, propertyName: String) {
        val indexClassName = cls.qualifiedName!!
        var properties: MutableSet<String>? = cachedProperties[indexClassName]
        if (properties == null) {
            properties = HashSet()
            cachedProperties[indexClassName] = properties
        }
        properties.add(propertyName)
    }

    fun isEntityCached(cls: KClass<*>): Boolean {
        return cachedEntities.contains(cls.qualifiedName)
    }

    fun isPropertyCached(cls: KClass<*>, propertyName: String): Boolean {
        val properties = cachedProperties[cls.qualifiedName] ?: return false
        return properties.contains(propertyName)
    }
}


object ModelCacheConfigurator {

    fun configure(cache: CacheConfiguration) {
        for (item in DomainMetaRegistry.get().documents.values) {
            if ("true" == item.parameters.get("x-cache-resolve")) {
                cache.registerCachedEntity(ReflectionUtils.getClass<BaseDocument>(item.id))
            }
        }
        for (item in DomainMetaRegistry.get().indexes.values) {
            for (prop in item.properties
                    .values) {
                if ("true" == prop.parameters.get("x-cache-find")) {
                    cache.registerCachedProperty(
                            ReflectionUtils.getClass<BaseIndex<BaseDocument>>(item.id), prop.id)
                }
            }
        }
        for (item in DomainMetaRegistry.get().assets.values) {
            if ("true" == item.parameters.get("x-cache-resolve")) {
                cache.registerCachedEntity(ReflectionUtils.getClass<BaseAsset>(item.id))
            }
            for (prop in item.properties
                    .values) {
                if ("true" == prop.parameters.get("x-cache-find")) {
                    cache.registerCachedProperty(
                            ReflectionUtils.getClass<BaseAsset>(item.id), prop.id)
                }
            }
        }

    }

}
