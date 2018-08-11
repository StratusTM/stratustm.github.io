package me.connorjayr.stratustm.stratus.profile.objective;

import java.awt.Color;
import java.net.URL;

public class Wool extends Objective {

  private final Color color;

  public Wool(String name, Color color, String map, URL match) {
    super(name, map, match);
    this.color = color;
  }

  public Color getColor() {
    return color;
  }

}
