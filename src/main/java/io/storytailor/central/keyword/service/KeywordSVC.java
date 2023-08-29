package io.storytailor.central.keyword.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.storytailor.central.keyword.mapper.KeywordMapper;
import io.storytailor.central.keyword.vo.KeywordVO;

@Service
public class KeywordSVC {
    @Autowired
    private KeywordMapper keywordMapper;

    public KeywordVO getKeyword(Integer sessionId) {
        /* Request AI */
        return new KeywordVO();
    }

    public void createKeyword(KeywordVO keywordVO) {
        /* Request AI */
        /* Save Database */
        keywordMapper.insertKeyword(keywordVO);
    }
}
