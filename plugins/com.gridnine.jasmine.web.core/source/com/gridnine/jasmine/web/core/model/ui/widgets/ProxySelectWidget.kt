/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/
@file:Suppress("unused", "UNCHECKED_CAST")

package com.gridnine.jasmine.web.core.model.ui.widgets

import com.gridnine.jasmine.web.core.model.ui.*

class ProxySelectWidget : SelectWidget() {

    private var value:SelectItemJS? = null

    private var validation:String? = null

    private lateinit var configuration:SelectConfigurationJS

    init {

        setData = {
            value = it
        }
        configure = { it -> configuration = it}
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