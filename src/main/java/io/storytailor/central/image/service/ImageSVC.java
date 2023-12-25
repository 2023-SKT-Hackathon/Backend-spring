package io.storytailor.central.image.service;

import com.theokanning.openai.image.CreateImageEditRequest;
import com.theokanning.openai.image.CreateImageRequest;
import com.theokanning.openai.image.ImageResult;
import com.theokanning.openai.service.OpenAiService;
import io.storytailor.central.code.FileCode;
import io.storytailor.central.config.rest.RestService;
import io.storytailor.central.image.mapper.ImageMapper;
import io.storytailor.central.image.vo.ImageAiResponseVO;
import io.storytailor.central.image.vo.ImageInfoVO;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
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
  private ImageMapper imageMapper;

  @Autowired
  private RestService restService; 

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
    /* Convert Image Extension */
    if (originFile.getOriginalFilename().endsWith(".jpg")) {
      resOriginPath = convertImgJpgToPng(sessionId, originFile);
    }
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

  public String createAndSaveAiImage(
    Integer sessionId,
    String prompt,
    Integer pageIdx
  ) {
    try {
      OpenAiService openAiService = new OpenAiService(
        openaiApiKey,
        Duration.ofSeconds(55)
      );
      if (pageIdx == 0) {
        String expandImg =
          uploadPath +
          File.separator +
          sessionId.toString() +
          File.separator +
          "expand" +
          File.separator +
          "origin_expand.png";
        CreateImageEditRequest imageEditReq = new CreateImageEditRequest(
          prompt,
          1,
          "1024x1024",
          "url",
          null
        );
        ImageResult editRes = openAiService.createImageEdit(
          imageEditReq,
          expandImg,
          expandImg
        );
        String editImg = editRes.getData().get(0).getUrl();
        return downloadImageFromUrl(
          sessionId,
          FileCode.AI,
          editImg,
          pageIdx.toString()
        );
      } else {
        // CreateImageRequest imageReq = new CreateImageRequest(
        //   prompt,
        //   1,
        //   "1024x1024",
        //   null,
        //   null
        // );
        // ImageResult genRes = openAiService.createImage(imageReq);
        // String genImg = genRes.getData().get(0).getUrl();
        String genImg = createAiImageByDalle3(prompt, 1, "1024x1024", "dall-e-3", "standard");
        return downloadImageFromUrl(
          sessionId,
          FileCode.AI,
          genImg,
          pageIdx.toString()
        );
      }
    } catch (Exception e) {
      log.error("Fail to Generate Image", e);
      return null;
    }
  }
  private String createAiImageByDalle3(String prompt, Integer n, String size, String model, String quality){
    String url = "";
    Map<String, Object> params = new HashMap<>();
    params.put("prompt", prompt);
    params.put("n", n);
    params.put("size", size);
    params.put("model", model);
    params.put("quality", quality);
    Map<String, String> headers = new HashMap<>();
    headers.put("Content-Type", "application/json");

    ImageAiResponseVO res = restService.post("https://api.openai.com/v1/images/generations", null, headers, params, null, openaiApiKey, ImageAiResponseVO.class).getBody();
    url = res.getData().get(0).get("url");
    return url;

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

  public String convertImgJpgToPng(
    Integer sessionId,
    MultipartFile originFile
  ) {
    try {
      String basePath =
        uploadPath +
        File.separator +
        sessionId.toString() +
        File.separator +
        "origin" +
        File.separator;
      FileInputStream inputStream = new FileInputStream(
        basePath + StringUtils.cleanPath(originFile.getOriginalFilename())
      );
      FileOutputStream outputStream = new FileOutputStream(
        basePath + "origin.png"
      );
      // reads input image from file
      BufferedImage inputImage = ImageIO.read(inputStream);

      // writes to the output image in specified format
      ImageIO.write(inputImage, "png", outputStream);

      // needs to close the streams
      outputStream.close();
      inputStream.close();
      return (
        sessionId.toString() +
        File.separator +
        "origin" +
        File.separator +
        "origin.png"
      );
    } catch (IOException e) {
      e.printStackTrace();
      return null;
    }
  }
}
