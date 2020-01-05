/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/
@file:Suppress("unused")
package com.gridnine.jasmine.server.core.model.ui

import com.gridnine.jasmine.server.core.app.Environment
import com.gridnine.jasmine.server.core.model.common.BaseIdentityDescription
import com.gridnine.jasmine.server.core.model.common.BaseOwnedIdentityDescription


class ValidationMessageDescription(owner:String, id:String) : BaseOwnedIdentityDescription(owner, id)

class ValidationMessagesEnumDescription(id:String) : BaseIdentityDescription(id) {
    val items = linkedMapOf<String, ValidationMessageDescription>()
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
    BOOLEAN

}

enum class VMCollectionType {

    ENTITY,
}

class VMCollectionDescription(owner:String, id:String, val elementType:VMCollectionType, val elementClassName:String?) : BaseOwnedIdentityDescription(owner, id)

class VMPropertyDescription(owner:String, id:String, val type:VMPropertyType, val className:String?,val notNullable: Boolean) : BaseOwnedIdentityDescription(owner, id)

class VMEnumItemDescription(owner:String, id:String) : BaseOwnedIdentityDescription(owner, id)

class VMEnumDescription(id:String) : BaseIdentityDescription(id) {
    val items = linkedMapOf<String, VMEnumItemDescription>()
}

class VMEntityDescription(id: String) : BaseIdentityDescription(id) {

    val properties = linkedMapOf<String, VMPropertyDescription>()

    val collections = linkedMapOf<String, VMCollectionDescription>()
}

enum class VSPropertyType {
    ENUM_SELECT,
    SELECT,
    ENTITY_AUTOCOMPLETE,
    ENTITY,
    COLUMN_TEXT,
    COLUMN_INT,
    COLUMN_FLOAT,
    COLUMN_ENUM_SELECT,
    COLUMN_DATE,
    COLUMN_ENTITY,
}

enum class VSCollectionType {
   ENTITY
}

class VSCollectionDescription(owner:String, id:String, val elementType:VSCollectionType, val elementClassName:String?) : BaseOwnedIdentityDescription(owner, id)
class VSPropertyDescription(owner:String, id:String, val type:VSPropertyType, val className:String?) : BaseOwnedIdentityDescription(owner, id)
class VSEntityDescription(id: String) : BaseIdentityDescription(id) {

    val properties = linkedMapOf<String, VSPropertyDescription>()

    val collections = linkedMapOf<String, VSCollectionDescription>()
}


enum class VVPropertyType {
    STRING
}

enum class VVCollectionType {
    ENTITY
}

class VVCollectionDescription(owner:String, id:String, val elementType:VVCollectionType, val elementClassName:String?) : BaseOwnedIdentityDescription(owner, id)
class VVPropertyDescription(owner:String, id:String, val type:VVPropertyType, val className:String?) : BaseOwnedIdentityDescription(owner, id)
class VVEntityDescription(id: String) : BaseIdentityDescription(id) {

    val properties = linkedMapOf<String, VVPropertyDescription>()

    val collections = linkedMapOf<String, VVCollectionDescription>()
}





abstract class BaseWidgetDescription (owner: String, id: String) : BaseOwnedIdentityDescription(owner,id){
    var hSpan:Int? = null
}
class TableColumnDescription(val width:String?)

abstract class BaseLayoutDescription {
   val widgets = linkedMapOf<String, BaseWidgetDescription>()
}
enum class VerticalAlignment {
    TOP,
    BOTTOM,
    CENTER
}
enum class HorizontalAlignment {
    LEFT,
    RIGHT,
    CENTER
}
abstract class BaseViewDescription (id:String, val viewModel:String, val viewSettings:String, val viewValidation:String):BaseIdentityDescription(id)
class StandardViewDescription ( id: String, viewModel:String,  viewSettings:String,  viewValidation:String,val layout: BaseLayoutDescription) :
        BaseViewDescription(id,  viewModel, viewSettings, viewValidation)

class TableLayoutDescription(val expandLastRow:Boolean) : BaseLayoutDescription(){
    val columns = ArrayList<TableColumnDescription>()
}


class TableNextColumnDescription(owner: String, id: String) : BaseWidgetDescription(owner, id)
class TableNextRowDescription(owner: String, id: String) : BaseWidgetDescription(owner, id)
class LabelDescription(owner: String, id: String, val verticalAlignment: VerticalAlignment?, val horizontalAlignment: HorizontalAlignment?) : BaseWidgetDescription(owner, id)


