
var wadoURL ;
var mouseLocX1;
var mouseLocX2;
var mouseLocY1;
var mouseLocY2;
var wc;
var ww;
var wcenter;
var wwidth;
var rescale_Slope;
var rescale_Intercept;	
var lookupTable;
var huLookupTable;	
var pixelBuffer = new Array();
var imageLoaded=0;
var row=512;
var column=512;
var canRow="256";
var canColumn="256";
var lookupObj;
var mousePressed=0;
var canvasImgz;
var canvas;
var ctx;	
var myImageData;
var counti=0;
var canvasImg=document.getElementById('imgcanvas');
String.prototype.replaceAll = function(pcFrom, pcTo){
    var i = this.indexOf(pcFrom);
    var c = this;
    while (i > -1){
        c = c.replace(pcFrom, pcTo);
        i = c.indexOf(pcFrom);
    }
    return c;
}	
function getBrowserSize(){
               
//    if(document.documentElement.clientWidth >512 && document.documentElement.clientHeight >512 ){
//        canRow="513";
//        canColumn="513";
//    }else{
//        canColumn = document.documentElement.clientWidth;
//        canRow=canColumn;
//    }       
}
function mouseDownHandler(evt){
    mousePressed=1;	 	
    if(imageLoaded==1){
        mouseLocX = evt.pageX - canvasImg.offsetLeft;
        mouseLocY = evt.pageY - canvasImg.offsetTop;
//        console.log(mouseLocX +'='+ evt.pageX +'-'+ canvasImg.offsetLeft)
    }
}
function mouseupHandler(evt){
    mousePressed=0;	 		
}
function mousemoveHandler(evt){
    try
    {
        if(imageLoaded==1)
        {	
            mouseLocX1 = evt.pageX - canvasImg.offsetLeft;
            mouseLocY1 = evt.pageY - canvasImg.offsetTop;
            if(mouseLocX1>=0&&mouseLocY1>=0&&mouseLocX1<canColumn&&mouseLocY1<canRow)
            {
                showHUvalue(mouseLocX1,mouseLocY1);
                if(mousePressed==1)
                {
                    imageLoaded=0;																	
                    var diffX=mouseLocX1-mouseLocX;
                    var diffY=mouseLocY-mouseLocY1;								
                    wc=parseInt(wc)+diffY;
                    ww=parseInt(ww)+diffX;						
                    showWindowingValue(wc,ww);	
                    lookupObj.setWindowingdata(wc,ww);	
                    counti++;
                    genImage();	
                    mouseLocX=mouseLocX1 			
                    mouseLocY=mouseLocY1;
                    imageLoaded=1;										
                }						
            }			
        }		
    }
    catch(err)
    {
        console.log(err);		
    }
	
}
function changePreset()
{	
//applyPreset(parseInt(document.getElementById("menu").options[document.getElementById("menu").selectedIndex].value));
	
}
function applyPreset(preset)	
{	
    switch (preset)
    {
        case 1:	
            wc=wcenter;
            ww=wwidth;
            lookupObj.setWindowingdata(wc,ww);		
            genImage();	
            break;
	
        case 2:	
            wc=350;
            ww=40;
            lookupObj.setWindowingdata(wc,ww);		
            genImage();	
            break;
	
        case 3:
            wc=-600;
            ww=1500;
            lookupObj.setWindowingdata(wc,ww);		
            genImage();
            break;
	
        case 4:
            wc=40;
            ww=80;
            lookupObj.setWindowingdata(wc,ww);		
            genImage();
            break;
	
        case 5:
            wc=480;
            ww=2500;
            lookupObj.setWindowingdata(wc,ww);		
            genImage();
            break;
	
        case 6:
            wc=90;
            ww=350;
            lookupObj.setWindowingdata(wc,ww);		
            genImage();
            break;
    }
}
function showHUvalue(x,y)
{
//                var t=(y*column)+x;		
//                var hupanel=document.getElementById("huDisplayPanel");
//                hupanel.innerHTML="X :"+x+" Y :"+y+" HU :"+huLookupTable[pixelBuffer[t]];
//                $("#wlwcoly").html("WC:WW");
	
}
function showWindowingValue(wcenter,wwidth)
{
    $("#wlwcoly").html("WC:"+''+wcenter+":WW:"+''+wwidth);
}
function loadDicom(wado, imgRow, imgColumn)
{
    row = imgRow;
    column = imgColumn;
    wadoURL = wado;
    parseAndLoadDicom();
}
function getContextPath()
{
    var path = top.location.pathname;
    if (document.all) {
        path = path.replace(/\\/g,"/");
    }
    path = path.substr(0,path.lastIndexOf("/")+1);		
    return path;
}
function parseAndLoadDicom()
{	
    var reader=new DicomInputStreamReader();	
    reader.readDicom(wadoURL+"&type=dicom");
    var dicomBuffer=reader.getInputBuffer();
    var dicomReader=reader.getReader();
    var dicomParser=new DicomParser(dicomBuffer,dicomReader);
    dicomParser.parseAll();		
    var elementindex=0;
    for(;elementindex<dicomParser.dicomElement.length;elementindex++)
    {
        var dicomElement=dicomParser.dicomElement[elementindex];			
        if(dicomElement.name=="windowWidth")
        {
            wwidth=ww=dicomElement.value[0];
        }
        else if(dicomElement.name=="windowCenter")
        {
            wcenter=wc=dicomElement.value[0];			
        }
        else if(dicomElement.name=="rescaleIntercept")
        {
            rescale_Intercept=parseInt(dicomElement.value);
        }
        else if(dicomElement.name=="rescaleSlope")
        {
            rescale_Slope=parseInt(dicomElement.value);	
        }
    }		
    pixelBuffer=dicomParser.pixelBuffer;			
    lookupObj=new LookupTable();
    
   // alert(",,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,"+wc+"wd"+ww+"rescaleinterseption: ..."+rescale_Slope+"...re"+rescale_Intercept);
    lookupObj.setData(wc,ww,rescale_Slope,rescale_Intercept);
    lookupObj.calculateHULookup();
    huLookupTable=lookupObj.huLookup;	
    canvas = document.createElement("canvas");
    canvas.setAttribute("width", column);
    canvas.setAttribute("height",row);
    ctx = canvas.getContext("2d");					
    ctx.fillRect(0,0,column,row);        
    myImageData = ctx.getImageData(0,0,column,row);
     $("#Tbtnclose").removeClass('Tbtnclosegrey').addClass("Tbtncloses");
     $('#Tbtnclose').bind('click',presetData);
    genImage();	
    imageLoaded=1;
}
function genImage()
{		
    lookupObj.calculateLookup();
    lookupTable=lookupObj.ylookup;
    var n=0;	
    for(var yPix=0; yPix<row; yPix++)
    {
        for(var xPix=0; xPix<column;xPix++)
        {	    
            var offset = (yPix * column + xPix) *4;					
            var pxValue=lookupTable[pixelBuffer[n]];
            n++;			   
            myImageData.data[offset]=	parseInt(pxValue);
            myImageData.data[offset+1]=	parseInt(pxValue);
            myImageData.data[offset+2]=	parseInt(pxValue);
        }
    }			
    ctx.putImageData(myImageData, 0,0);
    canvasImg=document.getElementById("imgcanvas");
    //canvasImg.setAttribute("width", column);
    //canvasImg.setAttribute("height", row);
    var contextTemp=canvasImg.getContext('2d');
    contextTemp.drawImage(canvas,0,0,canvasImg.width,canvasImg.height);
    $.mobile.hidePageLoadingMsg();
}
        
 