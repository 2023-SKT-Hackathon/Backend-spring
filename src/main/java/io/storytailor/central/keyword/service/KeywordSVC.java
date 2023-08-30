package io.storytailor.central.keyword.service;

import io.storytailor.central.keyword.mapper.KeywordMapper;
import io.storytailor.central.keyword.vo.KeywordResponseVO;
import io.storytailor.central.keyword.vo.KeywordVO;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class KeywordSVC {

  @Autowired
  private KeywordMapper keywordMapper;

  public KeywordResponseVO getTempKeyword(Integer sessionId) {
    List<KeywordVO> res = keywordMapper.selectTempKeyword(sessionId);
    KeywordResponseVO keywordResponseVO = new KeywordResponseVO();
    keywordResponseVO.setSessionId(sessionId);
    for (KeywordVO keywordVO : res) {
      if (keywordVO.getType().equals("extract")) {
        keywordResponseVO.getExtractKeyword().add(keywordVO.getKeyword());
      } else {
        keywordResponseVO.getRecommandKeyword().add(keywordVO.getKeyword());
      }
    }
    return keywordResponseVO;
  }

  public KeywordResponseVO getKeyword(Integer sessionId) {
    List<KeywordVO> res = keywordMapper.selectKeyword(sessionId);
    KeywordResponseVO keywordResponseVO = new KeywordResponseVO();
    keywordResponseVO.setSessionId(sessionId);
    for (KeywordVO keywordVO : res) {
      if (keywordVO.getType().equals("extract")) {
        keywordResponseVO.getExtractKeyword().add(keywordVO.getKeyword());
      } else {
        keywordResponseVO.getRecommandKeyword().add(keywordVO.getKeyword());
      }
    }
    return keywordResponseVO;
  }

  public KeywordVO createTempKeyword(KeywordVO keywordVO) {
    /* Save Database */
    keywordMapper.insertTempKeyword(keywordVO);
    return keywordVO;
  }

  public KeywordResponseVO createKeyword(KeywordResponseVO keywordResponseVO) {
    for (String keyword : keywordResponseVO.getExtractKeyword()) {
      KeywordVO keywordVO = new KeywordVO();
      keywordVO.setSessionId(keywordResponseVO.getSessionId());
      keywordVO.setKeyword(keyword);
      keywordVO.setType("extract");
      /* Save Database */
      keywordMapper.insertKeyword(keywordVO);
    }
    for (String keyword : keywordResponseVO.getRecommandKeyword()) {
      KeywordVO keywordVO = new KeywordVO();
      keywordVO.setSessionId(keywordResponseVO.getSessionId());
      keywordVO.setKeyword(keyword);
      keywordVO.setType("recommand");
      /* Save Database */
      keywordMapper.insertKeyword(keywordVO);
    }
    return keywordResponseVO;
  }
}
