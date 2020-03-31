package com.gridnine.jasmine.web.core.test.ext

external interface Assert {
    fun equal(actual:Any,expected:Any)
}

external fun require(name:String):Any = definedExternally

external fun describe(name:String, config:()->Unit):Unit = definedExternally

external fun beforeEach(config:()->Any?):Any? = definedExternally

external fun afterEach(config:()->Any?):Any? = definedExternally

external fun before(config:()->Any?):Any? = definedExternally

external fun after(config:()->Any?):Any? = definedExternally

external fun it(name:String, config:()->Any?):Any? = definedExternally