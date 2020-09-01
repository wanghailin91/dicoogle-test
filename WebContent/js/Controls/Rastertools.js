var measurementData;
var canvasmac,circlemoving;
var    context3 ;
if (!innerWidth) var innerWidth = document.documentElement.clientWidth;
if (!innerHeight) var innerHeight = document.documentElement.clientHeight;

var previousPoints = [0, 0],
points = {
    circle2: {
        x:0, 
        y: 0
    }
};
var  pointData = {
    startA: {
        x: 0, 
        y: 0
    },
    startB: {
        x: 0, 
        y: 0
    }
},distance,gap,objectOnMouseDown;
$(document).ready(function() {
    init();
});
var canvas,context,cx,cy;
var pointA,pointB,currentPointx,currentPointy,resizelineline;
var plus;
var circlesize=40;
function init(){
    canvas = document.getElementById('overlayc');
    context = canvas.getContext('2d');
    var isiPhone = navigator.userAgent.toLowerCase().indexOf("iphone");
    var isiPad = navigator.userAgent.toLowerCase().indexOf("ipad");
    var isiPod = navigator.userAgent.toLowerCase().indexOf("ipod");
    //circlesize=15;
    if(isiPhone > -1)
    {
        circlesize=25;
    }
    if(isiPad > -1)
    {
        circlesize=35;
    }
    if(isiPod > -1)
    {
        circlesize=25;
    } 
    
}
var linedraging=false;
var lineStart=function (event) 
{
    //only run code if the user has two fingers touching
    //alert("single finger....."+event.originalEvent.touches.length);
       
    
    if (event.originalEvent.touches.length === 2) {
        var  pointsX = Math.abs(event.originalEvent.touches[0].pageX-event.originalEvent.touches[1].pageX);
        var  pointsY = Math.abs(event.originalEvent.touches[0].pageY-event.originalEvent.touches[1].pageY);
        var startdistance=Math.sqrt(pointsX * pointsX + pointsY * pointsY); 
        cx=((event.originalEvent.touches[0].pageX+event.originalEvent.touches[1].pageX)/2);
        cy=((event.originalEvent.touches[0].pageY+event.originalEvent.touches[1].pageY)/2);
        pointData.startA.x=event.originalEvent.touches[0].pageX;
        pointData.startA.y=event.originalEvent.touches[0].pageY;
        pointData.startB.x=event.originalEvent.touches[1].pageX;
        pointData.startB.y=event.originalEvent.touches[1].pageY;
        distance=startdistance;
        gap=(distance/40);
        pointA=false;
        pointB=false;
        resizelineline=false;
        calline( pointData.startA.x, pointData.startA.y-diffrentHeight, pointData.startB.x, pointData.startB.y-diffrentHeight);
        drawMeasure(context);
        linedraging=true;
    }else
     
    if (event.originalEvent.touches.length ===1 ){
       event.preventDefault();
    var evt = event.originalEvent.touches[0] || event.originalEvent.changedTouches[0];
        if(linedraging){
            // alert("Line start "+linedraging+pointData.startA.x+"...."+ pointData.startA.y+"...."+ circlesize/.7 +"...."+event.originalEvent.touches[0].pageX +"...."+event.originalEvent.touches[0].pageY);
            if(checkPoint(pointData.startA.x, pointData.startA.y, circlesize/.7, evt.pageX, evt.pageY)){
                pointA=true;
                pointB=false;
                resizeline=true;
                currentPointx=evt.pageX;
                currentPointy=evt.pageY;
                drawMeasureSinglemove(context);
            }else if(checkPoint(pointData.startB.x, pointData.startB.y, circlesize/.7, evt.pageX, evt.pageY)){
                pointB=true;
                pointA=false;
                currentPointx=evt.pageX;
                currentPointy=evt.pageY;
                drawMeasureSinglemove(context);
            }else{
                pointB=false;
                pointA=false;
                drawMeasureSinglemove(context);
            }
        }
    }
}
var lineMove=function (event) 
{   
    //only run code if the user has two fingers touching
    if (event.originalEvent.touches.length === 2) {
        var  pointsX = Math.abs(event.originalEvent.touches[0].pageX-event.originalEvent.touches[1].pageX);
        var  pointsY = Math.abs(event.originalEvent.touches[0].pageY-event.originalEvent.touches[1].pageY);
        var movedistance=Math.sqrt(pointsX * pointsX + pointsY * pointsY); 
        cx=((event.originalEvent.touches[0].pageX+event.originalEvent.touches[1].pageX)/2);
        cy=((event.originalEvent.touches[0].pageY+event.originalEvent.touches[1].pageY)/2);
        distance=movedistance;
        gap=(distance/10);
        pointData.startA.x=event.originalEvent.touches[0].pageX;
        pointData.startA.y=event.originalEvent.touches[0].pageY;
        pointData.startB.x=event.originalEvent.touches[1].pageX;
        pointData.startB.y=event.originalEvent.touches[1].pageY;
        pointA=false;
        resizeline=true;
        pointB=false;
        linedraging=true;
        calline(pointData.startA.x, pointData.startA.y-diffrentHeight, pointData.startB.x, pointData.startB.y-diffrentHeight);
        drawMeasure(context);    
    }else
    if (event.originalEvent.touches.length == 1 ){
        event.preventDefault();
       var evt = event.originalEvent.touches[0] || event.originalEvent.changedTouches[0];
        if(linedraging){
            if(pointA||pointB){
               currentPointx=evt.pageX;
                currentPointy=evt.pageY;
                drawMeasureSinglemove(context);
            }
       
        }
    }
}
var lineEnd=function (event) 
{ 
    //only run code if the user has two fingers touching
    if (event.originalEvent.touches.length === 2) {
    //        var  pointsX = Math.abs(event.originalEvent.touches[0].pageX-event.originalEvent.touches[1].pageX);
    //        var  pointsY = Math.abs(event.originalEvent.touches[0].pageY-event.originalEvent.touches[1].pageY);
    //        var movedistance=Math.sqrt(pointsX * pointsX + pointsY * pointsY); 
    //        cx=((event.originalEvent.touches[0].pageX+event.originalEvent.touches[1].pageX)/2);
    //        cy=((event.originalEvent.touches[0].pageY+event.originalEvent.touches[1].pageY)/2);
    //        pointData.startA.x=event.originalEvent.touches[0].pageX;
    //        pointData.startA.y=event.originalEvent.touches[0].pageY;
    //        pointData.startB.x=event.originalEvent.touches[1].pageX;
    //        pointData.startB.y=event.originalEvent.touches[1].pageY;
    //        drawMeasure();
    
    }else
    if (event.originalEvent.touches.length ===1 ){
       
    }
}
function drawMeasure(context){
    if(distance<90){
        plus=(distance/7);
    }else{
        plus=(distance/30);
    }
  
    context.clearRect(0, 0, innerWidth,innerHeight);
    context.lineWidth = 2;
    context.strokeStyle = '#9AFE2E';
    context.beginPath();
    context.moveTo(pointData.startA.x, pointData.startA.y-diffrentHeight);
    context.lineTo(pointData.startB.x, pointData.startB.y-diffrentHeight);
    
    context.stroke();
    context.beginPath();
    context.arc(pointData.startA.x, pointData.startA.y-diffrentHeight, circlesize, 0, 2 * Math.PI, false);
    context.fillStyle = "rgba(0,255,0,.1)";
    context.fill();
    context.strokeStyle = 'rgba(0,255,0,.1)';
    context.stroke();
    context.beginPath();
    context.arc(pointData.startB.x, pointData.startB.y-diffrentHeight, circlesize, 0,2 * Math.PI, false);
    context.fillStyle = "rgba(0,255,0,.1)";
    context.fill();
    context.strokeStyle = 'rgba(0,255,0,.1)';
    context.stroke();
    var cx=((pointData.startA.x+pointData.startB.x)/2);
    var cy=((pointData.startA.y+pointData.startB.y)/2);
    calline( pointData.startA.x,pointData.startA.y-diffrentHeight , pointData.startB.x, pointData.startB.y-diffrentHeight);
    context.font = '14pt Calibri';
    context.fillStyle = '#9AFE2E';
    context.fillText("Len:"+""+measurementData, cx+20, cy);
    context.lineWidth = 2;

}
var simpleheight=20;
function clearCanvasData(){
    drawing=false;
    linedraging=false;
    var canvas = document.getElementById("overlayc");
    var context = canvas.getContext("2d");
    context.clearRect(0, 0, canvas.width, canvas.width);
}
function drawMeasureSinglemove(context){
    if(distance<90){
        plus=(distance/7);
    }else{
        plus=(distance/30);
    }
    context.clearRect(0, 0, innerWidth,innerHeight);
    context.beginPath();
    if(pointA){
    
        context.moveTo( pointData.startB.x,  pointData.startB.y-diffrentHeight);
        context.lineTo( currentPointx, currentPointy-diffrentHeight);
        context.strokeStyle = '#9AFE2E';
        context.stroke();
        context.beginPath();
        context.arc(pointData.startB.x, pointData.startB.y-diffrentHeight, circlesize, 0, 2 * Math.PI, false);
        context.fillStyle = "rgba(0,255,0,.1)";
        context.fill();
        context.strokeStyle = 'rgba(0,255,0,.1)';
        context.stroke();
        context.beginPath();
        context.arc(currentPointx, currentPointy-diffrentHeight, circlesize, 0, 2 * Math.PI, false);
        context.fillStyle = "rgba(0,255,0,.1)";
        context.fill();
        context.strokeStyle = 'rgba(0,255,0,.1)';
        context.stroke();
        calline( pointData.startB.x,pointData.startB.y-diffrentHeight , currentPointx, currentPointy-diffrentHeight);
        var cx=pointData.startB.x;
        var cy=pointData.startB.y;
        context.font = '14pt Calibri';
        context.fillStyle = '#9AFE2E';
        context.fillText("Len:"+""+measurementData, cx+10, cy-diffrentHeight);
        pointData.startA.x=currentPointx;
        pointData.startA.y=currentPointy;
    }
    else if(pointB)
    {
       
        context.moveTo( pointData.startA.x,  pointData.startA.y-diffrentHeight);
        context.lineTo( currentPointx, currentPointy-diffrentHeight);
        context.strokeStyle = '#9AFE2E';
        context.stroke();
        context.beginPath();
        context.arc(pointData.startA.x, pointData.startA.y-diffrentHeight, circlesize, 0, 2 * Math.PI, false);
        context.fillStyle = "rgba(0,255,0,.1)";
        context.fill();
        context.strokeStyle = 'rgba(0,255,0,.1)';
        context.stroke();
        context.beginPath();
        context.arc(currentPointx, currentPointy-diffrentHeight, circlesize, 0, 2 * Math.PI, false);
        context.fillStyle = "rgba(0,255,0,.1)";
        context.fill();
        context.strokeStyle = 'rgba(0,255,0,.1)';
        context.stroke();
        calline( pointData.startA.x,pointData.startA.y-diffrentHeight ,currentPointx, currentPointy-diffrentHeight);
        var cx=pointData.startA.x;
        var cy=pointData.startA.y;
        context.font = '14pt Calibri';
        context.fillStyle = '#9AFE2E';
        context.fillText("Len:"+""+measurementData, cx+10, cy-diffrentHeight);
        pointData.startB.x=currentPointx;
        pointData.startB.y=currentPointy;
    }else {
        
        context.clearRect(0, 0, canvas.width, canvas.height);
        context.strokeStyle = '#9AFE2E';
        context.beginPath();
        context.moveTo(pointData.startA.x, pointData.startA.y-diffrentHeight);
        context.lineTo(pointData.startB.x, pointData.startB.y-diffrentHeight);
       
        context.stroke();
        context.beginPath();
        context.arc(pointData.startA.x, pointData.startA.y-diffrentHeight, circlesize, 0, 2 * Math.PI, false);
        context.fillStyle = "rgba(0,255,0,.1)";
        context.fill();
        context.strokeStyle = 'rgba(0,255,0,.1)';
        context.stroke();
        context.strokeStyle = 'rgba(0,255,0,.1)';
        context.beginPath();
        context.arc(pointData.startB.x, pointData.startB.y-diffrentHeight, circlesize, 0,2 * Math.PI, false);
        context.fillStyle = "rgba(0,255,0,.1)";
        context.fill();
        context.strokeStyle = 'rgba(0,255,0,.1)';
        context.stroke();
        //  calline( pointData.startB.x,pointData.startB.y ,currentPointx, currentPointy);
        var cx=((pointData.startA.x+pointData.startB.x)/2);
        var cy=((pointData.startA.y+pointData.startB.y)/2);
        context.font = '14pt Calibri';
        context.fillStyle = '#9AFE2E';
        context.fillText("Len:"+""+measurementData, cx+10, cy);
    }

}
function drawLine(context){
    var plus=(distance/30);
    var simpleheight=40;
    var  rectpoint = {
        pointA: {
            x: pointData.startA.x, 
            y: pointData.startA.y-simpleheight
        },
        pointB: {
            x: pointData.startA.y-simpleheight, 
            y: pointData.startA.y
        }
        ,
        pointC: {
            x: pointData.startA.y, 
            y: pointData.startA.y+simpleheight
        }
        ,
        pointD:  {
            x: pointData.startA.x, 
            y: pointData.startA.y+simpleheight
        }
    }
    context.clearRect(0, 0, innerWidth,innerHeight);
    context.beginPath();
    context.moveTo(pointData.startA.x, pointData.startA.y);
    context.lineTo(pointData.startB.x, pointData.startB.y);
    context.moveTo(pointData.startA.x, pointData.startA.y);
    context.lineTo(rectpoint.pointA.x, rectpoint.pointA.y);
    context.moveTo(pointData.startA.x, pointData.startA.y);
    context.lineTo(rectpoint.pointD.x, rectpoint.pointD.y);
    context.moveTo(pointData.startB.x, pointData.startB.y);
    context.lineTo(rectpoint.pointC.x, rectpoint.pointC.y);
    context.moveTo(pointData.startB.x, pointData.startB.y);
    context.lineTo(rectpoint.pointB.x, rectpoint.pointB.y);
    context.moveTo(rectpoint.pointA.x, rectpoint.pointA.y);
    context.lineTo(rectpoint.pointB.x, rectpoint.pointB.y);
    context.moveTo(rectpoint.pointA.x, rectpoint.pointA.y);
    context.lineTo(rectpoint.pointD.x, rectpoint.pointD.y);
    context.moveTo(rectpoint.pointC.x, rectpoint.pointC.y);
    context.lineTo(rectpoint.pointD.x, rectpoint.pointD.y);
    context.moveTo(rectpoint.pointC.x, rectpoint.pointC.y);
    context.lineTo(rectpoint.pointB.x, rectpoint.pointB.y);
    //if(xaxis)
    context.lineWidth = 2;
    context.strokeStyle = '#9AFE2E';
    context.stroke();
}
var  mainPointData,finalPointData,mainACorner,distanceRect;
function drawingRect(p,context){
    distanceRect=50;
    mainPointData = {
        points: {
            x:pointData.startA.x, 
            y: (pointData.startA.y-distanceRect)
        }
    }
    finalPointData = {
        points: {
            x: 0, 
            y: 0
        }
    }
    mainACorner = {
        points: {
            x: 0, 
            y: 0
        }
    }
    getAFinalPoint();
    var plus=(distance/30);
    context.clearRect(0, 0, canvas.width, canvas.height);
    context.beginPath();
    context.moveTo(pointData.startA.x, pointData.startA.y);
    context.lineTo(pointData.startB.x, pointData.startB.y);
    context.moveTo( pointData.startA.x,  pointData.startA.y);
    context.lineTo( pointData.startA.x,  pointData.startA.y-plus);
    context.moveTo( pointData.startA.x,  pointData.startA.y);
    context.lineTo( pointData.startA.x,  pointData.startA.y+plus);
    context.moveTo( pointData.startA.x,  pointData.startA.y);
    context.lineTo( pointData.startA.x-plus,  pointData.startA.y);
    context.moveTo( pointData.startA.x,  pointData.startA.y);
    context.lineTo( pointData.startA.x+plus,  pointData.startA.y);
    //draw the plus in pointb
    context.moveTo( pointData.startB.x,  pointData.startB.y);
    context.lineTo( pointData.startB.x,  pointData.startB.y-plus);
    context.moveTo( pointData.startB.x,  pointData.startB.y);
    context.lineTo( pointData.startB.x,  pointData.startB.y+plus);
    context.moveTo( pointData.startB.x,  pointData.startB.y);
    context.lineTo( pointData.startB.x-plus,  pointData.startB.y);
    context.moveTo( pointData.startB.x,  pointData.startB.y);
    context.lineTo( pointData.startB.x+plus,  pointData.startB.y);
    context.moveTo(pointData.startA.x, pointData.startA.y);
    context.lineTo(mainACorner.points.x,mainACorner.points.y);
    // context.lineTo(mainPointData.points.x,mainPointData.points.y);
    context.lineWidth = 2;
    context.strokeStyle = '#9AFE2E';
    context.stroke();
}
function calcAngle(x1, x2, y1, y2)
{
    var calcAngle = Math.abs(Math.atan2((y2-y1),(x2-x1)))*180/Math.PI;	
   
    return calcAngle;
}
function strightLine(x1,y1,x2,y2,x,y){
    //m=y2-y1/x2-x1...y=mx+b
    var mx=x2-x1,my=y2-y1;
    var slopeintercept=my/mx;
    var b=y-slopeintercept*x;
    if(y===((slopeintercept*x)+b)){
        return true;
    }else{
        return false;
    }
}
function getPos(p, context, isTop) {
    return p - (isTop ? context.canvas.offsetTop : context.canvas.offsetLeft);
}
function getAFinalPoint() {
    var x=mainPointData.points.x-pointData.startA.x;
    var y=mainPointData.points.y-pointData.startA.y;
    var distanceA,distanceB,distanceC;
    distanceA=Math.sqrt((pointData.startA.x-pointData.startB.x)*(pointData.startA.x-pointData.startB.x)+(pointData.startA.y-pointData.startB.y)*(pointData.startA.y-pointData.startB.y));
    distanceC=Math.sqrt((pointData.startA.x-mainPointData.points.x)*(pointData.startA.x-mainPointData.points.x)+(pointData.startA.y-mainPointData.points.y)*(pointData.startA.y-mainPointData.points.y));
    distanceB=Math.sqrt((pointData.startB.x-mainPointData.points.x)*(pointData.startB.x-mainPointData.points.x)+(pointData.startB.y-mainPointData.points.y)*(pointData.startB.y-mainPointData.points.y));
    var angle=(((distanceA*distanceA)+(distanceB*distanceB)-(distanceC*distanceC)))/(2*distanceA*distanceB);
    var content=Math.acos((angle));
    var firstdistance;
    var Angledistance=Math.asin(distanceA*Math.sin(content)/distanceC);
    // console.log("......Angle........"+(Angledistance* (180 / Math.PI))+"...A."+distanceA+".B..."+distanceB+".C."+distanceC+".....C angle "+content+"..invertangle...."+angle * 180 / Math.PI);
    //if(Angledistance===45){
    firstdistance=Math.tan(Math.abs(Angledistance* (180 / Math.PI)+45))*distanceRect;
    //    }else{
    //        firstdistance=Math.tan(Math.abs(Angledistance+45))*distanceRect;
    //    }
    mainACorner.points.x=(mainPointData.points.x-firstdistance);
    mainACorner.points.y=(mainPointData.points.y);
// console.log("...."+mainPointData.points.x+"..."+firstdistance+"..."+mainPointData.points.y+"...."+pointData.startA.x+"..."+pointData.startA.y);
//    mainACorner.points.x=Math.abs(x*Math.cos(90)-y*Math.sin(90));
//    mainACorner.points.y=y*Math.cos(90)+x*Math.sin(90);
//    console.log("..main point data "+ mainACorner.points.x+"......"+ mainACorner.points.y+".."+mainPointData.points.x+"=="+pointData.startA.x+".."+mainPointData.points.y+"=="+pointData.startA.y+"....x"+x+"...y"+y);
}
function pointsInRectangle() {
    var x1,x2,x3,x4,y1,y2,y3,y4,x,y,Area,height=50,width;
    width=Math.sqrt((x2-x1)*(x2-x1)+(y2-y1)*(y2-y1));
    Area=width*height;
    var a1=Math.sqrt((x1-x2)*(x1-x2)+(y1-y2)*(y1-y2));
    var a2=Math.sqrt((x2-x3)*(x2-x3)+(y2-y3)*(y2-y3));
    var a3=Math.sqrt((x3-x4)*(x3-x4)+(y3-y4)*(y3-y4));
    var a4=Math.sqrt((x4-x1)*(x4-x1)+(y4-y1)*(y4-y1));
    var b1=Math.sqrt((x1-x)*(x1-x)+(y1-y)*(y1-y));
    var b2=Math.sqrt((x2-x)*(x2-x)+(y2-y)*(y2-y));
    var b3=Math.sqrt((x3-x)*(x3-x)+(y3-y)*(y3-y));
    var b4=Math.sqrt((x4-x)*(x4-x)+(y4-y)*(y4-y));
    var u1=(a1+b1+b2)/2;
    var u2=(a2+b2+b3)/2;
    var u3=(a3+b3+b4)/2;
    var u4=(a4+b4+b1)/2;
    var Area1,Area2,Area3,Area4;
    Area1=Math.sqrt(u1*(u1-a1)*(u1-b1)*(u1-b2));
    Area2=Math.sqrt(u2*(u2-a2)*(u2-b2)*(u2-b3));
    Area3=Math.sqrt(u3*(u3-a3)*(u3-b3)*(u3-b4));
    Area4=Math.sqrt(u4*(u4-a4)*(u4-b4)*(u4-b1));
    var value=Area1+Area2+Area3+Area4;
    if(Area===value){
        return true;
    }else{
        return false;
    }
}
function checkPoint(x,y,r,pagex,pagey)
{
    var distance = Math.sqrt(Math.pow(x - pagex, 2) + Math.pow(y - pagey, 2));
    return distance < r;
}
var pointx=100,pointy=100,radious;
function getPos(p, context, isTop) {
    return p - (isTop ? context.canvas.offsetTop : context.canvas.offsetLeft);
}
function initCircle(){
    var previousPoints = [0, 0],
    doc = window.document,
    find = function (id) {
        return doc.getElementById(id);
    },
    isTouch = 'createTouch' in doc,
    context3 = find('imgcanvas').getContext('2d'),
    //context6 = find('canvas6').getContext('2d'),
    points = {
        circle2: {
            x:150, 
            y: 100
        }
    },
    objectOnMouseDown;
}
var  rectpointval = {
    pointA: {
        x: pointData.startA.x, 
        y: pointData.startA.y-simpleheight
    },
    pointB: {
        x: pointData.startA.y-simpleheight, 
        y: pointData.startA.y
    }
};
var circlestart=function (event) 
{
    canvasmac = document.getElementById('imgcanvas');
    context3 = canvasmac.getContext('2d');
    //only run code if the user has two fingers touching
    if (event.originalEvent.touches.length === 2) {
        var  pointsX = Math.abs(event.originalEvent.touches[0].pageX-event.originalEvent.touches[1].pageX);
        var  pointsY = Math.abs(event.originalEvent.touches[0].pageY-event.originalEvent.touches[1].pageY);
        var startdistance=Math.sqrt(pointsX * pointsX + pointsY * pointsY); 
        var xx=((event.originalEvent.touches[0].pageX+event.originalEvent.touches[1].pageX)/2);
        var yy=((event.originalEvent.touches[0].pageY+event.originalEvent.touches[1].pageY)/2);
        var rr=startdistance/2;
        points.circle2.x=xx;
        points.circle2.y=yy;
        radious=rr;
        pointx=xx;
        pointy=yy;
        drawing=true;
        //calcircle(event.originalEvent.touches[0].pageX, (event.originalEvent.touches[0].pageY), event.originalEvent.touches[1].pageX, (event.originalEvent.touches[1].pageY));
        //calcircle(event.originalEvent.touches[0].pageX, event.originalEvent.touches[0].pageY-diffrentHeight, event.originalEvent.touches[1].pageX, event.originalEvent.touches[1].pageY-diffrentHeight);
        draw_circle(xx,yy,radious);
    //calline(x1,y1,x2,y2)
                
    }else
    if (event.originalEvent.touches.length ===1 ){
         event.preventDefault();
       var evt = event.originalEvent.touches[0] || event.originalEvent.changedTouches[0];
        if(drawing){
            canvasmac = document.getElementById('overlayc');
            context3 = canvasmac.getContext('2d');
            var x, y;
            /* last two are good-coded! */
            {
                x = getPos(evt.pageX, context3);
                y = getPos(evt.pageY, context3, true);
                if (context3.isPointInPath(x, y)){
                    objectOnMouseDown = 'circle2';
                    previousPoints = [x, y];
                    drawCircle(context3, points.circle2);
                }
            }
        }
    }
}
var drawing=false;
var circlemove=function (event) 
{   
    if (event.originalEvent.touches.length === 2) {
        previousPoints = [pointx, pointy];
        var  pointsX = Math.abs(event.originalEvent.touches[0].pageX-event.originalEvent.touches[1].pageX);
        var  pointsY = Math.abs(event.originalEvent.touches[0].pageY-event.originalEvent.touches[1].pageY);
        var movedistance=Math.sqrt(pointsX * pointsX + pointsY * pointsY); 
        var xx=((event.originalEvent.touches[0].pageX+event.originalEvent.touches[1].pageX)/2);
        var yy=((event.originalEvent.touches[0].pageY+event.originalEvent.touches[1].pageY)/2);
        var rr=movedistance/2;
        radious=rr;
        pointx=xx;
        pointy=yy;
        drawing=true;
        //calcircle(event.originalEvent.touches[0].pageX, event.originalEvent.touches[0].pageY, event.originalEvent.touches[1].pageX, event.originalEvent.touches[1].pageY);
        // calcircle(event.originalEvent.touches[0].pageX, event.originalEvent.touches[0].pageY-diffrentHeight, event.originalEvent.touches[1].pageX, event.originalEvent.touches[1].pageY-diffrentHeight);
        draw_circle(xx,yy,radious);              
    }else
    if (event.originalEvent.touches.length ===1 ){
        var x, y;
        event.preventDefault();
       var evt = event.originalEvent.touches[0] || event.originalEvent.changedTouches[0];
        if(drawing){
            if (objectOnMouseDown === 'circle2') {
                x = getPos(evt.pageX, context3);
                y = getPos(evt.pageY, context3, true);
                if (context3.isPointInPath(x, y)){
                    var positiveX = x > previousPoints[0], 
                    positiveY = y > previousPoints[1], 
                    value;
                    value = positiveX ? (x - previousPoints[0]) : (previousPoints[0] - x);
                    if (positiveX) points.circle2.x += value;
                    else points.circle2.x -= value;
                    value = positiveY ? (y - previousPoints[1]) : (previousPoints[1] - y);
                    if (positiveY) points.circle2.y += value;
                    else points.circle2.y -= value;
                    previousPoints = [x, y];
                    //  console.log("..."+x+"...."+y);
                    drawCircle(context3, points.circle2);
                    pointx=points.circle2.x;
                    pointy=points.circle2.y;
                }
            }
        }
    }
}
var circleend=function (event) 
{
    if(drawing){
        //only run code if the user has two fingers touching
        if (event.originalEvent.touches.length === 2) {
  
        }else if (event.originalEvent.touches.length ===1 ){
            objectOnMouseDown = null;
        // draw_circle(xx,yy,radious);  
       
        }
        draw_circle(pointx,pointy,radious);   
    }
}
var circleview=50;
function draw_circle(x,y,r) {
    var rad;
    if((r-circleview)<0){
        rad=0;
    }else if((r-circleview)>0){
        rad=r-circleview;
    }
    context.clearRect(0, 0, innerWidth,innerHeight);
    context.beginPath();
    context.arc(x, (y-diffrentHeight), rad,Math.PI * 2, false);
    context.lineWidth = 2;
    context.strokeStyle = '#9AFE2E';
    context.stroke();
    var x1=x-rad;
    var x2=x+rad;
    var y1=(y-diffrentHeight);
    var y2=(y-diffrentHeight);
    calcircle(x1,y1,x2,y2);
}
function drawCircle(context, p) {
    var rad;
    if((radious-circleview)<0){
        rad=0;
    }else if((radious-circleview)>0){
        rad=radious-circleview;
    }
    context.clearRect(0, 0, innerWidth,innerHeight);
    context.beginPath();
    context.arc(p.x,(p.y-diffrentHeight), rad ,Math.PI * 2, false);
    context.lineWidth = 2;
    context.strokeStyle = '#9AFE2E';
    context.stroke();
    context.beginPath();
    context.arc(p.x, (p.y-diffrentHeight),rad ,Math.PI * 2, false);
    
    context.fillStyle = "rgba(0,255,0,.1)";
    context.fill();
}
function  calline(x1,y1,x2,y2){
    var mult = 1;
    var height = stageheight*currentzoom;
    var ratio = height / 512;
    var pixSpace = $('#pixelspacingoly').html().split('\\');
    var xDiff = x1 -x2;
    var yDiff = y1 - y2;
    var xHeaderSpacing,yHeaderSpacing;
    xHeaderSpacing=pixSpace[1];
    yHeaderSpacing=pixSpace[0];
    xDiff /= ratio;
    yDiff /= ratio;
    var xSpacing = 1;
    var ySpacing = 1;
    var units = "px";
    if (xHeaderSpacing && yHeaderSpacing){ 
        units = "mm";
        var xMult = getFloatShift(xHeaderSpacing);
        var yMult = getFloatShift(yHeaderSpacing);
        mult = Math.max(xMult, yMult);
        xSpacing = xHeaderSpacing;
        ySpacing = yHeaderSpacing;
    }
    var xDistance = mult * xSpacing * xDiff;
    var yDistance = mult * ySpacing * yDiff;
    var measurement = Math.sqrt((Math.pow(xDistance,2) + Math.pow(yDistance,2))/Math.pow(mult,2));
    measurement = measurement.toFixed(2) ;
    measurementData=measurement+" "+units;
//console.log("........measurement "+measurementData);
};
function  calcircle(x1,y1,x2,y2){
    var mult = 1;
    var height = stageheight*currentzoom;
    var ratio = height / 512;
    var pixSpace = $('#pixelspacingoly').html().split('\\');
    var xDiff = x1 -x2;
    var yDiff = y1 - y2;
    var xHeaderSpacing,yHeaderSpacing;
    xHeaderSpacing=pixSpace[1];
    yHeaderSpacing=pixSpace[0];
    xDiff /= ratio;
    yDiff /= ratio;
    var xSpacing = 1;
    var ySpacing = 1;
    var units = "px";
    if (xHeaderSpacing && yHeaderSpacing){ 
        units = " mm";
        var xMult = getFloatShift(xHeaderSpacing);
        var yMult = getFloatShift(yHeaderSpacing);
        mult = Math.max(xMult, yMult);
        xSpacing = xHeaderSpacing;
        ySpacing = yHeaderSpacing;
    }
    var xDistance = mult * xSpacing * xDiff;
    var yDistance = mult * ySpacing * yDiff;
    var measurement = Math.sqrt((Math.pow(xDistance,2) + Math.pow(yDistance,2))/Math.pow(mult,2));
    measurement = measurement.toFixed(2) ;
    var rads=(measurement/20);
    
    var area=(rads)*(rads)*Math.PI; 
    area=area.toFixed(2) ;
    if(units=="px"){
        area="None";
    }
    drawmeasure(measurement+units,area);
};
var getFloatShift = function (floatNum) {
    var decimalLen = 0;
    var floatElements = floatNum.toString().split('\.');
    // If the array is one element, float was an integer, with decimal length of 0
    // If it was 2, there is a decimal portion, get the length to be used to shift to integer
    if (floatElements.length === 2){
        decimalLen = floatElements[1].length; // how many decimals in the pixelSpacing String
    }
    var mult = Math.pow(10,decimalLen);
    return mult;
};
function drawmeasure(val,area){
    var canvas = document.getElementById('measure');
    var context = canvas.getContext('2d');
    var x = canvas.width / 2;
    var y = canvas.height / 4;
    context.clearRect(0, 0, canvas.width, canvas.height);
    context.font      = "bold 35px Arial";
    context.textAlign = 'center';
    context.fillStyle = '#9AFE2E';
    context.fillText("Dia."+":"+" "+val, x, y);
    context.fillText("Area"+":"+" "+area+" cm"+String.fromCharCode(178), x, y+40);
}