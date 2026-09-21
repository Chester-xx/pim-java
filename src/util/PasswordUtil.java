package util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class PasswordUtil {

    // Hash password SHA-256 and return
    public static String hash(String plainPassword) {
        
        try {

            // Create SHA-256 digest and hash password
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(plainPassword.getBytes());

            // Convert byte array to hex string
            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) {

                // Convert each byte to hex and append to string
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            
            }

            // Return hash
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) { throw new RuntimeException("SHA-256 algorithm not available", e); }
    
    }

}
