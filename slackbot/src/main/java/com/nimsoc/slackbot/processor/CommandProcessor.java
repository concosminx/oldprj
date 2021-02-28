package com.nimsoc.slackbot.processor;

import com.ullink.slack.simpleslackapi.SlackChannel;
import com.ullink.slack.simpleslackapi.SlackSession;
import com.ullink.slack.simpleslackapi.SlackUser;

public interface CommandProcessor {

  String getName();

  void init(SlackSession slackSession, SlackChannel channel, SlackUser initiator, String description);

  String cancel();

  String finish();

  boolean handleCommand(String command, SlackUser user);
}
