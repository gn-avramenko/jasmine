/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/
@file:Suppress("unused", "UNCHECKED_CAST")

package com.gridnine.jasmine.web.core.model.ui.widgets

import com.gridnine.jasmine.web.core.model.ui.EnumSelectConfigurationJS
import com.gridnine.jasmine.web.core.model.ui.EnumSelectWidget
import com.gridnine.jasmine.web.core.model.ui.FloatBoxWidget

class ProxyEnumSelectWidget<E:Enum<E>> : EnumSelectWidget<E>() {

    private var value:E? = null

    private var validation:String? = null

    private lateinit var configuration:EnumSelectConfigurationJS<E>

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