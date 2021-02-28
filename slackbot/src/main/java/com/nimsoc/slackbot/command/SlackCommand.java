package com.nimsoc.slackbot.command;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SlackCommand {
  private static final Logger LOGGER = LoggerFactory.getLogger(SlackCommand.class);

  //see https://docs.oracle.com/javase/7/docs/api/java/util/regex/Pattern.html
  //example @bot start something - description
  private final static Pattern PATTERN = Pattern.compile("([\\w\\s]+\\w)(\\s*-\\s*(['\\w\\s]+))?");

  private String processor;
  private String description;

  public SlackCommand(String commandString) {
    Matcher matcher = PATTERN.matcher(commandString);
    if (!matcher.matches()) {
      throw new RuntimeException("Unexpected command: " + commandString);
    }
    this.processor = matcher.group(1);
    this.description = matcher.group(3);
    
    LOGGER.info("processor:" + processor + "; description: " + description);
  }

  public String getProcessor() {
    return processor;
  }

  public String getDescription() {
    return description;
  }
}
