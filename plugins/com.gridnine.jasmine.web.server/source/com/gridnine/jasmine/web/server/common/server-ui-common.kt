/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.web.server.common

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

interface ServerUiHasWeight{
    fun getWeight():Double
}

interface ServerUiMainFrameMenuButton:ServerUiRegistryItem<ServerUiMainFrameMenuButton>,ServerUiHasWeight{
    fun getIcon():String?
    fun getDisplayName():String
    override fun getType(): ServerUiRegistryItemType<ServerUiMainFrameMenuButton>{
        return TYPE
    }
    companion object{
        val TYPE = ServerUiRegistryItemType<ServerUiMainFrameMenuButton>("menu-buttons-handlers")
    }
}

object ServerUiCommonUtils{

    private val dateTimeFormatter =  DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
    fun toString(value:Any?):String{
        return when(value){
            is LocalDateTime -> dateTimeFormatter.format(value)
            else -> value?.toString()?:""
        }
    }


}