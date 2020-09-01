package in.raster.ioviyam2.xml.model;

import java.util.List;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

@Root(name="configuration")
public class Configuration
{

  @ElementList(name="users")
  private List<User> usersList;

  @ElementList(name="servers")
  private List<Server> serversList;

  @Element
  private Listener listener;

  @ElementList(name="languages")
  private List<Language> languagesList;

  public List<Language> getLanguagesList()
  {
    return this.languagesList;
  }

  public void setLanguagesList(List<Language> languagesList) {
    this.languagesList = languagesList;
  }

  public List<Server> getServersList() {
    return this.serversList;
  }

  public void setServersList(List<Server> serversList) {
    this.serversList = serversList;
  }

  public List<User> getUsersList() {
    return this.usersList;
  }

  public void setUsersList(List<User> usersList) {
    this.usersList = usersList;
  }

  public Listener getListener() {
    return this.listener;
  }

  public void setListener(Listener listener) {
    this.listener = listener;
  }
}