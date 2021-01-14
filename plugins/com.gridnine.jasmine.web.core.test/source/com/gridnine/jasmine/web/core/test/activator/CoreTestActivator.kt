package com.gridnine.jasmine.web.core.test.activator

import com.gridnine.jasmine.web.core.application.EnvironmentJS
import com.gridnine.jasmine.web.core.remote.RpcManager
import com.gridnine.jasmine.web.core.test.rpc.TestRpcManager


class CoreTestActivator {
    fun configure(baseRestUrl:String){
        EnvironmentJS.unpublish(RpcManager::class)
        EnvironmentJS.publish(RpcManager::class, TestRpcManager(baseRestUrl))
    }
}