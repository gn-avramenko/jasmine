/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jasmine.server.core.storage

import com.gridnine.jasmine.server.core.model.domain.BaseAsset

class ExistingObjectData(val version:Int, val revision:Int, val oid:Int?, val content:ByteArray)
data class AssetReadData<A: BaseAsset>(val version:Int, val asset:A)