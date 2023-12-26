package io.storytailor.central.user.controller;

import io.storytailor.central.code.LoginSsoCode;
import io.storytailor.central.user.service.LoginSVC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class LoginController<T> {

  @Autowired
  private LoginSVC loginSVC;

  @GetMapping("/api/login/url")
  public ResponseEntity<?> getSsoLoginUrl(@RequestParam String platform) {
    for (LoginSsoCode code : LoginSsoCode.values()) {
      if (code.name().equalsIgnoreCase(platform)) {
        return ResponseEntity.ok(loginSVC.getSsoLoginUrl(code));
      }
    }
    return ResponseEntity
      .badRequest()
      .body("Not Supported Platform : " + platform);
  }

  @GetMapping("/api/login/callback/kakao")
  public ResponseEntity<?> loginCallback(
    @RequestParam("code") String code,
    @RequestParam(required = false, name = "error") String error,
    @RequestParam(required = false, name = "error_description") String errorDesc
  ) {
    if (error != null) {
      return ResponseEntity.badRequest().body(errorDesc);
    }
    return ResponseEntity.ok().body(loginSVC.loginCallback(code));
  }
}
