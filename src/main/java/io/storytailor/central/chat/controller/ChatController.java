package io.storytailor.central.chat.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class ChatController {
    @PostMapping("/api/chat/voice")
    public ResponseEntity<?> voice(@RequestParam("voiceFile") MultipartFile voiceFile) {
        return ResponseEntity.ok().body(null);
    }

    @PostMapping("/api/chat/{sessionId}")
    public ResponseEntity<?> chat() {
        return ResponseEntity.ok().body(null);
    }

    @GetMapping("/api/chat/hist")
    public ResponseEntity<?> chatHist() {
        return ResponseEntity.ok().body(null);
    }
}
