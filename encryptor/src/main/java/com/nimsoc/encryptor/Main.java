package com.nimsoc.encryptor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Main {
  public static void main(String[] args) throws IOException {
    Files.walkFileTree(Paths.get("sample"), new FileEncryptor.FileVisitor(true, "supramax"));
    Files.walkFileTree(Paths.get("sample"), new FileEncryptor.FileVisitor(false, "supramax"));
  }
}
