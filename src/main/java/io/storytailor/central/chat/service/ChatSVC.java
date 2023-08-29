package io.storytailor.central.chat.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;

import io.storytailor.central.chat.mapper.ChatMapper;
import io.storytailor.central.chat.vo.ChatRequestVO;
import io.storytailor.central.chat.vo.ChatResponseVO;
import io.storytailor.central.chat.vo.ChatVO;
import io.storytailor.central.chat.vo.WhisperResponseVO;
import io.storytailor.central.code.ChatTypeCode;
import io.storytailor.central.config.rest.RestService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ChatSVC {
    @Value("${flask.base-url}")
    private String flaskBaseUrl;

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

    @Autowired
    private ChatMapper chatMapper;

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

    public ChatResponseVO getInitAiChat(Integer sessionId) {
        ChatRequestVO chatRequestVO = new ChatRequestVO();
        chatRequestVO.setMsgNum(0);
        chatRequestVO.setMsgType(ChatTypeCode.USER.getCode());
        chatRequestVO.setSessionId(sessionId);
        chatRequestVO.setText("START");
        chatRequestVO.setHistory(new ArrayList<>());
        log.info("User Request First Chat Request: " + sessionId.toString());
        /* Send Ai question init */
        ResponseEntity<ChatResponseVO> res = restService.post(
                flaskBaseUrl + "chat",
                null,
                null,
                chatRequestVO,
                null,
                null,
                ChatResponseVO.class);
        log.info("AI Chat Response: " + res.getBody());
        /* save AI msg in Database */

        return res.getBody();
    }

    public ChatResponseVO sendAiChat(Boolean initMsg, ChatVO chatVO) {
        ChatRequestVO chatRequestVO = new ChatRequestVO();
        log.info("User Chat Request: " + chatRequestVO.toString());
        ResponseEntity<ChatResponseVO> res = restService.post(
                flaskBaseUrl + "chat",
                null,
                null,
                chatRequestVO,
                null,
                null,
                ChatResponseVO.class);
        log.info("AI Chat Response: " + res.getBody());
        ChatResponseVO responseVO = res.getBody();
        /* save AI msg in Database */

        /* keyword save */
        if (responseVO.getStatus().equals("True")){
            chatMapper.insertKeyword(chatVO);
        }
        return res.getBody();
    }
}
