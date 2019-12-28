/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/
@file:Suppress("unused")
package com.gridnine.jasmine.web.core.model.ui

import com.gridnine.jasmine.web.core.application.EnvironmentJS
import com.gridnine.jasmine.web.core.model.common.BaseIdentityDescriptionJS


enum class VMPropertyTypeJS {

    STRING,
    ENUM,
    LONG,
    INT,
    BIG_DECIMAL,
    ENTITY_REFERENCE,
    LOCAL_DATE_TIME,
    LOCAL_DATE,
    ENTITY,
    BOOLEAN

}

enum class VMCollectionTypeJS {

    ENTITY,
}

class VMCollectionDescriptionJS(id:String, val elementType:VMCollectionTypeJS, val elementClassName:String?) : BaseIdentityDescriptionJS(id)

class VMPropertyDescriptionJS(id:String, val type:VMPropertyTypeJS, val className:String?,val notNullable: Boolean) : BaseIdentityDescriptionJS(id)

class VMEnumItemDescriptionJS(id:String) : BaseIdentityDescriptionJS(id)

class VMEnumDescriptionJS(id:String) : BaseIdentityDescriptionJS(id) {
    val items = linkedMapOf<String, VMEnumItemDescriptionJS>()
}

class VMEntityDescriptionJS(id: String) : BaseIdentityDescriptionJS(id) {

    val properties = linkedMapOf<String, VMPropertyDescriptionJS>()

    val collections = linkedMapOf<String, VMCollectionDescriptionJS>()
}

enum class VSPropertyTypeJS {
    ENUM_SELECT,
    ENTITY_AUTOCOMPLETE,
    ENTITY,
    COLUMN_TEXT,
    COLUMN_INT,
    COLUMN_FLOAT,
    COLUMN_ENUM_SELECT,
    COLUMN_DATE,
    COLUMN_ENTITY,
}

enum class VSCollectionTypeJS {
   ENTITY
}

class VSCollectionDescriptionJS(id:String, val elementType:VSCollectionTypeJS, val elementClassName:String?) : BaseIdentityDescriptionJS(id)
class VSPropertyDescriptionJS(id:String, val type:VSPropertyTypeJS, val className:String?) : BaseIdentityDescriptionJS(id)
class VSEntityDescriptionJS(id: String) : BaseIdentityDescriptionJS(id) {

    val properties = linkedMapOf<String, VSPropertyDescriptionJS>()

    val collections = linkedMapOf<String, VSCollectionDescriptionJS>()
}


enum class VVPropertyTypeJS {
    STRING
}

enum class VVCollectionTypeJS {
    ENTITY
}

class VVCollectionDescriptionJS(id:String, val elementType:VVCollectionTypeJS, val elementClassName:String?) : BaseIdentityDescriptionJS(id)
class VVPropertyDescriptionJS(id:String, val type:VVPropertyTypeJS, val className:String?) : BaseIdentityDescriptionJS(id)
class VVEntityDescriptionJS(id: String) : BaseIdentityDescriptionJS(id) {

    val properties = linkedMapOf<String, VVPropertyDescriptionJS>()

    val collections = linkedMapOf<String, VVCollectionDescriptionJS>()
}





abstract class BaseWidgetDescriptionJS (id: String) : BaseIdentityDescriptionJS(id){
    var hSpan:Int? = null
}
class TableColumnDescriptionJS(val width:String?)

abstract class BaseLayoutDescriptionJS {
   val widgets = linkedMapOf<String, BaseWidgetDescriptionJS>()
}
enum class VerticalAlignmentJS {
    TOP,
    BOTTOM,
    CENTER
}
enum class HorizontalAlignmentJS {
    LEFT,
    RIGHT,
    CENTER
}
abstract class BaseViewDescriptionJS (id:String, val viewModel:String, val viewSettings:String, val viewValidation:String):BaseIdentityDescriptionJS(id)
class StandardViewDescriptionJS ( id: String, viewModel:String,  viewSettings:String,  viewValidation:String,val layout: BaseLayoutDescriptionJS) :
        BaseViewDescriptionJS(id,  viewModel, viewSettings, viewValidation)

class TableLayoutDescriptionJS(val expandLastRow:Boolean) : BaseLayoutDescriptionJS(){
    val columns = ArrayList<TableColumnDescriptionJS>()
}


class TableNextColumnDescriptionJS(id: String) : BaseWidgetDescriptionJS(id)
class TableNextRowDescriptionJS(id: String) : BaseWidgetDescriptionJS(id)
class LabelDescriptionJS(id: String, val displayName: String, val verticalAlignment: VerticalAlignmentJS?, val horizontalAlignment: HorizontalAlignmentJS?) : BaseWidgetDescriptionJS(id)


