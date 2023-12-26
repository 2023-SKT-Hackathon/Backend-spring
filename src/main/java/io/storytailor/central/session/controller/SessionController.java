package io.storytailor.central.session.controller;

import io.storytailor.central.session.service.SessionSVC;
import io.storytailor.central.session.vo.SessionVO;
import io.storytailor.central.user.service.LoginSVC;
import io.storytailor.central.user.vo.UserVO;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SessionController {

  @Autowired
  private SessionSVC sessionSVC;

  @Autowired
  private LoginSVC loginSVC;

  @GetMapping("/api/session")
  public ResponseEntity<SessionVO> getSession(HttpServletRequest request) {
    UserVO user = loginSVC.getUserVO(loginSVC.resolveToken(request));
    return ResponseEntity.ok().body(sessionSVC.getSession(user));
  }
}
