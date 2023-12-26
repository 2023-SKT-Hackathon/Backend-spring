package io.storytailor.central.story.mapper;

import io.storytailor.central.story.vo.PageVO;
import io.storytailor.central.story.vo.StoryVO;
import io.storytailor.central.user.vo.UserVO;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface StoryMapper {
  void insertStory(@Param("storyVO") StoryVO storyVO);

  void insertPage(@Param("pageVO") PageVO pageVO);

  List<StoryVO> selectStoryList(@Param("userVO") UserVO userVO);

  StoryVO selectStory(
    @Param("storyId") Integer storyId,
    @Param("lang") String lang,
    @Param("userVO") UserVO userVO
  );

  List<PageVO> selectPageList(@Param("storyId") Integer storyId);
  List<PageVO> selectPageListByLang(
    @Param("storyId") Integer storyId,
    @Param("lang") String lang
  );

  void deleteStory(
    @Param("storyId") Integer storyId,
    @Param("userVO") UserVO userVO
  );
  void deletePage(
    @Param("storyId") Integer storyId,
    @Param("userVO") UserVO userVO
  );
}
