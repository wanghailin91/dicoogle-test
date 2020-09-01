package in.raster.ioviyam2.servlets;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.Properties;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ImageServlet extends HttpServlet{
  File filelocation = new File(System.getProperty("user.dir"));
  String location = this.filelocation.getAbsolutePath() + File.separator;

  protected void processRequest(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException{
    try {
      String imageURL = "";
      String study = request.getParameter("studyUID");
      String series = request.getParameter("seriesUID");
      String object = request.getParameter("objectUID");
      String type = request.getParameter("type");
      String framenumber = request.getParameter("framedata");
      if (framenumber == null) {
        framenumber = "Empty";
      }
      Properties prop = new Properties();
      prop.load(new FileInputStream(this.location + "iOviyam.properties"));
      if ((type != null) && (!type.equalsIgnoreCase("")))
        imageURL = "http://" + prop.getProperty("Host") + ":" + prop.getProperty("wado") + "/wado/?requestType=WADO&studyUID=" + study + "&seriesUID=" + series + "&objectUID=" + object + "&transferSyntax=1.2.840.100008.1.2&contentType=application/dicom";
      else if (!framenumber.equals("Empty")){
        //imageURL = "http://" + prop.getProperty("Host") + ":" + prop.getProperty("wado") + "/wado?requestType=WADO&studyUID=" + study + "&seriesUID=" + series + "&objectUID=" + object + "&frameNumber=" + framenumber;
	     imageURL="http://" + prop.getProperty("Host") + ":" + prop.getProperty("wado") + "/wado/?requestType=WADO&studyUID=" + study + "&seriesUID=" + series + "&objectUID=" + object + "&frameNumber=" + framenumber;
      }else {
        //imageURL = "http://" + prop.getProperty("Host") + ":" + prop.getProperty("wado") + "/wado?requestType=WADO&studyUID=" + study + "&seriesUID=" + series + "&objectUID=" + object + "&rows=512&coloumns=512";
        imageURL="http://" + prop.getProperty("Host") + ":" + prop.getProperty("wado") + "/wado/?requestType=WADO&studyUID=" + study + "&seriesUID=" + series + "&objectUID=" + object;
      }
      InputStream resultInStream = null;
      OutputStream resultOutStream = response.getOutputStream();
      try{
        URL imgURL = new URL(imageURL);
        System.out.println("imageURL:"+imageURL);
        if ((object != null) && (object.length() > 0)) {
        	resultInStream = imgURL.openStream();
        	byte[] buffer = new byte[4096];
        	int bytes_read;
        	while ((bytes_read = resultInStream.read(buffer)) != -1) {
        		resultOutStream.write(buffer, 0, bytes_read);
        	}
        }
        resultOutStream.flush();
        resultOutStream.close();
        resultInStream.close();
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
    finally
    {
    }
  }

  protected void doGet(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
    processRequest(request, response);
  }

  protected void doPost(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException{
    processRequest(request, response);
  }

  public String getServletInfo() {
    return "Short description";
  }
}