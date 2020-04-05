package com.gridnine.jasmine.web.sandbox.test.suite

import com.gridnine.jasmine.web.core.model.domain.DomainMetaRegistryJS
import com.gridnine.jasmine.web.core.test.ext.Assert
import com.gridnine.jasmine.web.core.test.ext.describe
import com.gridnine.jasmine.web.core.test.ext.it

class LoadMetadataTest {
    fun loadMetadataTest() {
        val assert = com.gridnine.jasmine.web.core.test.ext.require("assert") as Assert
        describe("load-metadata-test") {
            it("test-load-metadata") {
                assert.ok(DomainMetaRegistryJS.get().enums.size >0)
            }
        }
    }
}