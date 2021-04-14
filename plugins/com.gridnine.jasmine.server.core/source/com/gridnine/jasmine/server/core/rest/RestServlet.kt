/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/
package com.gridnine.jasmine.server.core.rest

import com.gridnine.jasmine.common.core.meta.RestMetaRegistry
import com.gridnine.jasmine.common.core.model.BaseRestEntity
import com.gridnine.jasmine.common.core.reflection.ReflectionFactory
import com.gridnine.jasmine.common.core.serialization.SerializationProvider
import java.util.concurrent.ConcurrentHashMap
import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class RestServlet: HttpServlet() {

    private val handlers = ConcurrentHashMap<String, RestHandler<BaseRestEntity,BaseRestEntity>>()

    override fun doPost(req: HttpServletRequest, resp: HttpServletResponse) {
        val restId = req.pathInfo.substring(1)
        val op = RestMetaRegistry.get().operations[restId]?:throw IllegalArgumentException("no description found for id $restId")
        val handler = handlers.getOrPut(op.handler){ReflectionFactory.get().newInstance(op.handler)}
        val request = req.inputStream.use {
            SerializationProvider.get().deserialize(ReflectionFactory.get().getClass<BaseRestEntity>(op.requestEntity), it)
        }
        val result = handler.service(request, RestOperationContext(req, resp))
        resp.contentType = "application/json"
        resp.characterEncoding = "UTF-8"
        resp.status = 200
        resp.outputStream.use {
            SerializationProvider.get().serialize(result, it)
            it.flush()
        }
    }
}