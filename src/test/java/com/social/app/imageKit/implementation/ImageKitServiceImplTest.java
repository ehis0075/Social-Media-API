package com.social.app.imageKit.implementation;

import com.social.app.image.service.ImageService;
import io.imagekit.sdk.ImageKit;
import io.imagekit.sdk.exceptions.*;
import io.imagekit.sdk.models.FileCreateRequest;
import io.imagekit.sdk.models.results.Result;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@SpringBootTest
class ImageKitServiceImplTest {

    @InjectMocks
    private ImageService imageService;

    @Mock
    private ImageKit imageKit;

    @Test
    public void testUpload() throws ForbiddenException, TooManyRequestsException, InternalServerException, UnauthorizedException, BadRequestException, UnknownException {
        // Create sample input data
        String base64 = "sampleBase64Data";
        String fileName = "sampleFileName";

        // Create a FileCreateRequest with sample data
        FileCreateRequest fileCreateRequest = new FileCreateRequest(base64, fileName);
        fileCreateRequest.setUseUniqueFileName(false);
        fileCreateRequest.setFolder("SOCIAL");

        // Create a Result with sample data
        String fileId = "sampleFileId";
        String url = "sampleImageUrl";
        Result result = new Result();
        result.setFileId(fileId);
        result.setUrl(url);

        // Mock the behavior of the imageKit.upload() method
        when(imageKit.upload(fileCreateRequest)).thenReturn(result);

        // Perform the upload operation
        Map<String, String> returnedValues = imageService.upload(base64, fileName);

        // Verify that the imageKit.upload() method was called
        verify(imageKit, times(1)).upload(fileCreateRequest);

        // Assert the returned Map
        assertNotNull(returnedValues);
        assertEquals(fileId, returnedValues.get("fileId"));
        assertEquals(url, returnedValues.get("url"));
    }

    @Test
    public void testDelete() throws ForbiddenException, TooManyRequestsException, InternalServerException, UnauthorizedException, BadRequestException, UnknownException {
        // Create sample input data
        String fileId = "sampleFileId";

        // Perform the delete operation
        imageService.delete(fileId);

        // Verify that the imageKit.deleteFile() method was called
        verify(imageKit, times(1)).deleteFile(fileId);
    }
}