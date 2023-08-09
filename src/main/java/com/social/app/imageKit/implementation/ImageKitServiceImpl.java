package com.social.app.imageKit.implementation;

import com.social.app.imageKit.ImageKitService;
import io.imagekit.sdk.ImageKit;
import io.imagekit.sdk.models.FileCreateRequest;
import io.imagekit.sdk.models.MoveFileRequest;
import io.imagekit.sdk.models.results.Result;
import io.imagekit.sdk.models.results.ResultNoContent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;


@Slf4j
@Service
@RequiredArgsConstructor
public class ImageKitServiceImpl implements ImageKitService {

    @Value("${imageKit.urlEndPoint}")
    private String defaultUrl;

    @Value("${imagekit.merchantPath}")
    private String TEMP;

    private final ImageKit imageKit;

    @Override
    public Map<String, String> upload(String base64, String fileName) {

        log.info("Request to upload base64 image with fileName => {} to imageKit", fileName);

        try {
            FileCreateRequest fileCreateRequest = new FileCreateRequest(base64, fileName);
            fileCreateRequest.setUseUniqueFileName(false);
            fileCreateRequest.setFolder(TEMP);
            Result result = imageKit.upload(fileCreateRequest);

            String fileId = result.getFileId();
            String url = result.getUrl();
            log.info("Successfully uploaded the file => {}, url => {} and file ID => {}", fileName, url, fileId);

            Map<String, String> returnedValues = new HashMap<>();
            returnedValues.put("fileId", fileId);
            returnedValues.put("url", url);
            log.info("fileId=>{} and url=>{}", fileId, url);

            return returnedValues;
        } catch (Exception ex) {
            log.error("Failed to upload to imageKit with image file => " + fileName);
            log.error(ex.getMessage());
            return null;
        }

    }

    @Override
    public boolean moveFileFromTemp(String url) {
        String fileName = url.replace(defaultUrl, "");

        log.info("Moving file => {} to {}", fileName, TEMP);
        try {
            MoveFileRequest moveFileRequest = new MoveFileRequest();
            moveFileRequest.setSourceFilePath(fileName);
            moveFileRequest.setDestinationPath(TEMP);
            ResultNoContent resultNoContent = imageKit.moveFile(moveFileRequest);
            log.info("Response {}", resultNoContent);

            return true;
        } catch (Exception ex) {
            log.error("Failed to move image file {} to location => {}", fileName, TEMP);
            log.error(ex.getMessage());
        }

        return false;
    }

    @Override
    public void delete(String fileId) {
        log.info("Deleting image with file Id => {}", fileId);
        try {
            imageKit.deleteFile(fileId);
            log.info("======Successfully deleted image with fileID=>{} =======", fileId);
        } catch (Exception ex) {
            log.error("Failed to delete image with file ID => {}", fileId);
            log.error(ex.getMessage());
        }
    }
}
