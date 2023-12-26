package io.storytailor.central.user.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import io.storytailor.central.code.LoginSsoCode;
import io.storytailor.central.user.mapper.UserMapper;
import io.storytailor.central.user.vo.AccountVO;
import io.storytailor.central.user.vo.KakaoTokenResVO;
import io.storytailor.central.user.vo.LoginVO;
import io.storytailor.central.user.vo.UserVO;
import jakarta.servlet.http.HttpServletRequest;
import java.sql.Date;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
public class LoginSVC {

  @Value("${login.token.secret}")
  private String secretKey;

  @Value("${login.token.access-time}")
  private Long accessTime;

  @Value("${login.token.refresh-time}")
  private Long refreshTime;

  @Value("${login.kakao.login-url}")
  private String kakaoLoginUri;

  @Value("${login.kakao.token-url}")
  private String kakaoTokenUri;

  @Value("${login.kakao.redirect-url}")
  private String kakaoRedirectUri;

  @Value("${login.kakao.user-info-url}")
  private String userInfoUri;

  @Value("${login.kakao.client-id}")
  private String kakaoClientId;

  @Autowired
  private UserMapper userMapper;

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

  public UserVO loginCallback(String code) {
    String token = getAccessToken(code);
    UserVO user = getKakaoUserInfo(token);
    if (!isExistUser(user)) {
      userMapper.insertUser(user);
    }
    generateToken(user);
    userMapper.updateToken(user);
    return user;
  }

  private String getAccessToken(String code) {
    try {
      //header 생성
      HttpHeaders headers = new HttpHeaders();
      headers.add(
        "Content-type",
        "application/x-www-form-urlencoded;charset=utf-8"
      );

      //body 생성
      MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
      body.add("grant_type", "authorization_code");
      body.add("client_id", kakaoClientId);
      body.add("redirect_uri", kakaoRedirectUri);
      body.add("code", code);

      //HTTP 요청 보내기
      HttpEntity<MultiValueMap<String, String>> kakaoTokenRequest = new HttpEntity<>(
        body,
        headers
      );

      RestTemplate rt = new RestTemplate();
      ResponseEntity<KakaoTokenResVO> response = rt.exchange(
        getTokenUrl("kakao"),
        HttpMethod.POST,
        kakaoTokenRequest,
        KakaoTokenResVO.class
      );

      return response.getBody().getAccessToken();
    } catch (Exception e) {
      // TODO: handle exception
      return null;
    }
  }

  private UserVO getKakaoUserInfo(String accessToken) {
    try {
      //HTTP Header 생성
      HttpHeaders headers = new HttpHeaders();
      headers.add("Authorization", "Bearer " + accessToken);
      headers.add(
        "Content-type",
        "application/x-www-form-urlencoded;charset=utf-8"
      );

      //Http 요청 보내기
      HttpEntity<MultiValueMap<String, String>> kakaoUserInfoRequest = new HttpEntity<>(
        headers
      );
      RestTemplate rt = new RestTemplate();
      ResponseEntity<String> res = rt.exchange(
        userInfoUri,
        HttpMethod.POST,
        kakaoUserInfoRequest,
        String.class
      );
      String responseBody = res.getBody();
      UserVO user = new UserVO();

      ObjectMapper objectMapper = new ObjectMapper();
      JsonNode jsonNode = objectMapper.readTree(responseBody);
      AccountVO account = new AccountVO();
      account.setName(jsonNode.findValue("nickname").asText());
      account.setEmail(jsonNode.findValue("email").asText());
      user.setAccount(account);
      return user;
      // return res.getBody();
    } catch (Exception e) {
      // TODO: handle exception
      return null;
    }
  }

  private Boolean isExistUser(UserVO user) {
    AccountVO account = userMapper.selectUser(user);
    if (account == null) {
      return false;
    } else {
      return true;
    }
  }

  public void generateToken(UserVO user) {
    Claims claims = Jwts.claims();
    claims.put("name", user.getAccount().getName());
    claims.put("email", user.getAccount().getEmail());

    Date now = new Date(System.currentTimeMillis());
    user.setAccessToken(createToken(secretKey, accessTime, claims, now));
    user.setRefreshToken(createToken(secretKey, refreshTime, claims, now));
  }

  public String resolveToken(HttpServletRequest request) {
    String bearerToken = request.getHeader(HttpHeaders.AUTHORIZATION);
    if (bearerToken != null && bearerToken.startsWith("Bearer")) {
      return bearerToken.substring(7);
    }
    return null;
  }

  //token에 있는 회원정보 추출
  public String getUserInfo(String token) {
    return Jwts
      .parserBuilder()
      .setSigningKey(secretKey.getBytes())
      .build()
      .parseClaimsJws(token)
      .getBody()
      .getSubject();
  }

  public UserVO getUserVO(String token) {
    UserVO user = new UserVO();
    user.setAccessToken(token);
    Claims claims = Jwts
      .parserBuilder()
      .setSigningKey(secretKey.getBytes())
      .build()
      .parseClaimsJws(token)
      .getBody();
    String name = claims.get("name", String.class);
    String email = claims.get("email", String.class);
    AccountVO account = new AccountVO();
    account.setName(name);
    account.setEmail(email);
    user.setAccount(account);
    return user;
  }

  private String createToken(
    String secretKey,
    long expireTime,
    Claims claims,
    Date now
  ) {
    return Jwts
      .builder()
      .setClaims(claims)
      .setIssuedAt(now)
      .setExpiration(new Date(System.currentTimeMillis() + expireTime))
      .signWith(
        Keys.hmacShaKeyFor(secretKey.getBytes()),
        SignatureAlgorithm.HS256
      )
      .compact();
  }

  public boolean validateToken(String accessToken) {
    try {
      Jwts
        .parserBuilder()
        .setSigningKey(secretKey.getBytes())
        .build()
        .parseClaimsJws(accessToken);
      return true;
    } catch (ExpiredJwtException e) {
      log.error("Expired JWT token: ", e);
    } catch (UnsupportedJwtException e) {
      log.error("Unsupported JWT token: ", e);
    } catch (IllegalArgumentException e) {
      log.error("JWT claims string is empty: ", e);
    }

    return false;
  }
}
