package com.nimsoc.slackbot.processor;

import com.ullink.slack.simpleslackapi.SlackChannel;
import com.ullink.slack.simpleslackapi.SlackSession;
import com.ullink.slack.simpleslackapi.SlackUser;

public abstract class AbstractCommandProcessor implements CommandProcessor {

  protected SlackSession slackSession;
  protected SlackChannel channel;
  protected SlackUser initiator;
  protected String description;

  @Override
  public String cancel() {
    return getSessionTitle() + " cancelled. Result: \n" + formatResult();
  }

  @Override
  public String finish() {
    return getSessionTitle() + " finished. Result: \n" + formatResult();
  }

  abstract protected String formatResult();

  abstract protected String getSessionTitle();

  @Override
  final public boolean handleCommand(String command, SlackUser user) {
    if ("start ".startsWith(command)) {
      String descriptionStr = "";
      if (description != null) {
        descriptionStr = " (" + description + ")";
      }
      issueWarning(getName() + descriptionStr + " is still in session. Cancel or stop it first.", initiator);
    } else if ("cancel".equals(command)) {
      String cancellationMessage = cancel();
      issueAnnouncement(cancellationMessage);
      return false;
    } else if ("finish".equals(command)) {
      String finishMessage = finish();
      issueAnnouncement(finishMessage);
      return false;
    }

    return handleNonLifecycleCommand(command, user);
  }

  protected abstract boolean handleNonLifecycleCommand(String command, SlackUser user);

  @Override
  public void init(SlackSession slackSession, SlackChannel channel, SlackUser initiator, String description) {
    this.slackSession = slackSession;
    this.channel = channel;
    this.initiator = initiator;
    this.description = description;

    issueAnnouncement(getName() + " started. Ready for commands");
  }

  protected void issueAnnouncement(String announcement) {
    slackSession.sendMessage(channel, announcement);
  }

  protected void issueWarning(String warning, SlackUser targetUser) {
    slackSession.sendEphemeralMessage(channel, targetUser, ":astonished: " + warning);
  }
}
