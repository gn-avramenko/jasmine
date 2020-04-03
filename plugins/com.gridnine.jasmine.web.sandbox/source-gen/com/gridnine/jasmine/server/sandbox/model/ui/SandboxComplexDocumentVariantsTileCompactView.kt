/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: jasmine
 * This file is auto generated, don't modify it manually
 *****************************************************************/

@file:Suppress("unused","RemoveRedundantQualifierName","UNCHECKED_CAST","MemberVisibilityCanBePrivate","RemoveEmptyPrimaryConstructor", "FunctionName")

package com.gridnine.jasmine.server.sandbox.model.ui

class SandboxComplexDocumentVariantsTileCompactView():com.gridnine.jasmine.web.core.model.ui.BaseView<com.gridnine.jasmine.server.sandbox.model.ui.SandboxComplexDocumentVariantsTileCompactVMJS,com.gridnine.jasmine.server.sandbox.model.ui.SandboxComplexDocumentVariantsTileCompactVSJS,com.gridnine.jasmine.server.sandbox.model.ui.SandboxComplexDocumentVariantsTileCompactVVJS>(){

    lateinit var variants:com.gridnine.jasmine.web.core.model.ui.TableWidget<com.gridnine.jasmine.server.sandbox.model.ui.SandboxVariantsCompactTableVMJS,com.gridnine.jasmine.server.sandbox.model.ui.SandboxVariantsCompactTableVSJS,com.gridnine.jasmine.server.sandbox.model.ui.SandboxVariantsCompactTableVVJS>

    override fun getValue(propertyName: String): Any?{

        if("variants" == propertyName){
            return this.variants
        }

        return super.getValue(propertyName)
    }

    override fun setValue(propertyName:String, value:Any?){

        if("variants" == propertyName){
            this.variants=value as com.gridnine.jasmine.web.core.model.ui.TableWidget<com.gridnine.jasmine.server.sandbox.model.ui.SandboxVariantsCompactTableVMJS,com.gridnine.jasmine.server.sandbox.model.ui.SandboxVariantsCompactTableVSJS,com.gridnine.jasmine.server.sandbox.model.ui.SandboxVariantsCompactTableVVJS>
            return
        }

        super.setValue(propertyName, value)
    }
}