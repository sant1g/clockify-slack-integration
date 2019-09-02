package com.sant1g.horas.request;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.sant1g.horas.model.Block;
import com.sant1g.horas.model.History;
import com.sant1g.horas.model.Project;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SlackMessageRequest {

  @JsonProperty("response_type")
  private String responseType = "ephemeral";

  @JsonProperty("replace_original")
  private Boolean replaceOriginal = true;

  @JsonProperty("delete_original")
  private Boolean deleteOriginal = true;

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

  public Boolean getReplaceOriginal() {
    return replaceOriginal;
  }

  public void setReplaceOriginal(Boolean replaceOriginal) {
    this.replaceOriginal = replaceOriginal;
  }

  public Boolean getDeleteOriginal() {
    return deleteOriginal;
  }

  public void setDeleteOriginal(Boolean deleteOriginal) {
    this.deleteOriginal = deleteOriginal;
  }

  public static class Builder {

    private List<Block> blocks = new ArrayList<>();
    private Block header = new Block.Builder("section").text("Select your active project :pencil:").build();
    private Block divider = new Block.Builder("divider").build();

    public Builder(List<Project> projects) {
      blocks.add(new Block.Builder("section").text(":calendar: Pick a date").datepicker().build());
      blocks.add(header);
      blocks.add(divider);
      projects.forEach(project -> {
        String name = !project.getClientName().equals("") ? project.getName().concat(" (")
            .concat(project.getClientName()).concat(")") : project.getName();
        blocks.add(new Block.Builder("section").text(name).button(project.getId()).build());
      });
    }

    public Builder(List<History> entries, Boolean history) {
      blocks.add(new Block.Builder("section").text(":alarm_clock: Your past 10 entries:").build());
      blocks.add(divider);
      entries.forEach(entry -> {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM");
        String date = sdf.format(entry.getDate());
        String text = String.format("*[%s]* %s (%s)\n\t• %s",
            date,
            entry.getProject().getName(),
            entry.getProject().getClientName(),
            entry.getMessage());
        blocks.add(new Block.Builder("section").text(text).build());
      });
    }

    public Builder(List<Date> dates, Integer days) {
      blocks.add(new Block.Builder("section").text(":back: Days without entries:").build());
      blocks.add(divider);
      if (dates.size() > 0) {
        dates.forEach(date -> {
          SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
          blocks.add(new Block.Builder("section").text("• " + sdf.format(date)).build());
        });
      } else {
        blocks.add(new Block.Builder("section").text("Nothing to show. You are up to date :agite:").build());
      }
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
