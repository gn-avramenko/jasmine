var _jasmineIgnoreTableResize = false
function jasmineUpdateTableColumnsWidths(node){
    if(_jasmineIgnoreTableResize){
        return
    }
    _jasmineIgnoreTableResize = true
    try{
        var columns = JSON.parse(node.attr("data-columns"))
    //    console.log("total width: " + node.width())
        _calculateWidth(columns, node.width())
    //    console.log("column widths: " + columns.map(function(elm){
    //        return elm.calculatedWidth
    //    }))
    node.children("div").each(function(){ //div
        $(this).children("table").each(function(){
            $(this).children("colgroup").each(function(){
                var columnsNodes = $(this).children()
                 for(var n=0; n< columns.length; n++){
                    columnsNodes.eq(n).css("width", columns[n].calculatedWidth+"px")
                 }
            })
        })
    })
//        node.find("colgroup").each(function(){
//            var columnsNodes = $(this).children()
//            for(var n=0; n< columns.length; n++){
//                 columnsNodes.eq(n).css("width", columns[n].calculatedWidth+"px")
//            }
//        })
    } finally{
         _jasmineIgnoreTableResize = false
    }
}

function _calculateWidth(columns, totalWidth) {
        if (columns.length == 0) {
            return
        }
        var correctedTotalWidth = totalWidth > 0? totalWidth:  100
        var preferredTotalWidth = columns.map(function(elm){
            return elm.prefWidth? elm.prefWidth: 100
        }).reduce(function(elm1, elm2){
            return elm1+elm2
        }, 0)
        var coeff = correctedTotalWidth /preferredTotalWidth
        var corrected = []
        var newTotalWidth = totalWidth
        columns.forEach(function(elm){
            elm.calculatedWidth = Math.round((elm.prefWidth? elm.prefWidth: 100)*coeff)
            if(elm.minWidth != null && elm.calculatedWidth < elm.minWidth){
                elm.calculatedWidth = elm.minWidth
                corrected.push(elm)
                newTotalWidth = newTotalWidth - elm.calculatedWidth
            } else if (elm.maxWidth != null && elm.calculatedWidth > elm.maxWidth){
                elm.calculatedWidth = elm.maxWidth
                corrected.push(elm)
                newTotalWidth = newTotalWidth - elm.calculatedWidth
            }
        })
        if(corrected.length > 0){
            var rest = []
            columns.forEach(function(elm){
                if(corrected.indexOf(elm) == -1){
                    rest.push(elm)
                }
            })
            _calculateWidth(rest, newTotalWidth)
        }
}
