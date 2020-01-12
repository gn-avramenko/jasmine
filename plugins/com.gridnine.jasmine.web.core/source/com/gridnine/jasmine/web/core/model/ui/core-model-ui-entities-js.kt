/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/
@file:Suppress("unused", "UNCHECKED_CAST")
package com.gridnine.jasmine.web.core.model.ui

import com.gridnine.jasmine.web.core.model.common.BaseEntityJS
import com.gridnine.jasmine.web.core.model.common.BaseIntrospectableObjectJS
import com.gridnine.jasmine.web.core.model.domain.EntityReferenceJS
import kotlin.js.Date

abstract class BaseVMEntityJS:BaseEntityJS(){
    companion object{
        const val qualifiedClassName = "com.gridnine.jasmine.web.core.model.ui.BaseVMEntityJS"
    }
}

abstract class BaseVSEntityJS:BaseIntrospectableObjectJS(){
    companion object{
        const val qualifiedClassName = "com.gridnine.jasmine.web.core.model.ui.BaseVSEntityJS"
    }
}

abstract class BaseVVEntityJS:BaseIntrospectableObjectJS(){
    companion object{
        const val qualifiedClassName = "com.gridnine.jasmine.web.core.model.ui.BaseVVEntityJS"
    }
}

abstract class BaseView<VM:BaseVMEntityJS, VS:BaseVSEntityJS, VV:BaseVVEntityJS> {
    var parent:Any? = null
    lateinit var configure:(settings:VS) ->Unit
    lateinit var readData:(model:VM) ->Unit
    lateinit var writeData:(model:VM) ->Unit
    lateinit var showValidation:(validation:VV) ->Unit

    open fun getValue(propertyName: String): Any? {
        throw IllegalArgumentException(
                "invalid property name $propertyName")
    }

    open fun setValue(propertyName: String, value: Any?) {
        throw IllegalArgumentException("invalid propertyName $propertyName")
    }
}


class Editor<VM:BaseVMEntityJS, VS:BaseVSEntityJS, VV:BaseVVEntityJS, V:BaseView<VM, VS,VV>>{
    lateinit var view:V
    lateinit var setTitle:(String) ->Unit
    lateinit var close:() ->Unit
    lateinit var objectId:String
}


abstract class ValueWidget<T:Any, VS:Any>{
    lateinit var setData: (value:T?)->Unit
    lateinit var configure: (settings:VS)->Unit
    lateinit var getData: ()->T?
    lateinit var showValidation:(String?) ->Unit
    var valueChangeListener: ((newValue:T?, oldValue:T?) -> Unit)? = null
}

abstract class MultiValueWidget<T:Any, VS:Any>{
    lateinit var readData: (value:List<T>)->Unit
    lateinit var configure: (settings:VS)->Unit
    lateinit var writeData: (MutableList<T>)->Unit
    lateinit var showValidation:(String?) ->Unit
}

abstract class CollectionWidget<T:BaseVMEntityJS, VS:Any, VV:BaseVVEntityJS>{
    lateinit var readData: (value:List<T>)->Unit
    lateinit var configure: (settings:VS)->Unit
    lateinit var writeData: (value:MutableList<T>)->Unit
    lateinit var showValidation:(value:List<VV>) ->Unit
}

open class TextBoxWidget:ValueWidget<String, Unit>()
open class PasswordBoxWidget:ValueWidget<String, Unit>()
open class TextAreaWidget:ValueWidget<String,Unit>()
open class IntegerBoxWidget:ValueWidget<Int,Unit>()
open class FloatBoxWidget:ValueWidget<Double,Unit>()
open class BooleanBoxWidget:ValueWidget<Boolean,Unit>()
open class DateBoxWidget:ValueWidget<Date,Unit>()
open class DateTimeBoxWidget:ValueWidget<Date,Unit>()
open class EnumSelectWidget<E:Enum<E>>:ValueWidget<E,EnumSelectConfigurationJS<E>>()
open class SelectWidget:ValueWidget<SelectItemJS,SelectConfigurationJS>()
open class EnumMultiSelectWidget<E:Enum<E>>:MultiValueWidget<E,EnumSelectConfigurationJS<E>>()

open class EntityAutocompletetWidget:ValueWidget<EntityReferenceJS, EntityAutocompleteConfigurationJS>()

open class TableWidget<T:BaseVMEntityJS, VS:BaseVSEntityJS, VV:BaseVVEntityJS>:CollectionWidget<T, TableConfigurationJS<VS>, VV>()

class SelectItemJS(val id:String?, val caption:String?)

class EnumSelectConfigurationJS<E:Enum<E>> {
    var nullAllowed = true
    companion object{
        const val qualifiedClassName = "com.gridnine.jasmine.web.core.model.ui.EnumSelectConfigurationJS"
        const val nullAllowed = "nullAllowed"
    }
}


class SelectConfigurationJS {
    val possibleValues = arrayListOf<SelectItemJS>()
    var nullAllowed = true
    companion object{
        const val qualifiedClassName = "com.gridnine.jasmine.web.core.model.ui.SelectConfigurationJS"
        const val possibleValues = "possibleValues"
        const val nullAllowed = "nullAllowed"
    }
}


class EntityAutocompleteDataSourceJS {
    var name:String? = null
    var autocompleteId:String? = "standard"
    var indexClassName:String? = null
    val columnsNames = arrayListOf<String>()
    companion object{
        const val qualifiedClassName = "com.gridnine.jasmine.web.core.model.ui.EntityAutocompleteDataSourceJS"
        const val name = "name"
        const val autocompleteId = "autocompleteId"
        const val indexClassName = "indexClassName"
        const val columnsNames = "columnsNames"
    }
}

