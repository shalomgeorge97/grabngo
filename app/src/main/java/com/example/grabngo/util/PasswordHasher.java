
package com.example.grabngo.util;

import android.os.Build;
import android.util.Base64;

import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.SecureRandom;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public final class PasswordHasher {

    private static final int SALT_LENGTH = 16;
    private static final int KEY_LENGTH = 256;

    private PasswordHasher() {
    }

    // Generate a salted password hash.
    public static String hash(String password) {

        if (password == null || password.isEmpty()) {
            throw new IllegalArgumentException("Password cannot be empty.");
        }

        try {
            boolean useSha256 = Build.VERSION.SDK_INT >= 26;

            String algorithm = useSha256
                    ? "PBKDF2WithHmacSHA256"
                    : "PBKDF2WithHmacSHA1";

            String algorithmLabel = useSha256 ? "SHA256" : "SHA1";

            int iterations = useSha256 ? 600000 : 1300000;

            byte[] salt = new byte[SALT_LENGTH];
            new SecureRandom().nextBytes(salt);

            byte[] passwordHash =
                    deriveKey(password, salt, iterations, algorithm);

            return "PBKDF2$"
                    + algorithmLabel + "$"
                    + iterations + "$"
                    + Base64.encodeToString(salt, Base64.NO_WRAP) + "$"
                    + Base64.encodeToString(passwordHash, Base64.NO_WRAP);

        } catch (GeneralSecurityException exception) {
            throw new IllegalStateException(
                    "Unable to hash password.", exception);
        }
    }

    // Verify a password against its saved hash.
    public static boolean verify(String password, String storedHash) {

        if (password == null || storedHash == null) {
            return false;
        }

        try {
            String[] parts = storedHash.split("\\$");

            if (parts.length != 5 || !parts[0].equals("PBKDF2")) {
                return false;
            }

            String algorithm;

            if (parts[1].equals("SHA256")) {
                algorithm = "PBKDF2WithHmacSHA256";
            } else if (parts[1].equals("SHA1")) {
                algorithm = "PBKDF2WithHmacSHA1";
            } else {
                return false;
            }

            int iterations = Integer.parseInt(parts[2]);

            if (iterations < 10000 || iterations > 2000000) {
                return false;
            }

            byte[] salt = Base64.decode(parts[3], Base64.NO_WRAP);
            byte[] expectedHash = Base64.decode(parts[4], Base64.NO_WRAP);

            byte[] actualHash =
                    deriveKey(password, salt, iterations, algorithm);

            return MessageDigest.isEqual(expectedHash, actualHash);

        } catch (Exception exception) {
            return false;
        }
    }

    public static boolean isHashed(String storedPassword) {
        return storedPassword != null
                && storedPassword.startsWith("PBKDF2$");
    }

    private static byte[] deriveKey(
            String password,
            byte[] salt,
            int iterations,
            String algorithm
    ) throws GeneralSecurityException {

        PBEKeySpec spec = new PBEKeySpec(
                password.toCharArray(),
                salt,
                iterations,
                KEY_LENGTH
        );

        try {
            SecretKeyFactory factory =
                    SecretKeyFactory.getInstance(algorithm);

            return factory.generateSecret(spec).getEncoded();

        } finally {
            spec.clearPassword();
        }
    }
}