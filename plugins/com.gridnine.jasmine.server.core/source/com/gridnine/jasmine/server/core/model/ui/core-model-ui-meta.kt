/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/
@file:Suppress("unused")
package com.gridnine.jasmine.server.core.model.ui

import com.gridnine.jasmine.server.core.app.Disposable
import com.gridnine.jasmine.server.core.app.PublishableWrapper
import com.gridnine.jasmine.server.core.model.common.BaseModelElementDescription



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

class VMPropertyDescription(id:String, val type:VMPropertyType, val className:String?,val nonNullable: Boolean) : BaseUiElementDescription( id)


class VMEntityDescription(id: String) : BaseUiElementDescription(id) {

    val properties = linkedMapOf<String, VMPropertyDescription>()

    val collections = linkedMapOf<String, VMCollectionDescription>()
}

enum class VSPropertyType {
    TEXT_BOX_SETTINGS,
    PASSWORD_BOX_SETTINGS
}

enum class VSCollectionType {
    ENTITY
}

class VSCollectionDescription(id:String, val elementType:VSCollectionType, val elementClassName:String?) : BaseUiElementDescription( id)
class VSPropertyDescription(id:String, val type:VSPropertyType, val className:String?) : BaseUiElementDescription(id)
class VSEntityDescription(id: String) : BaseUiElementDescription(id) {

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
class VVPropertyDescription(id:String, val type:VVPropertyType, val className:String?) : BaseUiElementDescription(id)
class VVEntityDescription(id: String) : BaseUiElementDescription(id) {

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

class TextBoxWidgetDescription(notEditable:Boolean):BaseWidgetDescription(notEditable, WidgetType.TEXT_BOX)

enum class WidgetType{
    TEXT_BOX,
    PASSWORD_BOX
}
abstract class BaseWidgetDescription(val notEditable:Boolean, val widgetType:WidgetType)

enum class ViewType{
    GRID_CONTAINER
}

abstract class BaseViewDescription(val id:String,val viewType:ViewType)

class GridContainerCellDescription(val id:String, val caption:String, val colSpan:Int){
    lateinit var widget:BaseWidgetDescription
}

class GridContainerRowDescription(val predefinedHeight: PredefinedRowHeight, val customHeight:String?){
    val cells = arrayListOf<GridContainerCellDescription>()
}

class GridContainerColumnDescription(val predefinedWidth:PredefinedColumnWidth, val customWidth:String?)

class GridContainerDescription(id:String, val columnsCount:Int?, val appendAdditionalExpandedRow:Boolean?, val appendAdditionalExpandedColumn:Boolean?):BaseViewDescription(id, ViewType.GRID_CONTAINER){
    val columns = arrayListOf<GridContainerColumnDescription>()
    val rows = arrayListOf<GridContainerRowDescription>()
}

class UiMetaRegistry:Disposable{
    val enums = linkedMapOf<String, UiEnumDescription>()

    val views = linkedMapOf<String, BaseViewDescription>()

    val viewModels = linkedMapOf<String, VMEntityDescription>()

    val viewSettings = linkedMapOf<String, VSEntityDescription>()

    val viewValidations = linkedMapOf<String, VVEntityDescription>()

    override fun dispose() {
        wrapper.dispose()
    }
    companion object {
        private val wrapper = PublishableWrapper(UiMetaRegistry::class)
        fun get() = wrapper.get()
    }
}


