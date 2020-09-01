package in.raster.ioviyam2.xml.model;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root
public class User
{

  @Element(name="user-name")
  private String userName;

  @Element(name="search-params")
  private SearchParams searchParams;

  @Element
  private String theme;

  @Element(name="viewer-slider")
  private String viewerSlider;

  @Element(name="session-timeout")
  private String sessTimeout;

  public SearchParams getSearchParams()
  {
    return this.searchParams;
  }

  public void setSearchParams(SearchParams searchParams) {
    this.searchParams = searchParams;
  }

  public String getSessTimeout() {
    return this.sessTimeout;
  }

  public void setSessTimeout(String sessTimeout) {
    this.sessTimeout = sessTimeout;
  }

  public String getTheme() {
    return this.theme;
  }

  public void setTheme(String theme) {
    this.theme = theme;
  }

  public String getUserName() {
    return this.userName;
  }

  public void setUserName(String userName) {
    this.userName = userName;
  }

  public String getViewerSlider() {
    return this.viewerSlider;
  }

  public void setViewerSlider(String viewerSlider) {
    this.viewerSlider = viewerSlider;
  }
}