package com.gridnine.jasmine.web.core.ui.components

interface WebLabel: WebNode{
    fun setText(value: String?)
}

class WebLabelConfiguration:BaseWebComponentConfiguration()