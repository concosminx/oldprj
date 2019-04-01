package com.nimsoc.finder.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class FileWithSiblings {

  private final List<String> others = new ArrayList<>();
  private final String fileName;
  private final long size;
  private final int noOfDifChars;

  public FileWithSiblings(String fileName, long size, int noOfDifChars) {
    this.fileName = fileName;
    this.size = size;
    this.noOfDifChars = noOfDifChars;
  }

  public void addAnotherSimilarFile(String _other) {
    this.others.add(_other);
  }

  public boolean hasSimilarFiles() {
    return this.others.size() > 1;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    return hash;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final FileWithSiblings other = (FileWithSiblings) obj;
    if (this.size != other.size) {
      return false;
    }
    return noOfDifChars > 0 ? checkSimilarNames(this.fileName, other.fileName) : Objects.equals(this.fileName, other.fileName);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    others.forEach((s) -> {
      sb.append(s).append(System.lineSeparator());
    });

    return sb.toString();
  }

  protected boolean checkSimilarNames(String a, String b) {
    if (a == null || b == null) {
      return false;
    }

    String x = null;
    String y = null;
    if (a.length() <= b.length()) {
      x = a.toUpperCase();
      y = b.toUpperCase();
    } else {
      x = b.toUpperCase(); 
      y = a.toUpperCase();
    }
    
    int cnt = 0;
    for (int i = 0; i < x.length(); i++) {
      if (y.indexOf(x.charAt(i)) < 0) {
        cnt++;
      }
    }

    return cnt <= this.noOfDifChars;
  }
}
