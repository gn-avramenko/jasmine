/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.server.core.storage.cache

import com.gridnine.jasmine.server.core.app.Disposable
import com.gridnine.jasmine.server.core.app.PublishableWrapper
import com.gridnine.jasmine.server.core.model.common.BaseIdentity
import com.gridnine.jasmine.server.core.model.domain.CachedObject
import com.gridnine.jasmine.server.core.model.domain.ReadOnlyArrayList
import com.gridnine.jasmine.server.core.reflection.ReflectionFactory
import com.gridnine.jasmine.server.core.serialization.JsonSerializer
import com.gridnine.jasmine.server.core.serialization.ObjectMetadataProvider
import com.gridnine.jasmine.server.core.serialization.SerializablePropertyType

@Suppress("UNCHECKED_CAST")
class CachedObjectsConverter: Disposable {

    fun<T:BaseIdentity> toCachedObject(obj:T):T{
        if(obj is CachedObject){
            return obj
        }
        return toCachedObject(obj, hashMapOf())

    }

    private fun <T:BaseIdentity> toCachedObject(obj:T, ctx:MutableMap<String, BaseIdentity>):T{
        val className = obj::class.java.name
        val idx = className.lastIndexOf(".")
        val cachedClassName = className.substring(0, idx)+"._Cached"+className.substring(idx+1)
        val cachedObject = try {
            ReflectionFactory.get().newInstance<T>(cachedClassName)
        } catch (e:ClassNotFoundException){
            return obj
        }
        val provider =JsonSerializer.get().providersCache.getOrPut(className, { JsonSerializer.createProvider(className) }) as ObjectMetadataProvider<T>
        val uid = obj.uid
        if(uid != null){
            val existing = ctx[uid]
            if(existing != null){
                return existing as T
            }
            ctx[uid] = cachedObject
        }
        val cachedDocument = cachedObject as CachedObject
        cachedDocument.allowChanges = true
        provider.getAllProperties().forEach{prop->
            val cached = when(prop.type){
                SerializablePropertyType.ENTITY -> {
                    obj.getValue(prop.id)?.let{toCachedObject(it as BaseIdentity, ctx)}
                }
                else -> obj.getValue(prop.id)
            }
            cachedObject.setValue(prop.id, cached)
        }
        cachedDocument.allowChanges = false
        provider.getAllCollections().forEach{coll->
            val collection = obj.getCollection(coll.id)
            val cachedCollection = cachedObject.getCollection(coll.id) as ReadOnlyArrayList<Any>
            cachedCollection.allowChanges = true
            collection.forEach {elm ->
                val cached = when(coll.elementType){
                    SerializablePropertyType.ENTITY -> {
                        toCachedObject(elm as BaseIdentity, ctx)
                    }
                    else -> elm
                }
                cachedCollection.add(cached)
            }
            cachedCollection.allowChanges = false
        }
        return cachedDocument as T
    }


    override fun dispose() {
        wrapper.dispose()
    }
    companion object {
        private val wrapper = PublishableWrapper(CachedObjectsConverter::class)
        fun get() = wrapper.get()
    }
}