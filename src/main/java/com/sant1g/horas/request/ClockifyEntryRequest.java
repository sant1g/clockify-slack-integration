package com.sant1g.horas.request;

public class ClockifyEntryRequest {

  private String start;
  private String billable = "true";
  private String description;
  private String projectId;
  private String end;

  public ClockifyEntryRequest() {}

  public String getStart() {
    return start;
  }

  public void setStart(String start) {
    this.start = start;
  }

  public String getBillable() {
    return billable;
  }

  public void setBillable(String billable) {
    this.billable = billable;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getProjectId() {
    return projectId;
  }

  public void setProjectId(String projectId) {
    this.projectId = projectId;
  }

  public String getEnd() {
    return end;
  }

  public void setEnd(String end) {
    this.end = end;
  }
}
