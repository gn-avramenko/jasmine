/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: jtasks
 *****************************************************************/
package com.gridnine.jasmine.server.standard.model

import com.gridnine.jasmine.common.core.app.PublishableWrapper
import com.gridnine.jasmine.common.core.lock.LockUtils
import com.gridnine.jasmine.common.core.storage.Storage
import com.gridnine.jasmine.common.core.utils.AuthUtils
import com.gridnine.jasmine.common.standard.model.SequenceNumber
import kotlinx.coroutines.async
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.runBlocking

class SequenceNumberGenerator{

    fun incrementAndGet(key:String, startingValue:Int = 1):Int = LockUtils.withLock("sequence-number"){
        runBlocking {
            val job = async (newSingleThreadContext("sequence-number-generator")){
                val record = Storage.get().findUniqueAsset(SequenceNumber::class, SequenceNumber.keyProperty, key, true)?:run{
                    val result = SequenceNumber()
                    result.key = key
                    result.lastNumber = startingValue-1
                    result
                }
                record.lastNumber = record.lastNumber!!+1
                AuthUtils.setCurrentUser("system")
                try {
                    Storage.get().saveAsset(record, createNewVersion = false)
                    record.lastNumber!!
                } finally {
                    AuthUtils.resetCurrentUser()
                }
            }
            job.await()
        }
    }

    companion object{
        private val wrapper = PublishableWrapper(SequenceNumberGenerator::class)
        fun get() = wrapper.get()
    }

}


