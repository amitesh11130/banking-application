package com.monocept.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;

@Component
public class JwtUtil {

    public void validToken(String token) {
        Jwts.parser().verifyWith(getKey())
                .build()
                .parseSignedClaims(token).getPayload();
    }

    private SecretKey getKey() {
        String SECRET_KEY = "LJ9AHsb9HSZLxPhxpjIkHKB8odTpg6t9QNW4Dwq8WIY=";
        byte[] bytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(bytes);
    }

}