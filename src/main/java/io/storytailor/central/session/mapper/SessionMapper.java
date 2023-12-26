package io.storytailor.central.session.mapper;

import io.storytailor.central.session.vo.SessionVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface SessionMapper {
  void createSession(
    @Param("SessionVO") SessionVO session,
    @Param("name") String user,
    @Param("email") String email
  );
}
