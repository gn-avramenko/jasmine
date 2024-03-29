/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/
@file:Suppress("unused")
package com.gridnine.jasmine.common.core.meta

import com.gridnine.jasmine.common.core.app.Disposable
import com.gridnine.jasmine.common.core.app.PublishableWrapper


abstract class BaseUiElementDescription(val id:String)

class UiEnumItemDescription(id:String) :BaseModelElementDescription(id)

class UiEnumDescription(id:String) : BaseUiElementDescription(id){
    val items = linkedMapOf<String, UiEnumItemDescription>()
}

enum class VMPropertyType {

    STRING,
    ENUM,
    SELECT,
    LONG,
    INT,
    BIG_DECIMAL,
    ENTITY_REFERENCE,
    LOCAL_DATE_TIME,
    LOCAL_DATE,
    ENTITY,
    BOOLEAN,
}

enum class VMCollectionType {
    ENTITY
}

class VMCollectionDescription(id:String, val elementType:VMCollectionType, val elementClassName:String?) : BaseUiElementDescription(id)

class VMPropertyDescription(id:String, val type:VMPropertyType, val className:String?,val nonNullable: Boolean, val lateInit:Boolean) : BaseUiElementDescription( id)


class VMEntityDescription(id: String) : BaseUiElementDescription(id) {
    var extendsId:String? = null

    val properties = linkedMapOf<String, VMPropertyDescription>()

    val collections = linkedMapOf<String, VMCollectionDescription>()
}

enum class VSPropertyType {
    STRING,
    ENTITY,
    TEXT_BOX_SETTINGS,
    PASSWORD_BOX_SETTINGS,
    FLOAT_NUMBER_BOX_SETTINGS,
    INTEGER_NUMBER_BOX_SETTINGS,
    BOOLEAN_BOX_SETTINGS,
    ENTITY_SELECT_BOX_SETTINGS,
    GENERAL_SELECT_BOX_SETTINGS,
    ENUM_SELECT_BOX_SETTINGS,
    DATE_BOX_SETTINGS,
    DATE_TIME_BOX_SETTINGS,
    RICH_TEXT_EDITOR_SETTINGS

}

enum class VSCollectionType {
    ENTITY
}

class VSCollectionDescription(id:String, val elementType:VSCollectionType, val elementClassName:String?) : BaseUiElementDescription( id)
class VSPropertyDescription(id:String, val type:VSPropertyType, val className:String?, val lateInit:Boolean) : BaseUiElementDescription(id)
class VSEntityDescription(id: String) : BaseUiElementDescription(id) {

    var extendsId:String? = null

    val properties = linkedMapOf<String, VSPropertyDescription>()

    val collections = linkedMapOf<String, VSCollectionDescription>()
}


enum class VVPropertyType {
    STRING,
    ENTITY
}

enum class VVCollectionType {
    ENTITY
}

class VVCollectionDescription(id:String, val elementType:VVCollectionType, val elementClassName:String?) : BaseUiElementDescription( id)
class VVPropertyDescription(id:String, val type:VVPropertyType, val className:String?, val lateInit:Boolean) : BaseUiElementDescription(id)
class VVEntityDescription(id: String) : BaseUiElementDescription(id) {

    var extendsId:String? = null

    val properties = linkedMapOf<String, VVPropertyDescription>()

    val collections = linkedMapOf<String, VVCollectionDescription>()
}

enum class PredefinedColumnWidth{
    STANDARD,
    REMAINING,
    CUSTOM
}

enum class PredefinedRowHeight{
    AUTO,
    REMAINING,
    CUSTOM
}

class PasswordBoxWidgetDescription(notEditable:Boolean):BaseWidgetDescription(notEditable, WidgetType.PASSWORD_BOX)

class TextBoxWidgetDescription(notEditable:Boolean, val multiline:Boolean):BaseWidgetDescription(notEditable, WidgetType.TEXT_BOX)

class RichTextBoxEditorDescription(notEditable:Boolean, val height:String?):BaseWidgetDescription(notEditable, WidgetType.RICH_TEXT_EDITOR)

class BigDecimalNumberBoxWidgetDescription(notEditable:Boolean, val precision:Int = 2):BaseWidgetDescription(notEditable, WidgetType.BIG_DECIMAL_NUMBER_BOX)

class IntegerNumberBoxWidgetDescription(notEditable:Boolean, val nonNullable: Boolean):BaseWidgetDescription(notEditable, WidgetType.INTEGER_NUMBER_BOX)

class CustomValueWidgetDescription(val viewModel:String, val viewSettings:String, val viewValidation:String)

class CustomValueWidgetRef(val ref:String, val params:Map<String,String>):BaseWidgetDescription(false, WidgetType.CUSTOM_VALUE)

class BooleanBoxWidgetDescription(notEditable:Boolean):BaseWidgetDescription(notEditable, WidgetType.BOOLEAN_BOX)

class EntitySelectBoxWidgetDescription(notEditable:Boolean, val objectId:String):BaseWidgetDescription(notEditable, WidgetType.ENTITY_SELECT_BOX)