class TextAreaDescriptionJS(id: String) : BaseWidgetDescriptionJS(id)


class TextboxDescriptionJS(id: String)  : BaseWidgetDescriptionJS(id)
class PasswordBoxDescriptionJS(id: String)  : BaseWidgetDescriptionJS(id)


class IntegerBoxDescriptionJS(id: String, val notNullable:Boolean)  : BaseWidgetDescriptionJS(id)

class FloatBoxDescriptionJS(id: String, val notNullable:Boolean)  : BaseWidgetDescriptionJS(id)

class EnumSelectDescriptionJS(id: String, val enumId:String) : BaseWidgetDescriptionJS(id)

class EntityAutocompleteDescriptionJS(id: String, val entityClassName:String) : BaseWidgetDescriptionJS(id)

class DateboxDescriptionJS(id: String)  : BaseWidgetDescriptionJS(id)

class DateTimeBoxDescriptionJS(id: String)  : BaseWidgetDescriptionJS(id)

class BooleanBoxDescriptionJS(id: String, val notNullable:Boolean)  : BaseWidgetDescriptionJS(id)


abstract class BaseTableColumnDescriptionJS(id:String, val displayName: String):BaseIdentityDescriptionJS(id){
    var width:Int? = null
}

class TextTableColumnDescriptionJS(id:String,displayName: String) :BaseTableColumnDescriptionJS(id,displayName)
class IntegerTableColumnDescriptionJS( id:String, displayName: String,val notNullable: Boolean) :BaseTableColumnDescriptionJS(id, displayName)
class FloatTableColumnDescriptionJS(id:String, displayName: String,val notNullable: Boolean) :BaseTableColumnDescriptionJS(id,displayName)
class EnumTableColumnDescriptionJS(id:String, val enumId:String,displayName: String) :BaseTableColumnDescriptionJS(id,displayName)
class EntityTableColumnDescriptionJS(id:String, val entityClassName:String,displayName: String) :BaseTableColumnDescriptionJS(id,displayName)
class DateTableColumnDescriptionJS(id:String,displayName: String) :BaseTableColumnDescriptionJS(id,displayName)

class ListDescriptionJS(id:String, val objectId:String) : BaseIdentityDescriptionJS(id){
    val toolButtons= arrayListOf<ListToolButtonDescriptionJS>()
}


class TableDescriptionJS(id: String, val className:String) : BaseWidgetDescriptionJS(id){
    val columns = linkedMapOf<String, BaseTableColumnDescriptionJS>()
    var additionalRowDataClass:String? = null
}
class ListToolButtonDescriptionJS(id:String, val handler:String, val weight:Double, val displayName:String) : BaseIdentityDescriptionJS(id)


class EditorToolButtonDescriptionJS( id:String, val handler:String, val weight:Double, val displayName:String) : BaseIdentityDescriptionJS(id)
class SharedEditorToolButtonDescriptionJS(id:String, val handler:String, val weight:Double, val displayName:String) : BaseIdentityDescriptionJS(id)
class DialogToolButtonDescriptionJS( id:String, val handler:String, val displayName:String) : BaseIdentityDescriptionJS(id)

class DialogDescriptionJS(id:String, val viewId:String, val width:Int, val height:Int) : BaseIdentityDescriptionJS(id){
    val buttons = arrayListOf<DialogToolButtonDescriptionJS>()
}

class EditorDescriptionJS(id:String, val viewId:String) : BaseIdentityDescriptionJS(id){
    val toolButtons = arrayListOf<EditorToolButtonDescriptionJS>()
    val handlers  = arrayListOf<String>()
}




class UiMetaRegistryJS{

    val sharedEditorToolButtons = arrayListOf<SharedEditorToolButtonDescriptionJS>()

    val viewModels = linkedMapOf<String, VMEntityDescriptionJS>()

    val viewSettings = linkedMapOf<String, VSEntityDescriptionJS>()

    val viewValidations = linkedMapOf<String, VVEntityDescriptionJS>()

    val views = linkedMapOf<String, BaseViewDescriptionJS>()

    val editors = linkedMapOf<String, EditorDescriptionJS>()

    val lists = linkedMapOf<String, ListDescriptionJS>()

    val dialogs = linkedMapOf<String, DialogDescriptionJS>()

    companion object{
        fun get() = EnvironmentJS.getPublished(UiMetaRegistryJS::class)
    }
}
