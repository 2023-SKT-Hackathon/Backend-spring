package io.storytailor.central.code;

public enum LangCode {
  KO("ko"),
  EN("en"),
  JA("ja"),
  ZH("zh");

  private String code;

  private LangCode(String code) {
    this.code = code;
  }

  public String getCode() {
    return code;
  }

  public static LangCode parseCode(String code) {
    for (LangCode langCode : values()) {
      if (langCode.getCode().equals(code)) {
        return langCode;
      }
    }
    return null;
  }
}
