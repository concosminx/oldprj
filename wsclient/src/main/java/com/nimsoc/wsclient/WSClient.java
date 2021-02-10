package com.nimsoc.wsclient;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.ssl.SSLContextBuilder;

public class WSClient {

  public static void main(String[] args) throws Exception {
    //jsonTest();
    requestXMLFromBNR();
  }

  private static void jsonTest() throws RuntimeException, IOException, XMLStreamException {
    HttpClient httpclient = HttpClientBuilder.create().build();
    HttpGet get = new HttpGet("https://restcountries.eu/rest/v2/all");
    HttpResponse response = httpclient.execute(get);

    if (response.getStatusLine().getStatusCode() != 200) {
      throw new RuntimeException("Failed : HTTP error code : " + response.getStatusLine().getStatusCode());
    } else {
      HttpEntity resEntity = response.getEntity();
      if (resEntity != null) {
        String result = new BufferedReader(new InputStreamReader(resEntity.getContent()))
                .lines().collect(Collectors.joining("\n"));
        System.out.println("result:" + result);
      }
    }
  }

  private static void requestXMLFromBNR() throws RuntimeException, IOException, XMLStreamException, KeyManagementException, NoSuchAlgorithmException, KeyStoreException {
    SSLContext sslContext = new SSLContextBuilder()
            .loadTrustMaterial(null, (TrustStrategy) (arg0, arg1) -> true).build();

    CloseableHttpClient httpClient = HttpClients
            .custom()
            .setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE)
            .setSSLContext(sslContext)
            .build();

    List<NameValuePair> params = new LinkedList<>();
    params.add(new BasicNameValuePair("bogusparam", "bogusvalue"));
    String paramString = URLEncodedUtils.format(params, "utf-8");

    HttpGet get = new HttpGet("YOUR_URL_HERE" + paramString);

    get.addHeader("bogusheader", "bogusheadervalue");
    HttpResponse response = httpClient.execute(get);

    if (response.getStatusLine().getStatusCode() != 200) {
      throw new RuntimeException("Failed : HTTP error code : " + response.getStatusLine().getStatusCode());
    } else {
      HttpEntity resEntity = response.getEntity();
      if (resEntity != null) {
        BufferedInputStream bf = null;
        XMLStreamReader reader = null;
        String tagContent = null;
        try {
          XMLInputFactory xmlInputFactory = XMLInputFactory.newInstance();
          bf = new BufferedInputStream((resEntity.getContent()));
          reader = xmlInputFactory.createXMLStreamReader("BLABLA_BLABLA" + new Random().nextLong(), bf);

          while (reader.hasNext()) {
            int event = reader.next();
            switch (event) {
              case XMLStreamConstants.START_ELEMENT:
                if ("Rate".equals(reader.getLocalName())) {
                  String currency = reader.getAttributeValue(null, "currency");
                  System.out.print("currency: " + currency + "; ");
                }

                break;

              case XMLStreamConstants.CHARACTERS:
                tagContent = reader.getText().trim();
                System.out.print("tagContent: " + tagContent + "; ");
                break;

              case XMLStreamConstants.END_ELEMENT:
                break;
            }
          }

        } finally {
          try {
            if (bf != null) {
              bf.close();
            }
          } catch (Exception exc) {
            exc = null;
          }
          try {
            if (reader != null) {
              reader.close();
            }
          } catch (Exception exc) {
            exc = null;
          }
        }

      }
    }
  }

}
