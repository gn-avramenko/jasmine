window = {
    setTimeout: setTimeout
}

var testModule = require("com.gridnine.jasmine.web.core.test")

new testModule.com.gridnine.jasmine.web.core.test.suite.CoreTestSuite().describeSuite()