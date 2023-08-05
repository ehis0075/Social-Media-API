package com.social.app.util;

import com.social.app.exception.GeneralException;
import com.social.app.general.enums.ResponseCodeAndMessage;
import com.social.app.image.util.BASE64DecodedMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.util.Base64;
import java.util.Date;

public class GeneralUtil {

    public static void validateNameAndPhoneNumber(String firstName, String lastName, String phoneNumber) {
        // check that first name is not null or empty
        if (GeneralUtil.stringIsNullOrEmpty(firstName)) {
            throw new GeneralException(ResponseCodeAndMessage.INCOMPLETE_PARAMETERS_91.responseCode, "First name cannot be null or empty!");
        }

        // check that last name is not null or empty
        if (GeneralUtil.stringIsNullOrEmpty(lastName)) {
            throw new GeneralException(ResponseCodeAndMessage.INCOMPLETE_PARAMETERS_91.responseCode, "Last name cannot be null or empty!");
        }

        // check that phone number is not null or empty
        if (GeneralUtil.stringIsNullOrEmpty(phoneNumber)) {
            throw new GeneralException(ResponseCodeAndMessage.INCOMPLETE_PARAMETERS_91.responseCode, "Phone number cannot be null or empty!");
        }
    }

    public static boolean stringIsNullOrEmpty(String arg) {
        if ((arg == null)) return true;
        else
            return ("".equals(arg)) || (arg.trim().length() == 0);
    }

    public static String getDateAsString(Date transactionDate) {
        return DateUtil.dateToString(transactionDate, "yyyy-MM-dd HH:mm:ss");
    }

    public static MultipartFile getFile(String base64) {
        try {
            String[] baseStrs = base64.split(",");

            Base64.getDecoder().decode(baseStrs[1]);
//            BASE64Decoder decoder = new BASE64Decoder();
            byte[] b = new byte[0];
//            b = decoder.decodeBuffer(baseStrs[1]);

            b = Base64.getDecoder().decode(baseStrs[1]);


            for (int i = 0; i < b.length; ++i) {
                if (b[i] < 0) {
                    b[i] += 256;
                }
            }
            return new BASE64DecodedMultipartFile(b, baseStrs[0]);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
