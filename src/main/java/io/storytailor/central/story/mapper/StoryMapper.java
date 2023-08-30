package io.storytailor.central.story.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import io.storytailor.central.story.vo.StoryVO;

@Mapper
public interface StoryMapper {
    List<StoryVO> selectStoryList();

    void deleteStory(Integer storyId);
}
