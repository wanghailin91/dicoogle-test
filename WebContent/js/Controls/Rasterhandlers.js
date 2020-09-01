var wlmove=function(e){
    e.preventDefault();
    var evt = e.originalEvent.touches[0] || e.originalEvent.changedTouches[0];
    mousePressed=1;	 	
    if(imageLoaded==1){
        mouseLocX = evt.pageX - canvas.offsetLeft;
        mouseLocY = evt.pageY - canvas.offsetTop;
    }		
};
var wlend=function(e){
    e.preventDefault();
    var evt = e.originalEvent.touches[0] || e.originalEvent.changedTouches[0];
    mousePressed=0;	 	
};
var wlstart=function(e){
    e.preventDefault();
    var evt = e.originalEvent.touches[0] || e.originalEvent.changedTouches[0];
    try
    {
        if(imageLoaded==1)
        {		
            mouseLocX1 = evt.pageX - canvas.offsetLeft;
            mouseLocY1 = evt.pageY - canvas.offsetTop;
//            if(mouseLocX1>=0&&mouseLocY1>=0&&mouseLocX1<column&&mouseLocY1<row)
            if(mouseLocX1>=0&&mouseLocY1>=0)
            {
                showHUvalue(mouseLocX1,mouseLocY1);
                if(mousePressed==1){
                    imageLoaded=0;																	
                    var diffX=mouseLocX1-mouseLocX;
                    var diffY=mouseLocY-mouseLocY1;
                    //add 添加普放检查类型窗宽窗位调节幅度
//                    var checkType=localStorage.getItem("selectedmodality");
//                    if(checkType=="CR"||checkType=="DX"){
//                    	diffX=diffX*5;
//                      	diffY=diffY*5;
//                    }
                    wc=parseInt(wc)+diffY;
                    ww=parseInt(ww)+diffX;						
                    showWindowingValue(wc,ww);	
                    lookupObj.setWindowingdata(wc,ww);								
                    counti++;
                    genImage();	
                    mouseLocX=mouseLocX1;			
                    mouseLocY=mouseLocY1;
                    imageLoaded=1;										
                }						
            }			
        }		
    }
    catch(err)
    {	
        console.log("error"+err);
    }     
}; 


var wlwcControl=function(e) {
    $('.cell').css("background-color"," #171616");
    $('#Tbtnwindowlevel').css("background-color","#1e5799");
    e.preventDefault();
    bindwlwc();
    addEventInArray("wlwc");
    closeToolbar();
    return false;
};


function loopchangenxt(){
    touchval=true;
    if(imgindex==imagearray.length-1){
        imgindex=0;
    }else{
        imgindex++;
    }
    imageChange(imgindex);
     if(tagData[0]['frametime']!=undefined){
                intervelnxt=setTimeout(loopchangenxt, loopval);
            }else if(tagData[0]['frametimevector']!=undefined){
               intervelnxt=setTimeout(loopchangenxt,looparrayval[imgindex] );
            }else{
                 intervelnxt=setTimeout(loopchangenxt, 200);
            }
}
function backloop(){
    touchval=true;
    if(0== imgindex){
        imgindex=imagearray.length-1;
    }else{ 
        imgindex--;
    }
    imageChange(imgindex);
    
     
  if(tagData[0]['frametime']!=undefined){
                intervelback=setTimeout(backloop,loopval );
            }else if(tagData[0]['frametimevector']!=undefined){
               intervelback=setTimeout(backloop, looparrayval[imgindex]);
            }else{
                 intervelback=setTimeout(backloop, 200);
            }
  
}

var resettool=function(e) {
    $('.cell').css("background-color"," #171616");
    $('#Tbtnreset').css("background-color","#1e5799");
    e.preventDefault();
    resetCanvas();
    //去掉chrome浏览下，点击重置时跳转到series.html页面的问题
    //事务号：GDSCSWORK-1057 2016.11.22 、
    //wangby
    //closeToolbar();
    return false;
};


var inverthandler=function(e) {
    $('.cell').css("background-color"," #171616");
    $('#Tbtninvert').css("background-color","#1e5799");
    e.preventDefault();
    addEventInArray("invert");
    invertcanvasdata();
    closeToolbar();
    return false;
};

