/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.web.core.utils

import com.gridnine.jasmine.server.standard.rest.MessageJS
import com.gridnine.jasmine.server.standard.rest.MessageTypeJS
import com.gridnine.jasmine.web.core.ui.UiLibraryAdapter
import com.gridnine.jasmine.web.core.ui.WebComponent
import kotlin.reflect.KClass

object UiUtils {
    fun<W:WebComponent> findParent(child:WebComponent, cls:KClass<W>):W?{
        if(cls.isInstance(child)){
            return child as W
        }
        if(child.getParent() == null){
            return null
        }
        return findParent(child.getParent()!!, cls)
    }

    fun showMessage(message: MessageJS?){
        if(message == null){
            return
        }
        val formatedMessage = when (message.type){
            MessageTypeJS.MESSAGE -> "<div class=\"notification-message\">${message.message}</div>"
            MessageTypeJS.WARNING -> "<div class=\"notification-warning\">${message.message}</div>"
            MessageTypeJS.ERROR -> "<div class=\"notification-error\">${message.message}</div>"
        }
        UiLibraryAdapter.get().showNotification(formatedMessage, 3000)
    }
}