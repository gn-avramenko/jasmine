/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.web.server.components

interface ServerUiDialog<W> where W:ServerUiComponent{
    fun close()
    fun getContent():W
}


class ServerUiDialogButtonConfiguration<W> where W:ServerUiComponent{
    lateinit var displayName:String
    lateinit var handler:(ServerUiDialog<W>)  ->Unit
}

class ServerUiDialogConfiguration<W>  where W:ServerUiComponent{
    constructor(conf:ServerUiDialogConfiguration<W>.()->Unit){
        this.conf()
    }
    var expandToMainFrame = false
    lateinit var title:String
    lateinit var editor: W
    val buttons = arrayListOf<ServerUiDialogButtonConfiguration<W>>()
    fun button(conf:ServerUiDialogButtonConfiguration<W>.()->Unit){
        val button = ServerUiDialogButtonConfiguration<W>()
        button.conf()
        buttons.add(button)
    }
    fun cancelButton(){
        val button = ServerUiDialogButtonConfiguration<W>()
        button.displayName = "Отмена"
        button.handler = {
            it.close()
        }
        buttons.add(button)
    }
}
