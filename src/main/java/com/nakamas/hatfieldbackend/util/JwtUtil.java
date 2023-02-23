package com.nakamas.hatfieldbackend.util;

import com.nakamas.hatfieldbackend.models.entities.User;
import io.fusionauth.jwt.Signer;
import io.fusionauth.jwt.Verifier;
import io.fusionauth.jwt.domain.JWT;
import io.fusionauth.jwt.hmac.HMACSigner;
import io.fusionauth.jwt.hmac.HMACVerifier;
import org.springframework.stereotype.Component;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;

@Component
public class JwtUtil {
    // Build an HMAC signer using a SHA-256 hash
    private final Signer signer = HMACSigner.newSHA256Signer("too many secrets");
    // Build an HMC verifier using the same secret that was used to sign the JWT
    private final Verifier verifier = HMACVerifier.newVerifier("too many secrets");

    public String extractUsername(String jwt) {
        // Verify and decode the encoded string JWT to a rich object
        JWT token = JWT.getDecoder().decode(jwt, verifier);
        return token.subject;
    }

    public boolean validateToken(String jwt, User userDetails) {
        // Verify and decode the encoded string JWT to a rich object
        JWT token = JWT.getDecoder().decode(jwt, verifier);

        // Assert the subject of the JWT is as expected
        return token.subject.equals(userDetails.getUsername());
    }

    public String encode(User user) {
        // Build a new JWT with an issuer(iss), issued at(iat), subject(sub) and expiration(exp)
        JWT jwt = new JWT().setIssuer("hatfield.com")
                .setIssuedAt(ZonedDateTime.now(ZoneOffset.UTC))
                .setSubject(user.getUsername())
                .setExpiration(ZonedDateTime.now(ZoneOffset.UTC).plusHours(2));

        // Sign and encode the JWT to a JSON string representation
        return JWT.getEncoder().encode(jwt, signer);
    }
}
