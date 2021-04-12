/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/
package com.gridnine.jasmine.common.core.model


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
data class SelectItem(var id:String, var text:String):BaseIntrospectableObject(){
    override fun equals(other: Any?): Boolean {
        return other is SelectItem && other.id == id
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


class FloatNumberBoxConfiguration() : BaseWidgetConfiguration(){
    constructor(configure:FloatNumberBoxConfiguration.()->Unit):this(){
        configure.invoke(this)
    }
}

class IntegerNumberBoxConfiguration() : BaseWidgetConfiguration(){
    constructor(configure:IntegerNumberBoxConfiguration.()->Unit):this(){
        configure.invoke(this)
    }
}

class LongNumberBoxConfiguration() : BaseWidgetConfiguration(){
    constructor(configure:LongNumberBoxConfiguration.()->Unit):this(){
        configure.invoke(this)
    }
}

class BooleanBoxConfiguration() : BaseWidgetConfiguration(){
    constructor(configure:BooleanBoxConfiguration.()->Unit):this(){
        configure.invoke(this)
    }
}

class EntitySelectBoxConfiguration() : BaseWidgetConfiguration(){
    constructor(configure:EntitySelectBoxConfiguration.()->Unit):this(){
        configure.invoke(this)
    }
}

class EnumSelectBoxConfiguration() : BaseWidgetConfiguration(){
    constructor(configure:EnumSelectBoxConfiguration.()->Unit):this(){
        configure.invoke(this)
    }
}

class DateBoxConfiguration() : BaseWidgetConfiguration(){
    constructor(configure:DateBoxConfiguration.()->Unit):this(){
        configure.invoke(this)
    }
}

class DateTimeBoxConfiguration() : BaseWidgetConfiguration(){
    constructor(configure:DateTimeBoxConfiguration.()->Unit):this(){
        configure.invoke(this)
    }
}

class GeneralSelectBoxConfiguration() : BaseWidgetConfiguration(){
    val possibleValues = arrayListOf<SelectItem>()
    constructor(configure:GeneralSelectBoxConfiguration.()->Unit):this(){
        configure.invoke(this)
    }
}

abstract class BaseNavigatorVariantVM:BaseVM(){
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

abstract class BaseNavigatorVariantVS:BaseVS(){
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

abstract class BaseNavigatorVariantVV:BaseVV(){
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

abstract class BaseTableBoxVM:BaseVM(){
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

abstract class BaseTableBoxVS:BaseVS(){
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

abstract class BaseTableBoxVV:BaseVV(){
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

class EditorReadDataBundle(val vm:BaseVM, val vs:BaseVS, val title:String)

class EditorVersionReadDataBundle(val vm:BaseVM, val vs:BaseVS)

class EditorWriteDataBundle(val vm:BaseVM?, val vs:BaseVS?, val vv:BaseVV?, val newUid:String?, val title:String?)