var flipV=function(e) {
    $('.cell').css("background-color"," #171616");
    $('#Tbtnflipver').css("background-color","#1e5799");
    e.preventDefault();
    addEventInArray("flipV");
    flipvertical();
    closeToolbar();
    return false;
};

var flipH=function(e) {
    $('.cell').css("background-color"," #171616");
    $('#Tbtnfliphori').css("background-color","#1e5799");
    e.preventDefault();
    addEventInArray("flipH");
    fliphori();
    closeToolbar();
    return false;
};

var rotateR=function(e) {
    $('.cell').css("background-color"," #171616");
    $('#Tbtnrotater').css("background-color","#1e5799");
    e.preventDefault();
    addEventInArray("rotateR");
    rotateright();
    closeToolbar();
    return false;
};

var rotateL=function(e) {
    $('.cell').css("background-color"," #171616");
    $('#Tbtnrotatel').css("background-color","#1e5799");
    e.preventDefault();
    addEventInArray("rotateL");
    rotateleft();
    closeToolbar();
    return false;
};
textOverlay=function(e) {
    $('.cell').css("background-color"," #171616");
    $('#Tbtntextovly').css("background-color","#1e5799");
    e.preventDefault();
    texthide();
    closeToolbar();
    return false;
};

var lineTool=function(e) {
    $('.cell').css("background-color"," #171616");
    $('#Tbtnline').css("background-color","#1e5799");
    e.preventDefault();
    linetool();
    addEventInArray("line");
    closeToolbar();
    return false;
};

var circleTool=function(e) {
    $('.cell').css("background-color"," #171616");
    $('#Tbtncircle').css("background-color","#1e5799");
    e.preventDefault();
    circle();
    addEventInArray("circle");
    closeToolbar();
    return false;
};
var zoomTool=function(e) {
    $('.cell').css("background-color"," #171616");
    $('#Tbtnzoomdrag').css("background-color","#1e5799");
    e.preventDefault();
    zoomdrag();
    addEventInArray("zoom");
    closeToolbar();
    return false;
};
var OrientationFlag=function overlay(Orientationchange){ 
    if(Orientationchange){
        $("#patNameoly").css("margin-top",0);
        $("#sexoly").css("margin-top", 20);
        $("#modalityoly").css("margin-top", 40);
        $("#studydateoly").css("right", 5);
        $("#studydateoly").css("margin-top", 0);
        $("#thicknewwoly").css("right", 5);
        $("#thicknewwoly").css("margin-top", 20);
        $("#imageoly").css("margin-top", 40);
        $("#imageoly").css("right", 5);
        $("#divtools").css("top", 95);
        $("#divtools").css("right", 3);
        $("#divtools").css("height", 27);
        $("#divtools").css("width", 27);
        $("#studydesceoly").css("bottom", 5);
        $("#wlwcoly").css("bottom", 25);
        $("#seriesdesceoly").css("bottom", 25);
        //  $("#pixelspacingoly").css("bottom", $("#footerview").height()+25);
        $("#resolution").css("right", 5);
        $("#wlwcoly").css("right", 5);
        $("#resolution").css("bottom", 5);
    }
};
var longTouch=function(){
    loopchangenxt();
};
var backLclick=function(){
    backloop();
};
var wlenable=function wlenableControl(data){
    if(data=='1'){
        $("#Tbtnwindowlevel").removeClass('Tbtnwindowlevels').addClass("btntouchwindowlevels");
        $("#Tbtnzoomdrag").removeClass('btntouchzoom').addClass("Tbtnzoomdrags");
        $("#Tbtnline").removeClass('btntouchline').addClass("Tbtnlines");
        $("#Tbtncircle").removeClass('btntouchcircle').addClass("Tbtncircles");
        $("#divtools").removeClass();
        $("#divtools").addClass("btntouchwindowlevels");
    }
    if(data=='3'){
        $("#Tbtnwindowlevel").removeClass('btntouchwindowlevels').addClass("Tbtnwindowlevels");
        $("#Tbtnzoomdrag").removeClass('btntouchzoom').addClass("Tbtnzoomdrags");
        $("#Tbtnline").removeClass('btntouchline').addClass("Tbtnlines");
        $("#Tbtncircle").removeClass('Tbtncircles').addClass("btntouchcircle");
        $("#divtools").removeClass();
        $("#divtools").addClass("btntouchcircle");
    }
    if(data=='2'){
        $("#Tbtnwindowlevel").removeClass('btntouchwindowlevels').addClass("Tbtnwindowlevels");
        $("#Tbtnzoomdrag").removeClass('btntouchzoom').addClass("Tbtnzoomdrags");
        $("#Tbtnline").removeClass('Tbtnlines').addClass("btntouchline");
        $("#Tbtncircle").removeClass('btntouchcircle').addClass("Tbtncircles");
        $("#divtools").removeClass();
        $("#divtools").addClass("btntouchline");
    }
    if(data=='4'){
        $("#Tbtnwindowlevel").removeClass('btntouchwindowlevels').addClass("Tbtnwindowlevels");
        $("#Tbtnzoomdrag").removeClass('Tbtnzoomdrags').addClass("btntouchzoom");
        $("#Tbtnline").removeClass('btntouchline').addClass("Tbtnlines");
        $("#Tbtncircle").removeClass('btntouchcircle').addClass("Tbtncircles");
        $("#divtools").removeClass();
        $("#divtools").addClass("btntouchzoom");
    }
    if(data=='0'){
        $("#Tbtnwindowlevel").removeClass('btntouchwindowlevels').addClass("Tbtnwindowlevels");
        $("#Tbtnzoomdrag").removeClass('btntouchzoom').addClass("Tbtnzoomdrags");
        $("#Tbtnline").removeClass('btntouchline').addClass("Tbtnlines");
        $("#Tbtncircle").removeClass('btntouchcircle').addClass("Tbtncircles");
        $("#divtools").removeClass();
    //$("#divtools").addClass("btntouchzoom");
    }
};
var touchstinvrt=function touchOnFh(e){
    wlenable(0);
    $("#Tbtninvert").removeClass('Tbtninverts').addClass("btntouchinvert");
    // console.log("touch start called:");
    $("#Tbtninvert").addClass("btntouchinvert");
    $("#divtools").removeClass();
   // $("#divtools").addClass("btntouchinvert");
};
var touchedinvrt=function touchOnFh(e){
    $("#Tbtninvert").removeClass('btntouchinvert')
    $("#Tbtninvert").addClass("Tbtninverts");
// alert("touch end called:");
};

