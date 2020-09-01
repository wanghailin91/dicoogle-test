package in.raster.ioviyam2.xml.handler;

import in.raster.ioviyam2.xml.model.Configuration;
import in.raster.ioviyam2.xml.model.Server;
import java.io.File;
import java.util.List;
import org.apache.log4j.Logger;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

public class ServerHandler
{
  private static Logger log = Logger.getLogger(ServerHandler.class);

  private Serializer serializer = null;
  private File source = null;
  private Configuration config = null;

  public ServerHandler()
  {
    try {
      this.serializer = new Persister();
      this.source = new File(new XMLFileHandler().getXMLFilePath());
      this.config = ((Configuration)this.serializer.read(Configuration.class, this.source));
    } catch (Exception ex) {
      log.error("Unable to read XML document", ex);
      return;
    }
  }

  public void addNewServer(Server server) {
    try {
      List serversList = this.config.getServersList();
      serversList.add(server);
      this.serializer.write(this.config, this.source);
    } catch (Exception ex) {
      log.error("Unable to add new server", ex);
    }
  }

  public void deleteExistingServer(Server server) {
    try {
      List<Server> serversList = this.config.getServersList();
      for (Server serObj : serversList) {
        if ((serObj.getAetitle().equals(server.getAetitle())) && (serObj.getHostname().equals(server.getHostname())) && (serObj.getPort().equals(server.getPort()))) {
          serversList.remove(serObj);
          break;
        }
      }
      this.serializer.write(this.config, this.source);
    } catch (Exception ex) {
      log.error("Unable to delete existing server", ex);
    }
  }

  public void editExistingServer(Server server) {
    try {
      List<Server> serversList = this.config.getServersList();
      for (Server serObj : serversList) {
        if (serObj.getLogicalname().equals(server.getLogicalname())) {
          serObj.setAetitle(server.getAetitle());
          serObj.setHostname(server.getHostname());
          serObj.setPort(server.getPort());
          serObj.setWadoport(server.getWadoport());
          break;
        }
      }
      this.serializer.write(this.config, this.source);
    } catch (Exception ex) {
      log.error("Unable to modify existing server", ex);
    }
  }

  public Server findServerByName(String serverName) {
    Server resultObj = null;

    List<Server> serversList = this.config.getServersList();

    if ((serverName != null) && (serverName.length() > 0)) {
      try {
        for (Server serObj : serversList)
          if (serObj.getLogicalname().equals(serverName)) {
            resultObj = serObj;
            break;
          }
      }
      catch (Exception ex) {
        log.error("Error while finding server by name");
      }
    }
    else if (serversList.size() == 1) {
      resultObj = (Server)serversList.get(0);
    }

    return resultObj;
  }
}