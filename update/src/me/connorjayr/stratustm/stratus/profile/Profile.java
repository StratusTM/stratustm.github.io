package me.connorjayr.stratustm.stratus.profile;

import me.connorjayr.stratustm.Update;
import me.connorjayr.stratustm.stratus.profile.objective.Objective;
import me.connorjayr.stratustm.stratus.profile.objective.Wool;
import me.connorjayr.stratustm.stratus.profile.pvp.PvpEncounter;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.awt.Color;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class Profile {

  private final String name;
  private final List<PvpEncounter> pvpEncounters;
  private final List<Objective> objectives;

  private Profile(String name, List<PvpEncounter> pvpEncounters, List<Objective> objectives) {
    this.name = name;
    this.pvpEncounters = pvpEncounters;
    this.objectives = objectives;
  }

  public static Profile parse(String name) {
    Document document;
    try {
      document = Jsoup.connect("https://stratus.network/users/" + name).get();
    } catch (IOException e) {
      Update.logger().severe("Profile for user \"" + name + "\" not found");
      e.printStackTrace();
      return null;
    }

    name = document.getElementsByTag("h2").first().text();

    List<PvpEncounter> pvpEncounters = new ArrayList<>();
    for (Element p : document.getElementById("pvp-encounters").getElementsByTag("p")) {
      Elements avatars = p.getElementsByTag("img");
      String killer = avatars.first().attr("alt");
      String killed = avatars.last().attr("alt");

      Elements links = p.getElementsByTag("a");
      String map = links.get(3).text();
      URL match = null;
      try {
        match = new URL("https://stratus.network" + links.get(4).attr("href"));
      } catch (MalformedURLException e) {
        Update.logger().severe("Malformed URL for \"https://stratus.network" + links.get(4).attr("href") + "\"");
        e.printStackTrace();
      }

      pvpEncounters.add(new PvpEncounter(killer, killed, map, match));
    }

    List<Objective> objectives = new ArrayList<>();
    try {
      Element woolsPlaced = document.getElementsContainingOwnText("placed").first().parent().parent().parent();
      Elements objectivesChildren = document.getElementById("objectives").children();
      for (Element p : objectivesChildren.get(objectivesChildren.indexOf(woolsPlaced) + 1).getElementsByTag("p")) {
        Element woolElement = p.getElementsByTag("strong").first();

        String woolName = woolElement.text();

        String style = woolElement.attr("style");
        String[] rgb = style.substring(style.indexOf('(') + 1, style.indexOf(')')).split(",");
        Color color = new Color(Integer.parseInt(rgb[0]), Integer.parseInt(rgb[1]), Integer.parseInt(rgb[2]));

        Elements links = p.getElementsByTag("a");
        String map = links.first().text();
        URL match = null;
        try {
          match = new URL("https://stratus.network" + links.last().attr("href"));
        } catch (MalformedURLException e) {
          Update.logger().severe("Malformed URL for \"https://stratus.network" + links.last().attr("href") + "\"");
          e.printStackTrace();
        }

        objectives.add(new Wool(woolName, color, map, match));
      }
    } catch (NullPointerException e) {}

    return new Profile(name, pvpEncounters, objectives);
  }

  public String getName() {
    return name;
  }

  public List<PvpEncounter> getPvpEncounters() {
    return pvpEncounters;
  }

  public List<Objective> getObjectives() {
    return objectives;
  }
}
