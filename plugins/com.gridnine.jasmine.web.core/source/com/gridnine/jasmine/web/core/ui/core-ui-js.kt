/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/
@file:Suppress("unused", "UnsafeCastFromDynamic", "UNCHECKED_CAST")

package com.gridnine.jasmine.web.core.ui

import com.gridnine.jasmine.web.core.application.EnvironmentJS

import kotlin.js.Promise

interface ErrorHandler{
    fun showError(msg:String, stacktrace:String)
    companion object{
        fun get() = EnvironmentJS.getPublished(ErrorHandler::class)
    }
}
interface UiAdapter{
    fun showConfirmDialog(question:String, handler:()->Unit)
    fun showNotification(message:String ,title:String?=null, timeout: Int=3000)
    fun showLoader()
    fun hideLoader()
    fun showWindow(fragment: WebFragment)
    companion object{
        fun get() = EnvironmentJS.getPublished(UiAdapter::class)
    }

}

interface WebFragment{
    fun getHtml():String
    fun decorate()
}

external var debugger: dynamic = definedExternally