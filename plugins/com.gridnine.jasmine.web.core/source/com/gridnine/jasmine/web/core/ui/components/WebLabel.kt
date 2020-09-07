/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.web.core.ui.components

import com.gridnine.jasmine.web.core.ui.WebComponent

interface WebLabel: WebComponent,HasWebClass{
    fun setText(value: String?)
    fun setWidth(value:String)
    fun setHeight(value:String)
}