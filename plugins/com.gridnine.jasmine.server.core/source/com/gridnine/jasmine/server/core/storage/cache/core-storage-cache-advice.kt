/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/
@file:Suppress("unused")
package com.gridnine.jasmine.server.core.storage.cache

import com.gridnine.jasmine.server.core.model.common.BaseEntity
import com.gridnine.jasmine.server.core.model.domain.BaseAsset
import com.gridnine.jasmine.server.core.model.domain.BaseDocument
import com.gridnine.jasmine.server.core.model.domain.BaseIndex
import com.gridnine.jasmine.server.core.model.domain.EntityReference
import com.gridnine.jasmine.server.core.storage.StorageAdvice
import com.gridnine.jasmine.server.core.storage.search.EqualitySupport
import com.gridnine.jasmine.server.core.storage.search.PropertyNameSupport
import kotlin.reflect.KClass


class CacheAdvice(override val priority: Double, val storage:CacheStorage) : StorageAdvice{

    override fun <D : BaseDocument> onLoadDocument(cls: KClass<D>, uid: String, callback: (cls: KClass<D>, uid: String) -> D?): D? {
        return onLoadEntity(cls, uid, callback)
    }

    private fun <D : BaseEntity> onLoadEntity(cls: KClass<D>, uid: String, callback: (cls: KClass<D>, uid: String) -> D?): D? {
        val config = CacheConfiguration.get()
        if (!config.isEntityCached(cls)) {
            return callback.invoke(cls, uid)
        }
        val doc = storage.getEntity(cls, uid)
        if (doc != null) {
            return if (doc == nullObject) null else doc
        }
        val res = callback.invoke(cls, uid)
        storage.putEntity(cls, uid, res)
        return res
    }


    override fun <D : BaseDocument, I : BaseIndex<D>,E> onFindUniqueDocumentReference(index: KClass<I>, property: E, propertyValue: Any?, callback: (index: KClass<I>, property: E, propertyValue: Any?) -> EntityReference<D>?): EntityReference<D>? where E: EqualitySupport, E: PropertyNameSupport {
        val config = CacheConfiguration.get()
        if (!config.isPropertyCached(index, property.name)) {
            return callback.invoke(index, property, propertyValue)
        }
        val ref = storage.getUniqueResult<I,BaseEntity>(index, property.name, callback)
        if (ref == nullReference) {
            return null
        }
        if(ref != null){
            @Suppress("UNCHECKED_CAST")
            return ref as EntityReference<D>
        }
        val res = callback.invoke(index, property, propertyValue)
        storage.putUniqueResult(index, property.name, propertyValue,  (if(res == null) nullReference else res) as BaseEntity)
        return res
    }



    override fun <D : BaseAsset> onLoadAsset(cls: KClass<D>, uid: String, callback: (cls: KClass<D>, uid: String) -> D?): D? {
        return onLoadEntity(cls, uid, callback)
    }

    override fun <D : BaseAsset> onDeleteAsset(doc: D, callback:(D) -> Unit) {
        onUpdateEntity(doc, callback)

    }

    private fun <D:BaseEntity> onUpdateEntity(doc: D, callback: (D) -> Unit) {
        val config = CacheConfiguration.get()
        if (config.isEntityCached(doc::class)) {
            storage.evict(doc::class, doc.uid)
        }
        callback.invoke(doc)
    }

    override fun <D : BaseAsset> onSaveAsset(doc: D, callback:(D) -> Unit) {
        onUpdateEntity(doc, callback)
    }

    override fun <D : BaseDocument> onDeleteDocument(doc: D, callback:(D) -> Unit) {
        onUpdateEntity(doc, callback)
    }

    override fun <D : BaseDocument> onSaveDocument(doc: D, callback:(D) -> Unit) {
        onUpdateEntity(doc, callback)
    }

    override fun <D : BaseAsset,E> onFindUniqueAsset(index: KClass<D>, propertyName: E, propertyValue: Any?, callback: (index: KClass<D>, propertyName: E, propertyValue: Any?) -> D?): D? where E: PropertyNameSupport, E: EqualitySupport {
        val config = CacheConfiguration.get()
        if (!config.isPropertyCached(index, propertyName.name)) {
            return callback.invoke(index, propertyName, propertyValue)
        }
        val ref = storage.getUniqueResult<D,BaseEntity>(index, propertyName.name, propertyValue)
        if (ref == nullReference) {
            return null
        }
        @Suppress("UNCHECKED_CAST")
        if(ref != null){
            return ref as D
        }
        val res = callback.invoke(index, propertyName, propertyValue)
        storage.putUniqueResult(index, propertyName.name, propertyValue,  (res ?: nullReference))
        return res
    }

}