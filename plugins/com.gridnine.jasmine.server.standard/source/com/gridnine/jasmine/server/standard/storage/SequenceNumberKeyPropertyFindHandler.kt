/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: JTasks
 *****************************************************************/

package com.gridnine.jasmine.server.standard.storage

import com.gridnine.jasmine.common.standard.model.SequenceNumber
import com.gridnine.jasmine.server.core.storage.cache.CacheConfiguration

class SequenceNumberKeyPropertyFindHandler:CacheConfiguration.AssetCachedPropertyHandler<SequenceNumber>(SequenceNumber::class, SequenceNumber.keyProperty.name)