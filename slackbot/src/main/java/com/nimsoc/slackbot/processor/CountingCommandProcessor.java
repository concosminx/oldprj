package com.nimsoc.slackbot.processor;

import com.ullink.slack.simpleslackapi.SlackUser;

import java.util.HashMap;
import java.util.Map;

public class CountingCommandProcessor extends AbstractCommandProcessor {

  private final Map<String, Integer> userCountMap = new HashMap<>();

  @Override
  protected String formatResult() {
    StringBuilder sb = new StringBuilder();
    int total = 0;
    for (Map.Entry<String, Integer> entry : userCountMap.entrySet()) {
      String id = entry.getKey();
      Integer count = entry.getValue();
      total += count;
      sb.append(slackSession.findUserById(id).getRealName()).append(" asking for ").append(count).append("\n");
    }
    sb.append("Total count: ").append(total);
    return sb.toString();
  }

  @Override
  public String getName() {
    return "Counting Processor";
  }

  @Override
  protected String getSessionTitle() {
    if (description == null) {
      return "Counting";
    } else {
      return "Counting for " + description;
    }
  }

  @Override
  protected boolean handleNonLifecycleCommand(String command, SlackUser user) {
    Integer count = null;

    try {
      count = Integer.parseInt(command);
    } catch (NumberFormatException nfe) {
      issueWarning("Please, I only can deal with integers > 0. Not " + command + ".", user);
      return true;
    }

    if (count < 0) {
      issueWarning("Please, I only can deal with integers >= 0. Not " + command + ".", user);
      return true;
    }

    if (userCountMap.containsKey(user.getId())) {
      issueAnnouncement("Updating count for " + user.getRealName() + " to " + count);
    } else {
      issueAnnouncement("Will take note and update count for " + user.getRealName() + " to " + count);
    }

    userCountMap.put(user.getId(), count);
    return true;
  }
}
