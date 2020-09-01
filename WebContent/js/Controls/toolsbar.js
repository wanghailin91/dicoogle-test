/* 
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
$(document).ready(function(){ 
    $.mobile.showPageLoadingMsg();
    
});
function rotate(direction){
    // alert(",,,,,"+direction);
    var jcanvas = document.getElementById('imgcanvas');
    var iNewWidth = jcanvas.width;
    var iNewHeight = jcanvas.height;
    var cpCanvas = document.createElement("canvas");
    cpCanvas.width = iNewWidth;
    cpCanvas.height = iNewHeight;
    cpCanvas.getContext('2d').drawImage(jcanvas, 0, 0);
    jcanvas.width = iNewHeight;
    jcanvas.height= iNewWidth;
    var oCtx = jcanvas.getContext("2d");
    oCtx.clearRect(0, 0, iNewWidth, iNewHeight);
    oCtx.save();
    if(direction=='left'){
        oCtx.translate(0, iNewHeight);
        oCtx.rotate(-90/180 * Math.PI);
        oCtx.drawImage(cpCanvas, 0, 0);
    }else{
        oCtx.rotate(90/180 * Math.PI);
        oCtx.drawImage(cpCanvas, 0, -iNewHeight);    
    }
    oCtx.restore();
}

function invert() {
    var jcanvas = document.getElementById('imgcanvas');
    var iNewWidth = jcanvas.width;
    var iNewHeight = jcanvas.height;
    var oCtx = jcanvas.getContext("2d");
    var dataSrc = oCtx.getImageData(0,0,iNewWidth,iNewHeight);
    var dataDst = oCtx.getImageData(0,0,iNewWidth,iNewHeight);
    var aDataSrc = dataSrc.data;
    var aDataDst = dataDst.data;
    var y = iNewHeight;
    do {
        var iOffsetY = (y-1)*iNewWidth*4;
        var x = iNewWidth;
        do {
            var iOffset = iOffsetY + (x-1)*4;
            aDataDst[iOffset]   = 255 - aDataSrc[iOffset];
            aDataDst[iOffset+1] = 255 - aDataSrc[iOffset+1];
            aDataDst[iOffset+2] = 255 - aDataSrc[iOffset+2];
            aDataDst[iOffset+3] = aDataSrc[iOffset+3];
        } while (--x);
    } while (--y);
    oCtx.putImageData(dataDst,0,0);
}
function flip(direction) {
    var jcanvas = document.getElementById('imgcanvas');
    var iNewWidth=jcanvas.width;
    var iNewHeight=jcanvas.height;
    var cpCanvas=document.createElement("canvas");
    cpCanvas.width=iNewWidth;
    cpCanvas.height=iNewHeight;
    cpCanvas.getContext("2d").drawImage(jcanvas,0,0);
    var oCtx=jcanvas.getContext("2d");
    oCtx.clearRect(0,0,iNewWidth,iNewHeight);
    oCtx.save();
    if(direction=='Horizontal'){
        
        oCtx.translate(iNewWidth,0);
        oCtx.scale(-1,1);
    }else{
        
        oCtx.translate(0,iNewHeight);
        oCtx.scale(1,-1);   
    }
    oCtx.drawImage(cpCanvas,0,0);
    oCtx.restore();
}
         
function touchHandler(event)
{
    var touches = event.changedTouches,
    first = touches[0],
    type = "";
    switch(event.type)
    {
        case "touchstart":
            type = "mousedown";
            break;
        case "touchmove":
            type="mousemove";
            break;        
        case "touchend":
            type="mouseup";
            break;
        default:
            return;
    }

    //initMouseEvent(type, canBubble, cancelable, view, clickCount, 
    //           screenX, screenY, clientX, clientY, ctrlKey, 
    //           altKey, shiftKey, metaKey, button, relatedTarget);
    
    var simulatedEvent = document.createEvent("MouseEvent");
    simulatedEvent.initMouseEvent(type, true, true, window, 1, 
        first.screenX, first.screenY, 
        first.clientX, first.clientY, false, 
        false, false, false, 0/*left*/, null);

    first.target.dispatchEvent(simulatedEvent);
    event.preventDefault();
}

