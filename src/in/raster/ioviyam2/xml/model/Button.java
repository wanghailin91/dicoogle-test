package in.raster.ioviyam2.xml.model;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root(name="button")
public class Button
{

  @Element(name="button-label")
  private String label;

  @Element(name="date-criteria", required=false)
  private String dateCrit;

  @Element(name="time-criteria", required=false)
  private String timeCrit;

  @Element(required=false)
  private String modality;

  @Element(name="auto-refresh")
  private String autoRefresh;

  public String getDateCrit()
  {
    return this.dateCrit;
  }

  public void setDateCrit(String dateCrit) {
    this.dateCrit = dateCrit;
  }

  public String getLabel() {
    return this.label;
  }

  public void setLabel(String label) {
    this.label = label;
  }

  public String getModality() {
    return this.modality;
  }

  public void setModality(String modality) {
    this.modality = modality;
  }

  public String getTimeCrit() {
    return this.timeCrit;
  }

  public void setTimeCrit(String timeCrit) {
    this.timeCrit = timeCrit;
  }

  public String getAutoRefresh() {
    return this.autoRefresh;
  }

  public void setAutoRefresh(String autoRefresh) {
    this.autoRefresh = autoRefresh;
  }
}