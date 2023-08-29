package io.storytailor.central.story.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import io.storytailor.central.story.service.StorySVC;
import io.storytailor.central.story.vo.StoryRequestVO;

@Controller
public class StoryController {
    @Autowired
    private StorySVC storySVC;
    
    @PostMapping("/api/story")
    public ResponseEntity<?> createStory(@RequestBody StoryRequestVO storyRequestVO) {
        storySVC.createStory(storyRequestVO);
        return ResponseEntity.ok().body("Success");
    }
}
