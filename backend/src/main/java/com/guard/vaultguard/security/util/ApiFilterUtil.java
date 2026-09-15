package com.guard.vaultguard.security.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class ApiFilterUtil {

    private static final int DEFAULT_API_KEY_BYTES = 32;

    public static String hashApiKey(String apiKey) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(apiKey.getBytes(StandardCharsets.UTF_8));

        return bytesToHex(hash); // returns the hashed API key as a hex string
    }

    private static String bytesToHex(byte[] hash) {
        StringBuilder hexString = new StringBuilder(2 * hash.length);
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }

    public static String generateApiKey(String prefix) {
        return generateApiKey(prefix, generateRawApiKey());
    }

    public static String generateApiKey(String prefix, String rawApiKey) {
        return prefix + rawApiKey;
    }

    public static String generateRawApiKey() {
        SecureRandom secRandom = new SecureRandom();
        byte[] randomBytes = new byte[DEFAULT_API_KEY_BYTES]; // 256 bits
        secRandom.nextBytes(randomBytes); // fill the byte array with random bytes
        return bytesToHex(randomBytes);
    }

    public static String stripPrefix(String fullApiKey, String prefix) {
        if (fullApiKey == null) {
            return null;
        }
        if (prefix == null || prefix.isEmpty()) {
            return fullApiKey;
        }
        return fullApiKey.startsWith(prefix)
                ? fullApiKey.substring(prefix.length())
                : fullApiKey;
    }

}
