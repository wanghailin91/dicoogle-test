/**
 *  DicomParser.js
 *  Version 0.5
 *  Author: BabuHussain<babuhussain.a@raster.in>
 */
var elementIndex = 0;
function DicomParser(inputBuffer, reader)
{
    this.inputBuffer = inputBuffer;
    this.reader = reader;
    this.dicomElement;
    this.parseAll = parseAll;
    this.pixelBuffer;
    this.pixelDataOffset;
	elementIndex = 0;
    //this.imgEndIndex;
}

function getPixelBuffer()
{
    return this.pixelBuffer;
}

function parseAll()
{
    //read wc value
    var index = this.readTag(0, 40, 0, 80, 16, "windowCenter");
    //read ww value
    index = this.readTag(index, 40, 0, 81, 16, "windowWidth");
    var ii = index;
    //read rescale slope value
    index = this.readTag(index, 40, 0, 82, 16, "rescaleIntercept");
    //read rescale intercept value
    index = this.readTag(index, 40, 0, 83, 16, "rescaleSlope");
    //move to pixel data
    index = this.moveToPixelDataTag(index);
    //read pixel data
    this.readImage(index);
}

DicomParser.prototype.setDicomElement = function(name, vr, vl, group, element, value, offset)
{
    if (this.dicomElement == null)
        this.dicomElement = new Array();

    this.dicomElement[elementIndex++] = new DicomElement(name, vr, vl, group, element, value, offset);
}

DicomParser.prototype.readTag = function(index, firstContent, secondContent, thirdContent, fourthContent, tagName)
{
    var i = index;
    var flag = false;
    for (; i < this.inputBuffer.length; i++)
    {
        if (this.reader.readNumber(1, i) == firstContent && this.reader.readNumber(1, i + 1) == secondContent && this.reader.readNumber(1, i + 2) == thirdContent && this.reader.readNumber(1, i + 3) == fourthContent)
        {
            i = i + 4;
            var vr = this.reader.readString(2, i);
            var vl = this.reader.readNumber(2, i + 2);
            var val = this.reader.readString(vl, i + 4);
            var tagValue = val.split("\\");
            this.setDicomElement(tagName, vr, vl, firstContent + secondContent, thirdContent + fourthContent, tagValue, i - 4);
//            console.log(tagName +" -- "+tagValue);
            i = i + 4 + vl;
            flag = true;
            break;

        }
    }
    if (flag) {
        return i;
    } else {
        return  index;
    }
}

DicomParser.prototype.moveToPixelDataTag = function(index)
{
   // alert(index + " -- " + this.inputBuffer.length);
    var i = index;
    for (; i < this.inputBuffer.length; i++)
    {

        if (this.reader.readNumber(1, i) == 224 && this.reader.readNumber(1, i + 1) == 127 && this.reader.readNumber(1, i + 2) == 16 && this.reader.readNumber(1, i + 3) == 0)
        {
            i = i + 4;
            var vr = this.reader.readString(2, i);
            var vl = this.reader.readNumber(2, i + 2);
            //i=i+28;
            //i=i+32;

            break;
        }
    }
    this.pixelDataOffset = i;
    return i;
}

DicomParser.prototype.readImage = function(index)
{
    this.pixelBuffer = new Array();
    var i = index;
    var pixelIndex = 0;
//    console.log(this.reader)
    for (; i < this.inputBuffer.length; i += 2)
    {
        this.pixelBuffer[pixelIndex] = this.reader.readNumber(2, i);
       // document.write(this.pixelBuffer[pixelIndex] + "__");

        pixelIndex++;

    }
    //return pixelIndex;

}