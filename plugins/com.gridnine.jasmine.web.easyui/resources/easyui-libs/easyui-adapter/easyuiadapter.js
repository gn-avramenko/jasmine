function createDatagrid(id, columns, loader,onDblClickRow) {
    var div = $("#"+id);
    div.datagrid({
        fitColumns:true,
        columns:columns,
        loader :loader,
        onDblClickRow:onDblClickRow,
        fit:true
    });
    return div
}



function createTable(id, columns, loader, onClickRow) {
    var div = $("#"+id);
    div.datagrid({
        fitColumns:true,
        columns:columns,
        onClickRow:onClickRow,
        fit:true,
        loader:loader
    });
    return div
}



function createSearchBox(id, prompt, searcher) {
   var div = $("#"+id);
   div.searchbox({
        prompt:prompt,
        searcher :searcher
    });
   return div;

}


function confirm(question, clb){
    $.messager.confirm( {
        ok:'Да',
        cancel:'Нет',
        title: 'Вопрос',
	    msg: question,
	    fn: function(r){
		    if (r){
                clb()
            }
	    }
    })
}

function showMessage(title, message ,timeout){
    $.messager.show({
        title:title,
        showType: 'show',
        timeout: timeout,
        msg:message
    });
}

function showError(title, message, stacktrace){
    if(stacktrace == null){
        $.messager.alert({
                title :title,
                msg:message,
                icon:'error'
            });
        return
    }
    var dialogDiv = $("#error-dialog")
    if(dialogDiv.length == 0){
        var html =
            "<div id=\"error-dialog\"'>" +
                "<div id = \"error-dialog-layout\">" +
                    "<div data-options=\"region:'north',split:false\">" +
                        "<div id = \"error-dialog-message\" style=\"height:100%;width=100%;padding: 5px\"></div>" +
                    "</div>" +
                "<div data-options=\"region:'center',split:false\">" +
                    "<input id = \"error-dialog-stacktrace\" style=\"width:100%;height:300px\">"+
                "</div>" +
                "<div style='padding: 5px' data-options=\"region:'south',split:false\">" +
                    "<a id = \"error-dialog-close\" style=\"float:right\">Закрыть</a>"
                "</div>" +
            "</div>"
        $("body").append(html)
        $("#error-dialog-stacktrace").textbox({
            multiline:true
        })
        $("#error-dialog-close").linkbutton({
            onClick:function(){
                $("#error-dialog").dialog("close")
            }
        })
        dialogDiv = $("#error-dialog")
    }
    $("#error-dialog-message").html(message)
    $("#error-dialog-stacktrace").textbox("setValue", stacktrace)

    dialogDiv.dialog( {title: 'Ошибка',
        width: 700,
        closable: true,
        modal: true,
        iconCls:"icon-error"
    })
}

function openFileJS(accept, resolve){
    var control = $("#open-file-input");
    if (control.length > 0) {
        control.remove()
    }
    if(accept == null) {
        $("body").append("<input id = \"open-file-input\" type=\"file\" style= \"display:none\">");
    } else {
        $("body").append("<input id = \"open-file-input\" accept=\""+accept+"\" type=\"file\" style= \"display:none\">");
    }
    control = $("#open-file-input")
    control.change(function (e) {
        try {
            var files = control.prop('files')
            if (files.length == 0) {
                resolve(null)
                return
            }
            var file = files[0]
            var reader = new FileReader();
            reader.onload = function (e) {
                resolve({
                    name:file.name,
                    type:file.type,
                    content:e.target.result
                });
            }
            reader.readAsDataURL(file);
        } finally {
            control.remove()
        }
    });
    control.trigger('click')
}



$.fn.combobox.defaults.labelAlign="right";
$.fn.combobox.defaults.panelHeight="auto";
$.fn.combobox.defaults.panelMaxHeight=200;

$.fn.tagbox.defaults.labelAlign="right";
$.fn.tagbox.defaults.panelHeight="auto";
$.fn.tagbox.defaults.panelMaxHeight=200;

$.fn.textbox.defaults.labelAlign = "right";
$.fn.passwordbox.defaults.labelAlign = "right";


//var oldSetValue = $.fn.datagrid.defaults.editors.combobox.setValue
//$.fn.datagrid.defaults.editors.combobox.setValue = function(target, value){
//                                                               console.log(value)
//                                                               oldSetValue(target, value);
//                                                           }