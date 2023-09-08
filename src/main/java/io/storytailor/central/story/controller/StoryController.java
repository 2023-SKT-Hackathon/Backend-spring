package io.storytailor.central.story.controller;

import io.storytailor.central.story.service.StorySVC;
import io.storytailor.central.story.vo.StoryChatVO;
import io.storytailor.central.story.vo.StoryRequestVO;
import io.storytailor.central.story.vo.StoryVO;
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

  @GetMapping("/api/story/list")
  public ResponseEntity<List<StoryVO>> getStoryList() {
    List<StoryVO> res = storySVC.getStoryList();
    return ResponseEntity.ok().body(res);
  }

  @GetMapping("/api/story/{storyId}")
  public ResponseEntity<StoryVO> getStory(
    @PathVariable Integer storyId,
    @RequestParam("lang") String lang
  ) {
    if (lang == null) {
      lang = "ko";
    }
    StoryVO res = storySVC.getStoryById(storyId, lang);
    return ResponseEntity.ok().body(res);
  }

  @GetMapping("/api/story/{storyId}/chat")
  public ResponseEntity<StoryChatVO> getStoryChat(
    @PathVariable Integer storyId
  ) {
    StoryChatVO res = storySVC.getStoryChatById(storyId);
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
  public ResponseEntity<?> deleteStory(@PathVariable Integer storyId) {
    storySVC.deleteStory(storyId);
    return ResponseEntity.ok().body("Success");
  }
}
