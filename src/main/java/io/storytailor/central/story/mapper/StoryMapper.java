package io.storytailor.central.story.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import io.storytailor.central.story.vo.PageVO;
import io.storytailor.central.story.vo.StoryVO;

@Mapper
public interface StoryMapper {
    void insertStory(@Param("storyVO") StoryVO storyVO);

    void insertPage(@Param("pageVO") PageVO pageVO);

    List<StoryVO> selectStoryList();

    StoryVO selectStory(@Param("storyId") Integer storyId);

    List<PageVO> selectPageList(@Param("storyId") Integer storyId);

    void deleteStory(@Param("storyId") Integer storyId);
    void deletePage(@Param("storyId") Integer storyId);
}
