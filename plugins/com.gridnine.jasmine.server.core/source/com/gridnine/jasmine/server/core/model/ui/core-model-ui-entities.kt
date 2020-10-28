/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/
@file:Suppress("unused")
package com.gridnine.jasmine.server.core.model.ui

import com.gridnine.jasmine.server.core.model.common.BaseIntrospectableObject

abstract class BaseVM:BaseIntrospectableObject()
abstract class BaseVS:BaseIntrospectableObject()
abstract class BaseVV:BaseIntrospectableObject()

abstract class BaseWidgetConfiguration: BaseIntrospectableObject(){
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

class TextBoxConfiguration() : BaseWidgetConfiguration(){
    constructor(configure:TextBoxConfiguration.()->Unit):this(){
        configure.invoke(this)
    }
}

class PasswordBoxConfiguration() : BaseWidgetConfiguration(){
    constructor(configure:PasswordBoxConfiguration.()->Unit):this(){
        configure.invoke(this)
    }
}