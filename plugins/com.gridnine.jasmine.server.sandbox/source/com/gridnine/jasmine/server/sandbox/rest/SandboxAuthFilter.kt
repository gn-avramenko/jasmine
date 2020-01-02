/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: jasmine
 * This file is auto generated, don't modify it manually
 *****************************************************************/

@file:Suppress("unused", "RemoveRedundantQualifierName", "UNCHECKED_CAST", "MemberVisibilityCanBePrivate", "RemoveEmptyPrimaryConstructor", "FunctionName")

package com.gridnine.jasmine.server.sandbox.rest


import com.gridnine.jasmine.server.core.storage.Storage
import com.gridnine.jasmine.server.core.utils.DesUtil
import com.gridnine.jasmine.server.sandbox.model.domain.SandboxUserAccountIndex
import org.slf4j.LoggerFactory
import javax.servlet.Filter
import javax.servlet.FilterChain
import javax.servlet.ServletRequest
import javax.servlet.ServletResponse
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

private val authInfo = ThreadLocal<AuthInfo>()

class AuthInfo(val loginName: String)

class SandboxAuthFilter : Filter {

    override fun doFilter(request: ServletRequest, response: ServletResponse, chain: FilterChain) {
        val httpRequest = request as HttpServletRequest
        val httpResponse = response as HttpServletResponse
        val restId = httpRequest.pathInfo.substring(1)
        val cookie = httpRequest.cookies?.find { it.name == AUTH_COOKIE }
        if (cookie == null || cookie.value == null) {
            if (restId == "sandbox_auth_checkAuth" || restId == "standard_standard_meta" || restId == "login") {
                chain.doFilter(request, response)
                return
            }
            httpResponse.status = 403
            return
        }
        if (restId == "logout") {
            cookie.maxAge = 0
            response.addCookie(cookie)
            return
        }
        val decoded = DesUtil.decode(cookie.value).split("|")
        try {
            val login = decoded[0]
            val password = decoded[1]
            val userAccount = Storage.get().findUniqueDocument(SandboxUserAccountIndex::class, SandboxUserAccountIndex.login, login)
            if(userAccount == null || password != userAccount.password){
                cookie.maxAge = 0
                response.addCookie(cookie)
                httpResponse.status = 403
                return
            }
            authInfo.set(AuthInfo(login))
            try {
                chain.doFilter(request, response)
            } finally {
                authInfo.remove()
            }
        } catch (e:Exception){
            LoggerFactory.getLogger(javaClass).error("unable to check credentials", e)
            cookie.maxAge = 0
            response.addCookie(cookie)
            httpResponse.status = 403
            return
        }
    }

    companion object {
        const val AUTH_COOKIE = "AUTH_COOKIE"
        fun getAuthInfo(): AuthInfo? {
            return authInfo.get()
        }
    }

}