package io.storytailor.central.chat.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import io.storytailor.central.chat.service.ChatSVC;
import io.storytailor.central.chat.vo.ChatVO;

@Controller
public class ChatController {
    @Autowired
    private ChatSVC chatSVC;

    @PostMapping("/api/chat/voice")
    public ResponseEntity<?> voice(@RequestParam("voiceFile") MultipartFile voiceFile) {
        /* vaildation mp3 file */
        return ResponseEntity.ok().body(chatSVC.convertVoiceToText(voiceFile));
    }

    @PostMapping("/api/chat/{sessionId}")
    public ResponseEntity<?> chat(@PathVariable Integer sessionId, @RequestBody ChatVO chatVO) {
        return ResponseEntity.ok().body(null);
    }

    @GetMapping("/api/chat/hist")
    public ResponseEntity<?> chatHist(@RequestParam("sessionId") Integer sessionId) {
        return ResponseEntity.ok().body(null);
    }
}
