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
                property.type = toRestFieldType(propertyDescrition.type)
                property.nonNullable = propertyDescrition.nonNullable
                property.lateInit = propertyDescrition.lateinit
                entityDescr.properties.add(property)
            }
            it.collections.values.forEach { collectionDescription ->
                val coll = RestCollectionDescriptionDT()
                coll.elementClassName = getClassName(collectionDescription.elementClassName)
                coll.id = collectionDescription.id
                coll.elementType = toRestFieldType(collectionDescription.elementType)
                entityDescr.collections.add(coll)
            }
            it.maps.values.forEach { mapDescr ->
                val map = RestMapDescriptionDT()
                map.id = mapDescr.id
                map.keyClassType  = toRestFieldType(mapDescr.keyClassType)
                map.keyClassName = getClassName(mapDescr.keyClassName)
                map.valueClassType  = toRestFieldType(mapDescr.valueClassType)
                map.valueClassName = getClassName(mapDescr.valueClassName)
                entityDescr.maps.add(map)
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
    private fun toRestFieldType(type: RestPropertyType): RestPropertyTypeDT {
        return RestPropertyTypeDT.valueOf(type.name)
    }

    private fun updateMiscMetadata(result: GetMetadataResponse, registry:MiscMetaRegistry,  pluginId: String?) {
        registry.enums.values.forEach {
            if(!it.exposedAtRest){
                return@forEach
            }
            if (pluginId != null && WebPluginsAssociationsRegistry.get().associations[it.id] != pluginId) {
                return@forEach
            }
            val enumDescr = MiscEnumDescriptionDT()
            enumDescr.id = it.id+"JS"
            it.items.values.forEach { enumItemDescription ->
                enumDescr.items.add(enumItemDescription.id)
            }
            result.miscEnums.add(enumDescr)
        }
        registry.entities.values.forEach {
            if(!it.exposedAtRest){
                return@forEach
            }
            if (pluginId != null && WebPluginsAssociationsRegistry.get().associations[it.id] != pluginId) {
                return@forEach
            }
            val entityDescr = MiscEntityDescriptionDT()
            entityDescr.id = it.id+"JS"
            entityDescr.isAbstract = it.isAbstract
            entityDescr.extendsId = it.extendsId?.let { ext -> ext+"JS"}

            it.properties.values.forEach { propertyDescrition ->
                val property = MiscPropertyDescriptionDT()
                property.className = getClassName(propertyDescrition.className)
                property.id = propertyDescrition.id
                property.type = toMiscFieldType(propertyDescrition.type)
                property.nonNullable = propertyDescrition.nonNullable
                property.lateInit = propertyDescrition.lateinit
                entityDescr.properties.add(property)
            }
            it.collections.values.forEach { collectionDescription ->
                val coll = MiscCollectionDescriptionDT()
                coll.elementClassName = getClassName(collectionDescription.elementClassName)
                coll.id = collectionDescription.id
                coll.elementType = toMiscFieldType(collectionDescription.elementType)
                entityDescr.collections.add(coll)
            }
            it.maps.values.forEach { mapDescr ->
                val map = MiscMapDescriptionDT()
                map.id = mapDescr.id
                map.keyClassType  = toMiscFieldType(mapDescr.keyClassType)
                map.keyClassName = getClassName(mapDescr.keyClassName)
                map.valueClassType  = toMiscFieldType(mapDescr.valueClassType)
                map.valueClassName = getClassName(mapDescr.valueClassName)
                entityDescr.maps.add(map)
            }
            result.miscEntities.add(entityDescr)
        }

    }
    private fun toMiscFieldType(type: MiscFieldType): MiscFieldTypeDT {
        return MiscFieldTypeDT.valueOf(type.name)
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
        registry.documents.values.forEach {
            if (!it.exposedAtRest || (pluginId != null && WebPluginsAssociationsRegistry.get().associations[it.id] != pluginId)) {
                return@forEach
            }
            result.domainDocuments.add(createDocumentDescription(it))
        }

        registry.nestedDocuments.values.forEach {
            if (!it.exposedAtRest || (pluginId != null && WebPluginsAssociationsRegistry.get().associations[it.id] != pluginId)) {
                return@forEach
            }
            result.domainDocuments.add(createDocumentDescription(it))
        }

    }

    private fun createDocumentDescription(it: BaseDocumentDescription): DocumentDescriptionDT {
        val documentDescription = DocumentDescriptionDT()
        documentDescription.root = it is DocumentDescription
        documentDescription.id = it.id+"JS"
        documentDescription.extendsId = if(it.extendsId ==null) null else "${it.extendsId}JS"
        documentDescription.isAbstract = it.isAbstract
        it.properties.values.forEach { propertyDescription ->
            val property = DocumentPropertyDescriptionDT()
            property.className = getClassName(propertyDescription.className)
            property.id = propertyDescription.id
            property.type = toRestCollectionType(propertyDescription.type)
            property.nonNullable = propertyDescription.nonNullable
            documentDescription.properties.add(property)
        }
        it.collections.values.forEach { collectionDescription ->
            val coll = DocumentCollectionDescriptionDT()
            coll.elementClassName = getClassName(collectionDescription.elementClassName)
            coll.id = collectionDescription.id
            coll.elementType = toRestCollectionType(collectionDescription.elementType)
            documentDescription.collections.add(coll)
        }

        return documentDescription
    }

    private fun toRestCollectionType(type: DocumentPropertyType): DocumentPropertyTypeDT {
        return DocumentPropertyTypeDT.valueOf(type.name)

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
    private fun updateUiMetadata(result: GetMetadataResponse, registry: UiMetaRegistry, pluginId: String?) {
        registry.enums.values.forEach { en ->
            if (pluginId != null && WebPluginsAssociationsRegistry.get().associations[en.id] != pluginId) {
                return@forEach
            }
            val ed = UiEnumDescriptionDT()
            ed.id = en.id + "JS"
            result.uiEnums.add(ed)
            en.items.values.forEach {
                val item = UiEnumItemDescriptionDT()
                item.id = it.id
                item.displayName = it.getDisplayName()
                ed.items.add(item)
            }
        }
        registry.viewModels.values.forEach {ed->
            if (pluginId != null && WebPluginsAssociationsRegistry.get().associations[ed.id] != pluginId) {
                return@forEach
            }
            val entityDescriptionDT = VMEntityDescriptionDT()
            entityDescriptionDT.id = ed.id +"JS"
            entityDescriptionDT.extendsId = ed.extendsId?.let { it+"JS" }
            ed.properties.values.forEach { pd ->
                val propertyDescriptionDT = VMPropertyDescriptionDT()
                propertyDescriptionDT.className = getClassName(pd.className)
                propertyDescriptionDT.id = pd.id
                propertyDescriptionDT.nonNullable = pd.nonNullable
                propertyDescriptionDT.type = VMPropertyTypeDT.valueOf(pd.type.name)
                entityDescriptionDT.properties.add(propertyDescriptionDT)
            }
            ed.collections.values.forEach { cd ->
                val collectionDescriptionDT = VMCollectionDescriptionDT()
                collectionDescriptionDT.elementClassName = getClassName(cd.elementClassName)
                collectionDescriptionDT.id = cd.id
                collectionDescriptionDT.elementType = VMCollectionTypeDT.valueOf(cd.elementType.name)
                entityDescriptionDT.collections.add(collectionDescriptionDT)
            }
            result.viewModels.add(entityDescriptionDT)
        }
        registry.viewSettings.values.forEach {ed->
            if (pluginId != null && WebPluginsAssociationsRegistry.get().associations[ed.id] != pluginId) {
                return@forEach
            }
            val entityDescriptionDT = VSEntityDescriptionDT()
            entityDescriptionDT.id = ed.id+"JS"
            entityDescriptionDT.extendsId = ed.extendsId?.let { it+"JS" }
            ed.properties.values.forEach { pd ->
                val propertyDescriptionDT = VSPropertyDescriptionDT()
                propertyDescriptionDT.className = getClassName(pd.className)
                propertyDescriptionDT.id = pd.id
                propertyDescriptionDT.type = VSPropertyTypeDT.valueOf(pd.type.name)
                propertyDescriptionDT.lateInit = pd.lateInit
                entityDescriptionDT.properties.add(propertyDescriptionDT)
            }
            ed.collections.values.forEach { cd ->
                val collectionDescriptionDT = VSCollectionDescriptionDT()
                collectionDescriptionDT.elementClassName = getClassName(cd.elementClassName)
                collectionDescriptionDT.id = cd.id
                collectionDescriptionDT.elementType = VSCollectionTypeDT.valueOf(cd.elementType.name)
                entityDescriptionDT.collections.add(collectionDescriptionDT)
            }
            result.viewSettings.add(entityDescriptionDT)
        }
        registry.viewValidations.values.forEach {ed->
            if (pluginId != null && WebPluginsAssociationsRegistry.get().associations[ed.id] != pluginId) {
                return@forEach
            }
            val entityDescriptionDT = VVEntityDescriptionDT()
            entityDescriptionDT.id = ed.id+"JS"
            entityDescriptionDT.extendsId = ed.extendsId?.let { it+"JS" }
            ed.properties.values.forEach { pd ->
                val propertyDescriptionDT = VVPropertyDescriptionDT()
                propertyDescriptionDT.className = getClassName(pd.className)
                propertyDescriptionDT.id = pd.id
                propertyDescriptionDT.type = VVPropertyTypeDT.valueOf(pd.type.name)
                propertyDescriptionDT.lateInit = pd.lateInit
                entityDescriptionDT.properties.add(propertyDescriptionDT)
            }
            ed.collections.values.forEach { cd ->
                val collectionDescriptionDT = VVCollectionDescriptionDT()
                collectionDescriptionDT.elementClassName = getClassName(cd.elementClassName)
                collectionDescriptionDT.id = cd.id
                collectionDescriptionDT.elementType = VVCollectionTypeDT.valueOf(cd.elementType.name)
                entityDescriptionDT.collections.add(collectionDescriptionDT)
            }
            result.viewValidations.add(entityDescriptionDT)
        }
        registry.optionsGroups.values.forEach { og ->
            if(og.options.any { pluginId == null || pluginId == WebPluginsAssociationsRegistry.get().associations["options-${og.id}-${it.id}"]}){
                val group = OptionsGroupDescriptionDT()
                group.id = og.id
                result.optionsGroups.add(group)
                og.options.forEach {
                    if(pluginId == null || pluginId == WebPluginsAssociationsRegistry.get().associations["options-${og.id}-${it.id}"]){
                        val option = OptionDescriptionDT()
                        option.id = it.id
                        option.displayName = it.getDisplayName()
                        group.options.add(option)
                    }
                }
            }
        }
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
            ed.extendsId = ett.extendsId?.let { ext -> ext+"JS"}
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
                updateMiscMetadata(result, MiscMetaRegistry.get(), request.pluginId)
                updateUiMetadata(result, UiMetaRegistry.get(), request.pluginId)
                updateWebMessages(result, WebMessagesMetaRegistry.get(), request.pluginId)
                result
            }
        }
    }

    private fun updateWebMessages(result: GetMetadataResponse, registry: WebMessagesMetaRegistry, pluginId: String?) {
        registry.bundles.values.forEach {bundle ->
            if (pluginId != null && WebPluginsAssociationsRegistry.get().associations["web-messages-${bundle.id}"] != pluginId) {
                return@forEach
            }
            val b = WebMessagesBundleDT()
            b.id = bundle.id
            result.webMessages.add(b)
            bundle.messages.values.forEach {message->
                val m = WebMessageDT()
                m.id = message.id
                m.displayName = message.getDisplayName()
                b.messages.add(m)
            }
        }
    }


}