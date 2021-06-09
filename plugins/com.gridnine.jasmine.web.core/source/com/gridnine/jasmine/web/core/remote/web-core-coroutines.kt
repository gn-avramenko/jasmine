/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.web.core.remote

import com.gridnine.jasmine.common.core.model.XeptionJS
import com.gridnine.jasmine.web.core.common.EnvironmentJS
import kotlin.coroutines.Continuation
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.coroutines.startCoroutine

interface CoroutineExceptionHandler{
    fun handleException(e:Throwable)
    companion object{
        fun get() = EnvironmentJS.getPublished(CoroutineExceptionHandler::class)
    }
}

fun launch(block: suspend () -> Unit){
    launchInternal{
        try {
            block()
        } catch (error:Throwable){
            if(EnvironmentJS.isPublished(CoroutineExceptionHandler::class)){
                CoroutineExceptionHandler.get().handleException(error)
            } else {
                console.log("catched exception", error)
            }
        }
    }
}
private fun launchInternal(block: suspend () -> Unit) {
    block.startCoroutine(object : Continuation<Unit> {
        override val context: CoroutineContext get() = EmptyCoroutineContext
        override fun resumeWith(result: Result<Unit>) {
            if(result.isFailure){
                CoroutineExceptionHandler.get().handleException(result.exceptionOrNull()?:XeptionJS.forDeveloper("Unknown coroutine error"))
            }
        }
    })
}