/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/
@file:Suppress("unused", "UnsafeCastFromDynamic", "UNCHECKED_CAST")

package com.gridnine.jasmine.web.core.ui

import com.gridnine.jasmine.web.core.application.EnvironmentJS

interface ErrorHandler{
    fun showError(msg:String, stacktrace:String)
    companion object{
        fun get() = EnvironmentJS.getPublished(ErrorHandler::class)
    }
}