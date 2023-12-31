package io.storytailor.central.image.controller;

import io.storytailor.central.image.service.ImageSVC;
import io.storytailor.central.image.vo.ImageInfoVO;
import io.storytailor.central.user.service.LoginSVC;
import io.storytailor.central.user.vo.UserVO;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class ImageController {

  @Autowired
  private ImageSVC imageSVC;

  @Autowired
  private LoginSVC loginSVC;

  @PostMapping("/api/image/upload")
  public ResponseEntity<?> uploadImage(
    HttpServletRequest request,
    @RequestParam("originFile") MultipartFile originFile,
    @RequestParam("expandFile") MultipartFile expandFile,
    @RequestParam("sessionId") Integer sessionId
  ) {
    UserVO user = loginSVC.getUserVO(loginSVC.resolveToken(request));
    ImageInfoVO res = imageSVC.uploadImage(originFile, expandFile, sessionId);
    if (res == null) {
      return ResponseEntity.badRequest().body("Fail to upload image");
    } else {
      return ResponseEntity.ok().body(res);
    }
  }
}
