/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/
@file:Suppress("unused")

package com.gridnine.jasmine.server.core.model.common


import com.gridnine.jasmine.server.core.utils.LocalizationUtils
import com.gridnine.jasmine.server.core.utils.XmlNode
import com.gridnine.jasmine.server.core.utils.XmlUtils
import java.io.File
import java.io.InputStreamReader
import java.lang.IllegalArgumentException
import java.net.URL
import java.util.*


object ParserUtils {

    private val locales = arrayListOf(LocalizationUtils.EN_LOCALE, LocalizationUtils.RU_LOCALE)

    fun <T : BaseIdentityDescription> updateLocalizations(description: T, localizations: Map<String, Map<Locale, String>>) {
        val id = if (description is BaseOwnedIdentityDescription) "${description.fullId}.name" else "${description.id}.name"
        return updateLocalizations(description, localizations, id)
    }

    fun <T : BaseIdentityDescription> updateLocalizations(description: T, localizations: Map<String, Map<Locale, String>>, id:String) {
        localizations[id]?.entries?.forEach {
            description.displayNames[it.key] = it.value
        }
    }

    fun parseMeta(meta: File): Pair<XmlNode, Map<String, Map<Locale, String>>> {
        val content = meta.readBytes()
        val node = XmlUtils.parseXml(content)
        val baseName = meta.name.substring(0,meta.name.lastIndexOf("."))
        val dir = File(meta.parentFile, "l10n")
        val localizations = hashMapOf<String, MutableMap<Locale, String>>()
        for (locale in locales) {
            val file = File(dir, "${baseName}_${locale.language}.properties")
            if(file.exists()) {
                updateLocalization(file.toURI().toURL(), localizations, locale)
            }
        }
        return Pair(node, localizations)
    }




    private fun updateLocalization(url: URL, localizations: HashMap<String, MutableMap<Locale, String>>, locale: Locale) {
        url.openStream().use {
            val props = Properties()
            props.load(InputStreamReader(it, Charsets.UTF_8))
            for ((key1, value1) in props) {
                val key = key1 as String
                var value: MutableMap<Locale, String>? = localizations[key]
                if (value == null) {
                    value = linkedMapOf()
                    localizations[key] = value
                }
                value[locale] = value1 as String
            }
        }
    }


    fun parseMeta(metaQualifiedName: String, classLoader: ClassLoader): Pair<XmlNode, Map<String, Map<Locale, String>>> {

        var baseName = metaQualifiedName.substring(metaQualifiedName.lastIndexOf("/") + 1)
        baseName = baseName.substring(0, baseName.lastIndexOf("."))
        val packageName = metaQualifiedName.substring(0, metaQualifiedName.lastIndexOf("/"))
        val localizations = hashMapOf<String, MutableMap<Locale, String>>()
        for (locale in locales) {
            val url = classLoader.getResource("$packageName/l10n/${baseName}_${locale.language}.properties") ?: continue
            updateLocalization(url, localizations, locale)
        }
        val url = classLoader.getResource(metaQualifiedName)?:throw IllegalArgumentException("cannot access resource $metaQualifiedName")
        val node = XmlUtils.parseXml(url.readBytes())
        return Pair(node, localizations)
    }

    fun updateParameters(node: XmlNode, elm: BaseModelElementDescription) {

        node.attributes.entries.forEach {
            val key = it.key
            if (key.startsWith("x-")) {
                elm.parameters[key] = it.value
            }
        }
    }
    fun getIdAttribute(it: XmlNode): String {
        return it.attributes["id"]?:throw IllegalArgumentException("node ${it.name} has no id attribute")
    }

    fun getCaptionAttribute(it: XmlNode): String {
        return it.attributes["caption"]?:throw IllegalArgumentException("node ${it.name} has no caption attribute")
    }
}