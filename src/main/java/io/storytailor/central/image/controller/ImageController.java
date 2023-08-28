package io.storytailor.central.image.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import io.storytailor.central.image.service.ImageSVC;

@Controller
public class ImageController {

    @Autowired
    private ImageSVC imageSVC;

    @PostMapping("/api/image/upload")
    public ResponseEntity<?> uploadImage(@RequestParam("originFile") MultipartFile originFile,
            @RequestParam("expandFile") MultipartFile expandFile, @RequestParam("sessionId") Integer sessionId) {
        return ResponseEntity.ok().body(imageSVC.uploadImage(originFile, expandFile, sessionId));
    }
}