class EntityAutocompleteConfigurationJS {
    var nullAllowed:Boolean? = true
    var limit:Int = 10
    var dataSources = arrayListOf<EntityAutocompleteDataSourceJS>()
    companion object{
        const val qualifiedClassName = "com.gridnine.jasmine.web.core.model.ui.EntityAutocompleteConfigurationJS"
        const val nullAllowed = "nullAllowed"
        const val limit = "limit"
        const val dataSources = "dataSources"
    }
}


abstract class BaseColumnConfigurationJS{
    var notEditable = false
    companion object{
        const val notEditable = "notEditable"
    }
}

class TextColumnConfigurationJS:BaseColumnConfigurationJS(){
    companion object{
        const val qualifiedClassName = "com.gridnine.jasmine.web.core.model.ui.TextColumnConfigurationJS"
    }
}
class IntegerColumnConfigurationJS:BaseColumnConfigurationJS(){
    companion object{
        const val qualifiedClassName = "com.gridnine.jasmine.web.core.model.ui.IntegerColumnConfigurationJS"
    }
}
class FloatColumnConfigurationJS:BaseColumnConfigurationJS(){
    companion object{
        const val qualifiedClassName = "com.gridnine.jasmine.web.core.model.ui.FloatColumnConfigurationJS"
    }
}
class DateColumnConfigurationJS:BaseColumnConfigurationJS(){
    companion object{
        const val qualifiedClassName = "com.gridnine.jasmine.web.core.model.ui.DateColumnConfigurationJS"
    }
}
class EnumColumnConfigurationJS<E:Enum<E>>:BaseColumnConfigurationJS(){
    var nullAllowed:Boolean? = true
    companion object{
        const val nullAllowed ="nullAllowed"
        const val qualifiedClassName = "com.gridnine.jasmine.web.core.model.ui.EnumColumnConfigurationJS"
    }
}

class SelectColumnConfigurationJS:BaseColumnConfigurationJS(){
    var nullAllowed:Boolean = true
    val possibleValues = arrayListOf<SelectItemJS>()
    companion object{
        const val possibleValues = "possibleValues"
        const val nullAllowed ="nullAllowed"
        const val qualifiedClassName = "com.gridnine.jasmine.web.core.model.ui.SelectColumnConfigurationJS"
    }
}

class EntityColumnConfigurationJS:BaseColumnConfigurationJS(){
    var nullAllowed:Boolean? = true
    var limit:Int = 10
    var dataSources = arrayListOf<EntityAutocompleteDataSourceJS>()

    companion object{
        const val nullAllowed ="nullAllowed"
        const val limit ="limit"
        const val dataSources ="dataSources"
        const val qualifiedClassName = "com.gridnine.jasmine.web.core.model.ui.EntityColumnConfigurationJS"
    }
}

class TableConfigurationJS<VS:BaseVSEntityJS>{

    lateinit var columnSettings:VS

    var nonEditable:Boolean = false

    companion object{
        const val columnSettings ="columnSettings"
        const val qualifiedClassName = "com.gridnine.jasmine.web.core.model.ui.TableConfigurationJS"
    }
}

interface EditorToolButtonHandler<VM:BaseVMEntityJS, VS:BaseVSEntityJS, VV:BaseVVEntityJS, V:BaseView<VM,VS,VV>>{

    fun onClick(view:V)

}

class ObjectList(val listId:String)

interface ListToolButtonHandler{

    fun onClick(list:ObjectList)

}


class SimplePropertyWrapperVMJS<T:Any>():BaseVMEntityJS(){

    constructor(uid:String, property:T?):this(){
        this.uid = uid
        this.property = property
    }
    var property: T? = null

    override fun getValue(propertyName: String): Any? {
        if (SimplePropertyWrapperVMJS.property == propertyName) {
            return property
        }
        return super.getValue(propertyName)
    }

    override fun setValue(propertyName: String, value: Any?) {
        if (SimplePropertyWrapperVMJS.property == propertyName) {
            property = value as T?
            return
        }
        super.setValue(propertyName, value)
    }

    companion object{
        const val property = "property"
        const val qualifiedClassName = "com.gridnine.jasmine.web.core.model.ui.SimplePropertyWrapperVMJS"
    }
}

class SimplePropertyWrapperVSJS<T:Any>():BaseVSEntityJS(){

    constructor(property:T?):this(){
        this.property = property
    }
    var property: T? = null

    override fun getValue(propertyName: String): Any? {
        if (SimplePropertyWrapperVSJS.property == propertyName) {
            return property
        }
        return super.getValue(propertyName)
    }

    override fun setValue(propertyName: String, value: Any?) {
        if (SimplePropertyWrapperVSJS.property == propertyName) {
            property = value as T?
            return
        }
        super.setValue(propertyName, value)
    }

    companion object{
        const val property = "property"
    }
}

class SimplePropertyWrapperVVJS:BaseVVEntityJS(){
    var property: String? = null

    override fun getValue(propertyName: String): Any? {
        if (SimplePropertyWrapperVVJS.property == propertyName) {
            return property
        }
        return super.getValue(propertyName)
    }

    override fun setValue(propertyName: String, value: Any?) {
        if (SimplePropertyWrapperVVJS.property == propertyName) {
            property = value as String?
            return
        }
        super.setValue(propertyName, value)
    }

    companion object{
        const val property = "property"
    }
}


