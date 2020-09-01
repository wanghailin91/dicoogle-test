package in.raster.ioviyam2.xml.model;

import java.util.List;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

@Root(name="search-params")
public class SearchParams
{

  @ElementList(name="buttons")
  private List<Button> buttonsList;

  public List<Button> getButtonsList()
  {
    return this.buttonsList;
  }

  public void setButtonsList(List<Button> buttonsList) {
    this.buttonsList = buttonsList;
  }
}