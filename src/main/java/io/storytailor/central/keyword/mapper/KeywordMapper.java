package io.storytailor.central.keyword.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import io.storytailor.central.keyword.vo.KeywordVO;

@Mapper
public interface KeywordMapper {
    void insertKeyword(@Param("keywordVO") KeywordVO keywordVO);
}
