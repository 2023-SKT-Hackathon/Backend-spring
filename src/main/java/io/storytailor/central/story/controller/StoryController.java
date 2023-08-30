package io.storytailor.central.story.controller;

import io.storytailor.central.story.service.StorySVC;
import io.storytailor.central.story.vo.StoryRequestVO;
import jakarta.websocket.server.PathParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
public class StoryController {

  @Autowired
  private StorySVC storySVC;

  @GetMapping("/api/story/list")
  public ResponseEntity<?> getStoryList() {
    return ResponseEntity.ok().body(storySVC.getStoryList());
  }

  @GetMapping("/api/story/{storyId}")
  public ResponseEntity<?> getStory(@PathVariable Integer storyId) {
    return ResponseEntity.ok().body(storySVC.getStory(storyId));
  }

  @GetMapping("/api/story/{storyId}/chat")
  public ResponseEntity<?> getStoryChat(@PathVariable Integer storyId) {
    return ResponseEntity.ok().body(storySVC.getStoryChat(storyId));
  }

  @PostMapping("/api/story")
  public ResponseEntity<?> createStory(
    @RequestBody StoryRequestVO storyRequestVO
  ) {
    storySVC.createStory(storyRequestVO);
    return ResponseEntity.ok().body("Success");
  }

  @DeleteMapping("/api/story/{storyId}")
  public ResponseEntity<?> deleteStory(@PathVariable Integer storyId) {
    storySVC.deleteStory(storyId);
    return ResponseEntity.ok().body("Success");
  }
}
