/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.server.standard.rest

import com.gridnine.jasmine.common.core.meta.*
import com.gridnine.jasmine.common.core.parser.CustomMetadataParser
import com.gridnine.jasmine.common.core.parser.WebMessagesMetadataParser
import com.gridnine.jasmine.common.standard.model.rest.*
import com.gridnine.jasmine.server.core.rest.RestHandler
import com.gridnine.jasmine.server.core.rest.RestOperationContext
import java.util.concurrent.ConcurrentHashMap

class MetadataRestHandler : RestHandler<GetMetadataRequest, GetMetadataResponse> {

    private val cache = ConcurrentHashMap<String, GetMetadataResponse>()

    private fun updateWebMessagesMetadata(result: GetMetadataResponse, webMessagesRegistry: WebMessagesMetaRegistry, ) {
        webMessagesRegistry.bundles.values.forEach { bnd ->
            val mb = WebMessagesBundleDT()
            mb.id = bnd.id
            result.webMessages.add(mb)
            bnd.messages.values.forEach { msg ->
                val m = WebMessageDT()
                m.id = msg.id
                m.displayName = msg.getDisplayName()
                mb.messages.add(m)
            }
        }
    }
    private fun updateRestMetadata(result: GetMetadataResponse, registry:RestMetaRegistry,  pluginId: String?) {
        registry.enums.values.forEach {
            if (pluginId != null && WebPluginsAssociationsRegistry.get().associations[it.id] != pluginId) {
                return@forEach
            }
            val enumDescr = RestEnumDescriptionDT()
            enumDescr.id = it.id+"JS"
            it.items.values.forEach { enumItemDescription ->
                enumDescr.items.add(enumItemDescription.id)
            }
            result.restEnums.add(enumDescr)
        }
        registry.entities.values.forEach {
            if (pluginId != null && WebPluginsAssociationsRegistry.get().associations[it.id] != pluginId) {
                return@forEach
            }
            val entityDescr = RestEntityDescriptionDT()
            entityDescr.id = it.id+"JS"
            entityDescr.abstract = it.isAbstract
            entityDescr.extends = it.extendsId?.let { ext -> ext+"JS"}

            it.properties.values.forEach { propertyDescrition ->
                val property = RestPropertyDescriptionDT()
                property.className = getClassName(propertyDescrition.className)
                property.id = propertyDescrition.id
                property.type = toRestCollectionType(propertyDescrition.type)
                property.nonNullable = propertyDescrition.nonNullable
                property.lateInit = propertyDescrition.lateinit
                entityDescr.properties.add(property)
            }
            it.collections.values.forEach { collectionDescription ->
                val coll = RestCollectionDescriptionDT()
                coll.elementClassName = getClassName(collectionDescription.elementClassName)
                coll.id = collectionDescription.id
                coll.elementType = toRestCollectionType(collectionDescription.elementType)
                entityDescr.collections.add(coll)
            }

            result.restEntities.add(entityDescr)
        }
        registry.operations.values.forEach {
            val opDescr = RestOperationDescriptionDT()
            if (pluginId != null && WebPluginsAssociationsRegistry.get().associations[it.requestEntity] != pluginId) {
                return@forEach
            }
            opDescr.id = it.id
            opDescr.request = it.requestEntity+"JS"
            opDescr.response = it.responseEntity+"JS"
            result.operations.add(opDescr)
        }
    }
    private fun toRestCollectionType(type: RestPropertyType): RestPropertyTypeDT {
        return RestPropertyTypeDT.valueOf(type.name)
    }

