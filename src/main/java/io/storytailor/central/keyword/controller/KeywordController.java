package io.storytailor.central.keyword.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import io.storytailor.central.keyword.service.KeywordSVC;
import io.storytailor.central.keyword.vo.KeywordVO;

@Controller
public class KeywordController {

    @Autowired
    private KeywordSVC keywordSVC;

    @GetMapping("/api/keyword")
    public ResponseEntity<KeywordVO> getKeyword(@RequestParam("sessionId") Integer sessionId) {
        KeywordVO res = keywordSVC.getKeyword(sessionId);
        return new ResponseEntity<KeywordVO>(new KeywordVO(), null, null);
    }

    @PostMapping("/api/keyword")
    public ResponseEntity<?> createKeyword(@RequestBody KeywordVO keywordVO) {
        return ResponseEntity.ok().body(keywordVO);
    }
}
