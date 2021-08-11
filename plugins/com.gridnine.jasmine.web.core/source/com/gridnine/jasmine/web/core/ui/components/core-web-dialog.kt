/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

@file:Suppress("unused")

package com.gridnine.jasmine.web.core.ui.components


interface Dialog<W> where W: WebNode {
    fun close()
    fun getContent():W
    suspend fun simulateClick(buttonIdx:Int):Any?
}


class DialogButtonConfiguration<W> where W: WebNode {
    lateinit var displayName:String
    lateinit var handler:suspend (Dialog<W>)  ->Any?
}

class DialogConfiguration<W>  where W: WebNode {
    var expandToMainFrame = false
    lateinit var title:String
    var width = 300
    val buttons = arrayListOf<DialogButtonConfiguration<W>>()
    fun button(conf: DialogButtonConfiguration<W>.()->Unit){
        val button = DialogButtonConfiguration<W>()
        button.conf()
        buttons.add(button)
    }
    fun cancelButton(){
        val button = DialogButtonConfiguration<W>()
        button.displayName = "Нет"
        button.handler = {
            it.close()
        }
        buttons.add(button)
    }
}
