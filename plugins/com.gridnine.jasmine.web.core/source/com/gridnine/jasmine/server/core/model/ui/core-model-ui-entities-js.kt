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
