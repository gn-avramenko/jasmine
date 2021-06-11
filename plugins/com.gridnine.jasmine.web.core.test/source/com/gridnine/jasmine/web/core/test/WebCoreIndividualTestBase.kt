/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.web.core.test

import com.gridnine.jasmine.web.core.remote.launchAndHandleException
import kotlin.js.Promise

abstract class WebCoreIndividualTestBase {

    protected lateinit var assert: Assert

    protected fun test(testName:String, block: suspend () ->Unit){
        assert  = require("assert") as Assert
        it(testName) {
            Promise<Unit>{resolve, reject ->
                launchAndHandleException({
                    block()
                    resolve(Unit)
                }, reject)
            }
        }
    }

}