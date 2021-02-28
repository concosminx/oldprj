package com.nimsoc.slackbot;

import com.nimsoc.slackbot.processor.CountingCommandProcessor;
import com.nimsoc.slackbot.command.SlackCommand;
import com.ullink.slack.simpleslackapi.SlackChannel;
import com.ullink.slack.simpleslackapi.SlackSession;
import com.ullink.slack.simpleslackapi.SlackUser;
import com.ullink.slack.simpleslackapi.events.SlackMessagePosted;
import com.ullink.slack.simpleslackapi.impl.SlackSessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import com.nimsoc.slackbot.processor.CommandProcessor;
import com.nimsoc.slackbot.processor.JokingCommandProcessor;

@Component
public class RobotTheSlackBot {

  private static final Logger LOGGER = LoggerFactory.getLogger(RobotTheSlackBot.class);
  private Pattern targetUsPattern;

  @Autowired
  private SlackBotProperties slackBotProperties;
  private SlackSession slackSession;
  private final Map<String, CommandProcessor> slackProcessors = new HashMap<>();

  @PostConstruct
  public void init() throws IOException {
    slackSession = SlackSessionFactory.createWebSocketSlackSession(slackBotProperties.getAccessToken());
    slackSession.connect();
    
    LOGGER.info("Session established.");

    targetUsPattern = Pattern.compile("^\\s*<@" + slackSession.sessionPersona().getId() + ">\\s*(.*)");

    slackSession.addMessagePostedListener((slackMessagePosted, slackSession) -> {
      this.handleMessagePosted(slackMessagePosted);
    });
  }

  @PreDestroy
  public void shutdown() throws IOException {
    slackSession.disconnect();
  }

  protected synchronized void handleMessagePosted(SlackMessagePosted smp) {
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug(String.format("Message from[%s]: %s", smp.getSender().getRealName(), smp.getMessageContent()));
    }

    Matcher matcher = targetUsPattern.matcher(smp.getMessageContent());
    if (!matcher.find()) {
      return;
    }

    String command = matcher.group(1).trim().toLowerCase();
    LOGGER.debug("Command: " + command);

    String channelId = smp.getChannel().getId();
    CommandProcessor slackAssistant = slackProcessors.get(channelId);
    if (slackAssistant != null) {
      if (!slackAssistant.handleCommand(command, smp.getSender())) {
        slackProcessors.remove(channelId);
      }

      return;
    }

    if (command.startsWith("start ")) {
      handleStart(smp, channelId, command.substring("start ".length()));
    } else {
      issueWarning("Confussed .", smp.getChannel(), smp.getSender());
    }
  }

  private void handleStart(SlackMessagePosted smp, String channelId, String instruction) {
    CommandProcessor slackProcessor = null;

    SlackCommand instructionBreakdown = new SlackCommand(instruction);
    String proecessorName = instructionBreakdown.getProcessor();
    if (null != proecessorName) switch (proecessorName) {
      case "joking":
        slackProcessor = new JokingCommandProcessor();
        break;
      case "counting":
        slackProcessor = new CountingCommandProcessor();
        break;
      default:
        break;
    }

    if (slackProcessor != null) {
      slackProcessor.init(slackSession, smp.getChannel(), smp.getSender(), instructionBreakdown.getDescription());
      slackProcessors.put(channelId, slackProcessor);
    } else {
      issueWarning("No processor found for [" + proecessorName + "], sorry.", smp.getChannel(), smp.getSender());
    }
  }

  void issueWarning(String warning, SlackChannel channel, SlackUser targetUser) {
    slackSession.sendEphemeralMessage(channel, targetUser, ":astonished: " + warning);
  }
}
