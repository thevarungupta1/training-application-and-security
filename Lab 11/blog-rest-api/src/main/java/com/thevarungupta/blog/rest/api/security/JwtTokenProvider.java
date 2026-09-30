package com.thevarungupta.blog.rest.api.security;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Date;

@Component
public class JwtTokenProvider {

    // secret key
    private String jwtSecret = "dGhpcyBpcyBteSBzZWNyZXQga2V5IG5vYm9keSBrbm93IHRoaXMdfdf";
    // token expiry
    private long jwtExpirationInMs = 3600000L; // 1 hour

    // generate token
    public String generateToken(Authentication authentication) {
        String username = authentication.getName();
        Date currentDate = new Date();
        Date expiryDate = new Date(currentDate.getTime() + jwtExpirationInMs);
        return Jwts.builder()
                .subject(username)
                .issuedAt(currentDate)
                .expiration(expiryDate)
                .signWith(key())
                .compact();
    }
    private Key key(){
        return Keys.hmacShaKeyFor(Decoders.BASE64URL.decode(jwtSecret));
    }

    // get username from token
    public String getUsernameFromToken(String token) {
        return Jwts.parser()
                .verifyWith((SecretKey)key())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    // validate token
    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith((SecretKey)key())
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (MalformedJwtException ex) {
            throw new MalformedJwtException("Invalid JWT token");
        }catch (ExpiredJwtException ex){
            throw new ExpiredJwtException(null, null, "Expired JWT token");
        }catch (UnsupportedJwtException ex){
            throw new UnsupportedJwtException("Unsupported JWT token");
        }catch (IllegalArgumentException ex){
            throw new IllegalArgumentException("JWT claims string is empty.");
        }catch (Exception ex){
            throw new RuntimeException("JWT token validation failed");
        }
    }
}
