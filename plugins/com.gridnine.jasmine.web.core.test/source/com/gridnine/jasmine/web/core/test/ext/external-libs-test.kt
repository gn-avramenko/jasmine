package com.gridnine.jasmine.web.core.test.ext

import org.w3c.xhr.XMLHttpRequest

external interface Assert {
    fun equal(actual:Any?,expected:Any?)
    fun ok(value:Any?)
}

external fun require(name:String):Any = definedExternally

external fun describe(name:String, config:()->Unit):Unit = definedExternally

external fun beforeEach(config:()->Any?):Any? = definedExternally

external fun afterEach(config:()->Any?):Any? = definedExternally

external fun before(config:()->Any?):Any? = definedExternally

external fun after(config:()->Any?):Any? = definedExternally

external fun it(name:String, config:()->Any?):Any? = definedExternally

external var debugger: dynamic = definedExternally

external fun createXMLHttpRequest(): XMLHttpRequest = definedExternally