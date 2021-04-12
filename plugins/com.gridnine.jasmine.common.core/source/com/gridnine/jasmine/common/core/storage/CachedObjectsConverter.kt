/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.common.core.storage

import com.gridnine.jasmine.common.core.app.Disposable
import com.gridnine.jasmine.common.core.app.PublishableWrapper
import com.gridnine.jasmine.common.core.model.*
import com.gridnine.jasmine.common.core.reflection.ReflectionFactory
import com.gridnine.jasmine.common.core.serialization.ObjectMetadataProvider
import com.gridnine.jasmine.common.core.serialization.SerializablePropertyType
import com.gridnine.jasmine.common.core.serialization.SerializationProvider
import kotlin.reflect.KClass

@Suppress("UNCHECKED_CAST")
class CachedObjectsConverter: Disposable {

    fun<T:BaseIdentity> toCachedObject(obj:T):T{
        if(obj is CachedObject){
            return obj
        }
        return toCachedObject(obj, hashMapOf())

    }

    fun<T:BaseIdentity> toStandard(obj:T):T{
        if(obj !is CachedObject){
            return obj
        }
        return toStandardObject(obj, hashMapOf())
    }

    private fun <T:BaseIdentity> toCachedObject(obj:T, ctx:MutableMap<String, BaseIdentity>):T{
        if(obj is ObjectReference<*>){
            val result = _CachedObjectReference<BaseIdentity>()
            result.uid = obj.uid
            result.type = obj.type as KClass<BaseIdentity>
            result.caption = obj.caption
            return result as T
        }
        val className = obj::class.java.name
        val idx = className.lastIndexOf(".")
        val cachedClassName = className.substring(0, idx)+"._Cached"+className.substring(idx+1)
        val cachedObject = try {
            ReflectionFactory.get().newInstance<T>(cachedClassName)
        } catch (e:ClassNotFoundException){
            return obj
        }
        val provider =SerializationProvider.get().providersCache.getOrPut(className, { SerializationProvider.createProvider(className) }) as ObjectMetadataProvider<T>
        val uid = obj.uid
        if(cachedObject !is ObjectReference<*>){
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

    private fun <T:BaseIdentity> toStandardObject(obj:T, ctx:MutableMap<String, BaseIdentity>):T{
        val className = obj::class.java.name.replace("_Cached", "")
        val result =  ReflectionFactory.get().newInstance<T>(className)
        val provider =SerializationProvider.get().providersCache.getOrPut(className, { SerializationProvider.createProvider(className) }) as ObjectMetadataProvider<T>
        val uid = obj.uid
        if(result !is ObjectReference<*>){
            val existing = ctx[uid]
            if(existing != null){
                return existing as T
            }
            ctx[uid] = result
        }
        provider.getAllProperties().forEach{prop->
            val cached = when(prop.type){
                SerializablePropertyType.ENTITY -> {
                    obj.getValue(prop.id)?.let{toStandardObject(it as BaseIdentity, ctx)}
                }
                else -> obj.getValue(prop.id)
            }
            result.setValue(prop.id, cached)
        }
        provider.getAllCollections().forEach{coll->
            val collection = obj.getCollection(coll.id)
            val resultCollection = result.getCollection(coll.id)
            collection.forEach {elm ->
                val convertedObj = when(coll.elementType){
                    SerializablePropertyType.ENTITY -> {
                        toStandardObject(elm as BaseIdentity, ctx)
                    }
                    else -> elm
                }
                resultCollection.add(convertedObj)
            }
        }
        return result
    }

    override fun dispose() {
        wrapper.dispose()
    }
    companion object {
        private val wrapper = PublishableWrapper(CachedObjectsConverter::class)
        fun get() = wrapper.get()
    }
}