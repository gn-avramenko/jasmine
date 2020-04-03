package com.gridnine.jasmine.web.sandbox.test.suite

import com.gridnine.jasmine.web.core.test.ext.describe

class SandboxTestSuite{
    fun describeSuite(){
        describe("sandbox-test-suite"){
//            before {
//                val config = hashMapOf<String,Any?>()
//                config[StandardRpcManager.BASE_REST_URL_KEY] = "/sandbox/easyui/ui-rest"
//                val coreActivator = CoreActivatorJS()
//                coreActivator.configure(config)
//                coreActivator.activate()
//            }
            LoadMetadataTest().loadMetadataTest()
        }
    }
}