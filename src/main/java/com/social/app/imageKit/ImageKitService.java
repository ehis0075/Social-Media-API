package com.social.app.imageKit;

import java.util.Map;

public interface ImageKitService {

    Map<String, String> upload(String base64, String fileName);

    boolean moveFileFromTemp(String fileName);

    void delete(String fileId);
}
