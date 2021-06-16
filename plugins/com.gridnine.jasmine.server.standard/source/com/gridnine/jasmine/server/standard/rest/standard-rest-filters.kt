/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/
@file:Suppress("unused")

package com.gridnine.jasmine.server.standard.rest

import com.fasterxml.jackson.core.JsonEncoding
import com.fasterxml.jackson.core.JsonFactory
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter
import com.gridnine.jasmine.common.core.model.Xeption
import com.gridnine.jasmine.common.core.model.XeptionType
import com.gridnine.jasmine.common.core.utils.TextUtils
import org.slf4j.LoggerFactory
import java.io.File
import javax.servlet.*
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse


class KotlinFileDevFilter : Filter {

    override fun init(filterConfig: FilterConfig?) {
        //noops
    }

    override fun doFilter(request: ServletRequest, response: ServletResponse,
                          chain: FilterChain) {
        val httpRequest = request as HttpServletRequest
        val path = httpRequest.requestURI
        if(path != null && path.isNotBlank() && path.endsWith(".kt")){
            val pluginsFolders = arrayListOf<File>()
            pluginsFolders.addAll(collectFiles(File(".")))
            val submodulesFile = File("submodules")
            if(submodulesFile.exists()){
                submodulesFile.listFiles()!!.forEach {sf ->
                    if(sf.exists()){
                        pluginsFolders.addAll(collectFiles(sf))
                    }
                }
            }
            pluginsFolders.forEach{file ->
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

    private fun collectFiles(file: File): List<File> {
        val pluginsFolder = File(file, "plugins")
        return if(pluginsFolder.exists()) pluginsFolder.listFiles()!!.asList() else emptyList()

    }

    override fun destroy() {
        //noops
    }

}

class ExceptionFilter : Filter {

    private val jsonFactory = JsonFactory()

    private val printer = DefaultPrettyPrinter()

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
                val generator = jsonFactory.createGenerator(httpResponse.outputStream, JsonEncoding.UTF8)
                try {
                    generator.prettyPrinter = printer
                    generator.writeStartObject()
                    val xeption = findXeption(e)
                    if (xeption != null) {
                        generator.writeStringField("type", xeption.type.name)
                        generator.writeStringField(
                            "message", when (xeption.type) {
                                XeptionType.FOR_ADMIN -> xeption.adminMessage!!.toString()
                                XeptionType.FOR_END_USER -> xeption.userMessage!!.toString()
                                XeptionType.FOR_DEVELOPER -> xeption.developerMessage
                            }
                        )
                    } else {
                        generator.writeStringField("message", e.cause?.message ?: e.message)
                    }
                    generator.writeStringField("stacktrace", TextUtils.getExceptionStackTrace(e.cause ?: e))
                    generator.writeEndObject()
                    generator.flush()
                } finally {
                    generator.close()
                }
            }
        }

    }

    private fun findXeption(e: Throwable): Xeption? {
        if(e is Xeption){
            return e
        }
        if(e.cause != null){
         return findXeption(e.cause!!)
        }
        return null
    }

    override fun destroy() {
        //noops
    }

}