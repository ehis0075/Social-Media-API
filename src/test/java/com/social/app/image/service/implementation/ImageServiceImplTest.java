package com.social.app.image.service.implementation;

import com.social.app.image.service.ImageService;
import com.social.app.imageKit.ImageKitService;
import com.social.app.util.GeneralUtil;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ImageServiceImplTest {

    @InjectMocks
    private ImageService imageService;

    @Mock
    private ImageKitService imageKitService;

    @Test
    public void testUpload() {
        // Create sample input data
        String base64 = "sampleBase64Data";
        String fileName = "sampleFileName";

        // Create a Map with sample data
        Map<String, String> uploadResult = new HashMap<>();
        uploadResult.put("fileId", "sampleFileId");
        uploadResult.put("url", "sampleImageUrl");

        // Mock the behavior of GeneralUtil.stringIsNullOrEmpty()
        when(GeneralUtil.stringIsNullOrEmpty(base64)).thenReturn(false);

        // Mock the behavior of the imageKitService.upload() method
        when(imageKitService.upload(base64, fileName)).thenReturn(uploadResult);

        // Perform the upload operation
        Map<String, String> returnedValues = imageService.upload(base64, fileName);


        // Verify that the imageKitService.upload() method was called
        verify(imageKitService, times(1)).upload(base64, fileName);

        // Assert the returned Map
        assertNotNull(returnedValues);
        assertEquals("sampleFileId", returnedValues.get("fileId"));
        assertEquals("sampleImageUrl", returnedValues.get("url"));
    }

    @Test
    public void testDelete() {
        // Create sample input data
        String fileId = "sampleFileId";

        // Perform the delete operation
        imageService.delete(fileId);

        // Verify that the imageKitService.delete() method was called
        verify(imageKitService, times(1)).delete(fileId);
    }

    @Test
    public void testUploadImage() {
        // Create sample input data
        String base64 = "sampleBase64Data";
        String fileName = "sample FileName";

        // Create a Map with sample data
        Map<String, String> uploadResult = new HashMap<>();
        uploadResult.put("fileId", "sampleFileId");
        uploadResult.put("url", "sampleImageUrl");

        // Mock the behavior of GeneralUtil.stringIsNullOrEmpty()
        when(GeneralUtil.stringIsNullOrEmpty(base64)).thenReturn(false);

        // Mock the behavior of the imageKitService.upload() method
        when(imageKitService.upload(base64, fileName)).thenReturn(uploadResult);

        // Perform the uploadImage operation
        Map<String, String> returnedValues = imageService.uploadImage(base64, fileName);


        // Verify that the imageKitService.upload() method was called
        verify(imageKitService, times(1)).upload(base64, fileName);

        // Assert the returned Map
        assertNotNull(returnedValues);
        assertEquals("sampleFileId", returnedValues.get("fileId"));
        assertEquals("sampleImageUrl", returnedValues.get("url"));
    }
}