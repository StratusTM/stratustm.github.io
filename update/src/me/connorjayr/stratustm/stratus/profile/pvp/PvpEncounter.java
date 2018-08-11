package me.connorjayr.stratustm.stratus.profile.pvp;

import java.net.URL;

public class PvpEncounter {

  private final String killer;
  private final String killed;

  private final String map;
  private final URL match;

  public PvpEncounter(String killer, String killed, String map, URL match) {
    this.killer = killer;
    this.killed = killed;
    this.map = map;
    this.match = match;
  }

  public String getKiller() {
    return killer;
  }

  public String getKilled() {
    return killed;
  }

  public String getMap() {
    return map;
  }

  public URL getMatch() {
    return match;
  }

}
