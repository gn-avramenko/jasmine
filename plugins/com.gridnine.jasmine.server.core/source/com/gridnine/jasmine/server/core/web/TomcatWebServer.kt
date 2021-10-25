/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: jasmine
 *****************************************************************/
@file:Suppress("unused")
package com.gridnine.jasmine.server.core.web

import com.gridnine.jasmine.common.core.app.ConfigurationProvider
import com.gridnine.jasmine.common.core.app.Environment
import org.apache.catalina.Lifecycle
import org.apache.catalina.LifecycleException
import org.apache.catalina.LifecycleState
import org.apache.catalina.core.StandardContext
import org.apache.catalina.startup.Tomcat
import org.apache.tomcat.JarScanFilter
import org.apache.tomcat.util.descriptor.web.FilterDef
import org.apache.tomcat.util.descriptor.web.FilterMap
import org.slf4j.LoggerFactory
import java.io.File
import java.io.InputStream
import java.net.URL
import java.net.URLDecoder
import java.nio.file.Path
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashSet
import kotlin.collections.LinkedHashSet

class TomcatWebServer : WebServer {

    private val tomcat: Tomcat = Tomcat()

    init {
        val webappDir = File(Environment.tempFolder, "tomcat-workdir/webapps")
        if(!webappDir.exists()) webappDir.mkdirs()
        tomcat
                .setBaseDir(File(Environment.tempFolder, "tomcat-workdir").canonicalPath)

        val portStr = ConfigurationProvider.get().getProperty("tomcat.port")
        var port = 8080
        if (portStr != null && portStr.isNotBlank()) {
            port = Integer.parseInt(portStr.trim())
        }
        tomcat.setPort(port)
        tomcat.setHostname("localhost")

        tomcat.setPort(port)
        tomcat.setHostname("localhost")
        tomcat.connector
        val keystoreFile = ConfigurationProvider.get().getProperty("tomcat.keystore.file")
        val keystorePassword = ConfigurationProvider.get().getProperty("tomcat.keystore.password")
        if(keystoreFile != null && keystorePassword != null){
            tomcat.connector.scheme = "https"
            tomcat.connector.secure = true
            tomcat.connector.setAttribute("SSLEnabled", "true")
            tomcat.connector.setAttribute("keystoreFile", File(keystoreFile).absolutePath)
            tomcat.connector.setAttribute("keystorePass", keystorePassword)
            tomcat.connector.setAttribute("clientAuth", false)
            tomcat.connector.setAttribute("sslProtocol", "TLS")
        }
        val compression  = "on" == ConfigurationProvider.get().getProperty("tomcat.compression")
        if(compression) {
            tomcat.connector.setAttribute("compression", "force")
            tomcat.connector.setAttribute("useSendfile", "false")
        }
        val classLoader = TomcatParentClassLoader()
        tomcat.server.parentClassLoader = classLoader
        classLoader.addDelegate(javaClass.classLoader)

        for (app in WebServerConfig.get().getApplications()) {
            classLoader.addDelegate(app.classLoader)
            val docBase = app.docBase
            val file = File(URLDecoder.decode(docBase.file, "UTF-8"))
            val context = tomcat.addWebapp(app.path, file.absolutePath)
            if (context is StandardContext) {

                context.delegate = true
                context.tldValidation = false
                context.xmlValidation = false
                context.clearReferencesObjectStreamClassCaches = false
                context.clearReferencesRmiTargets = false
                context.clearReferencesThreadLocals = false
                context.jarScanner.jarScanFilter = JarScanFilter { _, _ ->
                    false }

            }
            WebServerConfig.get().globalFilters.forEach{
                val filterDef = FilterDef()
                filterDef.filterClass =it.cls.qualifiedName
                filterDef.filterName = it.name
                val filterMap = FilterMap()
                filterMap.addURLPattern("/*")
                filterMap.filterName = it.name
                context.addFilterDef(filterDef)
                context.addFilterMap(filterMap)
            }

            if (context.state !=  LifecycleState.STARTED) {
                (context as Lifecycle).start()
            }
        }
        tomcat.init()
        tomcat.start()
    }

    override fun dispose() {
        try {
            tomcat.stop()
            tomcat.destroy()
        } catch (e: LifecycleException) {
            LoggerFactory.getLogger(javaClass).error("unable to stop tomcat",
                    e)
        }

    }

    internal class TomcatParentClassLoader : ClassLoader() {

        private val delegates = LinkedHashSet<ClassLoader>()

        fun addDelegate(cl: ClassLoader) {
            delegates.add(cl)
        }

        @Throws(ClassNotFoundException::class)
        override fun loadClass(name: String): Class<*> {
            for (cl in delegates) {
                try {
                    val cls = cl.loadClass(name)
                    if (cls != null) {
                        return cls
                    }
                } catch (ex: NoClassDefFoundError) {
                    // noops
                }
                catch (ex: ClassNotFoundException) {
                    // noops
                }

            }
            return super.loadClass(name)
        }

        override fun getResourceAsStream(name: String): InputStream? {
            var resourceName: String? = name
            if (resourceName != null && resourceName.startsWith("/")) {
                resourceName = resourceName.substring(1)
            }
            for (cl in delegates) {
                val `is` = cl.getResourceAsStream(resourceName!!)
                if (`is` != null) {
                    return `is`
                }
            }
            return super.getResourceAsStream(name)
        }

        override fun getResource(name: String): URL? {
            var resourceName: String? = name
            if (resourceName != null && resourceName.startsWith("/")) {
                resourceName = resourceName.substring(1)
            }
            for (cl in delegates) {
                val url = cl.getResource(resourceName!!)
                if (url != null) {
                    return url
                }
            }
            return super.getResource(name)
        }


        override fun getResources(name: String): Enumeration<URL> {
            val result = ArrayList<URL>()
            val hs = HashSet<String>()
            for (cl in delegates) {
                val en = cl.getResources(name)
                while (en.hasMoreElements()) {
                    val elm = en.nextElement()
                    val elmStr = elm.toString()
                    if (!hs.contains(elmStr)) {
                        result.add(elm)
                        hs.add(elmStr)
                    }
                }
            }
            return object : Enumeration<URL> {

                var size = result.size

                var idx = 0

                override fun hasMoreElements(): Boolean {
                    return idx < size
                }

                override fun nextElement(): URL {
                    idx++
                    return result[idx - 1]
                }
            }
        }

    }

}
