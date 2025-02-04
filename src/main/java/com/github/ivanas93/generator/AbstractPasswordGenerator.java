package com.github.ivanas93.generator;

import com.github.ivanas93.exception.EntropySourceException;
import com.github.ivanas93.exception.HashingException;
import lombok.extern.slf4j.Slf4j;

import java.security.MessageDigest;
import java.util.Base64;

@Slf4j
public abstract class AbstractPasswordGenerator {

    public String generatePassword(int length) throws HashingException, EntropySourceException {
        log.info("Starting password generation process...");

        log.debug("Gathering system entropy from multiple sources...");
        byte[] entropyBytes = getSystemEntropy(length);
        log.debug("Entropy bytes collected: {}", entropyBytes.length);

        byte[] hashedEntropy = applySha256(entropyBytes);
        log.debug("SHA-256 hash applied to entropy bytes.");

        String password = encodePassword(hashedEntropy, length);
        log.info("Password generation complete.");

        return password;
    }

    protected abstract byte[] getSystemEntropy(int length) throws EntropySourceException;

    protected byte[] applySha256(byte[] input) throws HashingException {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return digest.digest(input);
        } catch (Exception e) {
            log.error("Error while applying SHA-256 hash: {}", e.getMessage());
            throw new HashingException("Failed to apply SHA-256 hash", e);
        }
    }

    private String encodePassword(byte[] entropyBytes, int length) {
        String password = Base64.getUrlEncoder().withoutPadding().encodeToString(entropyBytes);
        log.debug("Encoded password: {}", password);
        return password.substring(0, Math.min(length, password.length()));
    }

    byte[] combineEntropy(byte[] randomBytes, long timestamp) {
        byte[] combined = new byte[randomBytes.length + Long.BYTES];
        System.arraycopy(randomBytes, 0, combined, 0, randomBytes.length);

        for (int i = 0; i < Long.BYTES; i++) {
            combined[randomBytes.length + i] = (byte) (timestamp >> (i * 8));
        }
        return combined;

    }
}
