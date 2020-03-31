/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/
@file:Suppress("unused")

package com.gridnine.jasmine.server.core.rest

import com.google.gson.JsonObject
import com.gridnine.jasmine.server.core.utils.TextUtils
import org.slf4j.LoggerFactory
import java.io.File
import javax.servlet.*
import javax.servlet.http.HttpServletRequest
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

class KotlinFileDevFilter : Filter {

    override fun init(filterConfig: FilterConfig?) {
        //noops
    }

    override fun doFilter(request: ServletRequest, response: ServletResponse,
                          chain: FilterChain) {
        val httpRequest = request as HttpServletRequest
        val path = httpRequest.requestURI
        if(path != null && path.isNotBlank() && path.endsWith(".kt")){
            for(file in File("plugins").listFiles()){
                if(file.isDirectory){
                    val ktFile = File(file, path)
                    if(ktFile.exists()){
                        ktFile.inputStream().use { input ->
                            response.outputStream.use { output ->
                                input.copyTo(output, 256)
                                output.flush()
                                return
                            }
                        }

                    }
                }
            }
        }
        chain.doFilter(request,response)
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