/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/
@file:Suppress("unused")

package com.gridnine.jasmine.web.core.application

import com.gridnine.jasmine.server.standard.model.rest.LayoutTypeDTJS
import com.gridnine.jasmine.server.standard.model.rest.TableColumnTypeDTJS
import com.gridnine.jasmine.server.standard.model.rest.ViewTypeDTJS
import com.gridnine.jasmine.server.standard.model.rest.WidgetTypeDTJS
import com.gridnine.jasmine.web.core.model.common.FakeEnumJS
import com.gridnine.jasmine.web.core.model.domain.*
import com.gridnine.jasmine.web.core.model.rest.*
import com.gridnine.jasmine.web.core.model.ui.*
import com.gridnine.jasmine.web.core.remote.RpcManager
import com.gridnine.jasmine.web.core.remote.StandardRpcManager
import com.gridnine.jasmine.web.core.utils.ReflectionFactoryJS
import kotlin.js.Promise

@Suppress("UnsafeCastFromDynamic")
class CoreActivatorJS:ActivatorJS{

    override fun configure(config:Map<String,Any?>) {
        val reflectionFactory = ReflectionFactoryJS()
        EnvironmentJS.publish(reflectionFactory)

        ReflectionFactoryJS.get().registerClass(EntityReferenceJS.qualifiedClassName){EntityReferenceJS()}
        ReflectionFactoryJS.get().registerQualifiedName(EntityReferenceJS::class, EntityReferenceJS.qualifiedClassName)
        ReflectionFactoryJS.get().registerClass(EnumSelectConfigurationJS.qualifiedClassName) {EnumSelectConfigurationJS<FakeEnumJS>()}
        ReflectionFactoryJS.get().registerClass(EntityAutocompleteDataSourceJS.qualifiedClassName) { EntityAutocompleteDataSourceJS()}
        ReflectionFactoryJS.get().registerClass(EntityAutocompleteConfigurationJS.qualifiedClassName) { EntityAutocompleteConfigurationJS()}
        ReflectionFactoryJS.get().registerQualifiedName(EnumSelectConfigurationJS::class, EnumSelectConfigurationJS.qualifiedClassName)
        ReflectionFactoryJS.get().registerClass(TextColumnConfigurationJS.qualifiedClassName)  {TextColumnConfigurationJS()}
        ReflectionFactoryJS.get().registerClass(IntegerColumnConfigurationJS.qualifiedClassName) {IntegerColumnConfigurationJS()}
        ReflectionFactoryJS.get().registerClass(FloatColumnConfigurationJS.qualifiedClassName)  {FloatColumnConfigurationJS()}
        ReflectionFactoryJS.get().registerClass(EnumColumnConfigurationJS.qualifiedClassName)  {EnumColumnConfigurationJS<FakeEnumJS>()}
        ReflectionFactoryJS.get().registerClass(EntityColumnConfigurationJS.qualifiedClassName)  {EntityColumnConfigurationJS()}
        ReflectionFactoryJS.get().registerClass(TableConfigurationJS.qualifiedClassName) {TableConfigurationJS<BaseVSEntityJS>()}
        val domainRegisty = DomainMetaRegistryJS()
        EnvironmentJS.publish(domainRegisty)
        val restRegistry = RestMetaRegistryJS()
        EnvironmentJS.publish(restRegistry)
        val uiRegistry = UiMetaRegistryJS()
        EnvironmentJS.publish(uiRegistry)
        val rpcManager = StandardRpcManager(config[StandardRpcManager.BASE_REST_URL_KEY] as String)
        EnvironmentJS.publish(RpcManager::class, rpcManager)
    }
    override fun activate(): Promise<Unit> {
        return Promise{resolve, _ ->
            RpcManager.get().postDynamic("standard_standard_meta", "{}").then<dynamic>{
                initDomainRegistry(it)
                initRestRegistry(it)
                initUiRegistry(it)
                console.log(UiMetaRegistryJS.get())
                resolve(Unit)
            }
        }
    }

