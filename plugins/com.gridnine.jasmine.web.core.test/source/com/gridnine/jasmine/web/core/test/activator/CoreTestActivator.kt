package com.gridnine.jasmine.web.core.test.activator

import com.gridnine.jasmine.web.core.application.EnvironmentJS
import com.gridnine.jasmine.web.core.remote.RpcManager
import com.gridnine.jasmine.web.core.test.rpc.TestRpcManager
import com.gridnine.jasmine.web.core.test.ui.TestUiFactory
import com.gridnine.jasmine.web.core.ui.ErrorHandler
import com.gridnine.jasmine.web.core.ui.UiFactory

class CoreTestActivator {
    fun configure(baseRestUrl:String){
        EnvironmentJS.unpublish(RpcManager::class)
        EnvironmentJS.publish(RpcManager::class, TestRpcManager(baseRestUrl))
        EnvironmentJS.publish(ErrorHandler::class, object:ErrorHandler{
            override fun showError(msg: String, stacktrace: String) {
                console.log(msg)
            }
        })
        EnvironmentJS.unpublish(UiFactory::class)
        EnvironmentJS.publish(UiFactory::class, TestUiFactory())
    }


}