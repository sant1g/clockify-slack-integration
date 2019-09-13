package com.sant1g.horas.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;

@Entity
public class SlackUser implements Serializable {

  @Id
  @GeneratedValue
  private Long id;

  @JsonProperty("user_id")
  private String userId;

  @JsonProperty("api_key")
  private String apiKey;

  @JsonProperty("userName")
  private String userName;

  @JsonProperty
  private String clockifyId;

  @ManyToMany(cascade = CascadeType.ALL)
  @JoinTable(
      name = "slack_user_project",
      joinColumns = @JoinColumn(name = "slack_user_id"),
      inverseJoinColumns = @JoinColumn(name = "project_id"))
  private List<Project> projects;

  public SlackUser() {}

  public String getUserId() {
    return userId;
  }

  public void setUserId(String userId) {
    this.userId = userId;
  }

  public String getApiKey() {
    return apiKey;
  }

  public void setApiKey(String apiKey) {
    this.apiKey = apiKey;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getUserName() {
    return userName;
  }

  public void setUserName(String userName) {
    this.userName = userName;
  }

  public List<Project> getProjects() {
    return projects;
  }

  public void setProjects(List<Project> projects) {
    this.projects = projects;
  }

  public String getClockifyId() {
    return clockifyId;
  }

  public void setClockifyId(String clockifyId) {
    this.clockifyId = clockifyId;
  }
}
