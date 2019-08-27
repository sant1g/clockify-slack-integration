package com.sant1g.horas.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

public class Block {

  private String type;

  @JsonInclude(Include.NON_NULL)
  private Text text;

  @JsonInclude(Include.NON_NULL)
  private Accessory accessory;

  public Block() {}

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

  public Accessory getAccessory() {
    return accessory;
  }

  public void setAccessory(Accessory accessory) {
    this.accessory = accessory;
  }

  private static class Text {

    private String type = "mrkdwn";
    private String text;

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
  }

  private static class Accessory {

    private String type = "button";
    private Text text;
    private String value;

    public Accessory() {}

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

    public String getValue() {
      return value;
    }

    public void setValue(String value) {
      this.value = value;
    }
  }

  public static class Builder {

    private String type;
    private Text text;
    private Accessory accessory;

    public Builder(String type) {
      this.type = type;
    }

    public Builder text(String text) {
      this.text = new Text();
      this.text.setText(text);

      return this;
    }

    public Builder button(String buttonValue) {
      this.accessory = new Accessory();
      this.accessory.text = new Text();
      this.accessory.text.setType("plain_text");
      this.accessory.text.setText("Select");
      this.accessory.setValue(buttonValue);

      return this;
    }

    public Block build() {
      Block block = new Block();
      block.setType(type);
      block.setText(text);
      block.setAccessory(accessory);

      return block;
    }
  }
}
