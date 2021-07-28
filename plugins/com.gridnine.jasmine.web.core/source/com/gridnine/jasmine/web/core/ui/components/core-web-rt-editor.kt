/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.web.core.ui.components


interface WebRichTextEditor : WebNode,HasId{
    fun setContent(content:String?)
    fun getContent():String?
    fun setDisabled(value: Boolean)
}

class WebRichTextEditorConfiguration:BaseWebComponentConfiguration()


