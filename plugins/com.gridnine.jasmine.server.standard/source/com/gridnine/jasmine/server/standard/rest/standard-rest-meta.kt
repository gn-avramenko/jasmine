/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/
@file:Suppress("unused", "UnsafeCastFromDynamic", "UNCHECKED_CAST")

package com.gridnine.jasmine.server.standard.rest

import com.gridnine.jasmine.server.core.model.custom.CustomMetaRegistry
import com.gridnine.jasmine.server.core.model.custom.CustomType
import com.gridnine.jasmine.server.core.model.domain.*
import com.gridnine.jasmine.server.core.model.rest.RestMetaRegistry
import com.gridnine.jasmine.server.core.model.rest.RestPropertyType
import com.gridnine.jasmine.server.core.rest.RestHandler
import com.gridnine.jasmine.server.core.rest.RestOperationContext
import com.gridnine.jasmine.server.standard.model.custom.*
import com.gridnine.jasmine.server.standard.model.rest.*


class StandardMetaRestHandler : RestHandler<GetMetadataRequest, GetMetadataResponse> {
    override fun service(request: GetMetadataRequest, ctx:RestOperationContext): GetMetadataResponse {
        val result = GetMetadataResponse()
        CustomMetaRegistry.get().enums.values.forEach {
            val enumDescr = CustomEnumDescriptionDT()
            enumDescr.id = it.id+"JS"
            it.items.values.forEach { enumItemDescription ->
                enumDescr.items.add(enumItemDescription.id)
            }
            result.customEnums.add(enumDescr)
        }
        CustomMetaRegistry.get().entities.values.forEach {
            val entityDescr = CustomEntityDescriptionDT()
            entityDescr.id = it.id+"JS"
            entityDescr.abstract = it.isAbstract
            entityDescr.extends = it.extendsId?.let { ext -> ext+"JS"}

            it.properties.values.forEach { propertyDescrition ->
                val property = CustomPropertyDescriptionDT()
                property.className = getClassName(propertyDescrition.className)
                property.id = propertyDescrition.id
                property.type = toRestCollectionType(propertyDescrition.type)
                property.nonNullable = propertyDescrition.nonNullable
                property.lateInit = propertyDescrition.lateinit
                entityDescr.properties.add(property)
            }
            it.collections.values.forEach { collectionDescription ->
                val coll = CustomCollectionDescriptionDT()
                coll.elementClassName = getClassName(collectionDescription.elementClassName)
                coll.id = collectionDescription.id
                coll.elementType = toRestCollectionType(collectionDescription.elementType)
                entityDescr.collections.add(coll)
            }

            result.customEntities.add(entityDescr)
        }
        RestMetaRegistry.get().enums.values.forEach {
            val enumDescr = RestEnumDescriptionDT()
            enumDescr.id = it.id+"JS"
            it.items.values.forEach { enumItemDescription ->
                enumDescr.items.add(enumItemDescription.id)
            }
            result.restEnums.add(enumDescr)
        }
        RestMetaRegistry.get().entities.values.forEach {
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
        RestMetaRegistry.get().operations.values.forEach {
            val opDescr = RestOperationDescriptionDT()
            opDescr.id = it.id
            opDescr.request = it.requestEntity+"JS"
            opDescr.response = it.responseEntity+"JS"
            result.operations.add(opDescr)
        }
        DomainMetaRegistry.get().enums.values.forEach {
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
        DomainMetaRegistry.get().indexes.values.forEach {
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
        DomainMetaRegistry.get().assets.values.forEach {
            val assetDescription = AssetDescriptionDT()
            assetDescription.displaName = it.getDisplayName()
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
        DomainMetaRegistry.get().documents.values.forEach {
            if(it.parameters[DomainMetaRegistry.EXPOSED_IN_REST_KEY] == "true"){
                result.domainDocuments.add(createDocumentDescription(it))
            }
        }
        DomainMetaRegistry.get().nestedDocuments.values.forEach {
            if(it.parameters[DomainMetaRegistry.EXPOSED_IN_REST_KEY] == "true"){
                result.domainDocuments.add(createDocumentDescription(it))
            }
        }
        return result
    }

    private fun createDocumentDescription(it: BaseDocumentDescription): DocumentDescriptionDT {
        val documentDescription = DocumentDescriptionDT()
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

    private fun getClassName(className: String?): String? {
        return when(className){
            null -> null
            else -> "${className}JS"
        }
    }

    private fun toRestCollectionType(elementType: DatabaseCollectionType): DatabaseCollectionTypeDT {
        return DatabaseCollectionTypeDT.valueOf(elementType.name)

    }

    private fun toRestCollectionType(type: RestPropertyType): RestPropertyTypeDT {
        return RestPropertyTypeDT.valueOf(type.name)

    }
    private fun toRestCollectionType(type: CustomType): CustomTypeDT {
        return CustomTypeDT.valueOf(type.name)

    }

    private fun toRestCollectionType(type: DatabasePropertyType): DatabasePropertyTypeDT {
        return DatabasePropertyTypeDT.valueOf(type.name)

    }

    private fun toRestCollectionType(type: DocumentPropertyType): DocumentPropertyTypeDT {
        return DocumentPropertyTypeDT.valueOf(type.name)

    }
}