function inittouch() 
{
    document.addEventListener("touchstart", touchHandler, true);
    document.addEventListener("touchmove", touchHandler, true);
    document.addEventListener("touchend", touchHandler, true);
    document.addEventListener("touchcancel", touchHandler, true);    
}
            
function unBindwlwc(){
    wlwwflage=false;
    $('#imgcanvas').unbind('touchmove' ,wlstart);
    $('#imgcanvas').unbind('touchend',wlend);
    $('#imgcanvas').unbind('touchstart',wlmove);
}
   
function removeArr(arr) {
    var what, a = arguments, L = a.length, ax;
    while (L > 1 && arr.length) {
        what = a[--L];
        while ((ax= arr.indexOf(what)) !== -1) {
            arr.splice(ax, 1);
        }
    }
    return arr;
}
var textdata=true;
function clearMeasure(){
    var canvas = document.getElementById('measure');
    var context = canvas.getContext('2d');
    var x = canvas.width / 2;
    var y = canvas.height / 2;
    context.clearRect(0, 0, canvas.width, canvas.height);
}
function invertcanvasdata(){
    if(wlwwflage){
        unBindwlwc();
    }
    if(!dragtool){
        $("#imgcanvas").rasterDrag();
    }
    if(!zoomflag){
        zoombind();
    }
    $('#measure').hide();
    clearCanvasData();
    $('#overlayc').css('z-index', 0);
    $('#imgcanvas').css('z-index', 70);
    $("#popupPanel a").parent().removeClass('btninactive');
    $("#Tbtninvert").parent().addClass('btninactive');
    invert();
}
function rotateleft(){
    if(wlwwflage){
        unBindwlwc();
    }
    if(!dragtool){
        $("#imgcanvas").rasterDrag();
    }
    if(!zoomflag){
        zoombind();
    }
    $('#measure').hide();
    $('#overlayc').css('z-index', 0);
    clearCanvasData();
    $('#imgcanvas').css('z-index', 70);
    $("#popupPanel a").parent().removeClass('btninactive');
    $("#Tbtnrotatel").parent().addClass('btninactive');
    rotate('left');
}
function rotateright(){
    if(wlwwflage){
        unBindwlwc();
    }
    if(!dragtool){
        $("#imgcanvas").rasterDrag();
    }
    if(!zoomflag){
        zoombind();
    }
    $('#measure').hide();
    $('#overlayc').css('z-index', 0);
    clearCanvasData();
    $('#imgcanvas').css('z-index', 70);
    $("#popupPanel a").parent().removeClass('btninactive');
    $("#Tbtnrotater").parent().addClass('btninactive');
    rotate('right');
}
function flipvertical(){
    if(wlwwflage){
        unBindwlwc();
    }
    if(!dragtool){
        $("#imgcanvas").rasterDrag();
    }
    if(!zoomflag){
        zoombind();
    }
    $('#measure').hide();
    $('#overlayc').css('z-index', 0);
    clearCanvasData();
    $('#imgcanvas').css('z-index', 70);
    $("#popupPanel a").parent().removeClass('btninactive');
    $("#Tbtnflipver").parent().addClass('btninactive');
    flip('vertical');  
}
function fliphori(){
    if(wlwwflage){
        unBindwlwc();
    }
    if(!dragtool){
        $("#imgcanvas").rasterDrag();
    }
    if(!zoomflag){
        zoombind();
    }
    $('#measure').hide();
    $('#overlayc').css('z-index', 0);
    clearCanvasData();
    $('#imgcanvas').css('z-index', 70);
    $("#popupPanel a").parent().removeClass('btninactive');
    $("#Tbtnfliphori").parent().addClass('btninactive');
    flip('Horizontal');   
}
var statis=false;
function texthide(){
    
    if(statis){
        statis=false;
        $(".viewercover").show();
    }else{
        statis=true;
        $(".viewercover").hide();
    }
    $('#measure').hide();
    $('#overlayc').css('z-index', 0);
    clearCanvasData();
    $('#imgcanvas').css('z-index', 70);
    closeToolbar();
}
function linetool(){
    if(wlwwflage){
        unBindwlwc();
    }
    wlenable('2');
    clearCanvasData();
    $('#imgcanvas').css('z-index', 0);
    //  $('#imgcanvas').unBindDrag();
    $('#overlayc').css('z-index', 70);
    // $('#overlayc').css('z-index', 70);
    $('#overlayc').unbind('touchmove', circlemove);
    $('#overlayc').unbind('touchstart', circlestart);
    $('#overlayc').unbind('touchend', circleend);
    $('#overlayc').bind('touchmove', lineMove);
    $('#overlayc').bind('touchstart', lineStart);
    $('#overlayc').bind('touchend', lineEnd);
    $('#measure').hide();
    $("#popupPanel a").parent().removeClass('btninactive');
    $("#Tbtnline").parent().addClass('btninactive');
    $("#Tbtninvert").parent().removeClass('btninactive');
}
function circle(){
    if(wlwwflage){
        unBindwlwc();
    }
    wlenable('3');
    clearMeasure();
    clearCanvasData();
    $('#overlayc').unbind('touchmove', lineMove);
    $('#overlayc').unbind('touchstart', lineStart);
    $('#overlayc').unbind('touchend', lineEnd);
    $('#measure').show();
    $('#imgcanvas').css('z-index', 0);
    $('#overlayc').css('z-index', 70);
    // $('#overlayc').css('z-index', 7);
    $('#overlayc').bind('touchmove', circlemove);
    $('#overlayc').bind('touchstart', circlestart);
    $('#overlayc').bind('touchend', circleend);
    // $('#myCanvas').unBindDrag();
    $("#popupPanel a").parent().removeClass('btninactive');
    $("#Tbtncircle").parent().addClass('btninactive');
}
function resetCanvas(){
    if(wlwwflage){
        unBindwlwc();
    }
    if(!dragtool){
        $("#imgcanvas").rasterDrag();
    }
    if(!zoomflag){
        zoombind();
    }
    $("#imgcanvas").css('-webkit-transform', "scale3d(" + 1 + ", " + 1 + ", 1)");
    $("#wrap").css('-webkit-transform', "translate3d(" + 0 + "px, " + 0 + "px, 0)");
    scale=1;
    prevScale=1;
    resizewindow();
    clearCanvasData();
    imageChange(imgindex);
    $('#imgcanvas').css('z-index', 70);
    $('#overlayc').css('z-index', 0);
    $('#measure').hide();
    $("#popupPanel a").parent().removeClass('btninactive');
    $(".viewercover").show();
    $("#divtools").removeClass();
    closeToolbar();
    toolEvent = [];
}
function resetWl(){
    $("#divtools").removeClass();
    toolEvent = [];
    // alert("......"+wlwwflage);
    $("#divtools").removeClass();
    if(wlwwflage){
        unBindwlwc();
    }
    if(!dragtool){
        $("#imgcanvas").rasterDrag();
    }
    if(!zoomflag){
        zoombind();
    }
    $('#Tbtnclose').unbind('click',presetData);
//    $("#Tbtnzoomdrag").removeClass();
//    $("#Tbtnzoomdrag").addClass("Tbtnzoomdrags");
//    $("#Tbtnline").removeClass();
//    $("#Tbtnline").addClass("Tbtnlines");
//    $("#Tbtncircle").removeClass();
//    $("#Tbtncircle").addClass("Tbtncircles")
//    $("#Tbtnwindowlevel").removeClass();
//    $("#Tbtnwindowlevel").addClass("Tbtnwindowlevels");
    
}
function zoomdrag(){
    wlenable('4');
    if(wlwwflage){
        unBindwlwc();
    }
    if(!dragtool){
        $("#imgcanvas").rasterDrag();
    }
    if(!zoomflag){
        zoombind();
    }
    clearCanvasData();
    $('#measure').hide();
    $('#imgcanvas').css('z-index', 70);
    $('#overlayc').css('z-index', 0);
    //zoomer.bind('transformstart', zoomstart);
    // zoomer.bind('transform', zoommove);
    // zoomer.bind('transformend', zoomend);
    $("#popupPanel a").parent().removeClass('btninactive');
    $("#Tbtnzoomdrag").parent().addClass('btninactive');
}
   
