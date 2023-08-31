package io.storytailor.central.image.service;

import com.theokanning.openai.service.OpenAiService;
import io.storytailor.central.chat.vo.WhisperResponseVO;
import io.storytailor.central.code.FileCode;
import io.storytailor.central.config.rest.RestService;
import io.storytailor.central.image.mapper.ImageMapper;
import io.storytailor.central.image.vo.ImageAiResponseVO;
import io.storytailor.central.image.vo.ImageInfoVO;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
public class ImageSVC {

  @Value("${image.base-url}")
  private String baseUrl;

  @Value("${image.upload-path}")
  private String uploadPath;

  @Value("${openai-service.api-key}")
  private String openaiApiKey;

  @Value("${openai-service.api-url}")
  private String defaultOpenaiApiUrl;

  @Value("${openai-service.img.edit-path}")
  private String editPath;

  @Value("${openai-service.img.generate-path}")
  private String generatePath;

  @Autowired
  private RestService restService;

  @Autowired
  private ImageMapper imageMapper;

  public ImageInfoVO uploadImage(
    MultipartFile originFile,
    MultipartFile expandFile,
    Integer sessionId
  ) {
    ImageInfoVO imageInfoVO = new ImageInfoVO();
    Boolean resDict = createDict(sessionId);
    if (!resDict) {
      return null;
    } else {
      imageInfoVO.setSessionId(sessionId);
    }
    String resOriginPath = saveDiskFile(sessionId, FileCode.ORIGIN, originFile);
    if (resOriginPath == null) {
      return null;
    } else {
      imageInfoVO.setOriginFilePath(baseUrl + resOriginPath);
    }
    String resExpandPath = saveDiskFile(sessionId, FileCode.EXPAND, expandFile);
    if (resExpandPath == null) {
      return null;
    } else {
      imageInfoVO.setExpandFilePath(baseUrl + resExpandPath);
    }
    imageMapper.insertImage(imageInfoVO);
    return imageInfoVO;
  }

  public String saveDiskFile(
    Integer sessionId,
    FileCode fileType,
    MultipartFile file
  ) {
    Path copyOfLocation = Paths.get(
      uploadPath +
      File.separator +
      sessionId.toString() +
      File.separator +
      fileType.getCode() +
      File.separator +
      StringUtils.cleanPath(file.getOriginalFilename())
    );
    try {
      Files.copy(
        file.getInputStream(),
        copyOfLocation,
        StandardCopyOption.REPLACE_EXISTING
      );
      return (
        sessionId.toString() +
        File.separator +
        fileType.getCode() +
        File.separator +
        StringUtils.cleanPath(file.getOriginalFilename())
      );
    } catch (IOException e) {
      log.error(
        "Fail to save file : {}",
        StringUtils.cleanPath(file.getOriginalFilename()),
        e
      );
      return null;
    }
  }

  public ImageInfoVO getImageInfoBySessionId(Integer sessionId) {
    return imageMapper.selectImageInfo(sessionId);
  }

  public String createAiImage(
    Integer sessionId,
    File image,
    File mask,
    String prompt,
    String fileName
  ) {
    try {
      OpenAiService openAiService = new OpenAiService(openaiApiKey);

      String originImg =
        uploadPath +
        File.separator +
        sessionId.toString() +
        File.separator +
        "origin" +
        File.separator;
      File importedOriginImg = new File(originImg);
      openAiService.createImage(null);
      openAiService.createImageEdit(null, image, mask);
      Map<String, String> header = new HashMap<String, String>();
      header.put("Content-Type", "multipart/form-data");
      String url = defaultOpenaiApiUrl;
      if (image == null && mask == null) {
        url = url + generatePath;
      } else {
        url = url + editPath;
      }
      MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
      body.add("prompt", prompt);
      body.add("size", "1024x1024");
      if (image != null) {
        body.add("image", image);
      }
      if (mask != null) {
        body.add("mask", mask);
      }

      ResponseEntity<ImageAiResponseVO> res = restService.post(
        url,
        null,
        header,
        body,
        null,
        openaiApiKey,
        ImageAiResponseVO.class
      );
      /* Get Image Url */
      String imageUrl = res.getBody().getData().get(0).get("url");
      return imageUrl;
    } catch (Exception e) {
      log.error("Fail to Generate Image", e);
      return null;
    }
  }

  public String downloadImageFromUrl(
    Integer sessionId,
    FileCode imgType,
    String url,
    String fileName
  ) {
    try {
      URL urlEntity = new URL(url);
      InputStream in = new BufferedInputStream(urlEntity.openStream());
      ByteArrayOutputStream out = new ByteArrayOutputStream();
      byte[] buf = new byte[102400000];
      int n = 0;
      while (-1 != (n = in.read(buf))) {
        out.write(buf, 0, n);
      }
      out.close();
      in.close();
      byte[] response = out.toByteArray();
      /* save image file */
      FileOutputStream fos = new FileOutputStream(
        uploadPath +
        File.separator +
        sessionId.toString() +
        File.separator +
        imgType.getCode() +
        File.separator +
        fileName +
        ".png"
      );
      fos.write(response);
      fos.close();
      return (
        sessionId.toString() +
        File.separator +
        imgType.getCode() +
        File.separator +
        fileName +
        ".png"
      );
    } catch (Exception e) {
      log.error("Fali to Download Image From : {}", url, e);
      return null;
    }
  }

  private Boolean createDict(Integer sessionId) {
    try {
      String path = uploadPath + File.separator + sessionId.toString();
      Files.createDirectories(Paths.get(path));
      Files.createDirectories(Paths.get(path + "/origin"));
      Files.createDirectories(Paths.get(path + "/expand"));
      Files.createDirectories(Paths.get(path + "/ai"));
      Files.createDirectories(Paths.get(path + "/audio"));
      return true;
    } catch (Exception e) {
      log.error("Fail to Create Dict : {}", uploadPath, e);
      return false;
    }
  }

  private void convertImgJpgToPng() {}
}
