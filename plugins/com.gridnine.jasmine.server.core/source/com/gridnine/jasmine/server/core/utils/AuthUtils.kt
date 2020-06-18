/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.server.core.utils

object AuthUtils {
    private val users = ThreadLocal<String>()
    fun setCurrentUser(user:String){
        users.set(user)
    }

    fun getCurrentUser() = users.get()
}