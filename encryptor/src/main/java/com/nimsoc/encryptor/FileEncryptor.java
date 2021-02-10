package com.nimsoc.encryptor;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.spec.SecretKeySpec;

public class FileEncryptor {

  private static final String KEY = "StupidSA";//"HignDlPs";
  private final String algo;
  private File file;
  private final String cipher;

  public FileEncryptor(String algo, String path, String cipher) {
    this.algo = algo;
    this.file = new File(path);
    this.cipher = cipher;
  }

  public void encrypt() throws Exception {
    try (FileInputStream fis = new FileInputStream(file)) {
      file = new File(file.getAbsolutePath() + ".enc");
      FileOutputStream fos = new FileOutputStream(file);
      byte k[] = cipher != null ? cipher.getBytes() : KEY.getBytes();
      SecretKeySpec secretKeySpec = new SecretKeySpec(k, algo.split("/")[0]);
      Cipher encrypt = Cipher.getInstance(algo);
      encrypt.init(Cipher.ENCRYPT_MODE, secretKeySpec);
      try (CipherOutputStream cout = new CipherOutputStream(fos, encrypt)) {
        byte[] buf = new byte[1024];
        int read;
        while ((read = fis.read(buf)) != -1) {
          cout.write(buf, 0, read);
        }
        cout.flush();
      }
    }
  }

  public void decrypt() throws Exception {
    FileInputStream fis = new FileInputStream(file);
    file = new File(file.getAbsolutePath() + ".dec");
    try (FileOutputStream fos = new FileOutputStream(file)) {
      byte k[] = cipher != null ? cipher.getBytes() : KEY.getBytes();
      SecretKeySpec key = new SecretKeySpec(k, algo.split("/")[0]);
      Cipher decrypt = Cipher.getInstance(algo);
      decrypt.init(Cipher.DECRYPT_MODE, key);
      try (CipherInputStream cin = new CipherInputStream(fis, decrypt)) {
        byte[] buf = new byte[1024];
        int read = 0;
        while ((read = cin.read(buf)) != -1) {
          fos.write(buf, 0, read);
        }
      }
      fos.flush();
    }
  }

  static class FileVisitor extends SimpleFileVisitor<Path> {

    PathMatcher matcher;
    boolean encrypt;
    String cipher;

    public FileVisitor(boolean encrypt, String cipher) {
      this.encrypt = encrypt;
      this.cipher = cipher;
      if (encrypt) {
        matcher = FileSystems.getDefault().getPathMatcher("glob:*.{java,xml,properties,gradle,txt,xhtml,groovy}");
      } else {
        matcher = FileSystems.getDefault().getPathMatcher("glob:*.enc");
      }
    }

    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
      Path fileName = file.getFileName();
      if (matcher.matches(fileName)) {
        try {
          if (encrypt) {
            new FileEncryptor("DES/ECB/PKCS5Padding", file.toString(), cipher).encrypt();
          } else {
            new FileEncryptor("DES/ECB/PKCS5Padding", file.toString(), cipher).decrypt();
          }
        } catch (Exception ex) {
          Logger.getLogger(FileEncryptor.class.getName()).log(Level.SEVERE, null, ex);
        }
      }

      return FileVisitResult.CONTINUE;
    }
  }
}
