/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/
@file:Suppress("unused", "UNCHECKED_CAST")

package com.gridnine.jasmine.web.core.model.ui.widgets

import com.gridnine.jasmine.web.core.model.domain.EntityReferenceJS
import com.gridnine.jasmine.web.core.model.ui.EntityMultiSelectWidget
import com.gridnine.jasmine.web.core.model.ui.EntitySelectConfigurationJS

class ProxyEntityMultiSelectWidget : EntityMultiSelectWidget() {

    private lateinit var values : List<EntityReferenceJS>

    private var validation:String? = null

    private lateinit var configuration:EntitySelectConfigurationJS

    init {

        readData = {
            values = it
        }
        configure = { config: EntitySelectConfigurationJS -> configuration = config}
        showValidation = {
            validation = it
        }
        writeData = {
            it.clear()
            it.addAll(values)
        }
    }

    fun getValidationValue():String?{
        return validation
    }

    fun getConfiguration() = configuration

}