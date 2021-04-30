/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.web.core.common

class XeptionJS(val type:XeptionTypeJS, val userMessage:String?,  val developerMessage:String?,exception:Exception?) : Exception(getExceptionMessage(userMessage, developerMessage), exception){

    companion object{
        private fun getExceptionMessage(userMessage: String?,  developerMessage: String?): String? {
            if(developerMessage != null){
                return developerMessage
            }
            return null
        }
        fun forDeveloper(message:String, exception: Exception?=null) = XeptionJS(XeptionTypeJS.FOR_DEVELOPER, null, message,  exception)
    }
}




enum class XeptionTypeJS {
    FOR_END_USER,
    FOR_DEVELOPER
}