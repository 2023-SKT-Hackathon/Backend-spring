package io.storytailor.central.chat.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ChatSVC {

    public String convertVoiceToText(MultipartFile voiceFile) {

        return "text";
    }
}
