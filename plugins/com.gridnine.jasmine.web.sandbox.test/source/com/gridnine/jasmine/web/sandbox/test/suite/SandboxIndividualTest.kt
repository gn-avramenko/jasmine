package com.gridnine.jasmine.web.sandbox.test.suite

import com.gridnine.jasmine.web.core.test.ext.describe


class SandboxIndividualTest{
    fun describeSuite(){
        describe("sandbox-individual-test") {
            SandboxTestSuite().buildBefore()
            CreateNewAccountTest().createNewAccountTest()
        }
    }
}