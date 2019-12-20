/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: jasmine
 *****************************************************************/
@file:Suppress("unused")
package com.gridnine.jasmine.server.core.web

import com.gridnine.jasmine.server.core.app.Environment
import org.apache.catalina.Lifecycle
import org.apache.catalina.LifecycleException
import org.apache.catalina.LifecycleState
import org.apache.catalina.core.StandardContext
import org.apache.catalina.startup.Tomcat
import org.slf4j.LoggerFactory
import java.io.File
import java.io.InputStream
import java.net.URL
import java.net.URLDecoder
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashSet
import kotlin.collections.LinkedHashSet

class TomcatWebServer
constructor(port: Int) : WebServer {

    private val tomcat: Tomcat = Tomcat()

    init {
        tomcat
                .setBaseDir(File(Environment.tempFolder, "tomcat-workdir").canonicalPath)

        tomcat.setPort(port)
        tomcat.setHostname("localhost")
        tomcat.connector

        val classLoader = TomcatParentClassLoader()
        tomcat.server.parentClassLoader = classLoader
        classLoader.addDelegate(javaClass.classLoader)

        for (app in WebServerConfig.get().getApplications()) {
            val docBase = app.docBase
            val file = File(URLDecoder.decode(docBase.file, "UTF-8"))
            classLoader.addDelegate(app.classLoader)
            val context = tomcat.addWebapp(app.path,
                    file.absolutePath)
            if (context is StandardContext) {
                context.delegate = true
                context.tldValidation = false
                context.xmlValidation = false
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
