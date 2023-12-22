package io.storytailor.central.code;

public enum LoginSsoCode {
  KAKAO("kakao");

  private String code;

  private LoginSsoCode(String code) {
    this.code = code;
  }

  public String getCode() {
    return code;
  }

  public static LoginSsoCode parseCode(String code) {
    for (LoginSsoCode loginSsoCode : values()) {
      if (loginSsoCode.getCode().equals(code)) {
        return loginSsoCode;
      }
    }
    return null;
  }
}
