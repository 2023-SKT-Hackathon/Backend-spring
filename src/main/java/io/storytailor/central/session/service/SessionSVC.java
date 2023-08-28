package io.storytailor.central.session.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.storytailor.central.session.mapper.SessionMapper;
import io.storytailor.central.session.vo.SessionVO;

@Service
public class SessionSVC {

    @Autowired
    private SessionMapper sessionMapper;

    public SessionVO getSession() {
        return sessionMapper.createSession(new SessionVO());
    }
}
