/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/
@file:Suppress("unused")
package com.gridnine.jasmine.server.core.model.ui

import com.gridnine.jasmine.server.core.model.common.BaseEntity
import com.gridnine.jasmine.server.core.model.common.BaseIntrospectableObject

abstract class BaseVMEntity:BaseIntrospectableObject() {

    var uid: String? = null

    override fun getValue(propertyName: String): Any? {
        if(BaseVMEntity.uid == propertyName){
            return uid
        }
        return super.getValue(propertyName)
    }

    override fun setValue(propertyName: String, value: Any?) {
        if(BaseVMEntity.uid == propertyName){
            uid = value as String?
            return
        }
        super.setValue(propertyName, value)
    }

    companion object{
        const val uid = "uid"
    }
}

abstract class BaseVSEntity:BaseIntrospectableObject()

abstract class BaseVVEntity :BaseIntrospectableObject()

object ValidationMessagesFactory {
    fun getMessage(messagesId: String, messageId: String, vararg params: Any): String? {
        val validationMessagesEnumDescription = UiMetaRegistry.get().validationMessages[messagesId]
                ?: return messageId
        val validationMessageDescription = validationMessagesEnumDescription.items[messageId]
                ?: return messageId
        return replace(validationMessageDescription.getDisplayName(), params)
    }

    private fun replace(str: String?, vararg data: Any?): String? {
        if (data.isEmpty() ||str == null) {
            return str
        }
        var result: String = str
        for (i in data.indices) {
            result = result.replace("{$i}",
                    data[i].toString())
        }
        return result
    }
}

class SelectItem(val id:String?, val caption:String?)

class SelectConfiguration{
    val possibleValues = arrayListOf<SelectItem>()
}

class EnumSelectConfiguration<E:Enum<E>>


class EntityAutocompleteConfiguration {
    var limit:Int = 10
    var dataSources = arrayListOf<String>()
}

abstract class BaseColumnConfiguration{
    var notEditable = false
}

class TextColumnConfiguration():BaseColumnConfiguration(){
    constructor(init: TextColumnConfiguration.()->Unit):this(){
        this.init()
    }
}
class IntegerColumnConfiguration():BaseColumnConfiguration()
{
    constructor(init: IntegerColumnConfiguration.()->Unit):this(){
        this.init()
    }
}

class DateColumnConfiguration():BaseColumnConfiguration()
{
    constructor(init: DateColumnConfiguration.()->Unit):this(){
        this.init()
    }
}

class FloatColumnConfiguration():BaseColumnConfiguration()
{
    constructor(init: FloatColumnConfiguration.()->Unit):this(){
        this.init()
    }
}
class EnumColumnConfiguration<E:Enum<E>>():BaseColumnConfiguration(){
    constructor(init: EnumColumnConfiguration<E>.()->Unit):this(){
        this.init()
    }
}
class EntityColumnConfiguration<E:BaseEntity>():BaseColumnConfiguration(){
    constructor(init: EntityColumnConfiguration<E>.()->Unit):this(){
        this.init()
    }
    var limit:Int = 10
    var dataSources = arrayListOf<String>()
}

class TableConfiguration<VS:BaseVSEntity>(){

    constructor(init:TableConfiguration<VS>.()->Unit):this(){
        this.init()
    }

    lateinit var columnSettings:VS
}

@Suppress("UNCHECKED_CAST")
class TileData<VMC:Any, VMF:Any>:BaseIntrospectableObject(){
    lateinit var compactData:VMC
    lateinit var fullData:VMF


    companion object{
        const val compactData = "compactData"
        const val fullData = "fullData"
    }
}