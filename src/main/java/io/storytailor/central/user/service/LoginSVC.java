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
    ChatResponseVO responseVO = res.getBody();
    ChatVO resChatVO = convertChatResponseVOToChatVO(responseVO);
    if (responseVO != null) {
      /* Insert Chat Hist */
      chatMapper.insertChat(chatVO);
      /* save AI msg in Database */
      chatMapper.insertChat(resChatVO);
      /* keyword save */
      if (responseVO.getStatus().toUpperCase().equals("TRUE")) {
        /* Extract Keyword */
        for (String keyword : responseVO.getKeyword()) {
          KeywordVO keywordVO = new KeywordVO();
          keywordVO.setSessionId(Integer.parseInt(responseVO.getSessionId()));
          keywordVO.setType("extract");
          keywordVO.setKeyword(keyword);
          keywordSVC.createTempKeyword(keywordVO);
        }
        /* Recommand Keyword */
        for (String keyword : responseVO.getRecoKeyword()) {
          KeywordVO keywordVO = new KeywordVO();
          keywordVO.setSessionId(Integer.parseInt(responseVO.getSessionId()));
          keywordVO.setType("recommand");
          keywordVO.setKeyword(keyword);
          keywordSVC.createTempKeyword(keywordVO);
        }
      }
    }
    return resChatVO;
  }
}
