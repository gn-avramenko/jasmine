package com.gridnine.jasmine.web.core.test.suite

import com.gridnine.jasmine.web.core.test.ext.Assert
import com.gridnine.jasmine.web.core.test.ext.it

class LoadMetadataTest {
    fun loadMetadataTest(){
        val assert = com.gridnine.jasmine.web.core.test.ext.require("assert") as Assert
        it("test-load-metadata"){
            assert.equal(2, 2)
        }
    }
}