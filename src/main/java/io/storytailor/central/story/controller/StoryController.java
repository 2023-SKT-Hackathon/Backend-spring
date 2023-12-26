package io.storytailor.central.story.controller;

import io.storytailor.central.story.service.StorySVC;
import io.storytailor.central.story.vo.StoryChatVO;
import io.storytailor.central.story.vo.StoryRequestVO;
import io.storytailor.central.story.vo.StoryVO;
import io.storytailor.central.user.service.LoginSVC;
import io.storytailor.central.user.vo.UserVO;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class StoryController {

  @Autowired
  private StorySVC storySVC;

  @Autowired
  private LoginSVC loginSVC;

  @GetMapping("/api/story/list")
  public ResponseEntity<List<StoryVO>> getStoryList(
    HttpServletRequest request
  ) {
    UserVO user = loginSVC.getUserVO(loginSVC.resolveToken(request));
    List<StoryVO> res = storySVC.getStoryList(user);
    return ResponseEntity.ok().body(res);
  }

  @GetMapping("/api/story/{storyId}")
  public ResponseEntity<StoryVO> getStory(
    HttpServletRequest request,
    @PathVariable Integer storyId,
    @RequestParam("lang") String lang
  ) {
    if (lang == null) {
      lang = "ko";
    }
    UserVO user = loginSVC.getUserVO(loginSVC.resolveToken(request));
    StoryVO res = storySVC.getStoryById(storyId, lang, user);
    return ResponseEntity.ok().body(res);
  }

  @GetMapping("/api/story/{storyId}/chat")
  public ResponseEntity<StoryChatVO> getStoryChat(
    HttpServletRequest request,
    @PathVariable Integer storyId
  ) {
    UserVO user = loginSVC.getUserVO(loginSVC.resolveToken(request));
    StoryChatVO res = storySVC.getStoryChatById(storyId, user);
    return ResponseEntity.ok().body(res);
  }

  @PostMapping("/api/story")
  public ResponseEntity<StoryVO> createStory(
    @RequestBody StoryRequestVO storyRequestVO
  ) {
    StoryVO res = storySVC.createStory(storyRequestVO);
    return ResponseEntity.ok().body(res);
  }

  @DeleteMapping("/api/story/{storyId}")
  public ResponseEntity<?> deleteStory(
    HttpServletRequest request,
    @PathVariable Integer storyId
  ) {
    UserVO user = loginSVC.getUserVO(loginSVC.resolveToken(request));
    storySVC.deleteStory(storyId, user);
    return ResponseEntity.ok().body("Success");
  }
}
