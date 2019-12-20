/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/
@file:Suppress("unused")

package com.gridnine.jasmine.server.core.rest

import com.google.gson.JsonObject
import com.gridnine.jasmine.server.core.utils.TextUtils
import org.slf4j.LoggerFactory
import javax.servlet.*
import javax.servlet.http.HttpServletResponse

class NoCacheFilter : Filter {

    override fun init(filterConfig: FilterConfig?) {
        //noops
    }

    override fun doFilter(request: ServletRequest, response: ServletResponse,
                          chain: FilterChain) {
        val httpResponse = response as HttpServletResponse
        httpResponse.setHeader("Cache-Control", "max-age=0, no-cache, no-store, must-revalidate")
        httpResponse.setHeader("Pragma", "no-cache")
        chain.doFilter(request, response)
    }

    override fun destroy() {
        //noops
    }

}

class ExceptionFilter : Filter {

    override fun init(filterConfig: FilterConfig?) {
        //noops
    }

    override fun doFilter(request: ServletRequest, response: ServletResponse,
                          chain: FilterChain) {
        try {
            chain.doFilter(request, response)
        } catch (e: Exception) {
            LoggerFactory.getLogger(javaClass)
                    .error("unable to service request", e)
            val httpResponse = response as HttpServletResponse
            httpResponse.contentType = "application/json"
            httpResponse.characterEncoding = "UTF-8"
            httpResponse.status = 500
            run {
                val jsonObject = JsonObject()
                jsonObject.addProperty("message", e.cause?.message?:e.message )
                jsonObject.addProperty("stacktrace", TextUtils.getExceptionStackTrace(e.cause?:e))
                httpResponse.writer.print(jsonObject.toString())
            }
        }

    }

    override fun destroy() {
        //noops
    }

}