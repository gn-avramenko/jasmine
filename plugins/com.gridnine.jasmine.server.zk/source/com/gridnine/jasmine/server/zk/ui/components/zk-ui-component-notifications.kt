/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.server.zk.ui.components

import com.gridnine.jasmine.server.core.ui.common.NotificationType
import org.zkoss.zk.ui.util.Clients

fun zkShowNotification(message: String, type: NotificationType, timeout: Int){
    Clients.showNotification(message, if(type== NotificationType.ERROR) "error" else "info", null, "bottom_right", timeout)
}