//    //Tbtnrotatel
var rotatelts=function touchOnFh(e){
    wlenable(0);
    $("#Tbtnrotatel").removeClass('Tbtnrotaters').addClass("btntouchrotater");
    $("#Tbtnrotatel").addClass("btntouchrotater");
    $("#divtools").removeClass();
   // $("#divtools").addClass("btntouchrotater");
// console.log("touch start called:");
};
var rotatelte=function touchOnFh(e){
    $("#Tbtnrotatel").removeClass('btntouchrotater').addClass("Tbtnrotaters");
    $("#Tbtnrotatel").addClass("Tbtnrotaters");
// console.log("touch start called:");
};
//Tbtntextovly
var textovlyts=function touchOnFh(e){
    wlenable(0);
    $("#Tbtntextovly").removeClass('Tbtntextovlys').addClass("btntouchtextovly");
    $("#Tbtntextovly").addClass("btntouchtextovly");
    $("#divtools").removeClass();
   // $("#divtools").addClass("btntouchtextovly");
    texthide();
//closeToolbar();
// console.log("touch start called:");
};
var textovlyte=function touchOnFh(e){
    $("#Tbtntextovly").removeClass('btntouchtextovly').addClass("Tbtntextovlys");
    $("#Tbtntextovly").addClass("Tbtntextovlys");
         
// console.log("touch start called:");
};
//Tbtnrotater
var rotaterts=function touchOnFh(e){
    wlenable(0);
    $("#Tbtnrotater").removeClass('Tbtnrotaters').addClass("btntouchrotater");
    $("#divtools").removeClass();
   // $("#divtools").addClass("btntouchrotater");
// console.log("touch start called:");
};
var rotaterte=function touchOnFh(e){
    $("#Tbtnrotater").removeClass('btntouchrotater').addClass("Tbtnrotaters");
};
//Tbtnreset
var resetts=function touchOnFh(e){
    wlenable(0);
    $("#Tbtnreset").removeClass('Tbtnresets').addClass("btntouchreset");
    $("#divtools").removeClass();
};
var resette=function touchOnFh(e){
    wlenable(0);
    $("#Tbtnreset").removeClass('btntouchreset').addClass("Tbtnresets");
};
//Tbtnfliphori
var fliphorits=function touchOnFh(e){
    wlenable(0);
    $("#Tbtnfliphori").removeClass('Tbtnfliphoris').addClass("btntouchfliphari");
    $("#divtools").removeClass();
};
var fliphorite=function touchOnFh(e){
    $("#Tbtnfliphori").removeClass('btntouchfliphari').addClass("Tbtnfliphoris");
};
//Tbtnflipver
var flipverts=function touchOnFh(e){
    wlenable(0);
    $("#Tbtnflipver").removeClass('Tbtnflipvers').addClass("btntouchflipver");
    $("#divtools").removeClass();
};
var flipverte=function touchOnFh(e){
    $("#Tbtnflipver").removeClass('btntouchflipver').addClass("Tbtnflipvers");
};
//Tbtnclose
var closets=function touchOnFh(e){
    wlenable(0);
    $("#Tbtnclose").removeClass('Tbtncloses').addClass("btntouchclose");
    $("#divtools").removeClass();
};
var closete=function touchOnFh(e){
    $("#Tbtnclose").removeClass('btntouchclose').addClass("Tbtncloses");
};
 var seletedradios=function getSelectedRadio(){
    var currentDate = new Date();
    var day = currentDate.getDate();
    var month = currentDate.getMonth() + 1;
    var year = currentDate.getFullYear();
    var hour=currentDate.getHours();
    var mnts=currentDate.getMinutes();
    // subtract 3 hours
    var newdate = new Date(currentDate);
    newdate.setDate(newdate.getDate() - 1);
    var lastweek = new Date(newdate);
    var newdateyesterday = new Date(currentDate);
    newdateyesterday.setDate(newdateyesterday.getDate() - 7);
    var lastweekdate = new Date(newdateyesterday);
    var newdatemnth = new Date(currentDate);
    newdatemnth.setDate(newdatemnth.getDate() - 30);
    var lastmonthdate = new Date(newdatemnth);

    var today,yesterday,lastweekdated,lastmonthd,fromdate,todate;
    today=Date.parse($.trim(day+"/"+month+"/"+year)).toString("yyyy/MM/dd");
    var radioV=( $("#radioField input[type='radio']:checked").val());

    if(radioV=='0'){
        $("#studydatetxt").html("Any Date");
        localStorage.setItem("localfromdate", ""); 
        localStorage.setItem("localtodate", "");
        localStorage.setItem("localfromtime", '000000'); 
        localStorage.setItem("localtotime", '235900');
    }else
    if(radioV=='1'){
                                
        $("#studydatetxt").html("Today");
        localStorage.setItem("localfromdate", today); 
        localStorage.setItem("localtodate", today);
        localStorage.setItem("localfromtime", '000000'); 
        localStorage.setItem("localtotime", '235900');
    }else
    if(radioV=='2'){
        $("#studydatetxt").html("Today AM");
        localStorage.setItem("localfromdate", today); 
        localStorage.setItem("localtodate", today);
        localStorage.setItem("localfromtime", '00000'); 
        localStorage.setItem("localtotime", '115900');
    }else
    if(radioV=='3'){
        $("#studydatetxt").html("Today PM");
        localStorage.setItem("localfromdate", today); 
        localStorage.setItem("localtodate", today);
        localStorage.setItem("localfromtime",'000000'); 
        localStorage.setItem("localtotime", '235900');
    }else
    if(radioV=='4'){
        var subbed = new Date(currentDate - 1*60*60*1000);
        var  timedly1hour=Date.parse($.trim(subbed.getHours()+":"+subbed.getMinutes()+":"+subbed.getSeconds())).toString("HH:MM:00");
        $("#studydatetxt").html("Last Hour");
        localStorage.setItem("localfromdate", today); 
        localStorage.setItem("localtodate", today);
        localStorage.setItem("localfromtime", timedly1hour); 
        localStorage.setItem("localtotime", hour+":"+mnts+":"+'00');
    }else
    if(radioV=='5'){
        var subbedfour = new Date(currentDate - 4*60*60*1000);
        var  timedly4hour=Date.parse($.trim( subbedfour.getHours()+":"+subbedfour.getMinutes()+":"+'00')).toString("HH:MM:00");
        $("#studydatetxt").html("Last 4 Hour's");
        localStorage.setItem("localfromdate",today); 
        localStorage.setItem("localtodate",today);
        localStorage.setItem("localfromtime", timedly4hour); 
        localStorage.setItem("localtotime", hour+":"+mnts+":"+'00');
    }else
    if(radioV=='6'){
        yesterday=Date.parse($.trim(lastweek.getDate()+"/"+(lastweek.getMonth()+1)+"/"+lastweek.getFullYear())).toString("yyyy/MM/dd");
        $("#studydatetxt").html("Yesterday");
        localStorage.setItem("localfromdate", today); 
        localStorage.setItem("localtodate", yesterday);
        localStorage.setItem("localfromtime", '000000'); 
        localStorage.setItem("localtotime", '235900');
    }else
    if(radioV=='7'){
        lastweekdated=Date.parse($.trim(lastweekdate.getDate()+"/"+(lastweekdate.getMonth()+1)+"/"+lastweekdate.getFullYear())).toString("yyyy/MM/dd");
        $("#studydatetxt").html("Last Week");
        localStorage.setItem("localfromdate", today); 
        localStorage.setItem("localtodate", lastweekdated);
        localStorage.setItem("localfromtime", '000000'); 
        localStorage.setItem("localtotime", '235900');
    }else
    if(radioV=='8'){
        lastmonthd=Date.parse(lastmonthdate.getDate()+"/"+(lastmonthdate.getMonth()+1)+"/"+lastmonthdate.getFullYear()).toString("yyyy/MM/dd");
        $("#studydatetxt").html("Last Month");
        localStorage.setItem("localfromdate",today); 
        localStorage.setItem("localtodate", lastmonthd);
        localStorage.setItem("localfromtime",'000000'); 
        localStorage.setItem("localtotime",'235900');
    }else
    if(radioV=='custom'){
        var datevalfrom=Date.parse($.trim($("#test_defaulta").val())).toString("yyyy/MM/dd"); 
        var datevalto=Date.parse($.trim($("#test_default1").val())).toString("yyyy/MM/dd");  
        localStorage.setItem("localfromdate", datevalfrom); 
        localStorage.setItem("localtodate",datevalto);
        localStorage.setItem("localfromtime", '000000'); 
        localStorage.setItem("localtotime",'235900');
        $("#studydatetxt").html(datevalfrom+"-"+datevalto);
    }
};
var studylevelquery=function touchOnFh(e){
    if(localStorage.getItem("localfromdate")==null&&localStorage.getItem("localtodate")==null){
        var currentDate = new Date();
        var day = currentDate.getDate();
        var month = currentDate.getMonth() + 1;
        var year = currentDate.getFullYear();
        var today=Date.parse($.trim(day+"/"+month+"/"+year)).toString("yyyy/MM/dd");
        localStorage.setItem("localfromdate", today); 
        localStorage.setItem("localtodate", today);
        localStorage.setItem("localfromtime", '000000'); 
        localStorage.setItem("localtotime", '235900');
    }
    // alert("study clicked");
    localStorage.setItem("localpatid", $.trim($("#patidtxt").val()));
    localStorage.setItem("localpatname", $.trim($("#patnametxt").val()));
    localStorage.setItem("localaccno", $.trim($("#accnotxt").val()));
    var datevalto="";
    if($.trim($("#test_default").val())!=""&&$.trim($("#test_default").val())!=null){
        datevalto=Date.parse($.trim($("#test_default").val())).toString("yyyy/MM/dd"); 
    }
    localStorage.setItem("localbirthdate", datevalto);
    
    window.location="patient.html";  
};
var defaultbtn=function(){
    applyPreset(1);
};
var abdomenbtn=function(){
    applyPreset(2);
};
var lungbtn=function(){
    applyPreset(3);
};
var brainbtn=function(){
    applyPreset(4);
};
var bonebtn=function(){
    applyPreset(5);
};
var headneckbtn=function(){
    applyPreset(6);
};
    
    