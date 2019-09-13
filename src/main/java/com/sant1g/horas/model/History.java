package com.sant1g.horas.model;

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
public class History implements Serializable {

  @Id
  @GeneratedValue
  private Long id;

  @ManyToOne
  @JoinColumn(name = "slack_user_id", nullable = false)
  private SlackUser slackUser;

  @ManyToOne
  @JoinColumn(name = "project_id", nullable = false)
  private Project project;

  private String message;

  @Temporal(TemporalType.TIMESTAMP)
  private Date date;

  public History() {
  }

  public History(TimeEntry entry, Project project) {
    this.slackUser = entry.getSlackUser();
    this.project = project;
    this.message = entry.getMessage();
    this.date = entry.getDate();
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

  public Project getProject() {
    return project;
  }

  public void setProject(Project project) {
    this.project = project;
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
