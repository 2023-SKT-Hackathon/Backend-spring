package io.storytailor.central.user.mapper;

import io.storytailor.central.user.vo.AccountVO;
import io.storytailor.central.user.vo.UserVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserMapper {
  AccountVO selectUser(@Param("userVO") UserVO userVO);

  void insertUser(@Param("userVO") UserVO userVO);

  void updateToken(@Param("userVO") UserVO userVO);
}
