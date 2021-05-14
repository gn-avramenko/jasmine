/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

@file:Suppress("unused")

package com.gridnine.jasmine.web.standard.widgets

import com.gridnine.jasmine.common.core.meta.DomainMetaRegistryJS
import com.gridnine.jasmine.common.core.model.BaseIndexJS

abstract class BaseWidgetConfiguration{
    var width:String? = null
    var height:String? = null
}

interface AutocompleteHandler{
    fun getIndexClassName():String
    fun getAutocompleteFieldName():String

    companion object{
        fun createMetadataBasedAutocompleteHandler(objectId: String):AutocompleteHandler{
            return object:AutocompleteHandler{

                val indexClassName = DomainMetaRegistryJS.get().indexes.values.find { it.document == objectId }!!.id.substringBeforeLast("JS")
                override fun getIndexClassName(): String {
                    return indexClassName
                }

                override fun getAutocompleteFieldName(): String {
                    return BaseIndexJS.documentField+"Caption"
                }

            }
        }
    }
}

enum class WidgetTypeJS{
    TEXT_BOX,
    PASSWORD_BOX,
    FLOAT_NUMBER_BOX,
    INTEGER_NUMBER_BOX,
    BOOLEAN_BOX,
    ENTITY_SELECT_BOX,
    GENERAL_SELECT_BOX,
    ENUM_SELECT_BOX,
    DATE_BOX,
    DATE_TIME_BOX,
    TABLE_BOX
}

abstract class BaseWidgetDescriptionJS(val notEditable:Boolean, val widgetType:WidgetTypeJS)

class PasswordBoxWidgetDescriptionJS(notEditable:Boolean):BaseWidgetDescriptionJS(notEditable, WidgetTypeJS.PASSWORD_BOX)

class TextBoxWidgetDescriptionJS(notEditable:Boolean):BaseWidgetDescriptionJS(notEditable, WidgetTypeJS.TEXT_BOX)

class BigDecimalNumberBoxWidgetDescriptionJS(notEditable:Boolean):BaseWidgetDescriptionJS(notEditable, WidgetTypeJS.FLOAT_NUMBER_BOX)

class IntegerNumberBoxWidgetDescriptionJS(notEditable:Boolean, val nonNullable: Boolean):BaseWidgetDescriptionJS(notEditable, WidgetTypeJS.INTEGER_NUMBER_BOX)

class BooleanBoxWidgetDescriptionJS(notEditable:Boolean):BaseWidgetDescriptionJS(notEditable, WidgetTypeJS.BOOLEAN_BOX)

class EntitySelectBoxWidgetDescriptionJS(notEditable:Boolean, val objectId:String):BaseWidgetDescriptionJS(notEditable, WidgetTypeJS.ENTITY_SELECT_BOX)

class GeneralSelectBoxWidgetDescriptionJS(notEditable:Boolean):BaseWidgetDescriptionJS(notEditable, WidgetTypeJS.GENERAL_SELECT_BOX)

class EnumSelectBoxWidgetDescriptionJS(notEditable:Boolean, val enumId:String):BaseWidgetDescriptionJS(notEditable, WidgetTypeJS.ENUM_SELECT_BOX)

class DateBoxWidgetDescriptionJS(notEditable:Boolean):BaseWidgetDescriptionJS(notEditable, WidgetTypeJS.DATE_BOX)

class DateTimeBoxWidgetDescriptionJS(notEditable:Boolean):BaseWidgetDescriptionJS(notEditable, WidgetTypeJS.DATE_TIME_BOX)