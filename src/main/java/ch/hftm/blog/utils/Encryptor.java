package ch.hftm.blog.utils;


import ch.hftm.blog.config.SecretConfig;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

public class Encryptor {

    private static final String ALGORITHM = "AES";

    private final SecretConfig secretConfig;
    private final byte[] secret;

    public Encryptor(SecretConfig secretConfig) {
        this.secretConfig = secretConfig;
        this.secret = secretConfig.getEncryptionKey().getBytes();
    }

    public String encrypt(String string) throws Exception {
        Key key = new SecretKeySpec(secret, ALGORITHM);
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, key);

        byte[] encryptedEmailBytes = cipher.doFinal(string.getBytes());

        return Base64.getEncoder().encodeToString(encryptedEmailBytes);
    }

    public String decrypt(String encryptedString) throws Exception {
        Key key = new SecretKeySpec(secret, ALGORITHM);
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, key);

        byte[] decryptedEmailBytes = cipher.doFinal(Base64.getDecoder().decode(encryptedString));

        return new String(decryptedEmailBytes);
    }

    public String hashPassword(String password) throws NoSuchAlgorithmException {
        String saltedPassword = secretConfig.getSalt() + password;
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(saltedPassword.getBytes(StandardCharsets.UTF_8));
        return new String(hash, StandardCharsets.UTF_8);
    }

    private static final SecureRandom secureRandom = new SecureRandom();
    private static final Base64.Encoder base64Encoder = Base64.getUrlEncoder();

    public static String generateAESKey() {
        return generateRandomString(32);
    }

    public static String generateRandomString(int length) {
        byte[] randomBytes = new byte[length];
        secureRandom.nextBytes(randomBytes);
        return base64Encoder.encodeToString(randomBytes).substring(0, length);
    }

    public static String generateRandomCode(int length) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append(secureRandom.nextInt(10));
        }
        return sb.toString();
    }
}

