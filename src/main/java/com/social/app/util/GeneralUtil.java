package com.social.app.util;

import com.social.app.exception.GeneralException;
import com.social.app.general.enums.ResponseCodeAndMessage;

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

}
