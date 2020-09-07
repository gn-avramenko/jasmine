/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/
@file:Suppress("unused", "UnsafeCastFromDynamic", "UNCHECKED_CAST")
package com.gridnine.jasmine.web.core.remote

import com.gridnine.jasmine.server.core.model.rest.RestMetaRegistryJS
import com.gridnine.jasmine.web.core.application.EnvironmentJS
import com.gridnine.jasmine.web.core.serialization.JsonSerializerJS
import com.gridnine.jasmine.web.core.ui.UiLibraryAdapter
import com.gridnine.jasmine.web.core.utils.MiscUtilsJS
import org.w3c.xhr.XMLHttpRequest
import kotlin.browser.window
import kotlin.js.Date
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

    private var loaderActive = false

    private val requests = hashMapOf<String, Date>()

    override fun getTemplate(path: String): Promise<String> {
        return Promise { resolve, reject ->
            val result = templatesCache[path]
            if (result != null) {
                resolve(result)
                return@Promise
            }

            val xhr = XMLHttpRequest()
            val uuid = MiscUtilsJS.createUUID()
            requests[uuid] = Date()
            window.setTimeout({
                updateLoaderState()
            }, 300)
            xhr.open("GET", path)
            xhr.addEventListener("load", {
                requests.remove(uuid)
                updateLoaderState()
                val status = xhr.status
                if (status != 200.toShort()) {
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
            val uuid = MiscUtilsJS.createUUID()
            requests[uuid] = Date()
            val xhr = XMLHttpRequest()
            xhr.open("POST", "$baseRestUrl/${path}")
            window.setTimeout({
                updateLoaderState()
            }, 300)
            xhr.addEventListener("load", {
                requests.remove(uuid)
                updateLoaderState()
                val status = xhr.status
                var obj: Any? = xhr.response
                if (status != 200.toShort()) {
                    if(obj is String){
                        obj = JSON.parse(obj)
                    }
                    // ErrorHandler.get().showError(obj.asDynamic().message, obj.asDynamic().stacktrace)
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

    private fun updateLoaderState(){
        if(loaderActive){
            if(requests.isEmpty()){
                loaderActive = false
                if(EnvironmentJS.isPublished(UiLibraryAdapter::class)) {
                    //hide loader
                }
            }
            return
        }
        var delta = 0
        val currentDate = Date()
        requests.forEach {
            val delta2 = MiscUtilsJS.getDiffInMilliseconds(currentDate, it.value)
            if(delta2 > delta) delta = delta2
        }
        if(delta > 200){
            loaderActive = true
            if(EnvironmentJS.isPublished(UiLibraryAdapter::class)) {
                //UiAdapter.get().showLoader()
            }
        }
    }

    override fun <RQ : Any, RP : Any> post(restId: String, request: RQ): Promise<RP> {
        val op = RestMetaRegistryJS.get().operations[restId]?:throw IllegalArgumentException("no description found for $restId")
        val requestStr = JsonSerializerJS.get().serializeToString(request)
        return postDynamic(restId, requestStr).then { json: dynamic ->
            val response: RP = JsonSerializerJS.get().deserialize(op.responseEntity, json)
            response
        }

    }

    companion object{
     const val BASE_REST_URL_KEY ="baseRestUrl"
        const val DELAY =200
    }



}
