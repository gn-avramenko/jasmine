package com.gridnine.jasmine.web.core.test.rpc

import com.gridnine.jasmine.web.core.model.rest.RestMetaRegistryJS
import com.gridnine.jasmine.web.core.remote.RpcError
import com.gridnine.jasmine.web.core.remote.RpcManager
import com.gridnine.jasmine.web.core.serialization.RestSerializationUtilsJS
import com.gridnine.jasmine.web.core.test.ext.createXMLHttpRequest
import com.gridnine.jasmine.web.core.ui.ErrorHandler
import kotlin.js.Promise

class TestRpcManager(private val baseRestUrl:String) : RpcManager {

    private var cookieHeader:String? = null

    private val templatesCache: MutableMap<String, String> = hashMapOf()

    override fun getTemplate(path: String): Promise<String> {
        return Promise { resolve, reject ->
            val result = templatesCache[path]
            if (result != null) {
                resolve(result)
                return@Promise
            }
            val xhr = com.gridnine.jasmine.web.core.test.ext.require("xmlhttprequest").asDynamic().XMLHttpRequest;
            xhr.open("GET", path)
            if(cookieHeader?.isNotBlank() == true) {
                xhr.setRequestHeader("AUTH_COOKIE", cookieHeader!!)
            }
            xhr.addEventListener("load", {
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
            val xhr = createXMLHttpRequest()
            xhr.withCredentials = true
            xhr.open("POST", "$baseRestUrl/${path}")
            if(cookieHeader?.isNotBlank() == true) {
                xhr.setRequestHeader("AUTH_COOKIE", cookieHeader as String)
            }
            xhr.asDynamic().addEventListener("load") {
                val status = xhr.status
                var obj: Any? = xhr.responseText
                val cookie = xhr.getResponseHeader("Set-Cookie")
                if(cookieHeader == null && cookie!= null){
                    var hCookie = cookie.asDynamic()[0] as String
                    hCookie= hCookie.substring(hCookie.indexOf("=")+1, hCookie.indexOf(";"))
                    cookieHeader = hCookie
                }
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
            }
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



}
