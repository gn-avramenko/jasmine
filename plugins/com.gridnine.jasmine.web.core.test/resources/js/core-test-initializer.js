window = {
    setTimeout: setTimeout,
    localStorage: {
        setItem : function(key, item){},
        getItem : function(key){
            return null
        }
    },
    testMode: true
}
var XMLHttpRequest = require("xmlhttprequest").XMLHttpRequest;

createXMLHttpRequest = function(){
     return new XMLHttpRequest();
}
