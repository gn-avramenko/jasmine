/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: jasmine
 * This file is auto generated, don't modify it manually
 *****************************************************************/

@file:Suppress("unused","RemoveRedundantQualifierName","UNCHECKED_CAST","MemberVisibilityCanBePrivate","RemoveEmptyPrimaryConstructor", "FunctionName")

package com.gridnine.jasmine.server.standard.model.rest

class GetMetadataResponseJS():com.gridnine.jasmine.web.core.model.rest.BaseRestEntityJS(){

    val restEnums = arrayListOf<com.gridnine.jasmine.server.standard.model.rest.EnumDescriptionDTJS>()

    val restEntities = arrayListOf<com.gridnine.jasmine.server.standard.model.rest.RestEntityDescriptionDTJS>()

    val domainEnums = arrayListOf<com.gridnine.jasmine.server.standard.model.rest.DomainEnumDescriptionDTJS>()

    val domainIndexes = arrayListOf<com.gridnine.jasmine.server.standard.model.rest.IndexDescriptionDTJS>()

    val domainAssets = arrayListOf<com.gridnine.jasmine.server.standard.model.rest.AssetDescriptionDTJS>()

    val operations = arrayListOf<com.gridnine.jasmine.server.standard.model.rest.RestOperationDescriptionDTJS>()

    val vmEntities = arrayListOf<com.gridnine.jasmine.server.standard.model.rest.VMEntityDescriptionDTJS>()

    val vsEntities = arrayListOf<com.gridnine.jasmine.server.standard.model.rest.VSEntityDescriptionDTJS>()

    val vvEntities = arrayListOf<com.gridnine.jasmine.server.standard.model.rest.VVEntityDescriptionDTJS>()

    val views = arrayListOf<com.gridnine.jasmine.server.standard.model.rest.BaseViewDescriptionDTJS>()

    val editors = arrayListOf<com.gridnine.jasmine.server.standard.model.rest.EditorDescriptionDTJS>()

    val lists = arrayListOf<com.gridnine.jasmine.server.standard.model.rest.ListDescriptionDTJS>()

    val dialogs = arrayListOf<com.gridnine.jasmine.server.standard.model.rest.DialogDescriptionDTJS>()

    val sharedEditorButtons = arrayListOf<com.gridnine.jasmine.server.standard.model.rest.StandardButtonDescriptionDTJS>()

    val sharedListButtons = arrayListOf<com.gridnine.jasmine.server.standard.model.rest.StandardButtonDescriptionDTJS>()

    val autocompletes = arrayListOf<com.gridnine.jasmine.server.standard.model.rest.AutocompleteDescriptionDTJS>()

    @Suppress("UNCHECKED_CAST")
    override fun getCollection(collectionName: String): MutableCollection<Any>{

        if("restEnums" == collectionName){
            return this.restEnums as MutableCollection<Any>
        }

        if("restEntities" == collectionName){
            return this.restEntities as MutableCollection<Any>
        }

        if("domainEnums" == collectionName){
            return this.domainEnums as MutableCollection<Any>
        }

        if("domainIndexes" == collectionName){
            return this.domainIndexes as MutableCollection<Any>
        }

        if("domainAssets" == collectionName){
            return this.domainAssets as MutableCollection<Any>
        }

        if("operations" == collectionName){
            return this.operations as MutableCollection<Any>
        }

        if("vmEntities" == collectionName){
            return this.vmEntities as MutableCollection<Any>
        }

        if("vsEntities" == collectionName){
            return this.vsEntities as MutableCollection<Any>
        }

        if("vvEntities" == collectionName){
            return this.vvEntities as MutableCollection<Any>
        }

        if("views" == collectionName){
            return this.views as MutableCollection<Any>
        }

        if("editors" == collectionName){
            return this.editors as MutableCollection<Any>
        }

        if("lists" == collectionName){
            return this.lists as MutableCollection<Any>
        }

        if("dialogs" == collectionName){
            return this.dialogs as MutableCollection<Any>
        }

        if("sharedEditorButtons" == collectionName){
            return this.sharedEditorButtons as MutableCollection<Any>
        }

        if("sharedListButtons" == collectionName){
            return this.sharedListButtons as MutableCollection<Any>
        }

        if("autocompletes" == collectionName){
            return this.autocompletes as MutableCollection<Any>
        }

        return super.getCollection(collectionName)
    }
}