package io.storytailor.central.chat.mapper;

import io.storytailor.central.chat.vo.ChatVO;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface ChatMapper {
  void insertChat(@Param("chatVO") ChatVO chatVO);

  List<ChatVO> getChatList(@Param("sessionId") Integer sessionId);
}
