package in.raster.ioviyam2.xml.handler;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import org.apache.log4j.Logger;
import org.jboss.system.server.ServerConfig;
import org.jboss.system.server.ServerConfigLocator;

public class XMLFileHandler
{
  private static Logger log = Logger.getLogger(XMLFileHandler.class);
  File filelocation = ServerConfigLocator.locate().getServerHomeDir();
  String location = this.filelocation.getAbsolutePath() + File.separator;

  public String getXMLFilePath() { String xmlFilePath = getClass().getResource("/conf/oviyam2-config.xml").getPath();
    String fname = "oviyam2-config.xml";
    String retValue = this.location + File.separator + "oviyam2-config.xml";
    if (xmlFilePath.indexOf("default") > 0) try { File srcFile = new File(getClass().getResource("/conf/oviyam2-config.xml").toURI());
        retValue = xmlFilePath.substring(0, xmlFilePath.indexOf("default")) + "default" + File.separator + fname;
        File destFile = new File(retValue);

        if (destFile.exists());
      } catch (URISyntaxException ex) {
        log.error("Error while getting XML file path", ex);
        return "";
      }

    return retValue; }

  private void copyFile(File src, File dest)
  {
    FileInputStream in = null;
    FileOutputStream out = null;
    try {
      in = new FileInputStream(src);
      out = new FileOutputStream(dest);
      byte[] buffer = new byte[1024];
      int len;
      while ((len = in.read(buffer)) > 0)
        out.write(buffer, 0, len);
    }
    catch (Exception ex) {
      log.error("Error while copying XML File", ex);
    } finally {
      try {
        in.close();
        out.close();
      } catch (IOException ex) {
        log.error("Error while closing file", ex);
      }
    }
  }
}