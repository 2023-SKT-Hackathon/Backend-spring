package io.storytailor.central.chat.controller;

import io.storytailor.central.chat.service.ChatSVC;
import io.storytailor.central.chat.vo.ChatVO;
import io.storytailor.central.chat.vo.WhisperResponseVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class ChatController {

  @Autowired
  private ChatSVC chatSVC;

  @PostMapping("/api/chat/voice")
  public ResponseEntity<WhisperResponseVO> voice(
    @RequestParam("sessionId") Integer sessionId,
    @RequestParam("voiceFile") MultipartFile voiceFile
  ) {
    /* vaildation mp3 file */
    return ResponseEntity
      .ok()
      .body(chatSVC.convertVoiceToText(sessionId, voiceFile));
  }

  @GetMapping("/api/chat/{sessionId}")
  public ResponseEntity<ChatVO> getInitChat(@PathVariable Integer sessionId) {
    ChatVO res = chatSVC.getInitAiChat(sessionId);
    return ResponseEntity.ok().body(res);
  }

  @PostMapping("/api/chat/{sessionId}")
  public ResponseEntity<ChatVO> chat(
    @PathVariable Integer sessionId,
    @RequestBody ChatVO chatVO
  ) {
    ChatVO res = chatSVC.sendAiChat(sessionId, chatVO);
    return ResponseEntity.ok().body(res);
  }
}
