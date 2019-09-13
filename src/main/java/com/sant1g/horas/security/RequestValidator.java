package com.sant1g.horas.security;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class RequestValidator {

  @Value("${slack.security.version}")
  private String securityVersion;

  @Value("${slack.security.secret}")
  private String signingSecret;

  public Boolean validateSignature(String timestamp, String body, String signature) {
    String hashBody = generateBody(timestamp, body);
    try {
      String generatedSignature = hmacDigest(hashBody, signingSecret);
      return generatedSignature.equals(signature);
    } catch (NoSuchAlgorithmException | InvalidKeyException e) {
      return false;
    }
  }

  private String generateBody(String timestamp, String body) {
    return securityVersion.concat(":").concat(timestamp).concat(":").concat(body);
  }

  private String hmacDigest(String msg, String keyString)
      throws InvalidKeyException, NoSuchAlgorithmException {
    SecretKeySpec key = new SecretKeySpec((keyString).getBytes(StandardCharsets.UTF_8), "HmacSHA256");
    Mac mac = Mac.getInstance("HmacSHA256");
    mac.init(key);

    byte[] bytes = mac.doFinal(msg.getBytes(StandardCharsets.US_ASCII));

    StringBuilder hash = new StringBuilder();
    for (byte aByte : bytes) {
      String hex = Integer.toHexString(0xFF & aByte);
      if (hex.length() == 1) {
        hash.append('0');
      }
      hash.append(hex);
    }

    return securityVersion.concat("=").concat(hash.toString());
  }
}
