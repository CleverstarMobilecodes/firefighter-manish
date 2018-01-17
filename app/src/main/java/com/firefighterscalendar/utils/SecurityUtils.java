package com.firefighterscalendar.utils;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Random;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;


public class SecurityUtils {

    public static String PRIVATE_KEY = "fireofrnxhr892305hje93nf834m63lr93l5u3ntrhj4k";
    public static String SECRETE_KEY = "f!reF!ghter@chiragPatel";


    public static String getCurrentTimeStamp() {
        return "" + new Date().getTime();
    }

    public static String generateNonce() {
        String nonce = "";

        Random rnd = new Random();
        int numLetters = 5;

        String randomLetters = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";

        for (int n = 0; n < numLetters; n++)
            nonce = nonce + randomLetters.charAt(rnd.nextInt(randomLetters.length()));

        return nonce;
    }

    public static String GetTokenForURl(String Data) {

//        Log.i("TAG Data : ", Data);

        try {
            Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
            SecretKeySpec secret_key = new SecretKeySpec(PRIVATE_KEY.getBytes(), "HmacSHA256");
            sha256_HMAC.init(secret_key);

            byte byteData[] = sha256_HMAC.doFinal(Data.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte aByteData : byteData) {
                sb.append(Integer.toString((aByteData & 0xff) + 0x100, 16).substring(1));
            }
//            System.out.println("Hex format : " + sb.toString());
            return sb.toString();

        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }


    public static HashMap<String, String> setSecureParams(HashMap<String, String> params) {

        Calendar calendargmt = Calendar.getInstance();
        TimeZone mTimeZone = calendargmt.getTimeZone();
        int mGMTOffset = mTimeZone.getOffset(calendargmt.getTimeInMillis());
        TimeZone tz = TimeZone.getDefault();

        String nonce = generateNonce();
        String timeStamp = getCurrentTimeStamp();
        String hash = GetTokenForURl("nonce=" + nonce + "&timestamp=" + timeStamp + "|" + SECRETE_KEY);

        params.put("nonce", nonce);
        params.put("timestamp", timeStamp);
        params.put("token", hash);
        params.put("iTimezoneOffset", "" + TimeUnit.MINUTES.convert(mGMTOffset, TimeUnit.MILLISECONDS));
        params.put("vTimezoneOffsetString", tz.getDisplayName(false, TimeZone.SHORT));

//        Log.e("TAG", "nonce:" + nonce);
//        Log.e("TAG", "timeStamp:" + timeStamp);
//        Log.e("TAG", "HASH:" + hash);

        /*params.put(NONCE, "456852");
        params.put(TIMESTAMP, "1460696338");
        params.put(TOKEN,"4b8eb9daa1d7af26114eec90500dbd422a4f9fdd154114aa5c1fc8f3e182c8fb");*/
        return params;

    }
}