/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/
@file:Suppress("unused", "UnsafeCastFromDynamic", "UNCHECKED_CAST")

package com.gridnine.jasmine.web.core.serialization

import com.gridnine.jasmine.web.core.model.common.BaseEntityJS
import com.gridnine.jasmine.web.core.model.common.BaseIntrospectableObjectJS
import com.gridnine.jasmine.web.core.ui.TextUtilsJS
import com.gridnine.jasmine.web.core.utils.ReflectionFactoryJS

object CloneUtilsJS {
    fun <T : BaseIntrospectableObjectJS> clone(source: T, newUids: Boolean): T {
        val qualifiedName = ReflectionFactoryJS.get().getQualifiedClassName(source::class)
        val result = ReflectionFactoryJS.get().getFactory(qualifiedName).invoke() as T
        val provider = CommonSerializationUtilsJS.getProvider(qualifiedName, RestSerializationUtilsJS.restProviderFactory)
        copy(source, result, newUids, provider)
        return result
    }

    private fun <T : BaseIntrospectableObjectJS> copy(source: T, result: T, newUids: Boolean, provider: ObjectMetadataProviderJS<*>) {
        if (provider.hasUid()) {
            result.setValue(BaseEntityJS.uid, if (newUids) TextUtilsJS.createUUID() else source.getValue(BaseEntityJS.uid))
        }
        provider.properties.forEach {
            val value = source.getValue(it.id) ?: return@forEach
            if (it.id == BaseEntityJS.uid) {
                return@forEach
            }
            when (it.type) {
                SerializablePropertyTypeJS.ENTITY -> {
                    val qualifiedName = ReflectionFactoryJS.get().getQualifiedClassName(value::class)
                    val newEntity = ReflectionFactoryJS.get().getFactory(qualifiedName).invoke() as BaseIntrospectableObjectJS
                    val elmProvider = CommonSerializationUtilsJS.getProvider(qualifiedName, RestSerializationUtilsJS.restProviderFactory)
                    copy(value as BaseIntrospectableObjectJS, newEntity, newUids, elmProvider)
                    result.setValue(it.id, newEntity)
                }
                SerializablePropertyTypeJS.STRING,
                SerializablePropertyTypeJS.ENUM,
                SerializablePropertyTypeJS.BIG_DECIMAL,
                SerializablePropertyTypeJS.BOOLEAN,
                SerializablePropertyTypeJS.LONG,
                SerializablePropertyTypeJS.LOCAL_DATE,
                SerializablePropertyTypeJS.LOCAL_DATE_TIME,
                SerializablePropertyTypeJS.INT -> result.setValue(it.id, value)
                SerializablePropertyTypeJS.BYTE_ARRAY ->
                    result.setValue(it.id, (value as ByteArray).copyOf())
            }

        }
        provider.collections.forEach {
            val sourceColl = source.getCollection(it.id)
            val destColl = result.getCollection(it.id)
            sourceColl.forEach {elmValue ->
                when (it.elementType) {
                    SerializablePropertyTypeJS.ENTITY -> {
                        val qualifiedName = ReflectionFactoryJS.get().getQualifiedClassName(elmValue::class)
                        val newEntity = ReflectionFactoryJS.get().getFactory(qualifiedName).invoke() as BaseIntrospectableObjectJS
                        val elmProvider = CommonSerializationUtilsJS.getProvider(qualifiedName, RestSerializationUtilsJS.restProviderFactory)
                        copy(elmValue as BaseIntrospectableObjectJS, newEntity, newUids, elmProvider)
                        destColl.add(newEntity)
                    }
                    SerializablePropertyTypeJS.STRING,
                    SerializablePropertyTypeJS.ENUM,
                    SerializablePropertyTypeJS.BIG_DECIMAL,
                    SerializablePropertyTypeJS.BOOLEAN,
                    SerializablePropertyTypeJS.LONG,
                    SerializablePropertyTypeJS.LOCAL_DATE,
                    SerializablePropertyTypeJS.LOCAL_DATE_TIME,
                    SerializablePropertyTypeJS.INT -> destColl.add(elmValue)
                    SerializablePropertyTypeJS.BYTE_ARRAY ->destColl.add((elmValue as ByteArray).copyOf())
                }
            }
        }
    }

}