/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

@file:Suppress("UnsafeCastFromDynamic")

package com.gridnine.jasmine.web.core.remote

import com.gridnine.jasmine.common.core.meta.*

object WebCoreMetaRegistriesUpdater {
    suspend fun updateMetaRegistries(module:String){
        val response = RpcManager.get().postDynamic("core_core_getMetadata", """{
          "pluginId" : "$module"    
        }""".trimIndent())
        updateCustomMetadata(response)
        updateDomainMetadata(response)
        updateL10nRegistry(response)
        updateRestRegistry(response)
        updateUiRegistry(response)
    }

    private fun updateUiRegistry(it: dynamic) {
        val uiRegistry = UiMetaRegistryJS.get()
        it.uiEnums?.forEach{ itJs ->
            val enum = UiEnumDescriptionJS(itJs.id)
            itJs.items.forEach{ item:dynamic ->
                enum.items.put(item.id, UiEnumItemDescriptionJS(item.id).apply {
                    displayName = item.displayName
                })
            }
            uiRegistry.enums.put(enum.id, enum)
        }
        it.viewModels?.forEach{ itJs ->
            val entityDescription = VMEntityDescriptionJS(itJs.id)
            entityDescription.extendsId = itJs.extendsId
            itJs.properties?.forEach{ item:dynamic ->
                entityDescription.properties.put(item.id , VMPropertyDescriptionJS(item.id, VMPropertyTypeJS.valueOf(item.type), item.className, item.nonNullable, item.lateinit))
            }
            itJs.collections?.forEach{ item:dynamic ->
                entityDescription.collections.put(item.id , VMCollectionDescriptionJS(item.id, VMCollectionTypeJS.valueOf(item.elementType), item.elementClassName))
            }
            uiRegistry.viewModels.put(entityDescription.id, entityDescription)
        }
        it.viewSettings?.forEach{ itJs ->
            val entityDescription = VSEntityDescriptionJS(itJs.id)
            entityDescription.extendsId = itJs.extendsId
            itJs.properties?.forEach{ item:dynamic ->
                entityDescription.properties.put(item.id , VSPropertyDescriptionJS(item.id, VSPropertyTypeJS.valueOf(item.type), item.className, item.lateinit))
            }
            itJs.collections?.forEach{ item:dynamic ->
                entityDescription.collections.put(item.id , VSCollectionDescriptionJS(item.id, VSCollectionTypeJS.valueOf(item.elementType), item.elementClassName))
            }
            uiRegistry.viewSettings.put(entityDescription.id, entityDescription)
        }
        it.viewValidations?.forEach{ itJs ->
            val entityDescription = VVEntityDescriptionJS(itJs.id)
            entityDescription.extendsId = itJs.extendsId
            itJs.properties?.forEach{ item:dynamic ->
                entityDescription.properties.put(item.id , VVPropertyDescriptionJS(item.id, VVPropertyTypeJS.valueOf(item.type), item.className, item.lateinit))
            }
            itJs.collections?.forEach{ item:dynamic ->
                entityDescription.collections.put(item.id , VVCollectionDescriptionJS(item.id, VVCollectionTypeJS.valueOf(item.elementType), item.elementClassName))
            }
            uiRegistry.viewValidations.put(entityDescription.id, entityDescription)
        }
        it.optionsGroups?.forEach{ itJs ->
            val groupId = itJs.id
            val group  = uiRegistry.optionsGroups.getOrPut(groupId){
                OptionsGroupDescriptionJS(groupId)
            }
            itJs.options.forEach{optJs ->
                group.options.add(OptionDescriptionJS(optJs.id).apply {
                    displayName = optJs.displayName
                })
            }
        }
    }

    private fun updateRestRegistry(it: dynamic) {
        val restRegistry = RestMetaRegistryJS.get()
        it.restEnums?.forEach{ itJs ->
            val enum = RestEnumDescriptionJS(itJs.id)
            itJs.items.forEach{ item:dynamic ->
                enum.items.put(item, RestEnumItemDescriptionJS(item))
            }
            restRegistry.enums.put(enum.id, enum)
        }
        it.restEntities?.forEach{itJs ->
            val entity = RestEntityDescriptionJS(itJs.id)
            entity.isAbstract = itJs.abstract
            entity.extendsId = itJs.extends
            itJs.properties?.forEach{ prop:dynamic ->
                entity.properties.put(prop.id, RestPropertyDescriptionJS(
                        id = prop.id,
                        type = RestPropertyTypeJS.valueOf(prop.type),
                        lateinit = prop.lateInit,
                        nonNullable = prop.nonNullable).apply {
                    className = prop.className
                })
            }
            itJs.collections?.forEach{ coll:dynamic ->
                entity.collections.put(coll.id, RestCollectionDescriptionJS(
                        id = coll.id,
                        elementType = RestPropertyTypeJS.valueOf(coll.elementType)).apply {
                    elementClassName = coll.elementClassName
                })
            }
            restRegistry.entities.put(entity.id, entity)
        }
        it.operations?.forEach{itJs ->
            val op = RestOperationDescriptionJS(itJs.id, itJs.groupId, itJs.request, itJs.response)
            restRegistry.operations.put(op.id, op)
        }

    }
    private fun updateL10nRegistry(it: dynamic) {
        val registry = L10nMetaRegistryJS.get()
        it.webMessages?.forEach{item:dynamic ->
            val bundleId = item.id as String
            val messages = registry.messages.getOrPut(bundleId, { linkedMapOf()}) as MutableMap
            item.messages?.forEach{message ->
                messages[message.id] = message.displayName
                Unit
            }
        }
    }

