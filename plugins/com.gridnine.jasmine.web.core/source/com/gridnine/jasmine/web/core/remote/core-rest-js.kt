/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/
@file:Suppress("unused", "UnsafeCastFromDynamic", "UNCHECKED_CAST")
package com.gridnine.jasmine.web.core.remote

import com.gridnine.jasmine.web.core.application.EnvironmentJS
import com.gridnine.jasmine.web.core.model.rest.RestMetaRegistryJS
import com.gridnine.jasmine.web.core.serialization.RestSerializationUtilsJS
import com.gridnine.jasmine.web.core.ui.ErrorHandler
import org.w3c.xhr.XMLHttpRequest
import kotlin.js.Promise

class RpcError:Error()

interface RpcManager {

    fun getTemplate(path: String): Promise<String>

    fun postDynamic(path: String, request: String): Promise<dynamic>

    fun <RQ : Any, RP : Any> post(restId: String, request: RQ): Promise<RP>

    companion object {
        fun get() = EnvironmentJS.getPublished(RpcManager::class)
    }
}

class StandardRpcManager(private val baseRestUrl:String) : RpcManager {


    private val templatesCache: MutableMap<String, String> = hashMapOf()

    override fun getTemplate(path: String): Promise<String> {
        return Promise { resolve, reject ->
            val result = templatesCache[path]
            if (result != null) {
                resolve(result)
                return@Promise
            }
            val xhr = XMLHttpRequest()

            xhr.open("GET", path)
            xhr.addEventListener("load", {
                val status = xhr.status
                if (status != 200.toShort()) {
                    //MainFrame.get().showError("Ошибка", "Не удалось загруить шаблон $path", null)
                    reject(RpcError())
                } else {
                    templatesCache[path] =xhr.responseText
                    resolve(xhr.responseText)
                }
            })
            xhr.send()
        }
    }

    override fun postDynamic(path: String, request: String): Promise<dynamic> {
        return Promise<Any?> { resolve, reject ->
            val xhr = XMLHttpRequest()
            xhr.open("POST", "$baseRestUrl/${path}")

            xhr.addEventListener("load", {
                val status = xhr.status
                var obj: Any? = xhr.response
                if (status != 200.toShort()) {
                    if(obj is String){
                        obj = JSON.parse(obj)
                    }
                    ErrorHandler.get().showError(obj.asDynamic().message , obj.asDynamic().stacktrace)
                    reject(RpcError())
                } else {
                    if (obj is String) {
                        obj = JSON.parse(obj)
                    }
                    resolve(obj)
                }
            })
            xhr.send(request)
        }
    }

    override fun <RQ : Any, RP : Any> post(restId: String, request: RQ): Promise<RP> {
        val op = RestMetaRegistryJS.get().operations[restId]?:throw IllegalArgumentException("no description found for $restId")
        val requestStr = RestSerializationUtilsJS.serializeToString(request)
        return postDynamic(restId, requestStr).then { json: dynamic ->
            val response: RP = RestSerializationUtilsJS.deserializeFromJSON(op.responseEntity, json)
            response
        }

    }

    companion object{
     const val BASE_REST_URL_KEY ="baseRestUrl"
    }

}
