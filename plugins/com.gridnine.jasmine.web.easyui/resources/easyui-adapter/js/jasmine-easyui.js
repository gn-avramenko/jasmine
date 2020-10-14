function createTestSelect2(id) {
    $("#"+id).select2({
        language: "ru",
        multiple: false,
        allowClear : true,
        placeholder:"Select attr",
        ajax: {
            results : function(data){
                return {
                    results: [
                        {
                            id: "1",
                            text: "1"
                        },
                        {
                            id: "2",
                            text: "2"
                        }
                    ]
                }
            },
            transport: function (params) {
                console.log("test")
                params.success({
                    results:[
                        {
                            id:"1",
                            text:"1"
                        }
                    ]
                })
            }
        }
    });
}