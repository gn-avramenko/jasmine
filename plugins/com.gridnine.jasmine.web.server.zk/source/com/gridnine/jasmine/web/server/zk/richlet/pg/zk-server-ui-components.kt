/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.web.server.zk.richlet.pg

import org.zkoss.zk.ui.HtmlBasedComponent

abstract class ZkServerUiComponent:ServerUiComponent{
    var parent:ZkServerUiComponent? = null
    abstract fun createComponent():HtmlBasedComponent
}