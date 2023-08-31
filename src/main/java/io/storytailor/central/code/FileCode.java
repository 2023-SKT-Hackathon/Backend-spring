package io.storytailor.central.code;

public enum FileCode {
  ORIGIN("origin"),
  EXPAND("expand"),
  AUDIO("audio"),
  AI("ai");

  private String code;

  private FileCode(String code) {
    this.code = code;
  }

  public String getCode() {
    return code;
  }

  public static FileCode parseCode(String code) {
    for (FileCode imageCode : values()) {
      if (imageCode.getCode().equals(code)) {
        return imageCode;
      }
    }
    return null;
  }
}
