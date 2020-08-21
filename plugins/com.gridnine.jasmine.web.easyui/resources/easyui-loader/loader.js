jasmineLoader = {
    resources : [],
    addResource: function(path){
        this.resources.push(path)
    },
    loadResources: function(){
        $("body").append('<div id="progressBar" class="easyui-progressbar" data-options="value:0" style="width:400px;"></div>')
        $("#progressBar").progressbar()
        let progress = 0
        let resourcesCount = this.resources.length
        let func = $LAB.script(this.resources[0]).wait(function(){
            $("#progressBar").progressbar("setValue", Math.round(100/resourcesCount))
            progress++
        })
        for(let n= 1; n < resourcesCount; n++){
                          func = func.script(this.resources[n]).wait(function(){
                                                      $("#progressBar").progressbar("setValue", Math.round(100*progress/resourcesCount))
                                                      progress++
                                                      if(resourcesCount-1 == n){
                                                                  $("#progressBar").remove()
                                                      }
                                                  })
        }

    }
}