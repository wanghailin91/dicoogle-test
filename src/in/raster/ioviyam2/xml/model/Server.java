package in.raster.ioviyam2.xml.model;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root
public class Server
{

  @Element
  private String logicalname;

  @Element
  private String aetitle;

  @Element
  private String hostname;

  @Element
  private String port;

  @Element
  private String retrieve;

  @Element(required=false)
  private String wadocontext;

  @Element(required=false)
  private String wadoport;

  public String getAetitle()
  {
    return this.aetitle;
  }

  public void setAetitle(String aetitle) {
    this.aetitle = aetitle;
  }

  public String getHostname() {
    return this.hostname;
  }

  public void setHostname(String hostname) {
    this.hostname = hostname;
  }

  public String getLogicalname() {
    return this.logicalname;
  }

  public void setLogicalname(String logicalname) {
    this.logicalname = logicalname;
  }

  public String getPort() {
    return this.port;
  }

  public void setPort(String port) {
    this.port = port;
  }

  public String getWadoport() {
    return this.wadoport;
  }

  public void setWadoport(String wadoport) {
    this.wadoport = wadoport;
  }

  public String getRetrieve() {
    return this.retrieve;
  }

  public void setRetrieve(String retrieve) {
    this.retrieve = retrieve;
  }

  public String getWadocontext() {
    return this.wadocontext;
  }

  public void setWadocontext(String wadocontext) {
    this.wadocontext = wadocontext;
  }
}