package io.storytailor.central.chat.service;

import io.storytailor.central.chat.mapper.ChatMapper;
import io.storytailor.central.chat.vo.ChatHistoryVO;
import io.storytailor.central.chat.vo.ChatRequestVO;
import io.storytailor.central.chat.vo.ChatResponseVO;
import io.storytailor.central.chat.vo.ChatVO;
import io.storytailor.central.chat.vo.WhisperResponseVO;
import io.storytailor.central.code.ChatProgressCode;
import io.storytailor.central.code.ChatTypeCode;
import io.storytailor.central.config.rest.RestService;
import io.storytailor.central.keyword.service.KeywordSVC;
import io.storytailor.central.keyword.vo.KeywordVO;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;

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

  @Autowired
  private KeywordSVC keywordSVC;

  public WhisperResponseVO convertVoiceToText(MultipartFile voiceFile) {
    try {
      Map<String, String> header = new HashMap<String, String>();
      header.put("Content-Type", "multipart/form-data");
      MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
      body.add("file", voiceFile);
      body.add("model", audioModel);
      ResponseEntity<WhisperResponseVO> res = restService.post(
        defaultOpenaiApiUrl + audioPath,
        null,
        header,
        body,
        null,
        openaiApiKey,
        WhisperResponseVO.class
      );
      return res.getBody();
    } catch (Exception e) {
      log.error("Fail to Convert Voice to Text", e);
      return null;
    }
  }

  public ChatVO getInitAiChat(Integer sessionId) {
    ChatRequestVO chatRequestVO = new ChatRequestVO();
    chatRequestVO.setMsgNum(0);
    chatRequestVO.setMsgType(ChatTypeCode.USER.getCode());
    chatRequestVO.setSessionId(sessionId);
    chatRequestVO.setText("START");
    log.info("User Request First Chat Request: " + sessionId.toString());
    /* Send Ai question init */
    ResponseEntity<ChatResponseVO> res = restService.post(
      flaskBaseUrl + "chat",
      null,
      null,
      chatRequestVO,
      null,
      null,
      ChatResponseVO.class
    );
    ChatResponseVO responseVO = res.getBody();
    log.info("AI Chat Response: " + responseVO);
    if (responseVO != null) {
      ChatVO chatVO = new ChatVO();
      chatVO.setSessionId(sessionId);
      chatVO.setMsgNum(Integer.parseInt(responseVO.getMsgNum()));
      chatVO.setMsgType(ChatTypeCode.AI.getCode());
      chatVO.setText(responseVO.getText());
      chatVO.setProgress(ChatProgressCode.DOING.getCode());
      /* save AI msg in Database */
      chatMapper.insertChat(chatVO);
      return chatVO;
    } else return null;
  }

  public ChatVO sendAiChat(Integer sessionId, ChatVO chatVO) {
    /* Insert Chat Hist */
    chatMapper.insertChat(chatVO);
    /* Send Ai question */
    ChatRequestVO chatRequestVO = convertChatVOToChatRequestVO(chatVO);
    log.info("User Chat Request: " + chatRequestVO.toString());
    ResponseEntity<ChatResponseVO> res = restService.post(
      flaskBaseUrl + "chat",
      null,
      null,
      chatRequestVO,
      null,
      null,
      ChatResponseVO.class
    );
    log.info("AI Chat Response: " + res.getBody());
    ChatResponseVO responseVO = res.getBody();
    ChatVO resChatVO = convertChatResponseVOToChatVO(responseVO);
    if (responseVO != null) {
      /* save AI msg in Database */
      chatMapper.insertChat(resChatVO);
      /* keyword save */
      if (responseVO.getStatus().equals("True")) {
        /* Extract Keyword */
        for (String keyword : responseVO.getKeyword()) {
          KeywordVO keywordVO = new KeywordVO();
          keywordVO.setSessionId(Integer.parseInt(responseVO.getSessionId()));
          keywordVO.setType("extract");
          keywordVO.setKeyword(keyword);
          keywordSVC.createTempKeyword(keywordVO);
        }
        /* Recommand Keyword */
        for (String keyword : responseVO.getRecoKeyword()) {
          KeywordVO keywordVO = new KeywordVO();
          keywordVO.setSessionId(Integer.parseInt(responseVO.getSessionId()));
          keywordVO.setType("recommand");
          keywordVO.setKeyword(keyword);
          keywordSVC.createTempKeyword(keywordVO);
        }
      }
    }
    return resChatVO;
  }

  private ChatRequestVO convertChatVOToChatRequestVO(ChatVO chatVO) {
    ChatRequestVO chatRequestVO = new ChatRequestVO();
    chatRequestVO.setMsgNum(chatVO.getMsgNum());
    chatRequestVO.setMsgType(ChatTypeCode.USER.getCode());
    chatRequestVO.setSessionId(chatVO.getSessionId());
    chatRequestVO.setText(chatVO.getText());
    ChatHistoryVO chatHistoryVO = new ChatHistoryVO();
    /* Chat Hist Insert */
    List<ChatVO> chatHist = chatMapper.getChatList(chatVO.getSessionId());
    List<String> userChatHist = new ArrayList<>();
    List<String> aiChatHist = new ArrayList<>();
    for (ChatVO chat : chatHist) {
      if (chat.getMsgType().equals(ChatTypeCode.USER.getCode())) {
        userChatHist.add(chat.getText());
      } else {
        aiChatHist.add(chat.getText());
      }
    }
    chatHistoryVO.setUserChat(userChatHist);
    chatHistoryVO.setAiChat(aiChatHist);
    chatRequestVO.setHistory(chatHistoryVO);
    return chatRequestVO;
  }

  private ChatVO convertChatResponseVOToChatVO(ChatResponseVO chatResponseVO) {
    ChatVO chatVO = new ChatVO();
    chatVO.setSessionId(Integer.parseInt(chatResponseVO.getSessionId()));
    chatVO.setMsgNum(Integer.parseInt(chatResponseVO.getMsgNum()));
    chatVO.setMsgType(ChatTypeCode.AI.getCode());
    chatVO.setText(chatResponseVO.getText());
    if (chatResponseVO.getStatus().equals("True")) {
      chatVO.setProgress(ChatProgressCode.COMPLETE.getCode());
    } else {
      chatVO.setProgress(ChatProgressCode.DOING.getCode());
    }
    return chatVO;
  }
}
