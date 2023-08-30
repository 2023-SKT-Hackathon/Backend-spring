package io.storytailor.central.keyword.mapper;

import io.storytailor.central.keyword.vo.KeywordVO;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface KeywordMapper {
  void insertKeyword(@Param("keywordVO") KeywordVO keywordVO);
  void insertTempKeyword(@Param("keywordVO") KeywordVO keywordVO);

  List<KeywordVO> selectKeyword(@Param("sessionId") Integer sessionId);
  List<KeywordVO> selectTempKeyword(@Param("sessionId") Integer sessionId);
}
