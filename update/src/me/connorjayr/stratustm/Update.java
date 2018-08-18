package me.connorjayr.stratustm;

import me.connorjayr.stratustm.stratus.match.Match;
import me.connorjayr.stratustm.stratus.profile.Profile;
import me.connorjayr.stratustm.stratus.profile.objective.Objective;
import me.connorjayr.stratustm.stratus.profile.objective.Wool;
import me.connorjayr.stratustm.stratus.profile.pvp.PvpEncounter;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.awt.Color;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.logging.Logger;

public class Update {

  private static final Logger logger = Logger.getLogger("Update");

  public static Logger logger() {
    return logger;
  }

  public static void main(String[] args) throws IOException {
    Scanner matchInput = new Scanner(new File("matches.in"));
    while (matchInput.hasNextLine()) {
      Document document = Jsoup.parse(new File("game.in"), "UTF-8");

      String[] line = matchInput.nextLine().split(" ");


      Match match = Match.parse(new URL(line[1]));
      Map<String, Profile> profiles = new HashMap<>();
      for (String team : match.getTeams().keySet()) {
        for (String player : match.getTeams().get(team)) {
          profiles.put(player, Profile.parse(player));
        }
      }

      Element tr = document.getElementsMatchingOwnText("%TEAM%").first().parent();
      Element tbody = tr.parent();
      tbody.empty();
      for (String team : match.getTeams().keySet()) {
        Element teamElement = tr.clone();
        teamElement.getElementsByTag("td").first().text(team);

        int wools = 0;
        Element span = teamElement.getElementsByTag("span").first();
        Element woolsElement = span.parent();
        woolsElement.empty();
        for (String player : match.getTeams().get(team)) {
          Profile profile = profiles.get(player);
          for (Objective objective : profile.getObjectives()) {
            if (objective.getMatch().equals(match.getUrl())) {
              Element woolElement = span.clone();

              Color color = ((Wool) objective).getColor();
              woolElement.attr("style", "color: rgb(" + color.getRed() + "," + color.getGreen() + "," + color.getBlue() + ");");
              woolElement.appendTo(teamElement.getElementsByTag("td").last());
              ++wools;
            }
          }
        }
        if (wools == 2) {
          teamElement.addClass("winning-team");
        }
        while (wools < 2) {
          span.clone().appendTo(woolsElement);
          ++wools;
        }
        teamElement.appendTo(tbody);
      }

      Element gameElement = document.getElementsContainingOwnText("%LENGTH%").first();
      String gameText = gameElement.text().substring(0, 6);
      gameElement.text(gameText + " - " + match.getMap() + " (" + match.getLength().getSeconds() / 60 + ":" + (match.getLength().getSeconds() % 60 < 10 ? "0" : "") + match.getLength().getSeconds() % 60 + ")");

      tr = document.getElementsMatchingOwnText("%K%").first().parent();
      tbody = tr.parent();
      tbody.empty();
      for (String team : match.getTeams().keySet()) {
        for (String player : match.getTeams().get(team)) {
          Element playerElement = tr.clone();

          int k = 0;
          int d = 0;
          int w = 0;
          for (PvpEncounter pvp : profiles.get(player).getPvpEncounters()) {
            if (pvp.getMatch().equals(match.getUrl())) {
              if (pvp.getKiller().equalsIgnoreCase(player)) {
                ++k;
              } else {
                ++d;
              }
            }
          }
          for (Objective obj : profiles.get(player).getObjectives()) {
            if (obj.getMatch().equals(match.getUrl())) {
              ++w;
            }
          }

          playerElement.getElementsMatchingOwnText("%PLAYER%").first().text("[" + team + "] " + player);
          playerElement.getElementsMatchingOwnText("%K%").first().text(Integer.toString(k));
          playerElement.getElementsMatchingOwnText("%D%").first().text(Integer.toString(d));
          playerElement.getElementsMatchingOwnText("%W%").first().text(Integer.toString(w));

          playerElement.appendTo(tbody);
        }
      }

      File output = new File("match" + line[0] + ".out");
      FileWriter writer = new FileWriter(output);
      String html = document.body().children().first().toString();
      html = html.replace("%GAME%", line[0]);
      html = html.replace("%GAME_NUM%", line[0].substring(0, line[0].length() - 1));
      writer.write(html);
      writer.close();
    }
  }

}
