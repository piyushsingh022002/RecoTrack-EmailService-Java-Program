package com.reco.emailservice.security.hmac;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class HmacValidator {

    private static final String HMAC_ALGORITHM = "HmacSHA256";

    private final String secret;

    public HmacValidator(String secret) {
        this.secret = secret;
    }

    public void validate(String payload, String providedSignature) {
        if (providedSignature == null || providedSignature.isBlank()) {
            throw new IllegalArgumentException("Missing X-SIGNATURE");
        }

        String computedSignature = computeHmac(payload);

        if (!computedSignature.equals(providedSignature)) {
            throw new IllegalArgumentException("Invalid signature");
        }
    }

    private String computeHmac(String payload) {
        try {
            Mac mac = Mac.getInstance(HMAC_ALGORITHM);
            SecretKeySpec keySpec =
                    new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), HMAC_ALGORITHM);

            mac.init(keySpec);
            byte[] rawHmac = mac.doFinal(payload.getBytes(StandardCharsets.UTF_8));

            return Base64.getEncoder().encodeToString(rawHmac);
        } catch (Exception e) {
            throw new IllegalStateException("HMAC computation failed", e);
        }
    }
}
