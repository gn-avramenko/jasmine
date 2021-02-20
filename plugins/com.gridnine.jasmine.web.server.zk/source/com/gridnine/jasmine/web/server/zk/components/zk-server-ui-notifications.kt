/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.web.server.zk.components

import org.zkoss.zk.ui.util.Clients

fun zkShowNotification(message:String, timeout:Int){
    Clients.showNotification(message, "info", null, "bottom_right", timeout)
}