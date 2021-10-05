/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/
@file:Suppress("unused", "UnsafeCastFromDynamic", "UNCHECKED_CAST")

package com.gridnine.jasmine.web.core.utils

import com.gridnine.jasmine.common.core.meta.DatabasePropertyTypeJS
import com.gridnine.jasmine.common.core.meta.DomainMetaRegistryJS
import com.gridnine.jasmine.common.core.model.BaseIntrospectableObjectJS
import com.gridnine.jasmine.web.core.reflection.ReflectionFactoryJS
import kotlinx.browser.window
import kotlinx.coroutines.await
import kotlin.js.Date
import kotlin.js.Promise
import kotlin.math.round
import kotlin.random.Random

object MiscUtilsJS {
    private val random = Random(100)
    fun createUUID(): String {
        // http://www.ietf.org/rfc/rfc4122.txt
        val s = ArrayList<String>(37)
        val hexDigits = "0123456789abcdef"
        for (i in 0..36) {
            val round = round(random.nextDouble() * 0x10).toInt()
            val substr = hexDigits.substring(round, round + 1)
            s.add(substr)
        }
        s[14] = "4"
        s[8] = "-"
        s[13] = "-"
        s[18] = "-"
        s[23] = "-"
        return s.joinToString(separator = "")
    }

    fun fillWithZeros(value:Int, order:Int = 2):String{
        return "${if(order>2 && value < 100) "0" else ""}${if(value < 10) "0" else ""}$value"
    }

    fun getDiffInMilliseconds(date1: Date, date2: Date):Int{
        return date1.asDynamic()-date2.asDynamic()
    }

    fun isBlank(text:String?):Boolean{
        return text == null || text.isEmpty()
    }

    fun isNotBlank(text:String?):Boolean{
        return !isBlank(text)
    }

    fun toServerClassName(name:String):String{
        return name.substringBeforeLast("JS")
    }

    fun formatDateTime(value:Date?) : String? {
        return value?.let{"${it.getFullYear()}-${fillWithZeros(it.getMonth() + 1)}-${fillWithZeros(it.getDate())} ${fillWithZeros(it.getHours())}:${fillWithZeros(it.getMinutes())}:${fillWithZeros(it.getSeconds())}"}
    }

    private val dateListFormatter = { date: Date? ->
        date?.let { "${it.getFullYear()}-${fillWithZeros(it.getMonth() + 1)}-${fillWithZeros(it.getDate())}" }
    }

    private val dateListTimeFormatter = { date: Date? ->
        date?.let { "${it.getFullYear()}-${fillWithZeros(it.getMonth() + 1)}-${fillWithZeros(it.getDate())} ${fillWithZeros(it.getHours())}:${fillWithZeros(it.getMinutes())}" }
    }

    fun createListFormatter(type: DatabasePropertyTypeJS?) =
            { value: Any?, _: BaseIntrospectableObjectJS, _: Int ->
                lateinit var displayName: String
                displayName = if (value is Enum<*>) {
                    val qualifiedName = ReflectionFactoryJS.get().getQualifiedClassName(value::class)
                    val enumDescr = DomainMetaRegistryJS.get().enums[qualifiedName]
                    if (enumDescr != null) {
                        enumDescr.items[value.name]!!.displayName
                    } else {
                        value.name
                    }?:""

                } else if (value is Boolean) {
                    if (value) "Да" else "Нет"
                } else if (value?.asDynamic()?.caption != null) {
                    value.asDynamic()?.caption as String
                } else if (type == DatabasePropertyTypeJS.LOCAL_DATE) {
                    dateListFormatter(value as Date?) ?: "???"
                } else if (type == DatabasePropertyTypeJS.LOCAL_DATE_TIME) {
                    dateListTimeFormatter(value as Date?) ?: "???"
                } else {
                    value?.toString() ?: ""
                }
                if (displayName.length > 100) {
                    displayName = "<span title=\"$displayName\" class=\"easyui-tooltip\">${displayName.substring(0, 50)} ...</span>"
                }
                displayName
            }

    fun downloadFile(suggestedFileName:String, contentType:ContentTypeJS, base64EncodedContent:String){
        val contentTypeStr = when(contentType){
            ContentTypeJS.EXCEL -> "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
        }
        var a = window.document.createElement("a").asDynamic();
        a.href = "data:$contentTypeStr;base64,$base64EncodedContent"
        a.download = suggestedFileName;
        a.click();
    }
    suspend fun uploadFile(accept:String? = null) : FileData?{
        return Promise<FileData?>{resolve, _ ->
            var input = window.document.createElement("input").asDynamic();
            input.accept == accept
            input.type = "file"
            input.click()
            input.addEventListener("change") {
                if(input.files.length == 0){
                    resolve(null)
                } else {
                    val file= input.files[0]
                    val reader = eval("new FileReader()")
                    reader.readAsDataURL(file)
                    reader.onloadend = {
                        resolve(FileData(file.name, (reader.result as String).substringAfter("base64,"), file.type))
                    }
                    reader.onabort ={
                        resolve(null)
                    }
                    reader.onerror ={
                        resolve(null)
                    }
                }

            }
        }.await()

    }
}

data class FileData(val fileName:String, val content:String, val contentType:String)

enum class ContentTypeJS{
    EXCEL
}