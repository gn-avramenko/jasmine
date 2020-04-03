/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: jasmine
 * This file is auto generated, don't modify it manually
 *****************************************************************/

@file:Suppress("unused","RemoveRedundantQualifierName","UNCHECKED_CAST","MemberVisibilityCanBePrivate","RemoveEmptyPrimaryConstructor", "FunctionName")

package com.gridnine.jasmine.server.sandbox.model.ui

class SandboxComplexDocumentVariantsTileCompactVSJS():com.gridnine.jasmine.web.core.model.ui.BaseVSEntityJS(){

    var variants:com.gridnine.jasmine.web.core.model.ui.TableConfigurationJS<com.gridnine.jasmine.server.sandbox.model.ui.SandboxVariantsCompactTableVSJS>?=null

    override fun getValue(propertyName: String): Any?{

        if("variants" == propertyName){
            return this.variants
        }

        return super.getValue(propertyName)
    }

    override fun setValue(propertyName:String, value:Any?){

        if("variants" == propertyName){
            this.variants=value as com.gridnine.jasmine.web.core.model.ui.TableConfigurationJS<com.gridnine.jasmine.server.sandbox.model.ui.SandboxVariantsCompactTableVSJS>?
            return
        }

        super.setValue(propertyName, value)
    }
}