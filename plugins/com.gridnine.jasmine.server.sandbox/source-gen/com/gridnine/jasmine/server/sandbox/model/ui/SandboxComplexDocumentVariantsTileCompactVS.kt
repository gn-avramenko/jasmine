/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: jasmine
 * This file is auto generated, don't modify it manually
 *****************************************************************/

@file:Suppress("unused","RemoveRedundantQualifierName","UNCHECKED_CAST","MemberVisibilityCanBePrivate","RemoveEmptyPrimaryConstructor", "FunctionName")

package com.gridnine.jasmine.server.sandbox.model.ui

class SandboxComplexDocumentVariantsTileCompactVS():com.gridnine.jasmine.server.core.model.ui.BaseVSEntity(){

    constructor(init: SandboxComplexDocumentVariantsTileCompactVS.() ->Unit):this(){
         this.init()
    }

    var variants:com.gridnine.jasmine.server.core.model.ui.TableConfiguration<com.gridnine.jasmine.server.sandbox.model.ui.SandboxVariantsCompactTableVS>?=null

    override fun getValue(propertyName: String): Any?{

        if("variants" == propertyName){
            return this.variants
        }

        return super.getValue(propertyName)
    }

    override fun setValue(propertyName:String, value:Any?){

        if("variants" == propertyName){
            this.variants=value as com.gridnine.jasmine.server.core.model.ui.TableConfiguration<com.gridnine.jasmine.server.sandbox.model.ui.SandboxVariantsCompactTableVS>?
            return
        }

        super.setValue(propertyName, value)
    }
}