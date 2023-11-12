package io.storytailor.central.story.mapper;

import io.storytailor.central.story.vo.PageVO;
import io.storytailor.central.story.vo.StoryVO;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface StoryMapper {
  void insertStory(@Param("storyVO") StoryVO storyVO);

  void insertPage(@Param("pageVO") PageVO pageVO);

  List<StoryVO> selectStoryList();

  StoryVO selectStory(
    @Param("storyId") Integer storyId,
    @Param("lang") String lang
  );

  List<PageVO> selectPageList(@Param("storyId") Integer storyId);
  List<PageVO> selectPageListByLang(@Param("storyId") Integer storyId, @Param("lang") String lang);

  void deleteStory(@Param("storyId") Integer storyId);
  void deletePage(@Param("storyId") Integer storyId);
}
