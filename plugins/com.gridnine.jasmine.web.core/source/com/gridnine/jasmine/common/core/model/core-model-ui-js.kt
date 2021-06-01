/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/
@file:Suppress("unused")

package com.gridnine.jasmine.common.core.model

import com.gridnine.jasmine.web.core.common.EnvironmentJS
import com.gridnine.jasmine.web.core.ui.components.WebNode
import kotlin.reflect.KClass


abstract class BaseVMJS:BaseIntrospectableObjectJS()
abstract class BaseVSJS:BaseIntrospectableObjectJS()
abstract class BaseVVJS:BaseIntrospectableObjectJS()

abstract class BaseWidgetConfigurationJS: BaseIntrospectableObjectJS(){
    var notEditable = false

    override fun getValue(propertyName: String): Any? {
        if(notEditableField == propertyName){
            return notEditable
        }
        return super.getValue(propertyName)
    }

    override fun setValue(propertyName: String, value: Any?) {
        if(notEditableField == propertyName){
            notEditable = value as Boolean
            return
        }
        super.setValue(propertyName, value)
    }

    companion object{
        const val notEditableField = "notEditable"
    }

}
class SelectItemJS():BaseIntrospectableObjectJS(){
    lateinit var id:String
    lateinit var text:String
    constructor(id:String, text:String):this(){
        this.id= id
        this.text = text
    }

    override fun equals(other: Any?): Boolean {
        return other is SelectItemJS && other.id == id
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }

    override fun getValue(propertyName: String): Any? {
        if(idProperty == propertyName){
            return id
        }
        if(textProperty == propertyName){
            return text
        }
        return super.getValue(propertyName)
    }

    override fun setValue(propertyName: String, value: Any?) {
        if(idProperty == propertyName){
            id = value as String
            return
        }
        if(textProperty == propertyName){
            text = value as String
            return
        }
        super.setValue(propertyName, value)
    }
    companion object{
        const val idProperty = "id"
        const val textProperty = "text"
        const val qualifiedClassName = "com.gridnine.jasmine.common.core.model.SelectItemJS"
    }
}


class TextBoxConfigurationJS() : BaseWidgetConfigurationJS(){
    constructor(configure:TextBoxConfigurationJS.()->Unit):this(){
        configure.invoke(this)
    }
    companion object{
        const val qualifiedClassName = "com.gridnine.jasmine.common.core.model.TextBoxConfigurationJS"
    }
}

class PasswordBoxConfigurationJS() : BaseWidgetConfigurationJS(){
    constructor(configure:PasswordBoxConfigurationJS.()->Unit):this(){
        configure.invoke(this)
    }
    companion object{
        const val qualifiedClassName = "com.gridnine.jasmine.common.core.model.PasswordBoxConfigurationJS"
    }
}


class BigDecimalBoxConfigurationJS() : BaseWidgetConfigurationJS(){
    constructor(configure:BigDecimalBoxConfigurationJS.()->Unit):this(){
        configure.invoke(this)
    }
    companion object{
        const val qualifiedClassName = "com.gridnine.jasmine.common.core.model.BigDecimalBoxConfigurationJS"
    }
}

class IntegerNumberBoxConfigurationJS() : BaseWidgetConfigurationJS(){
    constructor(configure:IntegerNumberBoxConfigurationJS.()->Unit):this(){
        configure.invoke(this)
    }
    companion object{
        const val qualifiedClassName = "com.gridnine.jasmine.common.core.model.IntegerNumberBoxConfigurationJS"
    }
}

class LongNumberBoxConfigurationJS() : BaseWidgetConfigurationJS(){
    constructor(configure:LongNumberBoxConfigurationJS.()->Unit):this(){
        configure.invoke(this)
    }
    companion object{
        const val qualifiedClassName = "com.gridnine.jasmine.common.core.model.LongNumberBoxConfigurationJS"
    }
}

class BooleanBoxConfigurationJS() : BaseWidgetConfigurationJS(){
    constructor(configure:BooleanBoxConfigurationJS.()->Unit):this(){
        configure.invoke(this)
    }
    companion object{
        const val qualifiedClassName = "com.gridnine.jasmine.common.core.model.BooleanBoxConfigurationJS"
    }
}

class EntitySelectBoxConfigurationJS() : BaseWidgetConfigurationJS(){
    constructor(configure:EntitySelectBoxConfigurationJS.()->Unit):this(){
        configure.invoke(this)
    }
    companion object{
        const val qualifiedClassName = "com.gridnine.jasmine.common.core.model.EntitySelectBoxConfigurationJS"
    }
}

