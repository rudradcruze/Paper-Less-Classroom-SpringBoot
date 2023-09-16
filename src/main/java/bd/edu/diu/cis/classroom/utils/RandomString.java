package bd.edu.diu.cis.classroom.utils;

import java.util.Date;
import java.util.UUID;

public class RandomString {
    public static String getRandomId() {
        UUID randomUUID = UUID.randomUUID();
        return randomUUID.toString().replaceAll("_", "").replaceAll("-", "").replaceAll(":", "");
    }

    public static String getAlphaNumericString(int n) {
        String AlphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
                + (new Date().getTime())/1000
                + "abcdefghijklmnopqrstuvxyz"
                + "\"!\"#$%&'()*+,-./:;<=>?@[\\]^_`{|}~\"";

        StringBuilder sb = new StringBuilder(n);

        for (int i = 0; i < n; i++) {
            int index
                    = (int)(AlphaNumericString.length()
                    * Math.random());
            sb.append(AlphaNumericString
                    .charAt(index));
        }
        return sb.toString();
    }
}