/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/
@file:Suppress("unused", "UNCHECKED_CAST")

package com.gridnine.jasmine.web.sandbox.activator

import com.gridnine.jasmine.web.core.application.CoreActivatorJS
import com.gridnine.jasmine.web.core.remote.StandardRpcManager


fun main() {
    val config = hashMapOf<String,Any?>()
    config[StandardRpcManager.BASE_REST_URL_KEY] = "/sandbox/easyui/ui-rest"
    val coreActivator = CoreActivatorJS()
    coreActivator.configure(config)
    coreActivator.activate().then {
        println("started")
    }
}