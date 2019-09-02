package com.sant1g.horas.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SlackOptionResponse {

  private String type;
  private SlackUser user;
  private List<Action> actions;

  @JsonProperty("response_url")
  private String responseUrl;

  public SlackOptionResponse() {}

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public SlackUser getUser() {
    return user;
  }

  public void setUser(SlackUser user) {
    this.user = user;
  }

  public List<Action> getActions() {
    return actions;
  }

  public void setActions(List<Action> actions) {
    this.actions = actions;
  }

  public String getResponseUrl() {
    return responseUrl;
  }

  public void setResponseUrl(String responseUrl) {
    this.responseUrl = responseUrl;
  }

  public static class SlackUser {

    private String id;
    private String username;
    private String name;

    @JsonProperty("team_id")
    private String teamId;

    public SlackUser() {}

    public String getId() {
      return id;
    }

    public void setId(String id) {
      this.id = id;
    }

    public String getUsername() {
      return username;
    }

    public void setUsername(String username) {
      this.username = username;
    }

    public String getTeamId() {
      return teamId;
    }

    public void setTeamId(String teamId) {
      this.teamId = teamId;
    }

    public String getName() {
      return name;
    }

    public void setName(String name) {
      this.name = name;
    }
  }

  @JsonIgnoreProperties(ignoreUnknown = true)
  public static class Action {

    @JsonProperty("action_id")
    private String actionId;

    @JsonProperty("block_id")
    private String blockId;

    private String value;
    private String type;
    private Text text;

    @JsonProperty("selected_date")
    private String selectedDate;

    @JsonProperty("action_ts")
    private String actionTs;

    public Action() {}

    public String getActionId() {
      return actionId;
    }

    public void setActionId(String actionId) {
      this.actionId = actionId;
    }

    public String getBlockId() {
      return blockId;
    }

    public void setBlockId(String blockId) {
      this.blockId = blockId;
    }

    public String getValue() {
      return value;
    }

    public void setValue(String value) {
      this.value = value;
    }

    public String getType() {
      return type;
    }

    public void setType(String type) {
      this.type = type;
    }

    public Text getText() {
      return text;
    }

    public void setText(Text text) {
      this.text = text;
    }

    public String getSelectedDate() {
      return selectedDate;
    }

    public void setSelectedDate(String selectedDate) {
      this.selectedDate = selectedDate;
    }

    public String getActionTs() {
      return actionTs;
    }

    public void setActionTs(String actionTs) {
      this.actionTs = actionTs;
    }

    public static class Text {

      private String type;
      private String text;
      private Boolean emoji;

      public Text() {}

      public String getType() {
        return type;
      }

      public void setType(String type) {
        this.type = type;
      }

      public String getText() {
        return text;
      }

      public void setText(String text) {
        this.text = text;
      }

      public Boolean getEmoji() {
        return emoji;
      }

      public void setEmoji(Boolean emoji) {
        this.emoji = emoji;
      }
    }
  }
}
