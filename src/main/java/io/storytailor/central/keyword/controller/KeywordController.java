package io.storytailor.central.keyword.controller;

import io.storytailor.central.keyword.service.KeywordSVC;
import io.storytailor.central.keyword.vo.KeywordResponseVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class KeywordController {

  @Autowired
  private KeywordSVC keywordSVC;

  @GetMapping("/api/keyword")
  public ResponseEntity<KeywordResponseVO> getKeyword(
    @RequestParam("sessionId") Integer sessionId
  ) {
    KeywordResponseVO res = keywordSVC.getTempKeyword(sessionId);
    return ResponseEntity.ok().body(res);
  }

  @PostMapping("/api/keyword")
  public ResponseEntity<KeywordResponseVO> createKeyword(
    @RequestBody KeywordResponseVO keywordVO
  ) {
    KeywordResponseVO res = keywordSVC.createKeyword(keywordVO);
    return ResponseEntity.ok().body(res);
  }
}
