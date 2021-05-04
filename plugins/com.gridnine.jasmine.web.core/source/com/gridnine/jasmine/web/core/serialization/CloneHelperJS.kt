/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

@file:Suppress("UNCHECKED_CAST", "unused")

package com.gridnine.jasmine.web.core.serialization

import com.gridnine.jasmine.common.core.model.BaseIdentityJS
import com.gridnine.jasmine.common.core.model.BaseIntrospectableObjectJS
import com.gridnine.jasmine.web.core.reflection.ReflectionFactoryJS
import com.gridnine.jasmine.web.core.utils.MiscUtilsJS

object CloneHelperJS {

    suspend fun <T : BaseIntrospectableObjectJS> clone(source: T, newUids: Boolean): T {
        val qualifiedName = ReflectionFactoryJS.get().getQualifiedClassName(source::class)
        val result = ReflectionFactoryJS.get().getFactory(qualifiedName).invoke() as T
        val provider = JsonSerializerJS.get().getProvider(qualifiedName)
        copy(source, result, newUids, provider)
        return result
    }

    private suspend fun <T : BaseIntrospectableObjectJS> copy(source: T, result: T, newUids: Boolean, provider: ObjectMetadataProviderJS<*>) {
        if (provider.hasUid()) {
            result.setValue(BaseIdentityJS.uid, if (newUids) MiscUtilsJS.createUUID() else source.getValue(BaseIdentityJS.uid))
        }
        provider.properties.forEach {
            val value = source.getValue(it.id) ?: return@forEach
            if (it.id == BaseIdentityJS.uid) {
                return@forEach
            }
            when (it.type) {
                SerializablePropertyTypeJS.ENTITY -> {
                    val qualifiedName = ReflectionFactoryJS.get().getQualifiedClassName(value::class)
                    val newEntity = ReflectionFactoryJS.get().getFactory(qualifiedName).invoke() as BaseIntrospectableObjectJS
                    val elmProvider = JsonSerializerJS.get().getProvider(qualifiedName)
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
                SerializablePropertyTypeJS.INT,
                SerializablePropertyTypeJS.CLASS ->result.setValue(it.id, value)
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
                        val elmProvider = JsonSerializerJS.get().getProvider(qualifiedName)
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
                    SerializablePropertyTypeJS.INT,
                    SerializablePropertyTypeJS.CLASS-> destColl.add(elmValue)
                    SerializablePropertyTypeJS.BYTE_ARRAY ->destColl.add((elmValue as ByteArray).copyOf())
                }
            }
        }
    }

}