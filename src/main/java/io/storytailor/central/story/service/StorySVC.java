package io.storytailor.central.story.service;

import io.storytailor.central.chat.service.ChatSVC;
import io.storytailor.central.chat.vo.ChatVO;
import io.storytailor.central.config.rest.RestService;
import io.storytailor.central.image.service.ImageSVC;
import io.storytailor.central.keyword.service.KeywordSVC;
import io.storytailor.central.keyword.vo.KeywordResponseVO;
import io.storytailor.central.story.mapper.StoryMapper;
import io.storytailor.central.story.vo.PageVO;
import io.storytailor.central.story.vo.StoryAiRequestVO;
import io.storytailor.central.story.vo.StoryAiResponseVO;
import io.storytailor.central.story.vo.StoryChatVO;
import io.storytailor.central.story.vo.StoryRequestVO;
import io.storytailor.central.story.vo.StoryVO;
import io.storytailor.central.story.vo.TranslateVO;
import io.storytailor.central.user.vo.UserVO;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class StorySVC {

  @Value("${image.base-url}")
  private String baseUrl;

  @Value("${flask.base-url}")
  private String flaskBaseUrl;

  @Autowired
  private ChatSVC chatSVC;

  @Autowired
  private KeywordSVC keywordSVC;

  @Autowired
  private ImageSVC imageSVC;

  @Autowired
  private RestService restService;

  @Autowired
  private StoryMapper storyMapper;

  public StoryVO createStory(StoryRequestVO storyRequestVO) {
    /* Story 객체 생성 */
    StoryVO storyVO = new StoryVO();
    storyVO.setSessionId(storyRequestVO.getSessionId());
    storyVO.setCreater(storyRequestVO.getCreater());
    /* Get Cover Image Info */
    String coverImgPath =
      baseUrl +
      storyRequestVO.getSessionId().toString() +
      File.separator +
      "origin" +
      File.separator +
      "origin.png";
    storyVO.setCoverImg(coverImgPath);
    //TODO language Set Up
    storyVO.setLang("ko");
    /* session Id로 저장된 키워드 가져오기 */
    KeywordResponseVO getKeyword = keywordSVC.getKeyword(
      storyRequestVO.getSessionId()
    );
    List<String> finalKeyword = new ArrayList<>();
    for (String keyword : getKeyword.getExtractKeyword()) {
      finalKeyword.add(keyword);
    }
    for (String keyword : getKeyword.getRecommandKeyword()) {
      finalKeyword.add(keyword);
    }
    /* 가져온 키워드 AI 요청 */
    StoryAiRequestVO storyAiRequestVO = new StoryAiRequestVO();
    StoryAiResponseVO storyAiResponseVO = new StoryAiResponseVO();
    storyAiRequestVO.setKeyword(finalKeyword);
    log.info("User Story Request: " + storyAiRequestVO.toString());
    ResponseEntity<StoryAiResponseVO> res = restService.post(
      flaskBaseUrl + "makeStory",
      null,
      null,
      storyAiRequestVO,
      null,
      null,
      StoryAiResponseVO.class
    );
    log.info("AI Story Response: " + res.getBody());
    storyAiResponseVO = res.getBody();

    if (storyAiResponseVO != null) {
      storyVO.setTitle(storyAiResponseVO.getStory().get(0));
      /* insert data in db */
      storyMapper.insertStory(storyVO);
      List<PageVO> pageList = new ArrayList<>();
      for (int idx = 0; idx < storyAiResponseVO.getImgPrompt().size(); idx++) {
        PageVO pageVO = new PageVO();
        pageVO.setLang("ko");
        pageVO.setSessionId(storyRequestVO.getSessionId());
        pageVO.setStoryId(storyVO.getId());
        pageVO.setPageNo(idx);
        pageVO.setPageContent(storyAiResponseVO.getStory().get(idx + 1));
        /* Create AI Image */
        String path = imageSVC.createAndSaveAiImage(
          storyRequestVO.getSessionId(),
          storyAiResponseVO.getImgPrompt().get(idx),
          idx
        );
        pageVO.setPageImg(baseUrl + path);
        /* insert page data in db */
        storyMapper.insertPage(pageVO);
        pageList.add(pageVO);
      }
      storyVO.setPages(pageList);
    }
    return storyVO;
  }

  public List<StoryVO> getStoryList(UserVO user) {
    return storyMapper.selectStoryList(user);
  }

  public StoryVO getStoryById(Integer storyId, String lang, UserVO user) {
    /* if already exist Database */
    List<PageVO> pList = storyMapper.selectPageListByLang(storyId, lang);
    StoryVO storyVO = storyMapper.selectStory(storyId, "ko", user);
    if (pList.size() > 0) {
      storyVO.setLang(lang);
      storyVO.setPages(pList);
      return storyVO;
    } else {
      StoryVO storyKoVO = storyMapper.selectStory(storyId, "ko", user);
      List<PageVO> pageKoList = storyMapper.selectPageList(storyId);
      storyKoVO.setPages(pageKoList);
      TranslateVO translateVO = new TranslateVO();
      List<String> storyPages = new ArrayList<>();
      storyPages.add(storyKoVO.getTitle());
      for (PageVO pageVO : storyKoVO.getPages()) {
        storyPages.add(pageVO.getPageContent());
      }
      translateVO.setLang(lang);
      translateVO.setStory(storyPages);

      log.info("User Translate Request: " + translateVO.toString());
      ResponseEntity<TranslateVO> res = restService.post(
        flaskBaseUrl + "translate",
        null,
        null,
        translateVO,
        null,
        null,
        TranslateVO.class
      );
      log.info("AI Story Response: " + res.getBody());
      TranslateVO translateResponseVO = res.getBody();
      if (translateResponseVO != null) {
        storyKoVO.setLang(lang);
        storyKoVO.setTitle(translateResponseVO.getStory().get(0));

        List<PageVO> pageList = new ArrayList<>();
        for (int idx = 1; idx < translateResponseVO.getStory().size(); idx++) {
          PageVO pageVO = new PageVO();
          pageVO.setSessionId(storyKoVO.getSessionId());
          pageVO.setLang(lang);
          pageVO.setStoryId(storyId);
          pageVO.setPageNo(idx - 1);
          pageVO.setPageContent(translateResponseVO.getStory().get(idx));
          pageVO.setPageImg(storyKoVO.getPages().get(idx - 1).getPageImg());
          storyMapper.insertPage(pageVO);
          pageList.add(pageVO);
        }
        storyKoVO.setPages(pageList);
        storyKoVO.setLang(lang);
        storyMapper.insertStory(storyKoVO);
        return storyKoVO;
      } else return null;
    }
  }

  public StoryChatVO getStoryChatById(Integer storyId, UserVO user) {
    StoryChatVO storyChatVO = new StoryChatVO();
    StoryVO storyVO = storyMapper.selectStory(storyId, "ko", user);
    storyChatVO.setId(storyVO.getId());
    storyChatVO.setSessionId(storyVO.getSessionId());
    storyChatVO.setTitle(storyVO.getTitle());
    storyChatVO.setCoverImg(storyVO.getCoverImg());
    storyChatVO.setLang(storyVO.getLang());
    storyChatVO.setCreater(storyVO.getCreater());
    List<ChatVO> chatList = chatSVC.getChatList(storyVO.getSessionId());
    storyChatVO.setChat(chatList);
    KeywordResponseVO keywordResponseVO = keywordSVC.getKeyword(
      storyVO.getSessionId()
    );
    storyChatVO.setKeyword(keywordResponseVO);
    return storyChatVO;
  }

  public void deleteStory(Integer storyId, UserVO user) {
    storyMapper.deleteStory(storyId, user);
    storyMapper.deletePage(storyId, user);
  }
}
