
 

$(document).ready(function() {
     $.mobile.showPageLoadingMsg();
    $("#patientback").click(function() {
         $(this).addClass( $.mobile.activeBtnClass );
        window.location='home.html';
    });
    //console.log("From : "+ localStorage.getItem("localfromdate") + "To : " + localStorage.getItem("localtodate"));
    $.post("study.do", {
        "patientID":  localStorage.getItem("localpatid"),
        "patientName":localStorage.getItem("localpatname"),
        "Acc-no":localStorage.getItem("localaccno"),
        "Birthdate":localStorage.getItem("localbirthdate"),
        "Modality": localStorage.getItem("localmodality"),
        "From": localStorage.getItem("localfromdate"),
        "To": localStorage.getItem("localtodate"),
        "ae":localStorage.getItem("aetitle"),
        "tfrom":localStorage.getItem("localfromtime"),
        "tto":localStorage.getItem("localtotime"),
        "host":localStorage.getItem("hostname"),
        "port":localStorage.getItem("port"),
        "wadoport":localStorage.getItem("wado")
    },function(data){ 
        var i=0;
        $('#listviewuldata').html('');
        $('#numbrofstdy').html(data.length+"-"+'Records');
        $.each(data, function(i, row) {
            $('#listviewuldata').append(' <li id=studylist'+i +' data-theme="a"  patid='+row['PatientId']+','+row['PatientName']+','+row['thickness']+' '+'studyid='+row['studyid']+ ' '+'isex='+row['sex']+','+row['StudyDate']+  ' '+'imodality='+row['Modality']+ ' '+'istudydesc='+row['studydesc']+' >'+' <a href='+"#" + " "+ '"data-transition="none">'+
                '<label style="font: bold  15px helvetica;color:#AAAAAA;">'
                +row['PatientName']+
                '</label>'+'<br><label style=" font:  14px helvetica;color:#AAAAAA;">'
                +row['StudyDate']+
                 '</label>'+'<br><label style=" font:  14px helvetica;color:#AAAAAA;">'+
                ' '+"("+row['seriesno']+" series)"+
                 '</label>'+'<span style="font: bold 13px helvetica !important;" class="ui-li-count">'+
                row['Modality']
                +'</span>'+'</a>'+'</li>').listview('refresh');
        });
       i++;
        $('#listviewuldata').children('li').on('click', function() {
            $(this).addClass($.mobile.activeBtnClass);
            var   patient=$(this).index();
            localStorage.setItem("patiddata", $('#studylist'+patient).attr('patid'));
            localStorage.setItem("studyid", $('#studylist'+patient).attr('studyid'));
            localStorage.setItem("isex", $('#studylist'+patient).attr('isex'));
            localStorage.setItem("imodality", $('#studylist'+patient).attr('imodality'));
            localStorage.setItem("istudydesc", $('#studylist'+patient).attr('istudydesc'));
            window.location='series.html';
        });
         $.mobile.hidePageLoadingMsg();
    });
});