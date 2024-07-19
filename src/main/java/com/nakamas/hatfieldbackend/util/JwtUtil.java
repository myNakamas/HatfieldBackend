package com.nakamas.hatfieldbackend.util;

import com.nakamas.hatfieldbackend.config.exception.CustomException;
import com.nakamas.hatfieldbackend.models.entities.User;
import io.fusionauth.jwt.JWTException;
import io.fusionauth.jwt.Signer;
import io.fusionauth.jwt.Verifier;
import io.fusionauth.jwt.domain.JWT;
import io.fusionauth.jwt.hmac.HMACSigner;
import io.fusionauth.jwt.hmac.HMACVerifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.UUID;

@Component
public class JwtUtil {

    // Build an HMAC signer using a SHA-256 hash
    private final Signer signer;
    // Build an HMC verifier using the same secret that was used to sign the JWT
    private final Verifier verifier;

    public JwtUtil(@Value(value = "${jwtSecret}") String secret) {
        this.signer = HMACSigner.newSHA256Signer(secret);
        this.verifier = HMACVerifier.newVerifier(secret);
    }

    public UUID extractVerifier(String jwt) {
        // Verify and decode the encoded string JWT to a rich object
        try {
            JWT token = JWT.getDecoder().decode(jwt, verifier);
            return UUID.fromString(token.subject);
        } catch (JWTException e) {
            return null;
        }
    }

    public boolean validateToken(String jwt, User userDetails) {
        // Verify and decode the encoded string JWT to a rich object
        JWT token = JWT.getDecoder().decode(jwt, verifier);

        // Assert the subject of the JWT is as expected
        return userDetails.getId() != null && token.subject.equals(userDetails.getId().toString());
    }

    public String encode(User user) {
        if (user.getId() == null) throw new CustomException("User has no id");
        // Build a new JWT with an issuer(iss), issued at(iat), subject(sub) and expiration(exp)
        JWT jwt = new JWT().setIssuer("hatfield.com")
                .setIssuedAt(ZonedDateTime.now(ZoneOffset.UTC))
                .setSubject(user.getId().toString())
                .setExpiration(ZonedDateTime.now(ZoneOffset.UTC).plusDays(1));

        // Sign and encode the JWT to a JSON string representation
        return JWT.getEncoder().encode(jwt, signer);
    }

    public static String prepareBearerToken(String token) {
        if (token.startsWith("Bearer ")) return token;
        return "Bearer " + token;
    }
}
