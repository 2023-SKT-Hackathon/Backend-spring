package io.storytailor.central.image.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import io.storytailor.central.image.vo.ImageInfoVO;

@Mapper
public interface ImageMapper {
    void insertImage(@Param("imageInfoVO") ImageInfoVO imageInfoVO);
}
