/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.server.standard.rest

object StandardRestHelper {
    fun createMessage(type:MessageType, message:String):Message{
        val result = Message()
        result.type = type
        result.message = message
        return result
    }
}