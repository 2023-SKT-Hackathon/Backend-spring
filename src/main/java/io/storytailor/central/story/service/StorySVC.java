package io.storytailor.central.story.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.storytailor.central.story.mapper.StoryMapper;
import io.storytailor.central.story.vo.StoryRequestVO;

@Service
public class StorySVC {
    @Autowired
    private StoryMapper storyMapper;

    public void createStory(StoryRequestVO storyRequestVO) {
        
    }

    public void getStoryList(){
        
    }

    public void getStoryById(Integer storyId) {
        
    }

    public void deleteStory(Integer storyId) {
        
    }

    
}
