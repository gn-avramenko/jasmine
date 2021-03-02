/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.web.server.zk.components

import com.gridnine.jasmine.web.server.components.ServerUiNotificationType
import org.zkoss.zk.ui.util.Clients

fun zkShowNotification(message: String, type: ServerUiNotificationType, timeout: Int){
    Clients.showNotification(message, if(type== ServerUiNotificationType.ERROR) "error" else "info", null, "bottom_right", timeout)
}