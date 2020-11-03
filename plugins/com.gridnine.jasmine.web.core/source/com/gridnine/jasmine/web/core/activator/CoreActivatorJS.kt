/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.web.core.activator

import com.gridnine.jasmine.server.core.model.custom.*
import com.gridnine.jasmine.server.core.model.domain.*
import com.gridnine.jasmine.server.core.model.l10n.L10nMetaRegistryJS
import com.gridnine.jasmine.server.core.model.rest.*
import com.gridnine.jasmine.server.core.model.ui.*
import com.gridnine.jasmine.web.core.CoreWebMessagesInitializerJS
import com.gridnine.jasmine.web.core.DomainReflectionUtilsJS
import com.gridnine.jasmine.web.core.RestReflectionUtilsJS
import com.gridnine.jasmine.web.core.UiReflectionUtilsJS
import com.gridnine.jasmine.web.core.application.ActivatorJS
import com.gridnine.jasmine.web.core.application.EnvironmentJS
import com.gridnine.jasmine.web.core.mainframe.AdditionalMenuButton
import com.gridnine.jasmine.web.core.mainframe.ObjectsHandlersCache
import com.gridnine.jasmine.web.core.mainframe.SaveObjectButtonHandler
import com.gridnine.jasmine.web.core.mainframe.ShowVersionsMenuItem
import com.gridnine.jasmine.web.core.reflection.ReflectionFactoryJS
import com.gridnine.jasmine.web.core.remote.RpcManager
import com.gridnine.jasmine.web.core.remote.StandardRpcManager
import com.gridnine.jasmine.web.core.serialization.JsonSerializerJS
import com.gridnine.jasmine.web.core.ui.ClientRegistry
import kotlin.js.Promise

class CoreActivatorJS: ActivatorJS {

    override fun configure(config: Map<String, Any?>) {
        val reflectionFactory = ReflectionFactoryJS()
        EnvironmentJS.publish(reflectionFactory)
        ReflectionFactoryJS.get().registerClass(ObjectReferenceJS.qualifiedClassName){ObjectReferenceJS()}
        ReflectionFactoryJS.get().registerQualifiedName(ObjectReferenceJS::class, ObjectReferenceJS.qualifiedClassName)
        ReflectionFactoryJS.get().registerClass(TextBoxConfigurationJS.qualifiedClassName){TextBoxConfigurationJS()}
        ReflectionFactoryJS.get().registerQualifiedName(TextBoxConfigurationJS::class, TextBoxConfigurationJS.qualifiedClassName)
        ReflectionFactoryJS.get().registerClass(PasswordBoxConfigurationJS.qualifiedClassName){PasswordBoxConfigurationJS()}
        ReflectionFactoryJS.get().registerQualifiedName(PasswordBoxConfigurationJS::class, PasswordBoxConfigurationJS.qualifiedClassName)
        DomainReflectionUtilsJS.registerWebDomainClasses()
        RestReflectionUtilsJS.registerWebRestClasses()
        UiReflectionUtilsJS.registerWebUiClasses()
        val domainRegisty = DomainMetaRegistryJS()
        EnvironmentJS.publish(domainRegisty)
        val restRegistry = RestMetaRegistryJS()
        EnvironmentJS.publish(restRegistry)
        val customRegisty = CustomMetaRegistryJS()
        EnvironmentJS.publish(customRegisty)
        val uiRegisty = UiMetaRegistryJS()
        EnvironmentJS.publish(uiRegisty)
        val l10nRegistry = L10nMetaRegistryJS()
        EnvironmentJS.publish(l10nRegistry)
        val rpcManager = StandardRpcManager(config[StandardRpcManager.BASE_REST_URL_KEY] as String)
        EnvironmentJS.publish(RpcManager::class, rpcManager)
        EnvironmentJS.publish(JsonSerializerJS())
        EnvironmentJS.publish(ClientRegistry())
        EnvironmentJS.publish(ObjectsHandlersCache())
        ClientRegistry.get().register(SaveObjectButtonHandler())
        ClientRegistry.get().register(AdditionalMenuButton())
        ClientRegistry.get().register(ShowVersionsMenuItem())
    }

