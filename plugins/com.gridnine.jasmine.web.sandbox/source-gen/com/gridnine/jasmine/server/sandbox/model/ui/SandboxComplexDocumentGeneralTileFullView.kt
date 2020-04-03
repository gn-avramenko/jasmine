/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: jasmine
 * This file is auto generated, don't modify it manually
 *****************************************************************/

@file:Suppress("unused","RemoveRedundantQualifierName","UNCHECKED_CAST","MemberVisibilityCanBePrivate","RemoveEmptyPrimaryConstructor", "FunctionName")

package com.gridnine.jasmine.server.sandbox.model.ui

class SandboxComplexDocumentGeneralTileFullView():com.gridnine.jasmine.web.core.model.ui.BaseView<com.gridnine.jasmine.server.sandbox.model.ui.SandboxComplexDocumentGeneralTileFullVMJS,com.gridnine.jasmine.server.sandbox.model.ui.SandboxComplexDocumentGeneralTileFullVSJS,com.gridnine.jasmine.server.sandbox.model.ui.SandboxComplexDocumentGeneralTileFullVVJS>(){

    lateinit var stringProperty:com.gridnine.jasmine.web.core.model.ui.TextBoxWidget

    lateinit var floatProperty:com.gridnine.jasmine.web.core.model.ui.FloatBoxWidget

    lateinit var integerProperty:com.gridnine.jasmine.web.core.model.ui.IntegerBoxWidget

    lateinit var booleanProperty:com.gridnine.jasmine.web.core.model.ui.BooleanBoxWidget

    lateinit var entityProperty:com.gridnine.jasmine.web.core.model.ui.EntitySelectWidget

    lateinit var enumProperty:com.gridnine.jasmine.web.core.model.ui.EnumSelectWidget<com.gridnine.jasmine.server.sandbox.model.domain.SandboxEnumJS>

    lateinit var dateProperty:com.gridnine.jasmine.web.core.model.ui.DateBoxWidget

    lateinit var dateTimeProperty:com.gridnine.jasmine.web.core.model.ui.DateTimeBoxWidget

    lateinit var entityCollection:com.gridnine.jasmine.web.core.model.ui.TableWidget<com.gridnine.jasmine.server.sandbox.model.ui.SandboxTableVMJS,com.gridnine.jasmine.server.sandbox.model.ui.SandboxTableVSJS,com.gridnine.jasmine.server.sandbox.model.ui.SandboxTableVVJS>

    override fun getValue(propertyName: String): Any?{

        if("stringProperty" == propertyName){
            return this.stringProperty
        }

        if("floatProperty" == propertyName){
            return this.floatProperty
        }

        if("integerProperty" == propertyName){
            return this.integerProperty
        }

        if("booleanProperty" == propertyName){
            return this.booleanProperty
        }

        if("entityProperty" == propertyName){
            return this.entityProperty
        }

        if("enumProperty" == propertyName){
            return this.enumProperty
        }

        if("dateProperty" == propertyName){
            return this.dateProperty
        }

        if("dateTimeProperty" == propertyName){
            return this.dateTimeProperty
        }

        if("entityCollection" == propertyName){
            return this.entityCollection
        }

        return super.getValue(propertyName)
    }

    override fun setValue(propertyName:String, value:Any?){

        if("stringProperty" == propertyName){
            this.stringProperty=value as com.gridnine.jasmine.web.core.model.ui.TextBoxWidget
            return
        }

        if("floatProperty" == propertyName){
            this.floatProperty=value as com.gridnine.jasmine.web.core.model.ui.FloatBoxWidget
            return
        }

        if("integerProperty" == propertyName){
            this.integerProperty=value as com.gridnine.jasmine.web.core.model.ui.IntegerBoxWidget
            return
        }

        if("booleanProperty" == propertyName){
            this.booleanProperty=value as com.gridnine.jasmine.web.core.model.ui.BooleanBoxWidget
            return
        }

        if("entityProperty" == propertyName){
            this.entityProperty=value as com.gridnine.jasmine.web.core.model.ui.EntitySelectWidget
            return
        }

        if("enumProperty" == propertyName){
            this.enumProperty=value as com.gridnine.jasmine.web.core.model.ui.EnumSelectWidget<com.gridnine.jasmine.server.sandbox.model.domain.SandboxEnumJS>
            return
        }

        if("dateProperty" == propertyName){
            this.dateProperty=value as com.gridnine.jasmine.web.core.model.ui.DateBoxWidget
            return
        }

        if("dateTimeProperty" == propertyName){
            this.dateTimeProperty=value as com.gridnine.jasmine.web.core.model.ui.DateTimeBoxWidget
            return
        }

        if("entityCollection" == propertyName){
            this.entityCollection=value as com.gridnine.jasmine.web.core.model.ui.TableWidget<com.gridnine.jasmine.server.sandbox.model.ui.SandboxTableVMJS,com.gridnine.jasmine.server.sandbox.model.ui.SandboxTableVSJS,com.gridnine.jasmine.server.sandbox.model.ui.SandboxTableVVJS>
            return
        }

        super.setValue(propertyName, value)
    }
}