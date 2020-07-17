/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/
@file:Suppress("unused")
package com.gridnine.jasmine.web.core.application

import kotlin.js.Promise

interface ActivatorJS {
    fun configure(config:Map<String,Any?>){}
    fun activate():Promise<Unit>{
        return Promise{ resolve, _ ->
            resolve(Unit)
        }
    }
}