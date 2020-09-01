package in.raster.ioviyam2.xml.model;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root
public class Language
{

  @Element(name="lang")
  private String language;

  @Element(name="country")
  private String country;

  @Element(name="localeID")
  private String localeID;

  @Element(name="selected")
  private boolean selected;

  public boolean isSelected()
  {
    return this.selected;
  }

  public void setSelected(boolean selected) {
    this.selected = selected;
  }

  public String getLanguage() {
    return this.language;
  }

  public void setLanguage(String language) {
    this.language = language;
  }

  public String getCountry() {
    return this.country;
  }

  public void setCountry(String country) {
    this.country = country;
  }

  public String getLocaleID() {
    return this.localeID;
  }

  public void setLocaleID(String localeID) {
    this.localeID = localeID;
  }
}