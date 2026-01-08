package inventory.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;

public class HashingPassword {
    static final String SALT = "h51Yf4gGnPsk3qI4reNqA3MAyhspaDmr";
    public static String encrypt(String passwordToHash) {
        String result = null;
        byte[] salt = SALT.getBytes();
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-512");
            md.update(salt);
            byte[] hashedPassword = md.digest(passwordToHash.getBytes(StandardCharsets.UTF_8));
            result =  Base64.getEncoder().encodeToString(hashedPassword).substring(0, 32);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return result;
    }
}
