package io.storytailor.central.user.service;

import io.storytailor.central.code.LoginSsoCode;
import io.storytailor.central.keyword.vo.KeywordVO;
import io.storytailor.central.user.vo.LoginVO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class LoginSVC {

  @Value("${login.kakao.login-uri}")
  private String kakaoLoginUri;

  @Value("${login.kakao.token-uri}")
  private String kakaoTokenUri;

  @Value("${login.kakao.redirect-uri}")
  private String kakaoRedirectUri;

  @Value("${login.kakao.client-id}")
  private String kakaoClientId;

  public LoginVO getSsoLoginUrl(LoginSsoCode platform) {
    LoginVO loginVO = new LoginVO();
    loginVO.setType("sso");
    loginVO.setPlatform(platform.name());
    loginVO.setUrl(getLoginUrl(platform.name()));
    return loginVO;
  }


  private String getLoginUrl(String platform) {
    if (platform.equalsIgnoreCase("kakao")) {
      return (
        kakaoLoginUri +
        "?client_id=" +
        kakaoClientId +
        "&redirect_uri=" +
        kakaoRedirectUri +
        "&response_type=code"
      );
    }
    return null;
  }

  private String getTokenUrl(String platform) {
    if (platform.equalsIgnoreCase("kakao")) {
      return kakaoTokenUri;
    }
    return null;
  }

  private void getToken(LoginSsoCode platform){
    String tokenUrl = getTokenUrl(platform.name());
    if (platform.equalsIgnoreCase("kakao")) {
/* Send Ai question */
    ResponseEntity<ChatResponseVO> res = restService.post(
      tokenUrl,
      null,
      null,
      chatRequestVO,
      null,
      null,
      ChatResponseVO.class
    );
    log.info("AI Chat Response: " + res.getBody());

    if (responseVO != null) {
     
    }
    return resChatVO;
  }
}
}
