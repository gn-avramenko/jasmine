/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.web.easyui.activator

import com.gridnine.jasmine.web.core.common.ActivatorJS
import com.gridnine.jasmine.web.core.common.EnvironmentJS
import com.gridnine.jasmine.web.core.common.RegistryJS
import com.gridnine.jasmine.web.core.ui.WebUiLibraryAdapter
import com.gridnine.jasmine.web.easyui.components.EasyUiWebLibraryAdapter


const val pluginId = "com.gridnine.jasmine.web.easyui"

fun main(){
    RegistryJS.get().register(WebEasyUiActivator())
}

class WebEasyUiActivator : ActivatorJS{
    override suspend fun activate() {
        EnvironmentJS.publish(WebUiLibraryAdapter::class, EasyUiWebLibraryAdapter())
        console.log("web easyyui activated")
    }

    override fun getId(): String {
        return pluginId
    }
}

//fun main() {
//    console.log("main started")
////    launch {
////        console.log("coroutine started")
////        try {
////            val res = Promise<String> { result, reject ->
////                window.setTimeout(handler = {
////                    reject(Error("test reject"))
////                }, timeout = 1000)
////            }.await()
////            console.log(res)
////            console.log("coroutine finished")
////        } catch (e:Error){
////            console.log("coroutine finished with exception")
////            console.log(e)
////        }
////    }
//    console.log("main finished")
//}

//fun main() {
//    console.log("main started")
//    window.setTimeout({
//        console.log("starting to load document")
//        Promise<Unit>{resolve, _ ->
//            val script = document.createElement("script");
//            script.asDynamic().onload = {
//                console.log("script loaded")
//                resolve(Unit)
//            }
//            script.asDynamic().src = "/webapp-demo/com.gridnine.jasmine.web.demo.js"
//            document.head!!.appendChild(script);
//        }.then {
//            console.log("promise resolved")
//        }
//    }, 1000)
//}

//suspend fun <T> Promise<T>.await(): T = suspendCoroutine { cont ->
//    then({ cont.resumeWith(Result.success(it))}, {cont.resumeWith(Result.failure(it))})
//}
//
//fun launch(block: suspend () -> Unit) {
//    block.startCoroutine(object : Continuation<Unit> {
//        override val context: CoroutineContext get() = EmptyCoroutineContext
//        override fun resumeWith(result: Result<Unit>) {
//            if(result.isFailure){
//                console.log("Coroutine failed: ${result.exceptionOrNull()}")
//            }
//        }
//    })
//}

//fun testWebCore(){
//    console.log("hello from web core")
//}
//
//object TestWebCoreObject{
//    fun test2(){
//        console.log("hello from web core and test2")
//    }
//}