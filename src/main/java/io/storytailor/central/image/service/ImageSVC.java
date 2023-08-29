package io.storytailor.central.image.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import io.storytailor.central.code.ImageCode;
import io.storytailor.central.image.mapper.ImageMapper;
import io.storytailor.central.image.vo.ImageInfoVO;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ImageSVC {

    @Value("${image.base-url}")
    private String baseUrl;

    @Value("${image.upload-path}")
    private String uploadPath;

    @Autowired
    private ImageMapper imageMapper;
    
    public ImageInfoVO uploadImage(MultipartFile originFile,
            MultipartFile expandFile, Integer sessionId) {
        ImageInfoVO imageInfoVO = new ImageInfoVO();
        Boolean resDict = createDict(sessionId);
        if (!resDict) {
            return null;
        } else {
            imageInfoVO.setSessionId(sessionId);
        }
        String resOriginPath = saveDiskImage(sessionId, ImageCode.ORIGIN, originFile);
        if (resOriginPath == null) {
            return null;
        } else {
            imageInfoVO.setOriginFilePath(baseUrl + resOriginPath);
        }
        String resExpandPath = saveDiskImage(sessionId, ImageCode.EXPAND, expandFile);
        if (resExpandPath == null) {
            return null;
        } else {
            imageInfoVO.setExpandFilePath(baseUrl + resExpandPath);
        }

        imageMapper.insertImage(imageInfoVO);
        return imageInfoVO;
    }

    public String saveDiskImage(Integer sessionId, ImageCode imgType, MultipartFile file){
        Path copyOfLocation = Paths.get(uploadPath + File.separator + sessionId.toString() + File.separator + imgType.getCode() + File.separator + StringUtils.cleanPath(file.getOriginalFilename()));
        try {
            Files.copy(file.getInputStream(), copyOfLocation, StandardCopyOption.REPLACE_EXISTING);
            return sessionId.toString() + File.separator + imgType.getCode() + File.separator + StringUtils.cleanPath(file.getOriginalFilename());
        } catch (IOException e) {
            log.error("Fail to save image : {}", StringUtils.cleanPath(file.getOriginalFilename()), e);
            return null;
        }
    }

    public ImageInfoVO getImageInfoBySessionId(Integer sessionId) {
        return imageMapper.selectImageInfo(sessionId);
    }

    private Boolean createDict(Integer sessionId) {
        try {
            String path = uploadPath + File.separator + sessionId.toString();
            Files.createDirectories(Paths.get(path));
            Files.createDirectories(Paths.get(path+"/origin"));
            Files.createDirectories(Paths.get(path+"/expand"));
            Files.createDirectories(Paths.get(path+"/ai"));
            return true;
        } catch (Exception e) {
            log.error("Fail to Create Dict : {}",uploadPath, e);
            return false;
        }
    }
}
