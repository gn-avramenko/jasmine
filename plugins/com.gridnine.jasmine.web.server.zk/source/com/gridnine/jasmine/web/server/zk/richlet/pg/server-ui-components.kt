/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.web.server.zk.richlet.pg

interface ServerUiComponent{
    fun getParent():ServerUiComponent?
}


enum class ServerUiComponentHorizontalAlignment {
    LEFT,
    RIGHT,
    CENTER
}