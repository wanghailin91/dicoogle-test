package in.raster.ioviyam2.xml.handler;

import in.raster.ioviyam2.xml.model.Button;
import in.raster.ioviyam2.xml.model.Configuration;
import in.raster.ioviyam2.xml.model.SearchParams;
import in.raster.ioviyam2.xml.model.User;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

public class UserHandler
{
  private static Logger log = Logger.getLogger(QueryParamHandler.class);

  private Serializer serializer = null;
  private File source = null;
  Configuration config = null;

  public UserHandler() {
    try {
      this.serializer = new Persister();
      this.source = new File(new XMLFileHandler().getXMLFilePath());
      this.config = ((Configuration)this.serializer.read(Configuration.class, this.source));
    } catch (Exception ex) {
      log.error("Error while creating Query Parameter Handler object", ex);
    }
  }

  public void updateUser(User user) {
    try {
      List<User> usersList = this.config.getUsersList();
      for (User usr : usersList) {
        if (usr.getUserName().equals(user.getUserName())) {
          usr.setSearchParams(user.getSearchParams());
          usr.setSessTimeout(user.getSessTimeout());
          usr.setTheme(user.getTheme());
          usr.setUserName(user.getUserName());
          break;
        }
      }
      this.serializer.write(this.config, this.source);
    } catch (Exception ex) {
      log.error("Error while updating user", ex);
    }
  }

  public void addNewUser(Button btn, String userName)
  {
    try {
      List usersList = this.config.getUsersList();
      User newUser = new User();
      newUser.setUserName(userName);
      newUser.setSessTimeout("1800");
      newUser.setTheme("Dark Hive");
      newUser.setViewerSlider("hide");
      SearchParams spTmp = new SearchParams();
      List newBtnList = new ArrayList();
      newBtnList.add(btn);
      spTmp.setButtonsList(newBtnList);
      newUser.setSearchParams(spTmp);
      usersList.add(newUser);
      this.serializer.write(this.config, this.source);
    } catch (Exception ex) {
      log.error("Exception while creating new user", ex);
    }
  }

  public User findUserByName(String userName) {
    List<User> usersList = this.config.getUsersList();
    User currUser = null;
    for (User user : usersList) {
      if (user.getUserName().equals(userName)) {
        currUser = user;
        break;
      }
    }

    return currUser;
  }
}