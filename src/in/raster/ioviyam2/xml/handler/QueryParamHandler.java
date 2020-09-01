package in.raster.ioviyam2.xml.handler;

import in.raster.ioviyam2.xml.model.Button;
import in.raster.ioviyam2.xml.model.Configuration;
import in.raster.ioviyam2.xml.model.SearchParams;
import in.raster.ioviyam2.xml.model.User;
import java.io.File;
import java.util.List;
import org.apache.log4j.Logger;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

public class QueryParamHandler
{
  private static Logger log = Logger.getLogger(QueryParamHandler.class);

  private Serializer serializer = null;
  private File source = null;
  Configuration config = null;

  public QueryParamHandler() {
    try {
      this.serializer = new Persister();
      this.source = new File(new XMLFileHandler().getXMLFilePath());

      this.config = ((Configuration)this.serializer.read(Configuration.class, this.source));
    } catch (Exception ex) {
      log.error("Error while creating Query Parameter Handler object", ex);
    }
  }

  public void addNewButton(Button btn, String userName) {
    UserHandler uh = new UserHandler();
    User user = uh.findUserByName(userName);

    if (user != null) {
      SearchParams sp = user.getSearchParams();
      List btnsList = sp.getButtonsList();
      btnsList.add(btn);
      uh.updateUser(user);
    } else {
      uh.addNewUser(btn, userName);
    }
  }

  public void deleteExistingButton(Button btn, String userName) {
    try {
      List<User> usersList = this.config.getUsersList();
      for (User user : usersList) {
        if (user.getUserName().equals(userName)) {
          SearchParams sp = user.getSearchParams();
          List<Button> btnsList = sp.getButtonsList();
          for (Button btnTmp : btnsList) {
            if (btnTmp.getLabel().equals(btn.getLabel())) {
              btnsList.remove(btnTmp);
              break;
            }
          }
          break;
        }
      }
      this.serializer.write(this.config, this.source);
    } catch (Exception ex) {
      log.error("Error while deleting existing button", ex);
    }
  }

  public List<Button> getAllButtons(String userName) {
    List<User> usersList = this.config.getUsersList();
    List btnsList = null;
    for (User user : usersList) {
      if (user.getUserName().equals(userName)) {
        SearchParams sp = user.getSearchParams();
        btnsList = sp.getButtonsList();
        break;
      }

    }

    return btnsList;
  }
}