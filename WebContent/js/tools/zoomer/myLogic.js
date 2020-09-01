// JavaScript Document
var zIndexBackup = 10,tmove,tend,tstart,element,container;
$(function(){
//    var zoom = new ZoomView('#wrap','#wrap :first');
//		
//});

  element = $('#imgcanvas');
    container = $('#wrap');

    container = $(container).hammer({
        prevent_default: true,
        scale_treshold: 0,
        drag_min_distance: 0
    });
    element = $(element);
    var displayWidth = container.width();
    var displayHeight = container.height();

    //These two constants specify the minimum and maximum zoom
    var MIN_ZOOM = .2;
    var MAX_ZOOM = 30;

    var scaleFactor = 1;
    var previousScaleFactor = 1;

    //These two variables keep track of the X and Y coordinate of the finger when it first
    //touches the screen
    var startX = 0;
    var startY = 0;

    //These two variables keep track of the amount we need to translate the canvas along the X
    //and the Y coordinate
    var translateX = 0;
    var translateY = 0;

    //These two variables keep track of the amount we translated the X and Y coordinates, the last time we
    //panned.
    var previousTranslateX = 0;
    var previousTranslateY = 0;

    //Translate Origin variables

    var tch1 = 0, 
    tch2 = 0, 
    tcX = 0, 
    tcY = 0,
    toX = 0,
    toY = 0,
    cssOrigin = "";
     tstart= function(event){
        //We save the initial midpoint of the first two touches to say where our transform origin is.
        if (event.originalEvent.touches.length === 2) {
            //alert("...2 finger ....");
            e = event
            tch1 = [e.touches[0].x, e.touches[0].y],
            tch2 = [e.touches[1].x, e.touches[1].y]
            tcX = (tch1[0]+tch2[0])/2,
            tcY = (tch1[1]+tch2[1])/2
            toX = tcX
            toY = tcY
            var left = $(element).offset().left;
            var top = $(element).offset().top;
            cssOrigin = (-(left) + toX)/scaleFactor +"px "+ (-(top) + toY)/scaleFactor +"px";
            
        }
    }
     tmove=function(event) {
        if (event.originalEvent.touches.length === 2) {
            // alert("...2 finger ....");
            scaleFactor = previousScaleFactor * event.scale;
            scaleFactor = Math.max(MIN_ZOOM, Math.min(scaleFactor, MAX_ZOOM));
            transform(event);
        }
    }
     tend=function(event) {
        //if (event.originalEvent.touches.length === 2) {
        previousScaleFactor = scaleFactor;
        currentzoom=scaleFactor;
    //}
    } 
    
    /**
        * on drag
        */
  
    function transform(e) {
        //We're going to scale the X and Y coordinates by the same amount
        //var cssScale = "scaleX("+ scaleFactor +") scaleY("+ scaleFactor +") rotateZ("+ e.rotation +"deg)";
        var cssScale = "scaleX("+ scaleFactor +") scaleY("+ scaleFactor +")";
        element.css({
            webkitTransform: cssScale,
            webkitTransformOrigin: cssOrigin,

            transform: cssScale,
            transformOrigin: cssOrigin
        });

            
    }
//function zoomBind(){
//   //  var zoom = new ZoomView('#wrap','#wrap :first');
//     container.bind("transformstart",tstart);
//     container.bind("transform", tmove);
//     container.bind("transformend", tend);
////       var dragview = new DragView($(container));
////    container.bind("dragstart", $.proxy(dragview.OnDragStart, dragview));
////    container.bind("drag", $.proxy(dragview.OnDrag, dragview));
////    container.bind("dragend", $.proxy(dragview.OnDragEnd, dragview));
////    setInterval($.proxy(dragview.WatchDrag, dragview), 10);
//    
//}
//function zoomUnBind(){
//     var zoom = new ZoomView('#wrap','#wrap :first');
//     container.unbind("transformstart",tstart);
//     container.unbind("transform", tmove);
//     container.unbind("transformend", tend);
//       var dragview = new DragView($(container));
//    container.unbind("dragstart", $.proxy(dragview.OnDragStart, dragview));
//    container.unbind("drag", $.proxy(dragview.OnDrag, dragview));
//    container.unbind("dragend", $.proxy(dragview.OnDragEnd, dragview));
//    //setInterval($.proxy(dragview.WatchDrag, dragview), 10);
//}
});