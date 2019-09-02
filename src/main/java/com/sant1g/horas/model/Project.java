package com.sant1g.horas.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;

@Entity
@JsonIgnoreProperties(ignoreUnknown = true)
public class Project implements Serializable {

  @Id
  private String id;
  private String name;
  private String clientName;
  private String color;

  @ManyToMany(mappedBy = "projects")
  private List<SlackUser> slackUsers;

  public Project() { }

  public List<SlackUser> getSlackUsers() {
    return slackUsers;
  }

  public void setSlackUsers(List<SlackUser> slackUsers) {
    this.slackUsers = slackUsers;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getClientName() {
    return clientName;
  }

  public void setClientName(String clientName) {
    this.clientName = clientName;
  }

  public String getColor() {
    return color;
  }

  public void setColor(String color) {
    this.color = color;
  }
}
