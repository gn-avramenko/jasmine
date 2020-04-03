/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: jasmine
 * This file is auto generated, don't modify it manually
 *****************************************************************/

@file:Suppress("unused","RemoveRedundantQualifierName","UNCHECKED_CAST","MemberVisibilityCanBePrivate","RemoveEmptyPrimaryConstructor", "FunctionName")

package com.gridnine.jasmine.server.sandbox.model.ui

class SandboxComplexDocumentVariantsTileFullView():com.gridnine.jasmine.web.core.model.ui.BaseView<com.gridnine.jasmine.server.sandbox.model.ui.SandboxComplexDocumentVariantsTileFullVMJS,com.gridnine.jasmine.server.sandbox.model.ui.SandboxComplexDocumentVariantsTileFullVSJS,com.gridnine.jasmine.server.sandbox.model.ui.SandboxComplexDocumentVariantsTileFullVVJS>(){

    lateinit var variants:com.gridnine.jasmine.web.core.model.ui.NavigatorWidget<com.gridnine.jasmine.web.core.model.ui.BaseVMEntityJS,com.gridnine.jasmine.web.core.model.ui.BaseVSEntityJS,com.gridnine.jasmine.web.core.model.ui.BaseVVEntityJS>

    override fun getValue(propertyName: String): Any?{

        if("variants" == propertyName){
            return this.variants
        }

        return super.getValue(propertyName)
    }

    override fun setValue(propertyName:String, value:Any?){

        if("variants" == propertyName){
            this.variants=value as com.gridnine.jasmine.web.core.model.ui.NavigatorWidget<com.gridnine.jasmine.web.core.model.ui.BaseVMEntityJS,com.gridnine.jasmine.web.core.model.ui.BaseVSEntityJS,com.gridnine.jasmine.web.core.model.ui.BaseVVEntityJS>
            return
        }

        super.setValue(propertyName, value)
    }
}