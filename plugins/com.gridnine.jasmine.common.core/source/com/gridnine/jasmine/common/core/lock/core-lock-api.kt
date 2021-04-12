/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.common.core.lock

import com.gridnine.jasmine.common.core.app.Disposable
import com.gridnine.jasmine.common.core.app.PublishableWrapper
import com.gridnine.jasmine.common.core.model.BaseIdentity
import com.gridnine.jasmine.common.core.model.Xeption
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit
import java.util.concurrent.locks.Lock
import java.util.concurrent.locks.ReentrantLock

interface NamedLock : Lock,AutoCloseable{
    fun getName(): String
}

interface LockManager:Disposable{
    fun getLock(name:String): NamedLock

    override fun dispose() {
        wrapper.dispose()
    }
    companion object {
        private val wrapper = PublishableWrapper(LockManager::class)
        fun get() = wrapper.get()
    }
}

class LocalLock(private val _name:String, private val masterLock: ReentrantLock, private val locks:ConcurrentHashMap<String, LocalLock>):ReentrantLock(), NamedLock {

    internal val threadsMap = ConcurrentHashMap<Thread, Boolean>()

    override fun getName() = _name

    override fun close() {
        if(holdCount == 0){
            masterLock.lock()
            try{
                threadsMap.remove(Thread.currentThread())
                if(threadsMap.isEmpty()){
                    locks.remove(_name)
                }
            }finally {
                masterLock.unlock()
            }
        }
    }

}

class StandardLockManager: LockManager {

    private val masterLock = ReentrantLock()

    private val locks = ConcurrentHashMap<String, LocalLock>()

    override fun getLock(name: String): NamedLock {
        masterLock.lock()
        try{
            val result = locks.getOrPut(name){ LocalLock(name, masterLock, locks) }
            result.threadsMap[Thread.currentThread()] = true
            return result
        }finally {
            masterLock.unlock()
        }
    }

}

object LockUtils{
    fun withLock(obj:Any, tryTime:Long, timeUnit:TimeUnit, function:()->Unit){
        val lockName = getLockName(obj)
        LockManager.get().getLock(lockName).use {
            if(!it.tryLock(tryTime, timeUnit)){
                throw Xeption.forDeveloper("unable to get lock $lockName during $tryTime $timeUnit")
            }
            try{
                function.invoke()
            } finally {
                it.unlock()
            }
        }
    }

    fun withLock(obj:Any, function:()->Unit){
        withLock(obj, 1, TimeUnit.MINUTES, function)
    }

    private fun getLockName(obj:Any):String{
        if(obj is BaseIdentity){
            return "${obj::class.qualifiedName}-${obj.uid}"
        }
        return "${obj::class.qualifiedName}-${obj.hashCode()}"
    }


}