    override fun activate(): Promise<Unit> {
        return Promise{resolve, _ ->
            RpcManager.get().postDynamic("standard_standard_meta", "{}").then<dynamic>{
                initDomainRegistry(it)
                initRestRegistry(it)
                initCustomRegistry(it)
                initUiRegistry(it)
                initL10nRegistry(it)
                CoreWebMessagesInitializerJS.initialize()
                resolve(Unit)
            }
        }
    }

    private fun initUiRegistry(it: dynamic) {
        val uiRegistry = UiMetaRegistryJS.get()
        it.uiEnums?.forEach{ itJs ->
            val enum = UiEnumDescriptionJS(itJs.id)
            itJs.items.forEach{ item:dynamic ->
                enum.items.put(item.id, UiEnumItemDescriptionJS(item.id, item.displayName))
            }
            uiRegistry.enums.put(enum.id, enum)
        }
        it.viewModels?.forEach{ itJs ->
            val entityDescription = VMEntityDescriptionJS(itJs.id)
            itJs.properties?.forEach{ item:dynamic ->
                entityDescription.properties.put(item.id , VMPropertyDescriptionJS(item.id, VMPropertyTypeJS.valueOf(item.type), item.className, item.nonNullable))
            }
            itJs.collections?.forEach{ item:dynamic ->
                entityDescription.collections.put(item.id , VMCollectionDescriptionJS(item.id, VMCollectionTypeJS.valueOf(item.elementType), item.elementClassName))
            }
            uiRegistry.viewModels.put(entityDescription.id, entityDescription)
        }
        it.viewSettings?.forEach{ itJs ->
            val entityDescription = VSEntityDescriptionJS(itJs.id)
            itJs.properties?.forEach{ item:dynamic ->
                entityDescription.properties.put(item.id , VSPropertyDescriptionJS(item.id, VSPropertyTypeJS.valueOf(item.type), item.className))
            }
            itJs.collections?.forEach{ item:dynamic ->
                entityDescription.collections.put(item.id , VSCollectionDescriptionJS(item.id, VSCollectionTypeJS.valueOf(item.elementType), item.elementClassName))
            }
            uiRegistry.viewSettings.put(entityDescription.id, entityDescription)
        }
        it.viewValidations?.forEach{ itJs ->
            val entityDescription = VVEntityDescriptionJS(itJs.id)
            itJs.properties?.forEach{ item:dynamic ->
                entityDescription.properties.put(item.id , VVPropertyDescriptionJS(item.id, VVPropertyTypeJS.valueOf(item.type), item.className))
            }
            itJs.collections?.forEach{ item:dynamic ->
                entityDescription.collections.put(item.id , VVCollectionDescriptionJS(item.id, VVCollectionTypeJS.valueOf(item.elementType), item.elementClassName))
            }
            uiRegistry.viewValidations.put(entityDescription.id, entityDescription)
        }
    }

    private fun initL10nRegistry(it: dynamic) {
        val registry = L10nMetaRegistryJS.get()
        it.webMessages?.forEach{item:dynamic ->
            val bundleId = item.id as String
            val messages = registry.messages.getOrPut(bundleId, { linkedMapOf()}) as MutableMap
            item.messages?.forEach{message ->
                messages[message.id] = message.displayName
                Unit
            }
            Unit
        }
    }

