package com.okanetransfer.service;

import org.springframework.stereotype.Service;
import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * Service de chiffrement AES-256-GCM pour les données sensibles (pièces d'identité).
 * Utilise AES/GCM/NoPadding pour l'authenticité + confidentialité.
 * Format stocké : Base64(IV[12 bytes] + ciphertext + tag[16 bytes])
 */
@Service
public class EncryptionService {

    // Clé AES-256 (32 bytes) — à externaliser dans une variable d'environnement en production
    private static final String SECRET_KEY = System.getenv("AES_SECRET_KEY") != null
            ? System.getenv("AES_SECRET_KEY")
            : "OkaneTransfer2026SecretKey123456"; // 32 chars

    private static final String ALGORITHM = "AES/GCM/NoPadding";
    private static final int GCM_IV_LENGTH = 12;
    private static final int GCM_TAG_LENGTH = 128;

    public String encrypt(String plaintext) {
        try {
            byte[] iv = new byte[GCM_IV_LENGTH];
            new SecureRandom().nextBytes(iv);

            Cipher cipher = Cipher.getInstance(ALGORITHM);
            SecretKeySpec keySpec = new SecretKeySpec(SECRET_KEY.getBytes(), "AES");
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, new GCMParameterSpec(GCM_TAG_LENGTH, iv));

            byte[] ciphertext = cipher.doFinal(plaintext.getBytes());

            // Concaténer IV + ciphertext puis encoder en Base64
            byte[] combined = new byte[iv.length + ciphertext.length];
            System.arraycopy(iv, 0, combined, 0, iv.length);
            System.arraycopy(ciphertext, 0, combined, iv.length, ciphertext.length);

            return Base64.getEncoder().encodeToString(combined);
        } catch (Exception e) {
            throw new RuntimeException("Erreur de chiffrement", e);
        }
    }

    public String decrypt(String encrypted) {
        try {
            byte[] combined = Base64.getDecoder().decode(encrypted);

            byte[] iv = new byte[GCM_IV_LENGTH];
            byte[] ciphertext = new byte[combined.length - GCM_IV_LENGTH];
            System.arraycopy(combined, 0, iv, 0, iv.length);
            System.arraycopy(combined, iv.length, ciphertext, 0, ciphertext.length);

            Cipher cipher = Cipher.getInstance(ALGORITHM);
            SecretKeySpec keySpec = new SecretKeySpec(SECRET_KEY.getBytes(), "AES");
            cipher.init(Cipher.DECRYPT_MODE, keySpec, new GCMParameterSpec(GCM_TAG_LENGTH, iv));

            return new String(cipher.doFinal(ciphertext));
        } catch (Exception e) {
            throw new RuntimeException("Erreur de déchiffrement", e);
        }
    }
}
