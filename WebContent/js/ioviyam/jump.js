    function initConfigData() {
        $.post("config.do", {
            "type":"read"
        },function(data) { 
            if(data.toString()=='false'){
                window.location='config.html';
            }else{
                var objects = $.parseJSON(data);
                localStorage.setItem("aetitle",objects['aetitle']);
                localStorage.setItem("hostname",objects['hostname']);
                localStorage.setItem("port",objects['port']);
                localStorage.setItem("wado",objects['wado']);             
            }
        });
    }