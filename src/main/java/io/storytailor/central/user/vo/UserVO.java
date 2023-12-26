package io.storytailor.central.user.vo;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Data;

@Data
public class UserVO {

  private int id;
  private int sessionId;
  private String accessToken;
  private int accessExpire;
  private String refreshToken;
  private int refreshExpire;

  @JsonAlias("kakao_account")
  private AccountVO account;
}
