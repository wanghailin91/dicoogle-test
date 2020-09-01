package in.raster.ioviyam2.xml.model;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Path;

@Path("configuration")
public class Listener
{

  @Element
  private String aetitle;

  @Element
  private String port;

  public String getAetitle()
  {
    return this.aetitle;
  }

  public void setAetitle(String aetitle) {
    this.aetitle = aetitle;
  }

  public String getPort() {
    return this.port;
  }

  public void setPort(String port) {
    this.port = port;
  }
}