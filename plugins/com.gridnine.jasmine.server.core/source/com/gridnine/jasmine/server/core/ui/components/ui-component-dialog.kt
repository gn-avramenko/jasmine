/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.server.core.ui.components

import com.gridnine.jasmine.server.core.model.l10n.CoreServerL10nMessagesFactory
import com.gridnine.jasmine.server.core.ui.common.UiNode

interface Dialog<W> where W: UiNode {
    fun close()
    fun getContent():W
}


class DialogButtonConfiguration<W> where W: UiNode {
    lateinit var displayName:String
    lateinit var handler:(Dialog<W>)  ->Unit
}

class DialogConfiguration<W>  where W: UiNode {
    var expandToMainFrame = false
    lateinit var title:String
    val buttons = arrayListOf<DialogButtonConfiguration<W>>()
    fun button(conf: DialogButtonConfiguration<W>.()->Unit){
        val button = DialogButtonConfiguration<W>()
        button.conf()
        buttons.add(button)
    }
    fun cancelButton(){
        val button = DialogButtonConfiguration<W>()
        button.displayName = CoreServerL10nMessagesFactory.No()
        button.handler = {
            it.close()
        }
        buttons.add(button)
    }
}
