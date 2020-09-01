$(document).bind("mobileinit", function () {
    $.mobile.defaultPageTransition = "slidefade";
     
});
$(document).ready(function() {
    $.mobile.showPageLoadingMsg();

    var seriesLoad;
    var patientid =localStorage.getItem("patiddata");
    var studyid= localStorage.getItem("studyid");
    if(studyid==undefined||studyid==null){
        window.location='home.html';
    }
    var patientArray = patientid.split(",");
    //如果是多个纪录则显示返回按钮，否则隐藏返回按钮
    //添加是否显示返回按钮
    var isBack= localStorage.getItem("isBack");
    if(isBack=="true"){
    	$("#btnback").click(function() {
            $(this).addClass( $.mobile.activeBtnClass);
            window.location='jump.html';
            
        });
    }else{
    	$("#btnback").css("display","none");
    }
    localStorage.removeItem("isBack");
    
    try{
        $.urlParam = function(name){ 
            var results = new RegExp('[\\?&]' + name + '=([^&#]*)').exec(window.location.href);
            return results[1] || 0 ;
        };
    }catch(err){
        alert(err);
    }
    $('#headingseries').html(patientArray[1]);
    
    var arraylistvale;
    //添加session超时判断后，配置读取
//    if(hostname==null||aetitle==null
//    		||port==null||wado==null){
//    	$.post("config.do", {
//            "type":"read"
//        },function(data) { 
//            if(data.toString()=='false'){
//                window.location='config.html';
//            }else{
//                var objects = $.parseJSON(data);
//                localStorage.setItem("aetitle",objects['aetitle']);
//                localStorage.setItem("hostname",objects['hostname']);
//                localStorage.setItem("port",objects['port']);
//                localStorage.setItem("wado",objects['wado']);             
//            }
//        });
//    }
    //查询serial
    $.post("series.do", {
        "PatientID":patientArray[0],
        "StudyID":studyid,
        "ae":localStorage.getItem("aetitle"),
        "host":localStorage.getItem("hostname"),
        "port":localStorage.getItem("port"),
        "wado":localStorage.getItem("wado")
    },function(data) {  
        obj = $.parseJSON(data);
        localStorage.setItem("json", data);
        seriesList=new Array();
        arraylistvale=new Array();
        seriesLoad=new Array();
        $('#serieslist').html('');
        var i=0;
        $.each(obj, function(i, row) {
            seriesList.push(row);
            viewerarray=this['url'];
            var outofnull=new Array();
            for(var i=0;i<row['url'].length;i++){
                if(row['url'][i]!=null){
                    outofnull.push(row['url'][i]);
                }
            }
            arraylistvale.push(outofnull);
       
            
            if(row['url'][0]==null){
                valuurl=row['url'];
            }else{
                valuurl=row['url'][0];
            }
            
            seriesLoad.push(row['url']);
            $('#serieslist').append('<li id=serieslistvalue'+i +' data-theme="a"  ibodypart='+row['bodyPart']+','+row['seriesDesc']+' >'
                +'<a   href="" data-transition="slide">'
                +'<img  id="imgstudy" style="float:left;"  src='+outofnull[0]+'&frameNumber=1&rows=82&coloums=82'+'>'
                +
                //                    '<label style="font: bold 14px courier !important;">'+ "Series no:"+this['seriesNumber']+'</label>'+'<br>'+
                '<label style="float:left;font: bold 15px helvetica;color:#AAAAAA;">'+ ""+row['seriesDesc']+'</label>'+'<br>'+
                '<label style="float:left;font:  14px helvetica;color: #AAAAAA">'+row['modality']+'</label>'+'<br>'+
                //                    '<label style="font: bold 14px courier !important;">'+ "Body Part:"+this['bodyPart']+'</label>'+'<br>'+
                '<label style="float:left;font: normal 14px helvetica ;color: #AAAAAA">'+ "("+row['totalInstances']+" "+"images)"+'</label>'+
                
                '</a>'+'</li>').listview('refresh'); 
        });
        i++;
        $('#serieslist').children('li').on('click', function () {
            $(this).addClass( $.mobile.activeBtnClass );
            var patient=$(this).index();
            localStorage.setItem("ibodypart", seriesList[patient]['bodyPart']+','+seriesList[patient]['seriesDesc']+','+seriesList[patient]['totalInstances']+','+patientArray[2]);
            seletedindex=$(this).index();
            localStorage.setItem("patid", seriesList[patient]['patientId']);
            localStorage.setItem("studyid", seriesList[patient]['studyUID']);
            localStorage.setItem("seriesid",  seriesList[patient]['seriesUID']);
            localStorage.setItem("seletedseries", seletedindex);
            localStorage.setItem("selectedmodality", seriesList[patient]['modality']);
            var arry=new Array();
            //arry=seriesList[patient]['url'];
            arry=arraylistvale[patient];

            localStorage.setItem("serieLoadArray",arry);
            window.location="viewer.html";
        });
        $.mobile.hidePageLoadingMsg();
    });


});