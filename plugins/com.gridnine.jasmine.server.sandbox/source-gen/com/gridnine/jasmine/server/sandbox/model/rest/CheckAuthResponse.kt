/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: jasmine
 * This file is auto generated, don't modify it manually
 *****************************************************************/

@file:Suppress("unused","RemoveRedundantQualifierName","UNCHECKED_CAST","MemberVisibilityCanBePrivate","RemoveEmptyPrimaryConstructor", "FunctionName")

package com.gridnine.jasmine.server.sandbox.model.rest

class CheckAuthResponse():com.gridnine.jasmine.server.core.model.rest.BaseRestEntity(){

    var authorized:Boolean?=null

    override fun getValue(propertyName: String): Any?{

        if("authorized" == propertyName){
            return this.authorized
        }

        return super.getValue(propertyName)
    }

    override fun setValue(propertyName:String, value:Any?){

        if("authorized" == propertyName){
            this.authorized=value as Boolean?
            return
        }

        super.setValue(propertyName, value)
    }
}