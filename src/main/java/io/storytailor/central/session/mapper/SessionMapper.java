package io.storytailor.central.session.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import io.storytailor.central.session.vo.SessionVO;

@Mapper
public interface SessionMapper {
    SessionVO createSession(@Param("SessionVO") SessionVO session);
}
