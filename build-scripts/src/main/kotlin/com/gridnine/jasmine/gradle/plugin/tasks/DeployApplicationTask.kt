package com.gridnine.jasmine.gradle.plugin

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.net.InetAddress
import java.net.Socket
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Paths
import java.security.MessageDigest
import java.util.*
import java.util.concurrent.TimeUnit
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import kotlin.collections.HashMap

open class DeployApplicationTask: DefaultTask() {

    var host = "localhost"
    var port = 21567;
    var appInShellMode = true

    @TaskAction
    fun execute() {
        val distFiles = HashMap<String, String>()
        for (file in project.file("build/dist/lib").listFiles()) {
            if (file.isFile) {
                distFiles[file.name] = calculateCheckSum(file)
            }
        }
        val existingInfo = makeRequest("GET_EXISTING_FILES_INFO").toString(Charsets.UTF_8)
        val existingFiles = HashMap<String, String>()
        existingInfo.split("\r\n").forEach { line ->
            val parts= line.split("|")
            existingFiles[parts[0]] = parts[1]
        }
        val toDelete: MutableSet<String> = HashSet(existingFiles.keys)
        for ((key, value) in existingFiles) {
            val spfFile = distFiles[key]
            if (value == spfFile) {
                toDelete.remove(key)
                distFiles.remove(key)
            }
        }
        if(toDelete.isEmpty() && distFiles.isEmpty()){
            println("installation is up-to-date")
            return
        }
        val initResponse = makeRequest("INIT_LOCAL_REPOSITORY").toString(Charsets.UTF_8)
        if("OK" != initResponse){
            throw Exception(initResponse)
        }
        toDelete.forEach {
            val deleteResponse = makeRequest("DELETE_FILE", it).toString(Charsets.UTF_8)
            if("OK" != deleteResponse){
                throw Exception(deleteResponse)
            }
            println("deleted $it")
        }
        distFiles.keys.forEach {
            val addResponse = makeRequest("ADD_FILE", it, Files.readAllBytes(Paths.get(project.file("build/dist/lib/$it").toURI()))).toString(Charsets.UTF_8)
            if("OK" != addResponse){
                throw Exception(addResponse)
            }
            println("added $it")
        }
        val stopResponse = makeRequest("STOP_APP").toString(Charsets.UTF_8)
        if("OK" != stopResponse){
            throw Exception(stopResponse)
        }
        println("application stopped")
        val updateLibResponse = makeRequest("UPDATE_LIB").toString(Charsets.UTF_8)
        if("OK" != updateLibResponse){
            throw Exception(updateLibResponse)
        }
        println("lib directory updated")
        val startResponse = makeRequest("START_APP").toString(Charsets.UTF_8)
        if("OK" != startResponse){
            throw Exception(startResponse)
        }
        if(!appInShellMode) {
            println("application started")
            return
        }
        val startTime = System.currentTimeMillis()
        while(true){
            if(System.currentTimeMillis() - startTime > TimeUnit.MINUTES.toMillis(1)){
                throw Exception("application not started")
            }
            if(makeRequest("IS_APP_RUNNING").toString(Charsets.UTF_8) == "OK"){
                println("app started")
                break
            }
            Thread.sleep(TimeUnit.SECONDS.toMillis(1))
        }

    }

    private fun makeRequest(command: String, params: String? = null, body: ByteArray? = null):ByteArray{
        val inetAddress = InetAddress.getByName(host)
        Socket(inetAddress, port).use { socket ->
            socket.keepAlive = true
            socket.getOutputStream().use{ out ->
                out.write(byteArrayOf(command.length.toByte(), params?.let { it.length.toByte() } ?: 0.toByte()))
                out.write(command.toByteArray())
                if(params != null){
                    out.write(params.toByteArray())
                }
                if(body != null){
                    out.write(body)
                }
                out.flush()
                socket.shutdownOutput()
                socket.getInputStream().use { inputStream ->
                    val result = ByteArrayOutputStream()
                    val buf = ByteArray(16)
                    var len: Int
                    while (inputStream.read(buf).also { len = it } != -1) {
                        result.write(buf, 0, len)
                    }
                    socket.shutdownInput()
                    return@makeRequest result.toByteArray()
                }
            }
        }
    }

    private fun calculateCheckSum(file: File): String {
        val md = MessageDigest.getInstance("MD5")
        updateDigest(md, file)
        val digest = md.digest()
        val sb = StringBuilder()
        for (b in digest) {
            val st = String.format("%02X", b)
            sb.append(st)
        }
        return sb.toString()
    }

    private fun updateDigest(md: MessageDigest, file: File) {
        if(file.name.endsWith(".jar") || file.name.endsWith(".war")){
            ZipInputStream(file.inputStream()).use { zipStrm ->
                calculateCheckSum(md, zipStrm)
            }
        } else {
            file.inputStream().use {
                updateCheckSum(md, it)
            }
        }
    }

    private fun calculateCheckSum(md: MessageDigest,
                                  strm: ZipInputStream) {
        while (true) {
            var entry: ZipEntry = strm.nextEntry ?: break
            var entryName = entry.name
            md.update(entryName.toByteArray(StandardCharsets.UTF_8))
            if (entryName.endsWith("/")) {
                continue
            }
            entryName = entryName.toLowerCase()
            if (entryName.endsWith(".zip") || entryName.endsWith(".jar") || entryName.endsWith(".war")) {
                calculateCheckSum(md, ZipInputStream(strm))
            } else {
                updateCheckSum(md, strm)
            }
        }
    }

    private fun updateCheckSum(md: MessageDigest,
                               strm: InputStream) {
        val buf = ByteArray(256)
        var len: Int
        while (strm.read(buf).also { len = it } != -1) {
            md.update(buf, 0, len)
        }
    }
}