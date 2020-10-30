/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.web.easyui.adapter

import com.gridnine.jasmine.web.core.ui.WebComponent
import kotlin.reflect.KClass

object EasyUiUtils {
    fun getIconClass(iconName:String?) = if(iconName != null) "icon_${iconName.substringBefore(":")}_${iconName.substringAfterLast(":")}" else null
}