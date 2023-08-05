package com.social.app.image.service;

import java.util.Map;

public interface ImageService {
    Map<String, String> upload(String base64, String fileName);

    void delete(String fileID);

    Map<String, String> uploadImage(String base64, String fileName);
}
