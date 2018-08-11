package me.connorjayr.stratustm.stratus.profile.objective;

import java.net.URL;

public abstract class Objective {

  protected final String name;
  protected final String map;
  protected final URL match;

  public Objective(String name, String map, URL match) {
    this.name = name;
    this.map = map;
    this.match = match;
  }

  public String getName() {
    return name;
  }

  public String getMap() {
    return map;
  }

  public URL getMatch() {
    return match;
  }

}
