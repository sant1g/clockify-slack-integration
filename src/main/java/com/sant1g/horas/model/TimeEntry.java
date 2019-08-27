package com.sant1g.horas.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
public class TimeEntry implements Serializable {

  @Id
  @GeneratedValue
  private Long id;

  @ManyToOne
  @JoinColumn(name = "slack_user_id", nullable = false)
  private SlackUser slackUser;

  @JsonProperty("project_id")
  private String projectId;

  private String message;

  @Temporal(TemporalType.TIMESTAMP)
  private Date date;

  public TimeEntry() {
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public SlackUser getSlackUser() {
    return slackUser;
  }

  public void setSlackUser(SlackUser slackUser) {
    this.slackUser = slackUser;
  }

  public String getProjectId() {
    return projectId;
  }

  public void setProjectId(String projectId) {
    this.projectId = projectId;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public Date getDate() {
    return date;
  }

  public void setDate(Date date) {
    this.date = date;
  }
}
