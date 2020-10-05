/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/
@file:Suppress("unused")
package com.gridnine.jasmine.server.core.model.ui

import com.gridnine.jasmine.server.core.model.common.BaseMetaElementDescriptionJS
import com.gridnine.jasmine.server.core.model.domain.AssetDescriptionJS
import com.gridnine.jasmine.server.core.model.domain.DocumentDescriptionJS
import com.gridnine.jasmine.server.core.model.domain.DomainEnumDescriptionJS
import com.gridnine.jasmine.server.core.model.domain.IndexDescriptionJS
import com.gridnine.jasmine.web.core.application.EnvironmentJS

class UiEnumItemDescriptionJS(id:String,val displayName:String) : BaseMetaElementDescriptionJS(id)


class UiEnumDescriptionJS(id:String) : BaseMetaElementDescriptionJS(id){
    val items = linkedMapOf<String, UiEnumItemDescriptionJS>()
}

class UiMetaRegistryJS{
    val enums = linkedMapOf<String, UiEnumDescriptionJS>()

    companion object {
        fun get() = EnvironmentJS.getPublished(UiMetaRegistryJS::class)
    }
}