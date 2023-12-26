package io.storytailor.central.session.service;

import io.storytailor.central.session.mapper.SessionMapper;
import io.storytailor.central.session.vo.SessionVO;
import io.storytailor.central.user.vo.UserVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SessionSVC {

  @Autowired
  private SessionMapper sessionMapper;

  public SessionVO getSession(UserVO user) {
    SessionVO session = new SessionVO();
    sessionMapper.createSession(
      session,
      user.getAccount().getName(),
      user.getAccount().getEmail()
    );
    return session;
  }
}
