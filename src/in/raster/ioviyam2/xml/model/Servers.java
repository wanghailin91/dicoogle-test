package in.raster.ioviyam2.xml.model;

import java.util.List;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Path;

@Path("configuration")
public class Servers
{

  @ElementList(name="servers")
  private List<Server> serversList;

  public List<Server> getServersList()
  {
    return this.serversList;
  }

  public void setServersList(List<Server> serversList) {
    this.serversList = serversList;
  }
}