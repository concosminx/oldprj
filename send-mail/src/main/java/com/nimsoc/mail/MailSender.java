package com.nimsoc.mail;

import java.io.File;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import javax.mail.Address;
import javax.mail.Authenticator;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.SendFailedException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

public class MailSender {

  private static final String PASSWORD = "gmail password";
  private static final String USERNAME = "gmail user";
  private static final String TO = "to email address";
  

  public static void main(String[] args) {

    boolean ssl = true;
    boolean tls = false;

    String sslPort = "465";
    String tlsPort = "587";

    Properties props = new Properties();

    if (ssl || tls) {
      props.setProperty("mail.smtp.ssl.trust", "*");
    }
    
    if (ssl) {
      props.setProperty("mail.smtp.socketFactory.port", sslPort);
      props.setProperty("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
      props.setProperty("mail.smtp.socketFactory.fallback", "false");
      props.setProperty("mail.smtp.ssl.socketFactory.port", sslPort);
      props.setProperty("mail.smtp.ssl.enable", "true");
    }

    if (tls) {
      props.setProperty("mail.smtp.starttls.enable", "true");
    }

    props.setProperty("mail.user", USERNAME);
    props.setProperty("mail.password", PASSWORD);

    props.setProperty("mail.transport.protocol", "smtp");
    props.setProperty("mail.smtp.host", "smtp.gmail.com");
    props.setProperty("mail.smtp.auth", "true");
    props.setProperty("mail.port", ssl ? sslPort : tlsPort);
    props.setProperty("mail.smtp.port", ssl ? sslPort : tlsPort);

    props.setProperty("username", USERNAME);
    props.setProperty("password", PASSWORD);
    props.setProperty("mail.smtp.password", PASSWORD);

    props.setProperty("mail.smtp.connectiontimeout", "" + TimeUnit.SECONDS.toMillis(30));
    props.setProperty("mail.smtp.writetimeout", "" + TimeUnit.MINUTES.toMillis(30));
    props.setProperty("mail.smtp.timeout", "" + TimeUnit.MINUTES.toMillis(5));
    props.setProperty("mail.debug", "true");

    Session mailSession = null;

    if ("true".equalsIgnoreCase(props.getProperty("mail.smtp.auth"))
            && props.getProperty("username") != null
            && props.getProperty("password") != null) {

      final String userName = props.getProperty("username");
      final String password = props.getProperty("password");
      Authenticator auth = new javax.mail.Authenticator() {
        @Override
        protected PasswordAuthentication getPasswordAuthentication() {
          return new PasswordAuthentication(userName, password);
        }
      };
      mailSession = Session.getInstance(props, auth);

    } else {
      mailSession = Session.getInstance(props);
    }

    if (mailSession != null) {
      try {

        MimeMessage message = new MimeMessage(mailSession);
        message.setFrom(new InternetAddress(USERNAME));
        message.addRecipient(Message.RecipientType.TO, new InternetAddress(TO));
        message.setSubject("this is a test subject");
        BodyPart mbp1 = new MimeBodyPart();
        mbp1.setText("this is a test message body");

        Multipart mp = new MimeMultipart();
        mp.addBodyPart(mbp1);

        BodyPart mbFile = new MimeBodyPart();
        ((MimeBodyPart) mbFile).attachFile(new File("sample.zip"));
        mbFile.setFileName("attach.zip");
        mp.addBodyPart(mbFile);
        message.setContent(mp);

        try {
          Transport.send(message);
        } catch (SendFailedException sfe) {

          Address[] invalidAddresses = sfe.getInvalidAddresses();
          Address[] validUnsentAddresses = sfe.getValidUnsentAddresses();
          StringBuilder err = new StringBuilder();
          if (invalidAddresses != null && invalidAddresses.length > 0) {
            for (Address a : invalidAddresses) {
              err.append("invalid address: ").append(a).append("\n");
            }
          }
          if (validUnsentAddresses != null && validUnsentAddresses.length > 0) {
            for (Address a : validUnsentAddresses) {
              err.append("cannot send mail: ").append(a).append("\n");
            }
          }

          if (err.length() != 0) {
            System.out.println(err);
          }
        }
      } catch (Exception ex) {
        ex.printStackTrace();
      }
    }

  }

}