class TextAreaDescription(owner: String, id: String) : BaseWidgetDescription(owner, id)


class TextboxDescription(owner: String, id: String)  : BaseWidgetDescription(owner, id)
class PasswordBoxDescription(owner: String, id: String)  : BaseWidgetDescription(owner, id)


class IntegerBoxDescription(owner: String, id: String, val notNullable:Boolean)  : BaseWidgetDescription(owner, id)

class FloatBoxDescription(owner: String, id: String, val notNullable:Boolean)  : BaseWidgetDescription(owner, id)

class EnumSelectDescription(owner: String, id: String, val enumId:String) : BaseWidgetDescription(owner, id)

class SelectDescription(owner: String, id: String) : BaseWidgetDescription(owner, id)

class EntityAutocompleteDescription(owner: String, id: String, val entityClassName:String) : BaseWidgetDescription(owner, id)

class DateboxDescription(owner: String, id: String)  : BaseWidgetDescription(owner, id)

class DateTimeBoxDescription(owner: String, id: String)  : BaseWidgetDescription(owner, id)

class BooleanBoxDescription(owner: String, id: String, val notNullable:Boolean)  : BaseWidgetDescription(owner, id)


abstract class BaseTableColumnDescription(owner:String, id:String):BaseOwnedIdentityDescription(owner, id){
    var width:Int? = null
}

class TextTableColumnDescription(owner:String, id:String) :BaseTableColumnDescription(owner, id)
class IntegerTableColumnDescription(owner:String, id:String,val notNullable:Boolean) :BaseTableColumnDescription(owner, id)
class FloatTableColumnDescription(owner:String, id:String,val notNullable:Boolean) :BaseTableColumnDescription(owner, id)
class EnumTableColumnDescription(owner:String, id:String, val enumId:String) :BaseTableColumnDescription(owner, id)
class EntityTableColumnDescription(owner:String, id:String, val entityClassName:String) :BaseTableColumnDescription(owner, id)
class DateTableColumnDescription(owner:String, id:String) :BaseTableColumnDescription(owner, id)

class ListDescription(id:String, val objectId:String) : BaseIdentityDescription(id){
    val toolButtons= arrayListOf<ListToolButtonDescription>()
}


class TableDescription(owner: String, id: String, val className:String) : BaseWidgetDescription(owner, id){
    val columns = linkedMapOf<String, BaseTableColumnDescription>()
    var additionalRowDataClass:String? = null
}
class ListToolButtonDescription( owner:String, id:String, val handler:String, val weight:Double) : BaseOwnedIdentityDescription(owner, id)


class EditorToolButtonDescription( owner:String, id:String, val handler:String, val weight:Double) : BaseOwnedIdentityDescription(owner, id)
class SharedEditorToolButtonDescription(id:String, val handler:String, val weight:Double) : BaseIdentityDescription(id)
class DialogToolButtonDescription( owner:String, id:String, val handler:String,val caption:String) : BaseOwnedIdentityDescription(owner, id)

class DialogDescription(id:String, val viewId:String) : BaseIdentityDescription(id){
    var closable = true
    val buttons = arrayListOf<DialogToolButtonDescription>()
}

class EditorDescription(id:String, val entityId: String, val viewId:String) : BaseIdentityDescription(id){
    val toolButtons = arrayListOf<EditorToolButtonDescription>()
    val handlers  = arrayListOf<String>()
}




class UiMetaRegistry{

    val sharedEditorToolButtons = arrayListOf<SharedEditorToolButtonDescription>()

    val validationMessages = linkedMapOf<String, ValidationMessagesEnumDescription>()

    val viewModels = linkedMapOf<String, VMEntityDescription>()

    val viewSettings = linkedMapOf<String, VSEntityDescription>()

    val viewValidations = linkedMapOf<String, VVEntityDescription>()

    val views = linkedMapOf<String, BaseViewDescription>()

    val editors = linkedMapOf<String, EditorDescription>()

    val lists = linkedMapOf<String, ListDescription>()

    val dialogs = linkedMapOf<String, DialogDescription>()

    companion object{
        fun get() = Environment.getPublished(UiMetaRegistry::class)
    }
}
