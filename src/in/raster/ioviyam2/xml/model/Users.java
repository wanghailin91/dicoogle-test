package in.raster.ioviyam2.xml.model;

import java.util.List;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Path;

@Path("configuration")
public class Users
{

  @ElementList(name="users")
  private List<User> usersList;

  public List<User> getUsersList()
  {
    return this.usersList;
  }

  public void setUsersList(List<User> usersList) {
    this.usersList = usersList;
  }
}