    private fun updateDomainMetadata(result: GetMetadataResponse, registry: DomainMetaRegistry, pluginId: String?) {
        registry.enums.values.forEach {
            if (pluginId != null && WebPluginsAssociationsRegistry.get().associations[it.id] != pluginId) {
                return@forEach
            }
            val enumDescr = DomainEnumDescriptionDT()
            enumDescr.id = it.id+"JS"
            it.items.values.forEach { enumItem ->
                val itemDT = DomainEnumItemDescriptionDT()
                itemDT.id = enumItem.id
                itemDT.displayName = enumItem.getDisplayName()
                enumDescr.items.add(itemDT)
            }
            result.domainEnums.add(enumDescr)
        }
        registry.indexes.values.forEach {
            if (pluginId != null && WebPluginsAssociationsRegistry.get().associations[it.id] != pluginId) {
                return@forEach
            }
            val indexDescr = IndexDescriptionDT()
            indexDescr.displayName = it.getDisplayName()
            indexDescr.id = it.id+"JS"
            indexDescr.document = it.document+"JS"
            it.properties.values.forEach { propertyDescription ->
                val property = IndexPropertyDescriptionDT()
                property.className = getClassName(propertyDescription.className)
                property.id = propertyDescription.id
                property.type = toRestCollectionType(propertyDescription.type)
                property.displayName = propertyDescription.getDisplayName()
                property.nonNullable = propertyDescription.nonNullable
                indexDescr.properties.add(property)
            }
            it.collections.values.forEach { collectionDescription ->
                val coll = IndexCollectionDescriptionDT()
                coll.elementClassName = getClassName(collectionDescription.elementClassName)
                coll.id = collectionDescription.id
                coll.elementType = toRestCollectionType(collectionDescription.elementType)
                coll.displayName = collectionDescription.getDisplayName()
                coll.unique = collectionDescription.unique
                indexDescr.collections.add(coll)
            }
            result.domainIndexes.add(indexDescr)
        }
        registry.assets.values.forEach {
            if (pluginId != null && WebPluginsAssociationsRegistry.get().associations[it.id] != pluginId) {
                return@forEach
            }
            val assetDescription = AssetDescriptionDT()
            assetDescription.displayName = it.getDisplayName()
            assetDescription.id = it.id+"JS"
            it.properties.values.forEach { propertyDescription ->
                val property = IndexPropertyDescriptionDT()
                property.className = getClassName(propertyDescription.className)
                property.id = propertyDescription.id
                property.type = toRestCollectionType(propertyDescription.type)
                property.displayName = propertyDescription.getDisplayName()
                property.nonNullable = propertyDescription.nonNullable
                assetDescription.properties.add(property)
            }
            it.collections.values.forEach { collectionDescription ->
                val coll = IndexCollectionDescriptionDT()
                coll.elementClassName = getClassName(collectionDescription.elementClassName)
                coll.id = collectionDescription.id
                coll.elementType = toRestCollectionType(collectionDescription.elementType)
                coll.displayName = collectionDescription.getDisplayName()
                coll.unique = collectionDescription.unique
                assetDescription.collections.add(coll)
            }
            result.domainAssets.add(assetDescription)
        }

    }

    private fun getClassName(className: String?): String? {
        return when(className){
            null -> null
            else -> "${className}JS"
        }
    }

    private fun toRestCollectionType(type: DatabasePropertyType): DatabasePropertyTypeDT {
        return DatabasePropertyTypeDT.valueOf(type.name)

    }
    private fun toRestCollectionType(elementType: DatabaseCollectionType): DatabaseCollectionTypeDT {
        return DatabaseCollectionTypeDT.valueOf(elementType.name)

    }

