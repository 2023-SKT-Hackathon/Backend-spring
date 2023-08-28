package io.storytailor.central.image.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ImageSVC {
    public Boolean uploadImage(MultipartFile originFile,
            MultipartFile expandFile, Integer sessionId) {

        return true;
    }
}
