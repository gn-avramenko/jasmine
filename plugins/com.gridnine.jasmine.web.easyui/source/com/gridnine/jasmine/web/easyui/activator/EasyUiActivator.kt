/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.web.easyui.activator

import com.gridnine.jasmine.web.core.application.ActivatorJS
import com.gridnine.jasmine.web.core.application.EnvironmentJS
import com.gridnine.jasmine.web.core.ui.UiLibraryAdapter
import com.gridnine.jasmine.web.easyui.adapter.EasyUiLibraryAdapter

class EasyUiActivator:ActivatorJS {
    override fun configure(config: Map<String, Any?>) {
        EnvironmentJS.publish(UiLibraryAdapter::class, EasyUiLibraryAdapter())
    }
}