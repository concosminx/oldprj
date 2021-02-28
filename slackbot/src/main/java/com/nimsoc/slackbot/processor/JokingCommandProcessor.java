package com.nimsoc.slackbot.processor;

import com.ullink.slack.simpleslackapi.SlackUser;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.logging.Level;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JokingCommandProcessor extends AbstractCommandProcessor {

  private static final Logger LOGGER = LoggerFactory.getLogger(JokingCommandProcessor.class);

  @Override
  protected String formatResult() {
    return "Are You Not Entertained?!";
  }

  @Override
  protected String getSessionTitle() {
    return "This is not a joke!";
  }

  @Override
  protected boolean handleNonLifecycleCommand(String command, SlackUser user) {
    if (command == null) {
      issueWarning("Nooooooo ! " + command + ".", user);
    } else if (!"next".equalsIgnoreCase(command)) {
      issueWarning("I'm a stupid ro(bot)! What does it mean: " + command + "?", user);
    } else {
      issueAnnouncement(getAJoke());
    }
    return true;
  }

  private String getAJoke() {

    String r = "N/A";

    try {
      URL url = new URL("https://api.yomomma.info");
      HttpURLConnection conn = (HttpURLConnection) url.openConnection();
      conn.setRequestMethod("GET");
      conn.connect();

      int responsecode = conn.getResponseCode();
      if (responsecode != 200) {
        throw new RuntimeException("HttpResponseCode: " + responsecode);
      } else {
        JSONParser parser = new JSONParser();
        try (InputStreamReader in = new InputStreamReader(url.openStream())) {
          JSONObject jsonObject = (JSONObject) parser.parse(in);
          r = (String)jsonObject.get("joke");
        }
      }
    } catch (IOException | RuntimeException | ParseException e) {
      LOGGER.error("Error loading a new Joke from API ", e);
    }

    return r;
  }

  @Override
  public String getName() {
    return "Joker";
  }

}
