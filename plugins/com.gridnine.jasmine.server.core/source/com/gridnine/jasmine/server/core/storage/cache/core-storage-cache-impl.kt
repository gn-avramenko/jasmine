/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/
@file:Suppress("unused")

package com.gridnine.jasmine.server.core.storage.cache

import com.gridnine.jasmine.server.core.app.Environment
import com.gridnine.jasmine.server.core.model.common.BaseEntity
import com.gridnine.jasmine.server.core.model.domain.BaseAsset
import com.gridnine.jasmine.server.core.model.domain.EntityReference
import com.gridnine.jasmine.server.core.utils.EntityUtils
import java.util.concurrent.ConcurrentHashMap
import kotlin.reflect.KClass


interface NullObject

val nullObject: NullObject = object : NullObject {}
val nullReference = EntityReference(BaseEntity::class, "-1", null)


interface CacheStorage {

    companion object{
        fun get():CacheStorage{
            return Environment.getPublished(CacheStorage::class)
        }
    }

    fun <D : BaseEntity> getEntity(cls:KClass<D>, uid:String): D?

    fun <D : BaseEntity> putEntity(cls:KClass<D>, uid:String, result: D?)

    fun <D : BaseEntity> evict(cls:KClass<D>, uid:String)

    fun <I:Any, R:BaseEntity> getUniqueResult(
            indexClass: KClass<I>, propertyName: String, propertyValue: Any?): R?

    fun <I:Any, R:BaseEntity> putUniqueResult(indexClass: KClass<I>,
                                         propertyName: String, propertyValue: Any?, value: R)

}


@Suppress("UNCHECKED_CAST")
class SimpleMapCacheStorage : CacheStorage {

    private val docsCache = ConcurrentHashMap<String, BaseEntity>()

    private val indexCache = ConcurrentHashMap<String, Map<String, Map<Any?, Any>>>()

    private val inverseIndexCache = ConcurrentHashMap<EntityReference<BaseEntity>, Map<String, Map<String, Any?>>>()

    override fun <D : BaseEntity> getEntity(cls:KClass<D>, uid:String): D? {
        return docsCache["${cls.qualifiedName}_${uid}"] as D?
    }

    override fun <D : BaseEntity> putEntity(cls:KClass<D>, uid:String, result: D?) {
        docsCache["${cls.qualifiedName}_${uid}"] = result ?: nullObject as D
    }

    override fun <D : BaseEntity> evict(cls:KClass<D>, uid:String) {
        synchronized(this) {
            val ref = EntityReference(cls, uid, null)
            docsCache.remove("${cls.qualifiedName}_${uid}")
            processEntry(inverseIndexCache[ref as EntityReference<BaseEntity>])
            processEntry(inverseIndexCache[nullReference])
            inverseIndexCache.remove(ref)
        }

    }

    private fun processEntry(iim: Map<String, Map<String, Any?>>?) {
        if (iim == null) {
            return
        }
        for ((key, value) in iim) {
            val im = indexCache[key]
            if (im != null) {
                for ((key1, value1) in value) {
                    val pm = im[key1] as MutableMap?
                    if(pm != null){
                        pm.remove(value1)
                    }
                }
            }

        }
    }



    override fun <I:Any, R:BaseEntity> getUniqueResult(
            indexClass: KClass<I>, propertyName: String, propertyValue: Any?): R? {
        val classCache = indexCache[indexClass.qualifiedName!!] ?: return null
        val propertyCache = classCache[propertyName] ?: return null
        return propertyCache[propertyValue] as R?
    }

    override fun <I : Any, R:BaseEntity> putUniqueResult(indexClass: KClass<I>, propertyName: String, propertyValue: Any?, value: R) {
        synchronized(this) {
            var propertiesMap = indexCache[indexClass.qualifiedName] as MutableMap<String, Map<Any?, R>>?
            if (propertiesMap == null) {
                propertiesMap = ConcurrentHashMap()
                indexCache[indexClass.qualifiedName!!] = propertiesMap
            }
            var valuesMap = propertiesMap[propertyName] as MutableMap<Any?, R>?
            if (valuesMap == null) {
                valuesMap = ConcurrentHashMap()
                propertiesMap[propertyName] = valuesMap
            }
            valuesMap[propertyValue] = value
            val ref = if(value is BaseAsset) EntityUtils.toReference(value) else (value as EntityReference<BaseEntity>)
            var indexMap = inverseIndexCache[ref] as MutableMap<String, Map<String, Any?>>?
            if (indexMap == null) {
                indexMap = ConcurrentHashMap()
                inverseIndexCache[ref] = indexMap as MutableMap<String, Map<String, Any?>>
            }
            var ipm = indexMap[indexClass.qualifiedName!!] as MutableMap<String, Any?>?
            if (ipm == null) {
                ipm = ConcurrentHashMap()
                indexMap[indexClass.qualifiedName!!] = ipm
            }
            ipm.put(propertyName, propertyValue)
        }
    }


}
