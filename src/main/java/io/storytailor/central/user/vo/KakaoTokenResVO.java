package io.storytailor.central.user.vo;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Data;

@Data
public class KakaoTokenResVO {

  @JsonAlias("token_type")
  private String tokenType;

  @JsonAlias("access_token")
  private String accessToken;

  @JsonAlias("refresh_token")
  private String refreshToken;

  @JsonAlias("expires_in")
  private int expiresIn;

  private String scope;
}
