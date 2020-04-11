package com.gridnine.jasmine.web.core.utils

import kotlin.js.Date

object DateUtils {
    fun getDiffInMilliseconds(date1:Date, date2:Date):Int{
        return date1.asDynamic()-date2.asDynamic()
    }
}