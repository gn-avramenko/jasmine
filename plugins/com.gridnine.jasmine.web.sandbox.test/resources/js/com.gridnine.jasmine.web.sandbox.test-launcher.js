window = {
    setTimeout: setTimeout
}

var testModule = require("com.gridnine.jasmine.web.sandbox.test")

new testModule.com.gridnine.jasmine.web.sandbox.test.suite.SandboxTestSuite().describeSuite()