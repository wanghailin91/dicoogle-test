$(document).bind("mobileinit", function () {
    $.mobile.defaultPageTransition = "slidefade";
     
});
var ispopupOpen=false;
$( document ).on( "pageinit", function() {
    $( "#popupPanel" ).on({
        popupbeforeposition: function() {
            var h = $( window ).height();
            var w = $( window ).width();	
            var panelWidth=$("#lengthcalc").width();
            var panelHeight=$("#lengthcalc").height();
            $("#popupPanel").css("height",(panelHeight));
            $("#popupPanel").css("width",panelWidth);

        }
    });
    $("#popupclick").bind("click",function(){
        ispopupOpen=true;
    });
    $( "#popupPanel button").on("click", function() { 
        $( "#popupPanel").popup('close');
        clearDrawCanvas();
        ispopupOpen=false;
    });
   
});
var presetData;
$(document).ready(function() {
    $("#ivbtnback").click(function() {
        window.location="series.html";
    });
     presetData= function presetData(){
         $( "#popupPanel" ).popup('close');
           ispopupOpen=false;
           setTimeout(function(){$('#popupPanelmm').popup('open');},600);
         $('#popupPanelmm').popup('close');
    };

});
function closeToolbar(){
    $( "#popupPanel" ).popup('close');
    ispopupOpen=false;
}