    private fun updateDomainMetadata(it: dynamic) {
        val domainRegistry = DomainMetaRegistryJS.get()
        it.domainEnums?.forEach{ itJs ->
            val enum = DomainEnumDescriptionJS(itJs.id)
            itJs.items.forEach{ item:dynamic ->
                enum.items.put(item.id, DomainEnumItemDescriptionJS(item.id).apply {
                    displayName = item.displayName
                })
            }
            domainRegistry.enums.put(enum.id, enum)
        }
        it.domainIndexes?.forEach{itJs ->
            val entity = IndexDescriptionJS(itJs.id).apply {
                displayName = itJs.displayName
                document = itJs.document
            }
            fillBaseIndexDescription(entity, itJs)
            domainRegistry.indexes[entity.id] = entity
            Unit
        }
        it.domainAssets?.forEach{itJs ->
            val entity = AssetDescriptionJS(itJs.id).apply {
                displayName = itJs.displayName
            }
            fillBaseIndexDescription(entity, itJs)
            domainRegistry.assets[entity.id] = entity
            Unit
        }
    }

    private fun fillBaseIndexDescription(entity: BaseIndexDescriptionJS, itJs: dynamic) {
        itJs.properties?.forEach{ prop:dynamic ->
            val id = IndexPropertyDescriptionJS(prop.id).apply {
                type = DatabasePropertyTypeJS.valueOf(prop.type)
                className = prop.className
                displayName = prop.displayName
                nonNullable = prop.nonNullable
            }
            entity.properties.put(prop.id, id)
        }
        itJs.collections?.forEach{ coll:dynamic ->
            val cd = IndexCollectionDescriptionJS(coll.id).apply {
                elementType = DatabaseCollectionTypeJS.valueOf(coll.elementType)
                displayName = coll.displayName
                elementClassName = coll.elementClassName
                unique = coll.unique
            }
            entity.collections.put(coll.id, cd)
        }
    }


    private fun updateCustomMetadata(it: dynamic) {
        val customRegistry = CustomMetaRegistryJS.get()
        it.customEnums?.forEach{ itJs ->
            val enum = CustomEnumDescriptionJS(itJs.id)
            itJs.items.forEach{ item:dynamic ->
                enum.items.put(item, CustomEnumItemDescriptionJS(item))
            }
            customRegistry.enums.put(enum.id, enum)
        }
        it.customEntities?.forEach{itJs ->
            val entity = CustomEntityDescriptionJS(itJs.id)
            entity.isAbstract = itJs.isAbstract
            entity.extendsId = itJs.extendsId
            itJs.properties?.forEach{ prop:dynamic ->
                entity.properties.put(prop.id, CustomPropertyDescriptionJS(
                        id = prop.id,
                        type = CustomTypeJS.valueOf(prop.type),
                        lateinit = prop.lateInit,
                        nonNullable = prop.nonNullable).apply {
                    className = prop.className
                })

            }
            itJs.collections?.forEach{ coll:dynamic ->
                entity.collections.put(coll.id, CustomCollectionDescriptionJS(
                        id = coll.id,
                        elementType = CustomTypeJS.valueOf(coll.elementType)).apply {
                        elementClassName = coll.elementClassName
                })
            }
            itJs.maps?.forEach{ map:dynamic ->
                entity.maps.put(map.id, CustomMapDescriptionJS(
                        id = map.id,
                        keyClassType = CustomTypeJS.valueOf(it.keyClassType),
                        valueClassType = CustomTypeJS.valueOf(it.valueClassType)
                ).apply {
                    keyClassName = map.keyClassName
                    valueClassName = map.valueClassName
                })
            }
            customRegistry.entities.put(entity.id, entity)
        }
    }
}