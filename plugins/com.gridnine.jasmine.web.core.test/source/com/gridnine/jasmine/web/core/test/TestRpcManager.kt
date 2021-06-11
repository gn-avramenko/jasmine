package com.gridnine.jasmine.web.core.test

import com.gridnine.jasmine.common.core.meta.RestMetaRegistryJS
import com.gridnine.jasmine.web.core.remote.RpcError
import com.gridnine.jasmine.web.core.remote.RpcManager
import com.gridnine.jasmine.web.core.serialization.JsonSerializerJS
import kotlinx.coroutines.await
import kotlin.js.Promise

var testRestUrl = "http://localhost:8080/ui-rest"

class TestRpcManager : RpcManager {

    private var cookieHeader:String? = null

    override suspend fun postDynamic(path: String, request: String): dynamic {
           return Promise<Any?> { resolve, reject ->
            val xhr = createXMLHttpRequest()
            xhr.withCredentials = true
//            console.log("post to \"$testRestUrl/${path}\"")
            xhr.open("POST", "$testRestUrl/${path}")
            if(cookieHeader?.isNotBlank() == true) {
                xhr.setRequestHeader("AUTH_COOKIE", cookieHeader as String)
            }
            xhr.asDynamic().addEventListener("load") {
                val status = xhr.status
                var obj: Any? = xhr.responseText
//                console.log("dumping response")
//                console.log(obj)
                val cookie = xhr.getResponseHeader("Set-Cookie")
                if(cookieHeader == null && cookie!= null){
                    var hCookie = cookie.asDynamic()[0] as String
                    hCookie= hCookie.substring(hCookie.indexOf("=")+1, hCookie.indexOf(";"))
                    cookieHeader = hCookie
                }
                if (status != 200.toShort()) {
//                    console.log("invalid response status ${status}")
                    reject(RpcError("invalid response status $status"))
                } else {
                    if (obj is String) {
                        obj = JSON.parse(obj)
                    }
                    resolve(obj)
                }
            }
            xhr.send(request)
        }.await()
    }

    override suspend fun <RQ : Any, RP : Any> post(restId: String, request: RQ): RP {
        val op = RestMetaRegistryJS.get().operations[restId]?:throw IllegalArgumentException("no description found for $restId")
        val requestStr = JsonSerializerJS.get().serializeToString(request)
        val response = postDynamic(restId, requestStr)
        return JsonSerializerJS.get().deserialize(op.responseEntity, response)
    }
}