class EnumSelectBoxConfigurationJS() : BaseWidgetConfigurationJS(){
    constructor(configure:EnumSelectBoxConfigurationJS.()->Unit):this(){
        configure.invoke(this)
    }
    companion object{
        const val qualifiedClassName = "com.gridnine.jasmine.common.core.model.EnumSelectBoxConfigurationJS"
    }
}

class DateBoxConfigurationJS() : BaseWidgetConfigurationJS(){
    constructor(configure:DateBoxConfigurationJS.()->Unit):this(){
        configure.invoke(this)
    }
    companion object{
        const val qualifiedClassName = "com.gridnine.jasmine.common.core.model.DateBoxConfigurationJS"
    }
}

class DateTimeBoxConfigurationJS() : BaseWidgetConfigurationJS(){
    constructor(configure:DateTimeBoxConfigurationJS.()->Unit):this(){
        configure.invoke(this)
    }
    companion object{
        const val qualifiedClassName = "com.gridnine.jasmine.common.core.model.DateTimeBoxConfigurationJS"
    }
}

class GeneralSelectBoxConfigurationJS() : BaseWidgetConfigurationJS(){
    val possibleValues = arrayListOf<SelectItemJS>()
    constructor(configure:GeneralSelectBoxConfigurationJS.()->Unit):this(){
        configure.invoke(this)
    }
    companion object{
        const val qualifiedClassName = "com.gridnine.jasmine.common.core.model.GeneralSelectBoxConfigurationJS"
    }
}
class RemoteGeneralSelectBoxConfigurationJS() : BaseWidgetConfigurationJS(){
    constructor(configure:RemoteGeneralSelectBoxConfigurationJS.()->Unit):this(){
        configure.invoke(this)
    }
    companion object{
        const val qualifiedClassName = "com.gridnine.jasmine.common.core.model.RemoteGeneralSelectBoxConfigurationJS"
    }
}

abstract class BaseNavigatorVariantVMJS:BaseVMJS(){
    lateinit var uid:String
    lateinit var title:String
    override fun getValue(propertyName: String): Any? {
        if("uid" == propertyName){
            return uid
        }
        if("title" == propertyName){
            return title
        }
        return super.getValue(propertyName)
    }

    override fun setValue(propertyName: String, value: Any?) {
        if("uid" == propertyName){
            uid = value as String
            return
        }
        if("title" == propertyName){
            title = value as String
            return
        }
        super.setValue(propertyName, value)
    }
}

abstract class BaseNavigatorVariantVSJS:BaseVSJS(){
    lateinit var uid:String
    override fun getValue(propertyName: String): Any? {
        if("uid" == propertyName){
            return uid
        }
        return super.getValue(propertyName)
    }

    override fun setValue(propertyName: String, value: Any?) {
        if("uid" == propertyName){
            uid = value as String
            return
        }
        super.setValue(propertyName, value)
    }
}

abstract class BaseNavigatorVariantVVJS:BaseVVJS(){
    lateinit var uid:String
    override fun getValue(propertyName: String): Any? {
        if("uid" == propertyName){
            return uid
        }
        return super.getValue(propertyName)
    }

    override fun setValue(propertyName: String, value: Any?) {
        if("uid" == propertyName){
            uid = value as String
            return
        }
        super.setValue(propertyName, value)
    }
}

abstract class BaseTableBoxVMJS:BaseVMJS(){
    lateinit var uid:String
    override fun getValue(propertyName: String): Any? {
        if("uid" == propertyName){
            return uid
        }
        return super.getValue(propertyName)
    }

    override fun setValue(propertyName: String, value: Any?) {
        if("uid" == propertyName){
            uid = value as String
            return
        }
        super.setValue(propertyName, value)
    }
}

abstract class BaseTableBoxVSJS:BaseVSJS(){
    lateinit var uid:String
    override fun getValue(propertyName: String): Any? {
        if("uid" == propertyName){
            return uid
        }
        return super.getValue(propertyName)
    }

    override fun setValue(propertyName: String, value: Any?) {
        if("uid" == propertyName){
            uid = value as String
            return
        }
        super.setValue(propertyName, value)
    }
}

abstract class BaseTableBoxVVJS:BaseVVJS(){
    lateinit var uid:String
    override fun getValue(propertyName: String): Any? {
        if("uid" == propertyName){
            return uid
        }
        return super.getValue(propertyName)
    }

    override fun setValue(propertyName: String, value: Any?) {
        if("uid" == propertyName){
            uid = value as String
            return
        }
        super.setValue(propertyName, value)
    }
}

