/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

@file:Suppress("unused")

package com.gridnine.jasmine.web.core.ui.components


interface  WebSearchBox:WebNode{
    fun setSearcher(value: suspend (String?) ->Unit)
    fun getValue():String?
    fun setEnabled(value:Boolean)
}

class WebSearchBoxConfiguration:BaseWebComponentConfiguration(){
    var prompt:String? = null
}