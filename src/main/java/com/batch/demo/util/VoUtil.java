package com.batch.demo.util;

import java.util.regex.Pattern;

public class VoUtil {

    private final static Pattern BCRYPT_PATTERN = Pattern.compile("\\A\\$2(a|y|b)?\\$(\\d\\d)\\$[./0-9A-Za-z]{53}");

    public static boolean isEncoded(String password){
        return BCRYPT_PATTERN.matcher(password).matches();
    }
}
