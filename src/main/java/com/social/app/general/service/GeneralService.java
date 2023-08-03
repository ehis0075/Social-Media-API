package com.social.app.general.service;


import com.social.app.general.dto.Response;
import com.social.app.general.enums.ResponseCodeAndMessage;
import org.springframework.data.domain.Pageable;

public interface GeneralService {

    boolean isStringInvalid(String string);

    //used to format response body
    Response prepareResponse(ResponseCodeAndMessage codeAndMessage, Object data);

    Response prepareResponse(String responseCode, String responseMessage, Object data);


    Pageable getPageableObject(int size, int page);
}
