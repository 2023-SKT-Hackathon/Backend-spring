package io.storytailor.central.story.service;

import io.storytailor.central.chat.vo.ChatRequestVO;
import io.storytailor.central.chat.vo.ChatResponseVO;
import io.storytailor.central.chat.vo.ChatVO;
import io.storytailor.central.config.rest.RestService;
import io.storytailor.central.image.service.ImageSVC;
import io.storytailor.central.keyword.service.KeywordSVC;
import io.storytailor.central.keyword.vo.KeywordResponseVO;
import io.storytailor.central.keyword.vo.KeywordVO;
import io.storytailor.central.story.mapper.StoryMapper;
import io.storytailor.central.story.vo.PageVO;
import io.storytailor.central.story.vo.StoryAiRequestVO;
import io.storytailor.central.story.vo.StoryAiResponseVO;
import io.storytailor.central.story.vo.StoryRequestVO;
import io.storytailor.central.story.vo.StoryVO;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class StorySVC {

  @Value("${flask.base-url}")
  private String flaskBaseUrl;

  @Autowired
  private KeywordSVC keywordSVC;

  @Autowired
  private ImageSVC imageSVC;

  @Autowired
  private RestService restService;

  @Autowired
  private StoryMapper storyMapper;

  public void createStory(StoryRequestVO storyRequestVO) {
    /* Story 객체 생성 */
    StoryVO storyVO = new StoryVO();
    storyVO.setSessionId(storyRequestVO.getSessionId());
    storyVO.setCreater(storyRequestVO.getCreater());
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
    log.info("AI Chat Response: " + res.getBody());
    storyAiResponseVO = res.getBody();
    if (storyAiResponseVO != null) {
      storyVO.setTitle(storyAiResponseVO.getStory().get(0));
      for (int idx = 0; idx < storyAiResponseVO.getImgPrompt().size(); idx++) {
        PageVO pageVO = new PageVO();
        pageVO.setPageNo(idx);
        pageVO.setPageContent(storyAiResponseVO.getStory().get(idx + 1));
        /* Create AI Image */
        // imageSVC.createAiImage(null, null, storyAiResponseVO.getImgPrompt().get(idx), storyAiResponseVO.getImgPrompt().get(idx));
        // storyVO.getPageList().add(pageVO);
      }
      for (String imgPrompt : storyAiResponseVO.getImgPrompt()) {
        PageVO pageVO = new PageVO();
        // imageSVC.createAiImage(null, null, imgPrompt, imgPrompt)

        // storyVO.setImgPrompt(imgPrompt);
      }
    }
    /* 결과로 가져온 데이터로 이미지 생성 요청 */
    /* Url로 디스크에 저장 */
    /* insert data in db */
  }

  public void getStoryList() {}

  public void getStoryById(Integer storyId) {}

  public void deleteStory(Integer storyId) {}
}