function zoombind(){
    zoomflag=true;
    container.bind("transformstart",tstart);
    container.bind("transform", tmove);
    container.bind("transformend", tend);
}
function zoomunbind(){
    zoomflag=false;
    container.unbind("transformstart",tstart);
    container.unbind("transform", tmove);
    container.unbind("transformend", tend);
}
function canvasimgData(){
    var jcanvas = document.getElementById('imgcanvas');
    var iNewWidth = jcanvas.width;
    var iNewHeight = jcanvas.height;
    var oCtx = jcanvas.getContext("2d");
    var dataDst = oCtx.getImageData(0,0,iNewWidth,iNewHeight);
    oCtx.putImageData(canvasImgdata,0,0);
}
function readimgData(){
    var jcanvas = document.getElementById('imgcanvas');
    var iNewWidth = jcanvas.width;
    var iNewHeight = jcanvas.height;
    var oCtx = jcanvas.getContext("2d");
    canvasImgdata = oCtx.getImageData(0,0,iNewWidth,iNewHeight);        
}       
      
function bindwlwc(){
    wlenable('1');
    wlwwflage=true;
    if(dragtool){
        $('#imgcanvas').unBindDrag();
    }
    if(zoomflag){
        zoomunbind();
    }
    $('#measure').hide();
    $('#overlayc').css('z-index', 0);
    $('#imgcanvas').css('z-index', 70);
    $.mobile.showPageLoadingMsg();
    loadPixel(true);
    $('#imgcanvas').bind('touchmove' ,wlstart);
    $('#imgcanvas').bind('touchend',wlend);
    $('#imgcanvas').bind('touchstart',wlmove);
    $("#popupPanel button").parent().removeClass('btninactive');
    $("#Tbtnwindowlevel").parent().addClass('btninactive');
}
function hideText() {
    $(".viewercover").hide();
}
function showText() {
    $(".viewercover").show();
}
function resizewindow(){
   
    $.mobile.showPageLoadingMsg();
    diffrentHeight=$("#headerview").height();
    overlay.width=document.documentElement.clientWidth - (document.documentElement.clientWidth*(0.001)); 
    overlay.height = document.documentElement.clientHeight-parseInt(($("#headerview").height()));

    var canvasareawrap = document.getElementById('wrap');
    var newWidth = document.documentElement.clientWidth;
    var newHeight = document.documentElement.clientHeight;
	newWidth=newWidth-(newWidth*(0.05));
    newHeight=newHeight-(newHeight*(0.05));

    if(newWidth > newHeight){
   
		if(nativerow > nativecoloum ) {
		 newWidth =  (nativecoloum * newHeight)/nativerow;
		 canvas.style.width = newWidth + 'px';
		 canvas.style.height = newHeight + 'px';
		 canvasareawrap.style.width = newWidth + 'px';
		 canvasareawrap.style.height = newHeight + 'px';
		 //alert(newWidth+" -vew- "+newHeight);
		}else if(nativecoloum >nativerow ) {
		 newHeight =  (newWidth * nativerow)/nativecoloum;
		 canvas.style.width = newWidth + 'px';
		 canvas.style.height = newHeight + 'px';
		 canvasareawrap.style.width = newWidth + 'px';
		 canvasareawrap.style.height = newHeight + 'px';
		 //alert(newWidth+" -vew- "+newHeight);
		}else{
		   newWidth =  (nativecoloum * newHeight)/nativerow;
		 canvas.style.width = newWidth + 'px';
		 canvas.style.height = newHeight + 'px';
		 canvasareawrap.style.width = newWidth + 'px';
		 canvasareawrap.style.height = newHeight + 'px';
		}

   }else{
      
      if(nativerow > nativecoloum ) {
		 newWidth =  (nativecoloum * newHeight)/nativerow;
		 canvas.style.width = newWidth + 'px';
		 canvas.style.height = newHeight + 'px';
		 canvasareawrap.style.width = newWidth + 'px';
		 canvasareawrap.style.height = newHeight + 'px';
		 //alert(newWidth+" -vew- "+newHeight);
		}else if(nativecoloum >nativerow ) {
		 newHeight =  (newWidth * nativerow)/nativecoloum;
		 canvas.style.width = newWidth + 'px';
		 canvas.style.height = newHeight + 'px';
		 canvasareawrap.style.width = newWidth + 'px';
		 canvasareawrap.style.height = newHeight + 'px';
		 //alert(newWidth+" -vew- "+newHeight);
		}else{
		  newHeight =  (newWidth * nativerow)/nativecoloum;
		  canvas.style.width = newWidth + 'px';
		  canvas.style.height = newHeight + 'px';
		  canvasareawrap.style.width = newWidth + 'px';
		  canvasareawrap.style.height = newHeight + 'px';
		}

   }
   /*
    alert(newHeight+" -- "+newWidth);
    
    var widthToHeight = newHeight / newWidth;
    var newWidthToHeight = newWidth / newHeight;
	alert(nativerow+" -- "+nativecoloum);

    if (parseFloat(newWidthToHeight) > parseFloat(widthToHeight)) {
        // window width is too wide relative to desired  width

        //newWidth = newHeight * widthToHeight;
        canvas.style.height = nativerow + 'px';
        canvas.style.width = nativecoloum + 'px';
        canvasareawrap.style.height = newHeight + 'px';
        canvasareawrap.style.width = newWidth + 'px';
    } else {
        // window height is too high relative to desired  height
		
       // newHeight = parseFloat(newWidth) / parseFloat(widthToHeight);
        canvas.style.width = nativecoloum + 'px';
        canvas.style.height = nativerow + 'px';
        canvasareawrap.style.width = newWidth + 'px';
        canvasareawrap.style.height = newHeight + 'px';
    }
    */
    stageheight=newHeight;
    stagewidth=newWidth;
    var resize=(document.documentElement.clientWidth-parseInt($("#imgcanvas").width()));
    var clientheight=parseInt(parseInt($(window).height())-newHeight);
    //alert(clientheight);
    var clientWidth=parseInt($(window).width());    
    if(clientWidth>newWidth){
       var widthDiff=clientWidth-newWidth;
        $("#wrap").css("margin-left",Math.round(widthDiff/2));
    }
    
    var clientHeight=parseInt($(window).height());    
   
    if(clientHeight>newHeight){
       var heightDiff=clientHeight-newHeight;
	   $("#wrap").css("margin-top",Math.round(heightDiff/3));
    }

//  $("#imgcanvas").css("margin-top",Math.abs(Math.round((clientheight/2)-(parseInt($("#headerview").height()*2.5)))));
//  $("#imgcanvas").css("margin-left",Math.round(resize/2));
//     
//   alert(".."+clientheight+".."+$("#headerview").height()+".."+Math.round((clientheight/2)-parseInt($("#headerview").height())));
//     $("#wrap").css("margin-top",Math.abs(Math.round((clientheight/2)-$("#headerview").height())));
    //$("#wrap").css("margin-left",Math.round(resize/4));
    leftview=Math.round(resize/2);
    $("#containerdiv").css("margin-left",Math.round(resize/2));
    var height=(document.documentElement.clientHeight-parseInt(($("#imgcanvas").height()+$("#footerview").height()+$("#headerview").height())));
    //loadImage(imagearray[imgindex]);
    $.mobile.hidePageLoadingMsg();
    $('#measure').height(($("#headerview").height())*1);
    $('#measure').width(($("#headerview").width())/4);
    $('#measure').css("margin-left",($("#headerview").width())/2.5);
    $('.cover').removeClass('cover').addClass('viewercover');
    $("#footerview").css("bottom",$("#headerview").height()/1.4);
}
 