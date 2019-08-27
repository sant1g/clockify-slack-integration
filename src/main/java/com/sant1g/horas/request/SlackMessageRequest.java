package com.sant1g.horas.request;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.sant1g.horas.model.Block;
import com.sant1g.horas.model.Project;

import java.util.ArrayList;
import java.util.List;

public class SlackMessageRequest {

  @JsonProperty("response_type")
  private String responseType = "ephemeral";
  private List<Block> blocks;

  public SlackMessageRequest() {}

  public String getResponseType() {
    return responseType;
  }

  public void setResponseType(String responseType) {
    this.responseType = responseType;
  }

  public List<Block> getBlocks() {
    return blocks;
  }

  public void setBlocks(List<Block> blocks) {
    this.blocks = blocks;
  }

  public static class Builder {

    List<Block> blocks = new ArrayList<>();
    Block header = new Block.Builder("section").text("Select your active project :pencil:").build();
    Block divider = new Block.Builder("divider").build();

    public Builder(List<Project> projects) {
      blocks.add(header);
      blocks.add(divider);
      projects.forEach(project -> {
        String name = !project.getClientName().equals("") ? project.getName().concat(" (")
            .concat(project.getClientName()).concat(")") : project.getName();
        blocks.add(new Block.Builder("section").text(name).button(project.getId()).build());
      });
    }

    public Builder(String message) {
      blocks.add(new Block.Builder("section").text(message).build());
    }

    public SlackMessageRequest build() {
      SlackMessageRequest request = new SlackMessageRequest();
      request.setBlocks(blocks);

      return request;
    }
  }
}
