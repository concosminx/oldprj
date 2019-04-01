package com.nimsoc.finder.impl;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import static java.nio.file.FileVisitResult.CONTINUE;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;

public class Mp3Finder extends SimpleFileVisitor<Path> {
  
  private static final Logger LOGGER = Logger.getLogger(Mp3Finder.class.getName());
  
  private static final String FORBIDDEN_CHARS = " -.()_=+";
  private static final List<String> FORBIDDEN_EXTENSIONS = new ArrayList<>();
  
  static {
    FORBIDDEN_EXTENSIONS.add(".jpg");
    FORBIDDEN_EXTENSIONS.add(".png");
    FORBIDDEN_EXTENSIONS.add(".ini");
    FORBIDDEN_EXTENSIONS.add(".m3u");
    FORBIDDEN_EXTENSIONS.add(".nfo");
    FORBIDDEN_EXTENSIONS.add(".doc");
    FORBIDDEN_EXTENSIONS.add(".bmp");
    FORBIDDEN_EXTENSIONS.add(".pls");
    FORBIDDEN_EXTENSIONS.add(".ogg");
    FORBIDDEN_EXTENSIONS.add(".sfv");
    FORBIDDEN_EXTENSIONS.add(".gif");
    FORBIDDEN_EXTENSIONS.add(".cue");
    FORBIDDEN_EXTENSIONS.add(".url");
    FORBIDDEN_EXTENSIONS.add(".aac");
    FORBIDDEN_EXTENSIONS.add(".mpg");
    FORBIDDEN_EXTENSIONS.add(".log");
    FORBIDDEN_EXTENSIONS.add(".htt");
    FORBIDDEN_EXTENSIONS.add(".sfk");
    FORBIDDEN_EXTENSIONS.add(".tmp");
  }
  
  private final List<String> extensionsFound;
  private final List<FileWithSiblings> allFiles;
  private File deleteDirectory = null;
  private int noOfDeletedFilesByExtension;
  private int noOfRenamedFiles;
  private final int noOfDifChars;
  private boolean moveDuplicateFiles;
  private boolean deleteFileByExtension;
  private boolean renameFileByName;
  
  public Mp3Finder(File deleteDirectory, int noOfDifChars, boolean moveDuplicateFiles, boolean deleteFileByExtension, boolean renameFileByName) {
    if (deleteDirectory == null) {
      throw new IllegalArgumentException("delete directory cannot be null :( ");
    }
    this.allFiles = new ArrayList<>();
    this.extensionsFound = new ArrayList<>();
    this.deleteDirectory = deleteDirectory;
    if (!deleteDirectory.exists()) {
      deleteDirectory.mkdirs();
    }
    this.noOfDifChars = noOfDifChars;
    this.moveDuplicateFiles = moveDuplicateFiles;
    this.deleteFileByExtension = deleteFileByExtension;
    this.renameFileByName = renameFileByName;
  }
  
  private void renameOrDeleFile(Path file) {
    File toFile = file.toFile();
    String fileName = toFile.getName();
    
    LOGGER.log(Level.FINE, "fileName.before:|{0}|", fileName);
    
    FileWithSiblings d = new FileWithSiblings(fileName, toFile.length(), noOfDifChars);
    
    if (allFiles.contains(d)) {
      FileWithSiblings dd = allFiles.get(allFiles.indexOf(d));
      dd.addAnotherSimilarFile(toFile.getAbsolutePath());
      LOGGER.log(Level.FINE, "delete: {0}", toFile.getAbsolutePath());
      if (moveDuplicateFiles) {
        toFile.renameTo(new File(deleteDirectory, fileName));
      }
    } else {
      d.addAnotherSimilarFile(toFile.getAbsolutePath());
      allFiles.add(d);
    }
    
    String ext = null;
    if (fileName.contains(".")) {
      ext = fileName.substring(fileName.lastIndexOf(".")).toLowerCase();
      if (!extensionsFound.contains(ext)) {
        extensionsFound.add(ext);
      }
    }
    
    if (ext != null && FORBIDDEN_EXTENSIONS.contains(ext)) {
      LOGGER.log(Level.FINE, "fN.delete:|{0}|", toFile.getAbsolutePath());
      noOfDeletedFilesByExtension++;
      if (deleteFileByExtension) {
        toFile.delete();
      }
      return;
    }
    
    StringBuilder sb = new StringBuilder();
    boolean firstLetter = false;
    boolean hit = false;
    char[] toCharArray = fileName.toCharArray();
    for (char c : toCharArray) {
      if (!firstLetter && (Character.isDigit(c) || FORBIDDEN_CHARS.contains(Character.toString(c)))) {
        hit = true;
        continue;
      }
      if (!firstLetter) {
        c = Character.toUpperCase(c);
      }
      sb.append(c);
      firstLetter = true;
    }
    
    LOGGER.log(Level.FINE, "fileName.after:|{0}|", sb.toString());
    String newFilePath = toFile.getAbsolutePath().replace(toFile.getName(), "") + sb.toString();
    if (hit) {
      noOfRenamedFiles++;
      LOGGER.log(Level.FINE, "{0} fN.newFilePath:|{1}|", new Object[]{hit ? "**" : "", newFilePath});
      if (renameFileByName) {
        toFile.renameTo(new File(newFilePath));
      }
    }
  }
  
  @Override
  public FileVisitResult visitFile(Path file,
          BasicFileAttributes attrs) {
    renameOrDeleFile(file);
    return CONTINUE;
  }
  
  @Override
  public FileVisitResult visitFileFailed(Path file,
          IOException exc) {
    return CONTINUE;
  }
  
  public String printStats() {
    StringBuilder sb = new StringBuilder();
    
    sb.append("All_Files").append(System.lineSeparator());
    sb.append(allFiles.size()).append(System.lineSeparator());
    sb.append("Extensions: ").append(System.lineSeparator());
    this.extensionsFound.forEach((s) -> {
      sb.append(s).append(System.lineSeparator());
    });
    sb.append("Deleted: ").append(noOfDeletedFilesByExtension).append(System.lineSeparator());
    sb.append("Renamed: ").append(noOfRenamedFiles).append(System.lineSeparator());
    sb.append("Duplicates: ").append(System.lineSeparator());
    int filesWithDuplicates = 0;
    for (FileWithSiblings d : allFiles) {
      if (d.hasSimilarFiles()) {
        ++filesWithDuplicates;
        sb.append(d.toString()).append(System.lineSeparator());
      }
    }
    sb.append("Summary: ").append(System.lineSeparator());
    sb.append(filesWithDuplicates).append(" files with duplicates found").append(System.lineSeparator());
    
    return sb.toString();
  }
  
  
}
