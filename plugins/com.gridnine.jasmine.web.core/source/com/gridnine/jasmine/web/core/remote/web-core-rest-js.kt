/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/
@file:Suppress("unused", "UnsafeCastFromDynamic", "UNCHECKED_CAST")
package com.gridnine.jasmine.web.core.remote

import com.gridnine.jasmine.common.core.meta.RestMetaRegistryJS
import com.gridnine.jasmine.web.core.common.EnvironmentJS
import com.gridnine.jasmine.web.core.l10n.WebCoreL10nMessages
import com.gridnine.jasmine.web.core.serialization.JsonSerializerJS
import com.gridnine.jasmine.web.core.ui.WebUiLibraryAdapter
import com.gridnine.jasmine.web.core.utils.MiscUtilsJS
import kotlinx.browser.window
import kotlinx.coroutines.await
import org.w3c.dom.events.Event
import org.w3c.dom.events.EventListener
import org.w3c.xhr.XMLHttpRequest
import kotlin.js.Date
import kotlin.js.Promise

class RpcError(val reason:dynamic):Error()

interface RpcManager {

    suspend fun postDynamic(path: String, request: String): dynamic

    suspend fun <RQ : Any, RP : Any> post(restId: String, request: RQ): RP

    companion object {
        fun get() = EnvironmentJS.getPublished(RpcManager::class)
    }
}

class StandardRpcManager : RpcManager {

    private var loaderActive = false

    private val requests = hashMapOf<String, Date>()

    override suspend fun postDynamic(path: String, request: String): dynamic {
        return Promise<Any?> { resolve, reject ->
            val uuid = MiscUtilsJS.createUUID()
            requests[uuid] = Date()
            val xhr = XMLHttpRequest()
            xhr.open("POST", "${EnvironmentJS.restBaseUrl}/${path}")
            window.setTimeout({
                updateLoaderState()
            }, 300)
            xhr.addEventListener("load", object:EventListener{

                override fun handleEvent(event: Event) {
                    requests.remove(uuid)
                    updateLoaderState()
                    val status = xhr.status
                    var obj: Any? = xhr.response
                    if (status != 200.toShort()) {
                        if (obj is String) {
                            obj = try {
                                JSON.parse(obj)
                            } catch (e:Throwable){
                                object {
                                    val message =  WebCoreL10nMessages.Unknown_error
                                    val stacktrace = obj
                                }
                            }
                        }
                        reject(RpcError(obj))
                    } else {
                        if (obj is String) {
                            try {
                                obj = JSON.parse(obj)
                            } catch (e:Throwable){
                                reject(RpcError(object {
                                    val message =  WebCoreL10nMessages.Unknown_error
                                    val stacktrace = obj
                                }))
                                return
                            }
                        }
                        resolve(obj)
                    }
                }
            }, false)
            xhr.addEventListener("error", object:EventListener{
                override fun handleEvent(event: Event) {
                    requests.remove(uuid)
                    updateLoaderState()
                    reject(RpcError(object{
                        val message = WebCoreL10nMessages.Unknown_error
                    }))
                }
            }, false)
            xhr.send(request)
        }.await()

    }

    private fun updateLoaderState(){
        if(loaderActive){
            if(requests.isEmpty()){
                loaderActive = false
                if(EnvironmentJS.isPublished(WebUiLibraryAdapter::class)) {
                    WebUiLibraryAdapter.get().hideLoader()
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
            if(EnvironmentJS.isPublished(WebUiLibraryAdapter::class)) {
                WebUiLibraryAdapter.get().showLoader()
            }
        }
    }

    override suspend fun <RQ : Any, RP : Any> post(restId: String, request: RQ): RP {
        val op = RestMetaRegistryJS.get().operations[restId]?:throw IllegalArgumentException("no description found for $restId")
        val requestStr = JsonSerializerJS.get().serializeToString(request)
        val rd = postDynamic(restId, requestStr)
        return JsonSerializerJS.get().deserialize(op.responseEntity, rd)
    }

    companion object{
        const val DELAY =200
    }

}
