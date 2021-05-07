/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.web.core.activator

import com.gridnine.jasmine.common.core.meta.*
import com.gridnine.jasmine.common.core.model.ObjectReferenceJS
import com.gridnine.jasmine.common.core.model.PasswordBoxConfigurationJS
import com.gridnine.jasmine.common.core.model.SelectItemJS
import com.gridnine.jasmine.common.core.model.TextBoxConfigurationJS
import com.gridnine.jasmine.web.core.common.ActivatorJS
import com.gridnine.jasmine.web.core.common.EnvironmentJS
import com.gridnine.jasmine.web.core.common.RegistryJS
import com.gridnine.jasmine.web.core.l10n.WebCoreL10nMessagesInitializer
import com.gridnine.jasmine.web.core.reflection.ReflectionFactoryJS
import com.gridnine.jasmine.web.core.remote.*
import com.gridnine.jasmine.web.core.serialization.JsonSerializerJS


const val pluginId = "com.gridnine.jasmine.web.core"

fun main(){
    EnvironmentJS.publish(RegistryJS())
    RegistryJS.get().register(WebCoreActivator())
}

class WebCoreActivator : ActivatorJS{
    override suspend fun activate() {
        EnvironmentJS.publish(CustomMetaRegistryJS())
        EnvironmentJS.publish(DomainMetaRegistryJS())
        EnvironmentJS.publish(L10nMetaRegistryJS())
        EnvironmentJS.publish(RestMetaRegistryJS())
        EnvironmentJS.publish(UiMetaRegistryJS())
        EnvironmentJS.publish(ReflectionFactoryJS())
        EnvironmentJS.publish(JsonSerializerJS())
        EnvironmentJS.publish(WebPluginsHandler())
        ReflectionFactoryJS.get().registerClass(ObjectReferenceJS.qualifiedClassName){ObjectReferenceJS()}
        ReflectionFactoryJS.get().registerQualifiedName(ObjectReferenceJS::class, ObjectReferenceJS.qualifiedClassName)
        ReflectionFactoryJS.get().registerClass(TextBoxConfigurationJS.qualifiedClassName){TextBoxConfigurationJS()}
        ReflectionFactoryJS.get().registerQualifiedName(TextBoxConfigurationJS::class, TextBoxConfigurationJS.qualifiedClassName)
        ReflectionFactoryJS.get().registerClass(PasswordBoxConfigurationJS.qualifiedClassName){PasswordBoxConfigurationJS()}
        ReflectionFactoryJS.get().registerQualifiedName(PasswordBoxConfigurationJS::class, PasswordBoxConfigurationJS.qualifiedClassName)
        ReflectionFactoryJS.get().registerClass(SelectItemJS.qualifiedClassName){SelectItemJS()}
        ReflectionFactoryJS.get().registerQualifiedName(SelectItemJS::class, SelectItemJS.qualifiedClassName)

        if(!EnvironmentJS.isPublished(RpcManager::class)){
            EnvironmentJS.publish(RpcManager::class, StandardRpcManager())
        }
        WebCoreMetaRegistriesUpdater.updateMetaRegistries(pluginId)
        WebCoreL10nMessagesInitializer.initialize()
        console.log("web core activated")
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