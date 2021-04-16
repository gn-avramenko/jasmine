/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.server.core.storage.cache.ehcache

import com.gridnine.jasmine.server.core.storage.cache.CachedValue
import org.ehcache.spi.serialization.Serializer
import java.nio.ByteBuffer

@Suppress("UNUSED_PARAMETER")
class CachedValueSerializer(cl: ClassLoader) :Serializer<CachedValue<*>>{

    override fun equals(`object`: CachedValue<*>?, binary: ByteBuffer?): Boolean {
        val bv = read(binary)
        return `object`?.timeStamp == bv.timeStamp
    }

    override fun serialize(`object`: CachedValue<*>?): ByteBuffer {
        TODO("Not yet implemented")
    }

    override fun read(binary: ByteBuffer?): CachedValue<*> {
        TODO("Not yet implemented")
    }

}