    private fun updateCustomMetadata(result: GetMetadataResponse, registry: CustomMetaRegistry, pluginId: String?) {
        registry.enums.values.forEach { en ->
            if (pluginId != null && WebPluginsAssociationsRegistry.get().associations[en.id] != pluginId) {
                return@forEach
            }
            val ed = CustomEnumDescriptionDT()
            ed.id = en.id+"JS"
            result.customEnums.add(ed)
            en.items.values.forEach {
                ed.items.add(it.id)
            }
        }
        registry.entities.values.forEach { ett ->
            if (pluginId != null && WebPluginsAssociationsRegistry.get().associations[ett.id] != pluginId) {
                return@forEach
            }
            val ed = CustomEntityDescriptionDT()
            ed.id = ett.id+"JS"
            ed.isAbstract = ett.isAbstract
            ed.extendsId = ett.extendsId
            result.customEntities.add(ed)
            ett.properties.values.forEach {
                val pd = CustomPropertyDescriptionDT()
                pd.id = it.id
                pd.className = it.className+"JS"
                pd.lateInit = it.lateinit
                pd.nonNullable = it.nonNullable
                pd.type = when (it.type) {
                    CustomType.STRING -> CustomTypeDT.STRING
                    CustomType.ENUM -> CustomTypeDT.ENUM
                    CustomType.ENTITY -> CustomTypeDT.ENTITY
                    CustomType.LONG -> CustomTypeDT.LONG
                    CustomType.CLASS -> CustomTypeDT.CLASS
                    CustomType.INT -> CustomTypeDT.INT
                    CustomType.BIG_DECIMAL -> CustomTypeDT.BIG_DECIMAL
                    CustomType.ENTITY_REFERENCE -> CustomTypeDT.ENTITY_REFERENCE
                    CustomType.LOCAL_DATE_TIME -> CustomTypeDT.LOCAL_DATE_TIME
                    CustomType.LOCAL_DATE -> CustomTypeDT.LOCAL_DATE
                    CustomType.BOOLEAN -> CustomTypeDT.BOOLEAN
                    CustomType.BYTE_ARRAY -> CustomTypeDT.BYTE_ARRAY
                }
                ed.properties.add(pd)
            }
            ett.collections.values.forEach {
                val cd = CustomCollectionDescriptionDT()
                ed.collections.add(cd)
                cd.id = it.id
                cd.elementClassName = it.elementClassName+"JS"
                cd.elementType = when (it.elementType) {
                    CustomType.STRING -> CustomTypeDT.STRING
                    CustomType.ENUM -> CustomTypeDT.ENUM
                    CustomType.ENTITY -> CustomTypeDT.ENTITY
                    CustomType.LONG -> CustomTypeDT.LONG
                    CustomType.CLASS -> CustomTypeDT.CLASS
                    CustomType.INT -> CustomTypeDT.INT
                    CustomType.BIG_DECIMAL -> CustomTypeDT.BIG_DECIMAL
                    CustomType.ENTITY_REFERENCE -> CustomTypeDT.ENTITY_REFERENCE
                    CustomType.LOCAL_DATE_TIME -> CustomTypeDT.LOCAL_DATE_TIME
                    CustomType.LOCAL_DATE -> CustomTypeDT.LOCAL_DATE
                    CustomType.BOOLEAN -> CustomTypeDT.BOOLEAN
                    CustomType.BYTE_ARRAY -> CustomTypeDT.BYTE_ARRAY
                }
            }
            ett.maps.values.forEach { cmd ->
                val md = CustomMapDescriptionDT()
                md.id = cmd.id
                ed.maps.add(md)
                md.keyClassName = cmd.keyClassName
                md.valueClassName = cmd.valueClassName
                md.keyClassType = when (cmd.keyClassType) {
                    CustomType.STRING -> CustomTypeDT.STRING
                    CustomType.ENUM -> CustomTypeDT.ENUM
                    CustomType.ENTITY -> CustomTypeDT.ENTITY
                    CustomType.LONG -> CustomTypeDT.LONG
                    CustomType.CLASS -> CustomTypeDT.CLASS
                    CustomType.INT -> CustomTypeDT.INT
                    CustomType.BIG_DECIMAL -> CustomTypeDT.BIG_DECIMAL
                    CustomType.ENTITY_REFERENCE -> CustomTypeDT.ENTITY_REFERENCE
                    CustomType.LOCAL_DATE_TIME -> CustomTypeDT.LOCAL_DATE_TIME
                    CustomType.LOCAL_DATE -> CustomTypeDT.LOCAL_DATE
                    CustomType.BOOLEAN -> CustomTypeDT.BOOLEAN
                    CustomType.BYTE_ARRAY -> CustomTypeDT.BYTE_ARRAY
                }
                md.valueClassType = when (cmd.valueClassType) {
                    CustomType.STRING -> CustomTypeDT.STRING
                    CustomType.ENUM -> CustomTypeDT.ENUM
                    CustomType.ENTITY -> CustomTypeDT.ENTITY
                    CustomType.LONG -> CustomTypeDT.LONG
                    CustomType.CLASS -> CustomTypeDT.CLASS
                    CustomType.INT -> CustomTypeDT.INT
                    CustomType.BIG_DECIMAL -> CustomTypeDT.BIG_DECIMAL
                    CustomType.ENTITY_REFERENCE -> CustomTypeDT.ENTITY_REFERENCE
                    CustomType.LOCAL_DATE_TIME -> CustomTypeDT.LOCAL_DATE_TIME
                    CustomType.LOCAL_DATE -> CustomTypeDT.LOCAL_DATE
                    CustomType.BOOLEAN -> CustomTypeDT.BOOLEAN
                    CustomType.BYTE_ARRAY -> CustomTypeDT.BYTE_ARRAY
                }
            }
        }
    }

    override fun service(request: GetMetadataRequest, ctx: RestOperationContext): GetMetadataResponse {
        return cache.getOrPut(request.pluginId) {
            if (request.pluginId == "com.gridnine.jasmine.web.core") {
                val result = GetMetadataResponse()
                val customMetaregistry = CustomMetaRegistry()
                CustomMetadataParser.updateCustomMetaRegistry(customMetaregistry, "com/gridnine/jasmine/common/core/meta/core-custom.xml", javaClass.classLoader)
                updateCustomMetadata(result, customMetaregistry, null)
                val webMessagesRegistry = WebMessagesMetaRegistry()
                WebMessagesMetadataParser.updateWebMessages(webMessagesRegistry, "com/gridnine/jasmine/common/core/meta/core-web-messages.xml", javaClass.classLoader)
                updateWebMessagesMetadata(result, webMessagesRegistry)
                result
            } else {
                val result = GetMetadataResponse()
                updateCustomMetadata(result, CustomMetaRegistry.get(), request.pluginId)
                updateDomainMetadata(result, DomainMetaRegistry.get(), request.pluginId)
                updateRestMetadata(result, RestMetaRegistry.get(), request.pluginId)
                result
            }
        }
    }

}