    private fun initUiRegistry(it: dynamic) {
        val  uiRegistry = UiMetaRegistryJS.get()
        it.lists?.forEach { itJs ->
            val listId = itJs.id
            val objectId = itJs.objectId
            val listDescr = ListDescriptionJS(listId, objectId)
            itJs.toolbuttons?.forEach { button ->
                listDescr.toolButtons.add(ListToolButtonDescriptionJS(button.id, button.handler, button.weight, button.displayName))
            }
            uiRegistry.lists[listId] = listDescr
            Unit
        }
        it.sharedEditorButtons?.forEach{ itDT ->
            uiRegistry.sharedEditorToolButtons.add(SharedEditorToolButtonDescriptionJS(itDT.id,itDT.handler,itDT.weight, itDT.displayName))
        }
        it.editors?.forEach { itJs ->
            val editorId = itJs.id
            val editor = EditorDescriptionJS(editorId,  itJs.viewId)
            uiRegistry.editors[editorId] = editor
            itJs.buttons?.forEach { button ->
                editor.toolButtons.add(EditorToolButtonDescriptionJS(button.id, button.handler, button.weight, button.displayName))
                Unit
            }
            Unit
        }
        it.dialogs?.forEach { itJs ->
            val dialogId = itJs.id
            val dialog = DialogDescriptionJS(dialogId,  itJs.viewId, itJs.width, itJs.height)
            uiRegistry.dialogs[dialogId] = dialog
            itJs.buttons.forEach { button ->
                dialog.buttons.add(DialogToolButtonDescriptionJS(button.id, button.handler,  button.displayName))
                Unit
            }
            Unit
        }
        it.vmEntities?.forEach { itJs ->
            val entity = VMEntityDescriptionJS(itJs.id)
            itJs.properties?.forEach { prop: dynamic ->
                entity.properties.put(prop.id, VMPropertyDescriptionJS(prop.id, VMPropertyTypeJS.valueOf(prop.type), prop.className, prop.nonNullable))
            }
            itJs.collections?.forEach { coll: dynamic ->
                entity.collections.put(coll.id, VMCollectionDescriptionJS(coll.id, VMCollectionTypeJS.valueOf(coll.elementType), coll.elementClassName))
            }
            uiRegistry.viewModels[entity.id] = entity
            Unit
        }
        it.vsEntities?.forEach { itJs ->
            val entity = VSEntityDescriptionJS(itJs.id)
            itJs.properties?.forEach { prop: dynamic ->
                entity.properties.put(prop.id, VSPropertyDescriptionJS(prop.id, VSPropertyTypeJS.valueOf(prop.type), prop.className))
            }
            itJs.collections?.forEach { coll: dynamic ->
                entity.collections.put(coll.id, VSCollectionDescriptionJS(coll.id, VSCollectionTypeJS.valueOf(coll.elementType), coll.elementClassName))
            }
            uiRegistry.viewSettings[entity.id] = entity
            Unit
        }
        it.vvEntities?.forEach { itJs ->
            val entity = VVEntityDescriptionJS(itJs.id)
            itJs.properties?.forEach { prop: dynamic ->
                entity.properties.put(prop.id, VVPropertyDescriptionJS(prop.id, VVPropertyTypeJS.valueOf(prop.type), prop.className))
            }
            itJs.collections?.forEach { coll: dynamic ->
                entity.collections.put(coll.id, VVCollectionDescriptionJS(coll.id, VVCollectionTypeJS.valueOf(coll.elementType), coll.elementClassName))
            }
            uiRegistry.viewValidations[entity.id] = entity
            Unit
        }
        it.views?.forEach { viewJS ->
            val view2 = when (ViewTypeDTJS.valueOf(viewJS.type)) {
                ViewTypeDTJS.STANDARD-> {
                    when (LayoutTypeDTJS.valueOf(viewJS.layout.type)) {
                        LayoutTypeDTJS.TABLE -> {
                            val itLayout = viewJS.layout
                            val layout = TableLayoutDescriptionJS(itLayout.expandLastRow)

                            val view = StandardViewDescriptionJS(id = viewJS.id, viewModel = viewJS.viewModel,
                                    viewValidation = viewJS.viewValidation, viewSettings = viewJS.viewSettings, layout = layout)
                            itLayout.columns.forEach { columnIt ->
                                layout.columns.add(TableColumnDescriptionJS(columnIt.width))
                            }
                            itLayout.widgets.forEach { widgetIt ->
                                when (WidgetTypeDTJS.valueOf(widgetIt.type)) {
                                    WidgetTypeDTJS.TEXTBOX -> {
                                        val widget = TextboxDescriptionJS(widgetIt.id)
                                        widget.hSpan = widgetIt.hSpan
                                        layout.widgets[widgetIt.id] = widget
                                    }
                                    WidgetTypeDTJS.TEXTAREA -> {
                                        val widget = TextAreaDescriptionJS(widgetIt.id)
                                        widget.hSpan = widgetIt.hSpan
                                        layout.widgets[widgetIt.id] = widget
                                    }
                                    WidgetTypeDTJS.INTBOX -> {
                                        val widget = IntegerBoxDescriptionJS(widgetIt.id, widgetIt.notNullable)
                                        widget.hSpan = widgetIt.hSpan
                                        layout.widgets[widgetIt.id] = widget
                                    }
                                    WidgetTypeDTJS.FLOATBOX -> {
                                        val widget = FloatBoxDescriptionJS(widgetIt.id, widgetIt.notNullable)
                                        widget.hSpan = widgetIt.hSpan
                                        layout.widgets[widgetIt.id] = widget
                                    }
                                    WidgetTypeDTJS.ENUM_SELECT -> {
                                        val widget = EnumSelectDescriptionJS(widgetIt.id, widgetIt.enumId)
                                        widget.hSpan = widgetIt.hSpan
                                        layout.widgets[widgetIt.id] = widget
                                    }
                                    WidgetTypeDTJS.ENTITY_AUTOCOMPLETE -> {
                                        val widget = EntityAutocompleteDescriptionJS(widgetIt.id, widgetIt.elementClassName)
                                        widget.hSpan = widgetIt.hSpan
                                        layout.widgets[widgetIt.id] = widget
                                    }
                                    WidgetTypeDTJS.LABEL -> {
                                        val verticalAlignment = widgetIt.verticalAlignment
                                        val horizontalAlignment = widgetIt.horizontalAlignment
                                        val widget = LabelDescriptionJS(widgetIt.id, widgetIt.displayName, if (verticalAlignment != null) VerticalAlignmentJS.valueOf(verticalAlignment) else null
                                                , if (horizontalAlignment != null) HorizontalAlignmentJS.valueOf(horizontalAlignment) else null)
                                        widget.hSpan = widgetIt.hSpan
                                        layout.widgets[widgetIt.id] = widget
                                    }
                                    WidgetTypeDTJS.NEXT_ROW -> {
                                        layout.widgets[widgetIt.id] = TableNextRowDescriptionJS(widgetIt.id)
                                    }
                                    WidgetTypeDTJS.NEXT_COLUMN -> {
                                        layout.widgets[widgetIt.id] = TableNextColumnDescriptionJS(widgetIt.id)
                                    }
                                    WidgetTypeDTJS.DATEBOX ->{
                                        val widget = DateboxDescriptionJS(widgetIt.id)
                                        widget.hSpan = widgetIt.hSpan
                                        layout.widgets[widgetIt.id] = widget
                                    }
                                    WidgetTypeDTJS.DATETIMEBOX ->{
                                        val widget = DateTimeBoxDescriptionJS(widgetIt.id)
                                        widget.hSpan = widgetIt.hSpan
                                        layout.widgets[widgetIt.id] = widget
                                    }
                                    WidgetTypeDTJS.BOOLEANBOX ->{
                                        val widget = BooleanBoxDescriptionJS(widgetIt.id, widgetIt.notNullable)
                                        widget.hSpan = widgetIt.hSpan
                                        layout.widgets[widgetIt.id] = widget
                                    }
                                    WidgetTypeDTJS.PASSWORDBOX ->{
                                        val widget = PasswordBoxDescriptionJS(widgetIt.id)
                                        widget.hSpan = widgetIt.hSpan
                                        layout.widgets[widgetIt.id] = widget
                                    }
                                    WidgetTypeDTJS.TABLE -> {
                                        val widget = TableDescriptionJS(widgetIt.id, widgetIt.baseClassesName)
                                        widget.hSpan = widgetIt.hSpan
                                        layout.widgets[widgetIt.id] = widget
                                        val colls = widgetIt.columns
                                        colls.forEach { columnIt ->
                                            when (TableColumnTypeDTJS.valueOf(columnIt.columnType)) {
                                                TableColumnTypeDTJS.TEXT -> {
                                                    val columnDescriptionJS = TextTableColumnDescriptionJS(columnIt.id, columnIt.caption)
                                                    columnDescriptionJS.width = columnIt.width
                                                    widget.columns.put(columnDescriptionJS.id, columnDescriptionJS)
                                                }
                                                TableColumnTypeDTJS.INTEGER -> {
                                                    val columnDescriptionJS = IntegerTableColumnDescriptionJS(columnIt.id, columnIt.caption,columnIt.notNullable)
                                                    columnDescriptionJS.width = columnIt.width
                                                    widget.columns.put(columnDescriptionJS.id, columnDescriptionJS)
                                                }
                                                TableColumnTypeDTJS.FLOAT -> {
                                                    val columnDescriptionJS = FloatTableColumnDescriptionJS(columnIt.id, columnIt.caption,columnIt.notNullable)
                                                    columnDescriptionJS.width = columnIt.width
                                                    widget.columns.put(columnDescriptionJS.id, columnDescriptionJS)
                                                }
                                                TableColumnTypeDTJS.ENUM -> {
                                                    val columnDescriptionJS = EnumTableColumnDescriptionJS(columnIt.id, columnIt.caption, columnIt.enumId)
                                                    columnDescriptionJS.width = columnIt.width
                                                    widget.columns.put(columnDescriptionJS.id, columnDescriptionJS)
                                                }
                                                TableColumnTypeDTJS.ENTITY-> {
                                                    val columnDescriptionJS = EntityTableColumnDescriptionJS(columnIt.id, columnIt.caption, columnIt.entityClassName)
                                                    columnDescriptionJS.width = columnIt.width
                                                    widget.columns.put(columnDescriptionJS.id, columnDescriptionJS)
                                                }
                                            }

                                        }
                                    }

                                }
                            }
                            view
                        }
                    }
                }
            }
            uiRegistry.views[view2.id] = view2
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
            entity.abstract = itJs.abstract
            entity.extends = itJs.extends
            itJs.properties?.forEach{ prop:dynamic ->
                entity.properties.put(prop.id, RestPropertyDescriptionJS(prop.id, RestPropertyTypeJS.valueOf(prop.type), prop.className))
            }
            itJs.collections?.forEach{ coll:dynamic ->
                entity.collections.put(coll.id, RestCollectionDescriptionJS(coll.id, RestPropertyTypeJS.valueOf(coll.elementType), coll.elementClassName))
            }
            restRegistry.entities.put(entity.id, entity)
        }
        it.operations?.forEach{itJs ->
            val op = RestOperationDescriptionJS(itJs.id, itJs.request, itJs.response)
            restRegistry.operations.put(op.id, op)
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
            val entity = IndexDescriptionJS(itJs.id)
            entity.document = itJs.document
            fillBaseIndexDescription(entity, itJs)
            domainRegistry.indexes[entity.id] = entity
            Unit
        }
        it.domainAssets?.forEach{itJs ->
            val entity = AssetDescriptionJS(itJs.id)
            fillBaseIndexDescription(entity, itJs)
            domainRegistry.assets[entity.id] = entity
            Unit
        }

    }

    private fun fillBaseIndexDescription(entity: BaseIndexDescriptionJS, itJs: dynamic) {
        itJs.properties?.forEach{ prop:dynamic ->
            val id = DatabasePropertyDescriptionJS(prop.id,DatabasePropertyTypeJS.valueOf(prop.type),prop.displayName)
            id.className = prop.className
            entity.properties.put(prop.id, id)
        }
        itJs.collections?.forEach{ coll:dynamic ->
            val cd = DatabaseCollectionDescriptionJS(coll.id, DatabaseCollectionTypeJS.valueOf(coll.elementType), coll.displayName)
            cd.elementClassName = coll.elementClassName
            entity.collections.put(coll.id, cd)
        }
    }
}