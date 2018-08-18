package me.connorjayr.stratustm.stratus.match;


import me.connorjayr.stratustm.Update;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URL;
import java.time.Duration;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class Match {

  private final URL url;
  private final String map;
  private final String server;
  private final Duration length;
  private final LinkedHashMap<String, List<String>> teams;

  private Match(URL url, String map, String server, Duration length, LinkedHashMap<String, List<String>> teams) {
    this.url = url;
    this.map = map;
    this.server = server;
    this.length = length;
    this.teams = teams;
  }

  public static Match parse(URL url) {
    Document document;
    try {
      document = Jsoup.connect(url.toString()).get();
    } catch (IOException e) {
      Update.logger().severe("No match found at URL \"" + url.toString() + "\"");
      e.printStackTrace();
      return null;
    }

    Element header = document.getElementsByTag("h2").first();
    String map = header.getElementsByTag("a").first().text();

    Element serverElement = document.getElementsMatchingOwnText("server").first();
    String server = serverElement.parent().getElementsByTag("a").first().text();

    Element matchLength = document.getElementsMatchingOwnText("match length").first();
    String[] time = matchLength.parent().ownText().split(":");
    Duration length = Duration.ofSeconds(Integer.parseInt(time[0]) * 60 + Integer.parseInt(time[1]));

    LinkedHashMap<String, List<String>> teams = new LinkedHashMap<>();

    Elements teamDivs = document.getElementsByClass("col-md-4");
    for (int div = 3; div < teamDivs.size(); ++div) {
      Element teamDiv = teamDivs.get(div);
      String teamName = teamDiv.getElementsByTag("h4").first().ownText();
      List<String> players = new ArrayList<>();
      for (Element avatar : teamDiv.getElementsByTag("a")) {
        players.add(avatar.attr("href").substring(1));
      }
      teams.put(teamName, players);
    }

    return new Match(url, map, server, length, teams);
  }

  public URL getUrl() {
    return url;
  }

  public String getMap() {
    return map;
  }

  public String getServer() {
    return server;
  }

  public Duration getLength() {
    return length;
  }

  public LinkedHashMap<String, List<String>> getTeams() {
    return teams;
  }

}
