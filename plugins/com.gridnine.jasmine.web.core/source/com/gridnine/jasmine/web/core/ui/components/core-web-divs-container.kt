/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.web.core.ui.components


interface WebDivsContainer : WebNode {
    fun addDiv(id:String, content: WebNode)
    fun show(id:String)
    fun removeDiv(id:String)
    fun getDiv(id:String): WebNode?
    fun clear()
}

class WebDivsContainerConfiguration: BaseWebComponentConfiguration()