/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.server.core.storage.cache

import com.gridnine.jasmine.common.core.app.PublishableWrapper
import com.gridnine.jasmine.common.core.meta.DomainMetaRegistry
import com.gridnine.jasmine.common.core.model.BaseAsset
import com.gridnine.jasmine.common.core.model.BaseIdentity
import com.gridnine.jasmine.common.core.reflection.ReflectionFactory
import kotlin.reflect.KClass

class CacheConfiguration {

    private val cachedObjects = hashSetOf<String>()

    private val cachedProperties = hashMapOf<String, MutableSet<String>>()

    private val propertiesHandlers = hashMapOf<String, MutableList<CachedPropertyHandler<BaseIdentity>>>()

    init {
        DomainMetaRegistry.get().assets.values.forEach{
            if(it.parameters["x-cache-resolve"] == "true"){
                cachedObjects.add(it.id)
            }
            it.properties.values.forEach{ipd ->
                ipd.parameters["x-cache-find-handler"]?.let{
                    val handler = ReflectionFactory.get().newInstance(it) as CachedPropertyHandler<BaseIdentity>
                    propertiesHandlers.getOrPut(handler.getIdentityClass().qualifiedName!!, { arrayListOf()}).add(handler)
                    cachedProperties.getOrPut(handler.getIndexClass().qualifiedName!!, {hashSetOf()}).add(handler.getPropertyName())
                }
            }
        }
        DomainMetaRegistry.get().documents.values.forEach{
            if(it.parameters["x-cache-resolve"] == "true"){
                cachedObjects.add(it.id)
            }
        }
        DomainMetaRegistry.get().indexes.values.forEach{
            it.properties.values.forEach{ipd ->
                ipd.parameters["x-cache-find-handler"]?.let{
                    val handler = ReflectionFactory.get().newInstance(it) as CachedPropertyHandler<BaseIdentity>
                    propertiesHandlers.getOrPut(handler.getIdentityClass().qualifiedName!!, { arrayListOf()}).add(handler)
                    cachedProperties.getOrPut(handler.getIndexClass().qualifiedName!!, {hashSetOf()}).add(handler.getPropertyName())
                }
            }
        }
    }
    fun <D:Any> isCached(cls: KClass<D>): Boolean {
        return cachedObjects.contains(cls.qualifiedName as String)
    }

    fun <I:Any> isCached(cls: KClass<I>, name: String): Boolean {
        return cachedProperties[cls.qualifiedName]?.contains(name)?:false
    }

    fun <E:BaseIdentity> getCachedPropertyHandlers(cls:KClass<E>):Collection<CachedPropertyHandler<E>>{
        return (propertiesHandlers[cls.qualifiedName!!] as Collection<CachedPropertyHandler<E>>?)?: emptySet()
    }

    companion object {
        private val wrapper = PublishableWrapper(CacheConfiguration::class)
        fun get() = wrapper.get()
    }

    interface CachedPropertyHandler<E:BaseIdentity>{
        fun getIndexClass():KClass<*>
        fun getPropertyName():String
        fun getIdentityClass():KClass<E>
        fun getValue(obj:E):Any?
    }

    open class AssetCachedPropertyHandler<A:BaseAsset>(private val cls:KClass<A>, private val propName:String):CachedPropertyHandler<A>{
        override fun getIndexClass(): KClass<*> {
            return cls
        }

        override fun getPropertyName(): String {
            return propName
        }

        override fun getIdentityClass(): KClass<A> {
            return cls
        }

        override fun getValue(obj: A): Any? {
            return obj.getValue(propName)
        }

    }
}