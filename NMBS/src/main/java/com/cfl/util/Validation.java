package com.cfl.util;

import java.util.regex.Pattern;

/**
 * Created by shig on 2015/12/17.
 */
public class Validation {

    /**
     * Check the pattern of the email address.
     */
    private final static Pattern EMAIL_ADDRESS_PATTERN = Pattern
            .compile("[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" + "\\@"
                    + "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" + "(" + "\\."
                    + "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" + ")+");

    public static boolean validateEmail(String email){
        return EMAIL_ADDRESS_PATTERN.matcher(email).matches();
    }
}
