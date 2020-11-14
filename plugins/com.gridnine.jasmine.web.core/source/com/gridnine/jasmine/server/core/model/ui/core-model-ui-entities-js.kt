/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/
@file:Suppress("unused")
package com.gridnine.jasmine.server.core.model.ui

import com.gridnine.jasmine.server.core.model.common.BaseIntrospectableObjectJS

abstract class BaseWidgetConfigurationJS: BaseIntrospectableObjectJS(){
    var notEditable = false

    override fun getValue(propertyName: String): Any? {
        if("notEditable" == propertyName){
            return notEditable
        }
        return super.getValue(propertyName)
    }

    override fun setValue(propertyName: String, value: Any?) {
        if("notEditable" == propertyName){
            notEditable = value as Boolean
            return
        }
        super.setValue(propertyName, value)
    }

}

abstract class BaseVMJS:BaseIntrospectableObjectJS()
abstract class BaseVSJS:BaseIntrospectableObjectJS()
abstract class BaseVVJS:BaseIntrospectableObjectJS()

class TextBoxConfigurationJS : BaseWidgetConfigurationJS(){
    companion object{
        val qualifiedClassName = "com.gridnine.jasmine.server.core.model.ui.TextBoxConfigurationJS"
    }
}
class PasswordBoxConfigurationJS : BaseWidgetConfigurationJS(){
    companion object{
        val qualifiedClassName = "com.gridnine.jasmine.server.core.model.ui.PasswordBoxConfigurationJS"
    }
}


class DateTimeBoxConfigurationJS : BaseWidgetConfigurationJS(){
    companion object{
        val qualifiedClassName = "com.gridnine.jasmine.server.core.model.ui.DateTimeBoxConfigurationJS"
    }
}

class DateBoxConfigurationJS : BaseWidgetConfigurationJS(){
    companion object{
        val qualifiedClassName = "com.gridnine.jasmine.server.core.model.ui.DateBoxConfigurationJS"
    }
}

class EntitySelectBoxConfigurationJS : BaseWidgetConfigurationJS(){
    companion object{
        val qualifiedClassName = "com.gridnine.jasmine.server.core.model.ui.EntitySelectBoxConfigurationJS"
    }
}

class EnumSelectBoxConfigurationJS : BaseWidgetConfigurationJS(){
    companion object{
        val qualifiedClassName = "com.gridnine.jasmine.server.core.model.ui.EnumSelectBoxConfigurationJS"
    }
}
class FloatNumberBoxConfigurationJS : BaseWidgetConfigurationJS(){
    companion object{
        val qualifiedClassName = "com.gridnine.jasmine.server.core.model.ui.FloatNumberBoxConfigurationJS"
    }
}
class IntegerNumberBoxConfigurationJS : BaseWidgetConfigurationJS(){
    companion object{
        val qualifiedClassName = "com.gridnine.jasmine.server.core.model.ui.IntegerNumberBoxConfigurationJS"
    }
}

class BooleanBoxConfigurationJS : BaseWidgetConfigurationJS(){
    companion object{
        val qualifiedClassName = "com.gridnine.jasmine.server.core.model.ui.BooleanBoxConfigurationJS"
    }
}