class GeneralSelectBoxWidgetDescription(notEditable:Boolean):BaseWidgetDescription(notEditable, WidgetType.GENERAL_SELECT_BOX)

class HiddenWidgetDescription(val objectId:String, val nonNullable: Boolean):BaseWidgetDescription(false, WidgetType.HIDDEN)

class EnumSelectBoxWidgetDescription(notEditable:Boolean, val enumId:String):BaseWidgetDescription(notEditable, WidgetType.ENUM_SELECT_BOX)

class DateBoxWidgetDescription(notEditable:Boolean):BaseWidgetDescription(notEditable, WidgetType.DATE_BOX)

class DateTimeBoxWidgetDescription(notEditable:Boolean):BaseWidgetDescription(notEditable, WidgetType.DATE_TIME_BOX)

class TableColumnDescription(id:String, val prefWidth:String?, val widget:BaseWidgetDescription ):BaseModelElementDescription(id)

class TableBoxWidgetDescription(val id:String, notEditable: Boolean, var hideToolsColumn:Boolean = false):BaseWidgetDescription(notEditable, WidgetType.TABLE_BOX){
    val columns = arrayListOf<TableColumnDescription>()
}

enum class WidgetType{
    TEXT_BOX,
    PASSWORD_BOX,
    BIG_DECIMAL_NUMBER_BOX,
    INTEGER_NUMBER_BOX,
    BOOLEAN_BOX,
    ENTITY_SELECT_BOX,
    GENERAL_SELECT_BOX,
    ENUM_SELECT_BOX,
    DATE_BOX,
    DATE_TIME_BOX,
    HIDDEN,
    TABLE_BOX,
    RICH_TEXT_EDITOR,
    CUSTOM_VALUE
}
abstract class BaseWidgetDescription(val notEditable:Boolean, val widgetType:WidgetType)

enum class ViewType{
    GRID_CONTAINER,
    TILE_SPACE,
    NAVIGATOR
}

abstract class BaseViewDescription(val id:String,val viewType:ViewType){
    val interceptors = arrayListOf<String>()
}

class GridContainerCellDescription(id:String, val caption:String?, val colSpan:Int):BaseModelElementDescription(id){
    lateinit var widget:BaseWidgetDescription
}

class GridContainerRowDescription(val predefinedHeight: PredefinedRowHeight, val customHeight:String?){
    val cells = arrayListOf<GridContainerCellDescription>()
}

class GridContainerColumnDescription(val predefinedWidth:PredefinedColumnWidth, val customWidth:String?)

class GridContainerDescription(id:String, val columnsCount:Int?):BaseViewDescription(id, ViewType.GRID_CONTAINER){
    val columns = arrayListOf<GridContainerColumnDescription>()
    val rows = arrayListOf<GridContainerRowDescription>()
}

class TileSpaceOverviewDescription(val viewId:String):BaseModelElementDescription("overview")

class TileDescription(id:String, val fullViewId:String):BaseModelElementDescription(id)

class TileSpaceDescription(id:String, val overviewDescription: TileSpaceOverviewDescription?,  val tiles:MutableList<TileDescription> = arrayListOf()):BaseViewDescription(id, ViewType.TILE_SPACE)

class NavigatorVariantDescription(val modelId:String, val viewId: String)

class NavigatorDescription(id:String, val variants:MutableList<NavigatorVariantDescription> = arrayListOf()):BaseViewDescription(id, ViewType.NAVIGATOR)

open class OptionDescription(id:String) :BaseModelElementDescription(id)
class OptionsGroupDescription(id:String) :BaseModelElementDescription(id){
    val options = arrayListOf<OptionDescription>()
}

open class BaseActionDescription(id:String) :BaseModelElementDescription(id){
    var icon:String? = null
}

class ActionDescription(id:String) :BaseActionDescription(id){
    lateinit var actionHandler:String
    var displayHandlerRef:String? = null
}

class DisplayHandlerDescription(id:String) :BaseModelElementDescription(id){
    lateinit var className:String
}

class ActionsGroupDescription(id:String) :BaseActionDescription(id){
    val actionsIds = arrayListOf<String>()
    var root:Boolean = false
}

class UiMetaRegistry: Disposable {
    val enums = linkedMapOf<String, UiEnumDescription>()

    val views = linkedMapOf<String, BaseViewDescription>()

    val viewModels = linkedMapOf<String, VMEntityDescription>()

    val viewSettings = linkedMapOf<String, VSEntityDescription>()

    val viewValidations = linkedMapOf<String, VVEntityDescription>()

    val actions = linkedMapOf<String, BaseActionDescription>()

    val optionsGroups = linkedMapOf<String, OptionsGroupDescription>()

    val displayHandlers = linkedMapOf<String, DisplayHandlerDescription>()

    val customValueWidgets = linkedMapOf<String,CustomValueWidgetDescription>()

    override fun dispose() {
        wrapper.dispose()
    }
    companion object {
        private val wrapper = PublishableWrapper(UiMetaRegistry::class)
        fun get() = wrapper.get()
    }
}