    private fun initRestRegistry(it: dynamic) {
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
                        nonNullable = prop.nonNullable,
                        className = prop.className))

            }
            itJs.collections?.forEach{ coll:dynamic ->
                entity.collections.put(coll.id, RestCollectionDescriptionJS(
                        id = coll.id,
                        elementType = RestPropertyTypeJS.valueOf(coll.elementType),
                        elementClassName = coll.elementClassName))
            }
            restRegistry.entities.put(entity.id, entity)
        }
        it.operations?.forEach{itJs ->
            val op = RestOperationDescriptionJS(itJs.id, itJs.request, itJs.response)
            restRegistry.operations.put(op.id, op)
        }

    }

    private fun initCustomRegistry(it: dynamic) {
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
            entity.isAbstract = itJs.abstract
            entity.extendsId = itJs.extends
            itJs.properties?.forEach{ prop:dynamic ->
                entity.properties.put(prop.id, CustomPropertyDescriptionJS(
                        id = prop.id,
                        type = CustomTypeJS.valueOf(prop.type),
                        lateinit = prop.lateInit,
                        nonNullable = prop.nonNullable,
                        className = prop.className))

            }
            itJs.collections?.forEach{ coll:dynamic ->
                entity.collections.put(coll.id, CustomCollectionDescriptionJS(
                        id = coll.id,
                        elementType = CustomTypeJS.valueOf(coll.elementType),
                        elementClassName = coll.elementClassName))
            }
            customRegistry.entities.put(entity.id, entity)
        }
    }


    private fun initDomainRegistry(it: dynamic) {
        val domainRegistry = DomainMetaRegistryJS.get()
        it.domainEnums?.forEach{ itJs ->
            val enum = DomainEnumDescriptionJS(itJs.id)
            itJs.items.forEach{ item:dynamic ->
                enum.items.put(item.id, DomainEnumItemDescriptionJS(item.id, item.displayName))
            }
            domainRegistry.enums.put(enum.id, enum)
        }
        it.domainIndexes?.forEach{itJs ->
            val entity = IndexDescriptionJS(itJs.id, itJs.displayName, itJs.document)
            fillBaseIndexDescription(entity, itJs)
            domainRegistry.indexes[entity.id] = entity
            Unit
        }
        it.domainAssets?.forEach{itJs ->
            val entity = AssetDescriptionJS(itJs.id, itJs.displayName)
            fillBaseIndexDescription(entity, itJs)
            domainRegistry.assets[entity.id] = entity
            Unit
        }
        it.domainDocuments?.forEach{itJs ->
            val entity = DocumentDescriptionJS(itJs.id, itJs.isAbstract, itJs.extendsId)
            itJs.properties?.forEach{ prop:dynamic ->
                val id = DocumentPropertyDescriptionJS(id = prop.id,type = DocumentPropertyTypeJS.valueOf(prop.type),
                        className = prop.className, nonNullable = prop.nonNullable)
                entity.properties.put(prop.id, id)
            }
            itJs.collections?.forEach{ coll:dynamic ->
                val cd = DocumentCollectionDescriptionJS(id = coll.id,
                        elementType = DocumentPropertyTypeJS.valueOf(coll.elementType), elementClassName = coll.elementClassName)
                entity.collections.put(coll.id, cd)
            }
            domainRegistry.documents[entity.id] = entity

            Unit
        }

    }

    private fun fillBaseIndexDescription(entity: BaseIndexDescriptionJS, itJs: dynamic) {
        itJs.properties?.forEach{ prop:dynamic ->
            val id = IndexPropertyDescriptionJS(id = prop.id,type = DatabasePropertyTypeJS.valueOf(prop.type),
                    className = prop.className, displayName = prop.displayName, nonNullable = prop.nonNullable)
            entity.properties.put(prop.id, id)
        }
        itJs.collections?.forEach{ coll:dynamic ->
            val cd = IndexCollectionDescriptionJS(id = coll.id,
                    elementType = DatabaseCollectionTypeJS.valueOf(coll.elementType),
                    displayName = coll.displayName, elementClassName = coll.elementClassName, unique = coll.unique)
            entity.collections.put(coll.id, cd)
        }
    }
}