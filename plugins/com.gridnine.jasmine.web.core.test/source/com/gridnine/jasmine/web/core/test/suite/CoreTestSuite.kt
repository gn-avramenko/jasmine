package com.gridnine.jasmine.web.core.test.suite

import com.gridnine.jasmine.web.core.test.ext.describe

class CoreTestSuite{
    fun describeSuite(){
        describe("core-test-suite"){
//            before {
//                CoreActivatorJS().activate()
//            }
            LoadMetadataTest().loadMetadataTest()
        }
    }
}