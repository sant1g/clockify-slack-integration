package com.sant1g.horas.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.sant1g.horas.model.Block.Accessory.Placeholder;
import java.text.SimpleDateFormat;
import java.util.Date;

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

  protected static class Accessory {

    private String type = "button";

    @JsonInclude(Include.NON_NULL)
    private Text text;

    @JsonInclude(Include.NON_NULL)
    private String value;

    @JsonInclude(Include.NON_NULL)
    @JsonProperty("initial_date")
    private String initialDate;

    @JsonInclude(Include.NON_NULL)
    private Placeholder placeholder;

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

    public String getInitialDate() {
      return initialDate;
    }

    public void setInitialDate(String initialDate) {
      this.initialDate = initialDate;
    }

    public Placeholder getPlaceholder() {
      return placeholder;
    }

    public void setPlaceholder(Placeholder placeholder) {
      this.placeholder = placeholder;
    }

    protected static class Placeholder {

      private String type = "plain_text";
      private String text = "Select a date";
      private Boolean emoji = true;

      public Placeholder() {
      }

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

    public Builder datepicker() {
      SimpleDateFormat dt = new SimpleDateFormat("yyyy-MM-dd");
      Accessory accessory = new Accessory();
      accessory.setType("datepicker");
      accessory.setInitialDate(dt.format(new Date()));
      accessory.setPlaceholder(new Placeholder());
      this.accessory = accessory;
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
