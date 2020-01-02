/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/
@file:Suppress("unused", "UNCHECKED_CAST")

package com.gridnine.jasmine.web.easyui

import com.gridnine.jasmine.web.core.application.ActivatorJS
import com.gridnine.jasmine.web.core.application.EnvironmentJS
import com.gridnine.jasmine.web.core.ui.ErrorHandler
import com.gridnine.jasmine.web.core.ui.UiFactory

class EasyUiActivator : ActivatorJS{

    override fun configure(config: Map<String, Any?>) {
        EnvironmentJS.publish(ErrorHandler::class, object:ErrorHandler{
            override fun showError(msg: String, stacktrace: String) {
                showError(null, msg, stacktrace)
            }
        })
        EnvironmentJS.publish(UiFactory::class, EasyUiFactory())
    }
}