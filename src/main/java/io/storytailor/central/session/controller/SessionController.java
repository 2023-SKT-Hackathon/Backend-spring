package io.storytailor.central.session.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import io.storytailor.central.session.service.SessionSVC;
import io.storytailor.central.session.vo.SessionVO;

@Controller
public class SessionController {
    @Autowired
    private SessionSVC sessionSVC;

    @GetMapping("/api/session")
    public ResponseEntity<SessionVO> getSession() {
        return ResponseEntity.ok().body(sessionSVC.getSession());
    }
}
