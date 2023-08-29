package io.storytailor.central.chat.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;

import io.storytailor.central.chat.vo.WhisperResponseVO;
import io.storytailor.central.config.rest.RestService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ChatSVC {
    @Value("${openai-service.api-key}")
    private String openaiApiKey;

    @Value("${openai-service.api-url}")
    private String defaultOpenaiApiUrl;

    @Value("${openai-service.audio.path}")
    private String audioPath;

    @Value("${openai-service.audio.model}")
    private String audioModel;

    @Autowired
    private RestService restService;

    public WhisperResponseVO convertVoiceToText(MultipartFile voiceFile) {
        try {
            Map<String, String> header = new HashMap<String, String>();
            header.put("Content-Type", "multipart/form-data");
            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("file", voiceFile);
            body.add("model", audioModel);
            ResponseEntity<WhisperResponseVO> res = restService.post(defaultOpenaiApiUrl+audioPath, null, header, body, null, openaiApiKey, WhisperResponseVO.class);
            return res.getBody();
        } catch (Exception e) {
            log.error("Fail to Convert Voice to Text",e);
            return null;
        }
    }
}
