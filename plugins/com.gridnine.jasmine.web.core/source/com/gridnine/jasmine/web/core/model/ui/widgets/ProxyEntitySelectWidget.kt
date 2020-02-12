/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/
@file:Suppress("unused", "UNCHECKED_CAST")

package com.gridnine.jasmine.web.core.model.ui.widgets

import com.gridnine.jasmine.web.core.model.domain.EntityReferenceJS
import com.gridnine.jasmine.web.core.model.ui.*

class ProxyEntitySelectWidget : EntitySelectWidget() {

    private var value:EntityReferenceJS? = null

    private var validation:String? = null

    private lateinit var configuration:EntitySelectConfigurationJS

    init {

        setData = {
            value = it
        }
        configure = { configuration = it}
        showValidation = {
            validation = it
        }
        getData = {
            value
        }
    }

    fun getValidationValue():String?{
        return validation
    }

    fun getConfiguration() = configuration

}