/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/
@file:Suppress("unused")
package com.gridnine.jasmine.server.core.rest

import com.gridnine.jasmine.server.core.model.rest.BaseRestEntity
import com.gridnine.jasmine.server.core.model.rest.RestMetaRegistry
import com.gridnine.jasmine.server.core.serialization.RestSerializationUtils
import com.gridnine.jasmine.server.core.utils.ReflectionUtils
import java.util.concurrent.ConcurrentHashMap
import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import kotlin.reflect.full.primaryConstructor

class RestServlet: HttpServlet() {

    private val handlers = ConcurrentHashMap<String, RestHandler<BaseRestEntity,BaseRestEntity>>()

    override fun doPost(req: HttpServletRequest, resp: HttpServletResponse) {
        val restId = req.pathInfo.substring(1)
        val op = RestMetaRegistry.get().operations[restId]?:throw IllegalArgumentException("no description found for id $restId")
        val handler = handlers.getOrPut(op.handler){ReflectionUtils.getClass<RestHandler<BaseRestEntity,BaseRestEntity>>(op.handler).primaryConstructor!!.call()}
        val request = req.inputStream.use {
            RestSerializationUtils.deserialize(ReflectionUtils.getClass<BaseRestEntity>(op.requestEntity), it.readAllBytes())
        }
        val result = handler.service(request, RestOperationContext(req, resp))
        resp.contentType = "application/json"
        resp.characterEncoding = "UTF-8"
        resp.status = 200
        resp.outputStream.use {
            println(RestSerializationUtils.serializeToString(result))
            it.write(RestSerializationUtils.serializeToByteArray(result))
            it.flush()
        }
    }
}