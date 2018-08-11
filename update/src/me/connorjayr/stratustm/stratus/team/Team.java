package me.connorjayr.stratustm.stratus.team;

import me.connorjayr.stratustm.Update;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Team {

  private final String name;
  private final List<String> members;

  private Team(String name, List<String> members) {
    this.name = name;
    this.members = members;
  }

  public static Team parse(String name) {
    Document document;
    try {
      document = Jsoup.connect("https://stratus.network/teams/" + name.toLowerCase().replace(" ", "")).get();
    } catch (IOException e) {
      Update.logger().severe("Team \"" + name + "\" not found");
      e.printStackTrace();
      return null;
    }

    name = document.getElementsByTag("h2").text();

    int pageCount = 1;
    Elements pages = document.getElementsByClass("pagination");
    if (!pages.isEmpty()) {
      String href = pages.first().getElementsByTag("li").last().getElementsByTag("a").first().attr("href");
      pageCount = Integer.parseInt(href.substring(href.indexOf('=') + 1, href.length()));
    }

    List<String> members = new ArrayList<>();
    for (int page = 1; page <= pageCount; ++page) {
      if (page > 1) {
        try {
          document = Jsoup.connect("https://stratus.network/teams/" + name.toLowerCase().replace(" ", "") + "?page=" + page).get();
        } catch (IOException e) {
          Update.logger().severe("Page " + page + " for team \"" + name + "\" not found");
          e.printStackTrace();
          continue;
        }
      }
      for (Element tr : document.getElementsByTag("tbody").first().getElementsByTag("tr")) {
        members.add(tr.getElementsByTag("td").first().getElementsByTag("a").last().text());
      }
    }

    return new Team(name, members);
  }

  public String getName() {
    return name;
  }

  public List<String> getMembers() {
    return members;
  }

}
