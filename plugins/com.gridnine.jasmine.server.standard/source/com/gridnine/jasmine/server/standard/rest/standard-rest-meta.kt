/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/
@file:Suppress("unused", "UnsafeCastFromDynamic", "UNCHECKED_CAST")

package com.gridnine.jasmine.server.standard.rest

import com.gridnine.jasmine.server.core.model.domain.DatabaseCollectionType
import com.gridnine.jasmine.server.core.model.domain.DatabasePropertyType
import com.gridnine.jasmine.server.core.model.domain.DomainMetaRegistry
import com.gridnine.jasmine.server.core.model.rest.RestMetaRegistry
import com.gridnine.jasmine.server.core.model.rest.RestPropertyType
import com.gridnine.jasmine.server.core.model.ui.*
import com.gridnine.jasmine.server.core.rest.RestHandler
import com.gridnine.jasmine.server.core.rest.RestOperationContext
import com.gridnine.jasmine.server.standard.model.rest.*


class StandardMetaRestHandler : RestHandler<GetMetadataRequest, GetMetadataResponse> {
    override fun service(request: GetMetadataRequest, ctx:RestOperationContext): GetMetadataResponse {
        val result = GetMetadataResponse()
        RestMetaRegistry.get().enums.values.forEach {
            val enumDescr = EnumDescriptionDT()
            enumDescr.id = it.id+"JS"
            it.items.values.forEach { enumItemDescription ->
                enumDescr.items.add(enumItemDescription.id)
            }
            result.restEnums.add(enumDescr)
        }
        RestMetaRegistry.get().entities.values.forEach {
            val entityDescr = RestEntityDescriptionDT()
            entityDescr.id = it.id+"JS"
            entityDescr.abstract = it.abstract
            entityDescr.extends = it.extends?.let { ext -> ext+"JS"}

            it.properties.values.forEach { propertyDescrition ->
                val property = RestPropertyDescriptionDT()
                property.className = getClassName(propertyDescrition.className)
                property.id = propertyDescrition.id
                property.type = toRestCollectionType(propertyDescrition.type)
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
            indexDescr.displaName = it.getDisplayName()
            indexDescr.id = it.id+"JS"
            indexDescr.document = it.document+"JS"
            it.properties.values.forEach { propertyDescription ->
                val property = IndexPropertyDescriptionDT()
                property.className = getClassName(propertyDescription.className)
                property.id = propertyDescription.id
                property.type = toRestCollectionType(propertyDescription.type)
                property.displayName = propertyDescription.getDisplayName()
                indexDescr.properties.add(property)
            }
            it.collections.values.forEach { collectionDescription ->
                val coll = IndexCollectionDescriptionDT()
                coll.elementClassName = getClassName(collectionDescription.elementClassName)
                coll.id = collectionDescription.id
                coll.elementType = toRestCollectionType(collectionDescription.elementType)
                coll.displayName = collectionDescription.getDisplayName()
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
                assetDescription.properties.add(property)
            }
            it.collections.values.forEach { collectionDescription ->
                val coll = IndexCollectionDescriptionDT()
                coll.elementClassName = getClassName(collectionDescription.elementClassName)
                coll.id = collectionDescription.id
                coll.elementType = toRestCollectionType(collectionDescription.elementType)
                coll.displayName = collectionDescription.getDisplayName()
                assetDescription.collections.add(coll)
            }
            result.domainAssets.add(assetDescription)
        }
        UiMetaRegistry.get().viewModels.values.forEach {
            val entityDescription = VMEntityDescriptionDT()
            entityDescription.id = it.id+"JS"
            result.vmEntities.add(entityDescription)
            it.properties.values.forEach { propertyDescription ->
                val propertyDescriptionDT = VMPropertyDescriptionDT()
                propertyDescriptionDT.id = propertyDescription.id
                propertyDescriptionDT.className = getClassName(propertyDescription.className)
                propertyDescriptionDT.type = VMPropertyTypeDT.valueOf(propertyDescription.type.name)
                propertyDescriptionDT.nonNullable = propertyDescription.nonNullable
                entityDescription.properties.add(propertyDescriptionDT)
            }
            it.collections.values.forEach { collectionDescription ->
                val collectionDescriptionDT = VMCollectionDescriptionDT()
                collectionDescriptionDT.id = collectionDescription.id
                collectionDescriptionDT.elementClassName = getClassName(collectionDescription.elementClassName)
                collectionDescriptionDT.elementType = VMPropertyTypeDT.valueOf(collectionDescription.elementType.name)
                entityDescription.collections.add(collectionDescriptionDT)
            }
        }
        UiMetaRegistry.get().viewSettings.values.forEach {
            val entityDescription = VSEntityDescriptionDT()
            entityDescription.id = it.id+"JS"
            result.vsEntities.add(entityDescription)
            it.properties.values.forEach { propertyDescription ->
                val propertyDescriptionDT = VSPropertyDescriptionDT()
                propertyDescriptionDT.id = propertyDescription.id
                propertyDescriptionDT.className = getClassName(propertyDescription.className)
                propertyDescriptionDT.type = VSPropertyTypeDT.valueOf(propertyDescription.type.name)
                entityDescription.properties.add(propertyDescriptionDT)
            }
            it.collections.values.forEach { collectionDescription ->
                val collectionDescriptionDT = VSCollectionDescriptionDT()
                collectionDescriptionDT.id = collectionDescription.id
                collectionDescriptionDT.elementClassName = getClassName(collectionDescription.elementClassName)
                collectionDescriptionDT.elementType = VSPropertyTypeDT.valueOf(collectionDescription.elementType.name)
                entityDescription.collections.add(collectionDescriptionDT)
            }
        }
        UiMetaRegistry.get().viewValidations.values.forEach {
            val entityDescription = VVEntityDescriptionDT()
            entityDescription.id = it.id + "JS"
            result.vvEntities.add(entityDescription)
            it.properties.values.forEach { propertyDescription ->
                val propertyDescriptionDT = VVPropertyDescriptionDT()
                propertyDescriptionDT.id = propertyDescription.id
                propertyDescriptionDT.className = getClassName(propertyDescription.className)
                propertyDescriptionDT.type = VVPropertyTypeDT.valueOf(propertyDescription.type.name)
                entityDescription.properties.add(propertyDescriptionDT)
            }
            it.collections.values.forEach { collectionDescription ->
                val collectionDescriptionDT = VVCollectionDescriptionDT()
                collectionDescriptionDT.id = collectionDescription.id
                collectionDescriptionDT.elementClassName = getClassName(collectionDescription.elementClassName)
                collectionDescriptionDT.elementType = VVPropertyTypeDT.valueOf(collectionDescription.elementType.name)
                entityDescription.collections.add(collectionDescriptionDT)
            }
        }
        UiMetaRegistry.get().autocompletes.values.forEach {
            val autocompleteDescription = AutocompleteDescriptionDT()
            autocompleteDescription.id = it.id
            autocompleteDescription.entity = it.entity
            autocompleteDescription.sortOrder = AutocompleteSortOrderDT.valueOf(it.sortOrder.name)
            autocompleteDescription.sortProperty = it.sortProperty
            autocompleteDescription.columns.addAll(it.columns)
            autocompleteDescription.filters.addAll(it.filters)
            result.autocompletes.add(autocompleteDescription)
        }
        UiMetaRegistry.get().views.values.forEach { viewDescription ->
            when (viewDescription) {
                is StandardViewDescription -> {
                    val view = StandardViewDescriptionDT()
                    result.views.add(view)
                    view.id = viewDescription.id
                    view.type = ViewTypeDT.STANDARD
                    view.viewModel = viewDescription.viewModel + "JS"
                    view.viewSettings = viewDescription.viewSettings + "JS"
                    view.viewValidation = viewDescription.viewValidation + "JS"
                    view.interceptors.addAll(viewDescription.interceptors)
                    when (val layout = viewDescription.layout) {
                        is TableLayoutDescription -> {
                            val tlDT = TableLayoutDescriptionDT()
                            tlDT.type = LayoutTypeDT.TABLE
                            view.layout = tlDT
                            layout.columns.forEach {
                                val cd = TableColumnDescriptionDT()
                                tlDT.columns.add(cd)
                                cd.width = it.width
                            }
                            tlDT.expandLastRow = layout.expandLastRow
                            layout.widgets.values.forEach {
                                when (it) {
                                    is TileDescription -> {
                                        val widget = TileDescriptionDT()
                                        widget.id = it.id
                                        widget.type = WidgetTypeDT.TILE
                                        widget.hSpan = it.hSpan
                                        widget.compactViewId = it.compactView.id
                                        widget.fullViewId = it.fullView.id
                                        widget.displayName = it.getDisplayName()
                                        tlDT.widgets.add(widget)
                                    }
                                    is NavigatorDescription -> {
                                        val widget = NavigatorDescriptionDT()
                                        widget.id = it.id
                                        widget.hSpan = it.hSpan
                                        widget.type = WidgetTypeDT.NAVIGATOR
                                        widget.buttonsHandler = it.buttonsHandler
                                        widget.viewIds.addAll(it.viewIds)
                                        tlDT.widgets.add(widget)
                                    }
                                    is TableNextColumnDescription -> {
                                        val widget = TableNextColumnDescriptionDT()
                                        widget.id = it.id
                                        widget.type = WidgetTypeDT.NEXT_COLUMN
                                        tlDT.widgets.add(widget)
                                    }
                                    is TableNextRowDescription -> {
                                        val widget = TableNextRowDescriptionDT()
                                        widget.id = it.id
                                        widget.type = WidgetTypeDT.NEXT_ROW
                                        tlDT.widgets.add(widget)
                                    }
                                    is TextboxDescription -> {
                                        val widget = TextboxDescriptionDT()
                                        widget.id = it.id
                                        widget.hSpan = it.hSpan
                                        widget.notEditable = it.notEditable
                                        widget.type = WidgetTypeDT.TEXTBOX
                                        tlDT.widgets.add(widget)
                                    }
                                    is SelectDescription -> {
                                        val widget = SelectDescriptionDT()
                                        widget.id = it.id
                                        widget.hSpan = it.hSpan
                                        widget.notEditable = it.notEditable
                                        widget.type = WidgetTypeDT.SELECT
                                        tlDT.widgets.add(widget)
                                    }
                                    is TextAreaDescription -> {
                                        val widget = TextAreaDescriptionDT()
                                        widget.id = it.id
                                        widget.hSpan = it.hSpan
                                        widget.notEditable = it.notEditable
                                        widget.type = WidgetTypeDT.TEXTAREA
                                        tlDT.widgets.add(widget)
                                    }
                                    is IntegerBoxDescription -> {
                                        val widget = IntegerBoxDescriptionDT()
                                        widget.id = it.id
                                        widget.hSpan = it.hSpan
                                        widget.notEditable = it.notEditable
                                        widget.type = WidgetTypeDT.INTBOX
                                        widget.nonNullable = it.nonNullable
                                        tlDT.widgets.add(widget)
                                    }
                                    is FloatBoxDescription -> {
                                        val widget = FloatBoxDescriptionDT()
                                        widget.id = it.id
                                        widget.notEditable = it.notEditable
                                        widget.type = WidgetTypeDT.FLOATBOX
                                        widget.hSpan = it.hSpan
                                        widget.nonNullable = it.nonNullable
                                        tlDT.widgets.add(widget)
                                    }
                                    is EnumSelectDescription -> {
                                        val widget = EnumSelectDescriptionDT()
                                        widget.id = it.id
                                        widget.enumId = it.enumId
                                        widget.hSpan = it.hSpan
                                        widget.notEditable = it.notEditable
                                        widget.type = WidgetTypeDT.ENUM_SELECT
                                        tlDT.widgets.add(widget)
                                    }
                                    is EntitySelectDescription -> {
                                        val widget = EntityAutocompleteDescriptionDT()
                                        widget.id = it.id
                                        widget.entityClassName = it.entityClassName
                                        widget.hSpan = it.hSpan
                                        widget.notEditable = it.notEditable
                                        widget.type = WidgetTypeDT.ENTITY_AUTOCOMPLETE
                                        tlDT.widgets.add(widget)
                                    }
                                    is LabelDescription -> {
                                        val widget = LabelDescriptionDT()
                                        widget.id = it.id
                                        widget.displayName = it.getDisplayName()
                                        widget.verticalAlignment = if (it.verticalAlignment != null) VerticalAlignmentDT.valueOf(it.verticalAlignment!!.name) else null
                                        widget.horizontalAlignment = if (it.horizontalAlignment != null) HorizontalAlignmentDT.valueOf(it.horizontalAlignment!!.name) else null
                                        widget.hSpan = it.hSpan
                                        widget.notEditable = it.notEditable
                                        widget.type = WidgetTypeDT.LABEL
                                        tlDT.widgets.add(widget)
                                    }
                                    is BooleanBoxDescription -> {
                                        val widget = BooleanBoxDescriptionDT()
                                        widget.id = it.id
                                        widget.hSpan = it.hSpan
                                        widget.notEditable = it.notEditable
                                        widget.type = WidgetTypeDT.BOOLEANBOX
                                        widget.nonNullable = it.nonNullable
                                        tlDT.widgets.add(widget)
                                    }
                                    is DateboxDescription -> {
                                        val widget = DateBoxDescriptionDT()
                                        widget.id = it.id
                                        widget.hSpan = it.hSpan
                                        widget.notEditable = it.notEditable
                                        widget.type = WidgetTypeDT.DATEBOX
                                        tlDT.widgets.add(widget)
                                    }
                                    is DateTimeBoxDescription -> {
                                        val widget = DateTimeBoxDescriptionDT()
                                        widget.id = it.id
                                        widget.hSpan = it.hSpan
                                        widget.notEditable = it.notEditable
                                        widget.type = WidgetTypeDT.DATETIMEBOX
                                        tlDT.widgets.add(widget)
                                    }
                                    is PasswordBoxDescription -> {
                                        val widget = PasswordBoxDescriptionDT()
                                        widget.id = it.id
                                        widget.hSpan = it.hSpan
                                        widget.notEditable = it.notEditable
                                        widget.type = WidgetTypeDT.PASSWORDBOX
                                        tlDT.widgets.add(widget)
                                    }
                                    is TableDescription -> {
                                        val widget = TableDescriptionDT()
                                        widget.id = it.id
                                        widget.className = it.className
                                        widget.hSpan = it.hSpan
                                        widget.notEditable = it.notEditable
                                        widget.type = WidgetTypeDT.TABLE
                                        tlDT.widgets.add(widget)
                                        it.columns.values.forEach { column ->
                                            when (column) {
                                                is TextTableColumnDescription -> {
                                                    val columnDescriptionDT = TextTableColumnDescriptionDT()
                                                    columnDescriptionDT.id = column.id
                                                    columnDescriptionDT.columnType = TableColumnTypeDT.TEXT
                                                    columnDescriptionDT.caption = column.getDisplayName()
                                                    columnDescriptionDT.width = column.width
                                                    widget.columns.add(columnDescriptionDT)
                                                }
                                                is FloatTableColumnDescription -> {
                                                    val columnDescriptionDT = FloatTableColumnDescriptionDT()
                                                    columnDescriptionDT.id = column.id
                                                    columnDescriptionDT.caption = column.getDisplayName()
                                                    columnDescriptionDT.columnType = TableColumnTypeDT.FLOAT
                                                    columnDescriptionDT.width = column.width
                                                    columnDescriptionDT.nonNullable = column.nonNullable
                                                    widget.columns.add(columnDescriptionDT)
                                                }
                                                is IntegerTableColumnDescription -> {
                                                    val columnDescriptionDT = IntegerTableColumnDescriptionDT()
                                                    columnDescriptionDT.id = column.id
                                                    columnDescriptionDT.caption = column.getDisplayName()
                                                    columnDescriptionDT.columnType = TableColumnTypeDT.INTEGER
                                                    columnDescriptionDT.width = column.width
                                                    columnDescriptionDT.nonNullable = column.nonNullable
                                                    widget.columns.add(columnDescriptionDT)
                                                }
                                                is EnumTableColumnDescription -> {
                                                    val columnDescriptionDT = EnumTableColumnDescriptionDT()
                                                    columnDescriptionDT.id = column.id
                                                    columnDescriptionDT.enumId = column.enumId
                                                    columnDescriptionDT.caption = column.getDisplayName()
                                                    columnDescriptionDT.columnType = TableColumnTypeDT.ENUM
                                                    columnDescriptionDT.width = column.width
                                                    widget.columns.add(columnDescriptionDT)
                                                }
                                                is EntityTableColumnDescription -> {
                                                    val columnDescriptionDT = EntityTableColumnDescriptionDT()
                                                    columnDescriptionDT.id = column.id
                                                    columnDescriptionDT.caption = column.getDisplayName()
                                                    columnDescriptionDT.entityClassName = column.entityClassName
                                                    columnDescriptionDT.columnType = TableColumnTypeDT.ENTITY
                                                    columnDescriptionDT.width = column.width
                                                    widget.columns.add(columnDescriptionDT)
                                                }
                                                is NavigationTableColumnDescription -> {
                                                    val columnDescriptionDT = NavigationTableColumnDescriptionDT()
                                                    columnDescriptionDT.id = column.id
                                                    columnDescriptionDT.caption = column.getDisplayName()
                                                    columnDescriptionDT.columnType = TableColumnTypeDT.NAVIGATION
                                                    widget.columns.add(columnDescriptionDT)
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                else -> throw IllegalArgumentException("unsupported view type ${viewDescription::class.qualifiedName}")
            }
        }
        UiMetaRegistry.get().lists.values.forEach {
            val listDescriptionDT = ListDescriptionDT()
            listDescriptionDT.id = it.id
            result.lists.add(listDescriptionDT)
            listDescriptionDT.objectId = it.objectId
            it.toolButtons.forEach { buttonDescr ->
                val button = StandardButtonDescriptionDT()
                button.id = buttonDescr.id
                button.displayName = buttonDescr.getDisplayName()
                button.handler = buttonDescr.handler
                button.weight = buttonDescr.weight.toBigDecimal()
                listDescriptionDT.buttons.add(button)
            }
        }
        UiMetaRegistry.get().sharedEditorToolButtons.forEach {
            val button = StandardButtonDescriptionDT()
            button.id = it.id
            button.displayName = it.getDisplayName()
            button.handler = it.handler
            button.weight = it.weight.toBigDecimal()
            result.sharedEditorButtons.add(button)
        }
        UiMetaRegistry.get().sharedListToolButtons.forEach {
            val button = StandardButtonDescriptionDT()
            button.id = it.id
            button.displayName = it.getDisplayName()
            button.handler = it.handler
            button.weight = it.weight.toBigDecimal()
            result.sharedListButtons.add(button)
        }
        UiMetaRegistry.get().editors.values.forEach {
            val entityDescription = EditorDescriptionDT()
            entityDescription.id = it.id
            result.editors.add(entityDescription)
            entityDescription.viewId = it.viewId
            entityDescription.entityId = it.entityId
            it.toolButtons.forEach { buttonDescr ->
                val button = StandardButtonDescriptionDT()
                button.id = buttonDescr.id
                button.displayName = buttonDescr.getDisplayName()
                button.handler = buttonDescr.handler
                button.weight = buttonDescr.weight.toBigDecimal()
                entityDescription.buttons.add(button)
            }
        }
        UiMetaRegistry.get().dialogs.values.forEach {
            val dialogDescription = DialogDescriptionDT()
            dialogDescription.id = it.id
            result.dialogs.add(dialogDescription)
            dialogDescription.viewId = it.viewId
            dialogDescription.title = it.getDisplayName()
            dialogDescription.closable = it.closable
            it.buttons.forEach { buttonDescr ->
                val button = StandardButtonDescriptionDT()
                button.id = buttonDescr.id
                button.displayName = buttonDescr.getDisplayName()
                button.handler = buttonDescr.handler
                dialogDescription.buttons.add(button)
            }
        }
        return result
    }

    private fun getClassName(className: String?): String? {
        return when(className){
            null -> null
            BaseVVEntity::class.qualifiedName,
            BaseVSEntity::class.qualifiedName,
            BaseVMEntity::class.qualifiedName -> className
            else -> "${className}JS"
        }
    }

    private fun toRestCollectionType(elementType: DatabaseCollectionType): DatabaseCollectionTypeDT {
        return DatabaseCollectionTypeDT.valueOf(elementType.name)

    }

    private fun toRestCollectionType(type: RestPropertyType): RestPropertyTypeDT {
        return RestPropertyTypeDT.valueOf(type.name)

    }


    private fun toRestCollectionType(type: DatabasePropertyType): DatabasePropertyTypeDT {
        return DatabasePropertyTypeDT.valueOf(type.name)

    }


}

