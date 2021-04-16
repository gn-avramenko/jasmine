/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.server.core.storage.cache

import com.gridnine.jasmine.common.core.model.*
import com.gridnine.jasmine.common.core.storage.CachedObjectsConverter
import com.gridnine.jasmine.common.core.storage.Storage
import com.gridnine.jasmine.common.core.utils.TextUtils
import com.gridnine.jasmine.server.core.storage.StorageAdvice
import java.util.*
import kotlin.reflect.KClass

@Suppress("MISPLACED_TYPE_PARAMETER_CONSTRAINTS", "UNCHECKED_CAST")
class CacheStorageAdvice(override val priority: Double) : StorageAdvice{


    override fun <D : BaseDocument> onLoadDocument(cls: KClass<D>, uid: String, ignoreCache: Boolean, callback: (cls: KClass<D>, uid: String, ignoreCache: Boolean) -> D?): D? {
        return onResolve(cls, uid, ignoreCache, callback)
    }

    override fun <D : BaseAsset> onLoadAsset(cls: KClass<D>, uid: String, ignoreCache: Boolean, callback: (cls: KClass<D>, uid: String, ignoreCache: Boolean) -> D?): D? {
        return onResolve(cls, uid, ignoreCache, callback)
    }

    override fun <D : BaseDocument, I : BaseIndex<D>, E : EqualitySupport> onFindUniqueDocumentReference(index: KClass<I>, property: E, propertyValue: Any?, callback: (index: KClass<I>, property: E, propertyValue: Any?) -> ObjectReference<D>?): ObjectReference<D>? where E : PropertyNameSupport {
        if(!CacheConfiguration.get().isCached(index, property.name)){
            return callback.invoke(index,property,propertyValue)
        }
        val propValueStr = toString(propertyValue)
        val cache = CacheManager.get().getOrCreateFindCache<D>(index, property.name)
        val oldValue = cache.get(propValueStr)
        if(oldValue?.value != null){
            return if(oldValue.value == nullObject) null else oldValue.value
        }
        val actualStorageResult = callback.invoke(index,property,propertyValue)
        val newValue = CachedValue(System.nanoTime(), actualStorageResult?.let { CachedObjectsConverter.get().toCachedObject(it) }?: nullObjectReference as ObjectReference<D>)
        cache.replace(propValueStr, oldValue, newValue)
        return  if(newValue.value == nullObjectReference) null else newValue.value
    }

    private fun toString(propertyValue: Any?): String {
        return when(propertyValue){
            null -> "\$_null"
            is Enum<*> -> propertyValue.name
            is String -> propertyValue
            else -> throw Xeption.forDeveloper("unsupported property value type $propertyValue" )
        }
    }

    override fun <A : BaseAsset, E : PropertyNameSupport> onFindUniqueAsset(index: KClass<A>, propertyName: E, propertyValue: Any?, ignoreCache: Boolean, callback: (index: KClass<A>, propertyName: E, propertyValue: Any?, ignoreCache: Boolean) -> A?): A? where E : EqualitySupport {
        if(ignoreCache || !CacheConfiguration.get().isCached(index, propertyName.name)){
            return callback.invoke(index,propertyName,propertyValue, ignoreCache)
        }
        val propValueStr = toString(propertyValue)
        val cache = CacheManager.get().getOrCreateFindCache<A>(index::class, propertyName.name)
        val oldValue = cache.get(propValueStr)
        if(oldValue?.value != null){
            return if(oldValue.value == nullObject) null else onResolve(index, oldValue.value.uid, ignoreCache) { cls2, uid2, ignoreCache2 ->
                Storage.get().loadAsset(cls2, uid2, ignoreCache2)
            }
        }
        val actualStorageResult = callback.invoke(index,propertyName,propertyValue, ignoreCache)
        val newValue = CachedValue(System.nanoTime(), actualStorageResult?.let { CachedObjectsConverter.get().toCachedObject(EntityUtils.toReference(it))}?: nullObjectReference as ObjectReference<A>)
        cache.replace(propValueStr, oldValue, newValue)
        return  actualStorageResult?.let {  CachedObjectsConverter.get().toCachedObject(it) }
    }

    private fun <D : BaseIdentity> onResolve(cls: KClass<D>, uid: String, ignoreCache: Boolean, callback: (cls: KClass<D>, uid: String, ignoreCache: Boolean) -> D?): D? {
        if(ignoreCache || !CacheConfiguration.get().isCached(cls)){
            return callback.invoke(cls, uid, ignoreCache)
        }
        val cache = CacheManager.get().getOrCreateResolveCache(cls)
        val oldValue = cache.get(uid)
        if(oldValue?.value != null){
            return if(oldValue.value == nullObject) null else oldValue.value
        }
        val actualStorageResult = callback.invoke(cls, uid, ignoreCache)
        val newValue = CachedValue<D>(System.nanoTime(), actualStorageResult?.let { CachedObjectsConverter.get().toCachedObject(it) }?: nullObject as D)
        cache.replace(uid, oldValue, newValue)
        return  if(newValue.value == nullObject) null else newValue.value
    }


    fun invalidateResolveCache(kClass: KClass<*>, uid: String) {
        CacheManager.get().getOrCreateResolveCache(kClass as KClass<BaseIdentity>).put(uid, CachedValue(System.currentTimeMillis(), null))
    }

    fun  invalidateFindCache(kClass: KClass<*>, propertyName: String, value: Any?) {
        CacheManager.get().getOrCreateFindCache<BaseIdentity>(kClass, propertyName).put(toString(value), CachedValue(System.currentTimeMillis(), null))
    }


    companion object{
        private val nullObject:Any = object {}
        private val nullObjectReference = ObjectReference(BaseIdentity::class, TextUtils.generateUid(),null)

    }


}