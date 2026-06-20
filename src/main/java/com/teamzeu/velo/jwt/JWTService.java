package com.teamzeu.velo.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class JWTService {

    @Value("${app.jwtSecret}")
    private String secretKey;

    @Value("${app.accessTokenExpirationMs}")
    private long accessTokenExpirationMs;

    @Value("${app.refreshTokenExpirationMs}")
    private long refreshTokenExpirationMs;

    private SecretKey getSignKey() {
        byte[] KeyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(KeyBytes);
    }


    public String generateAccessToken(Authentication authentication) {
        return generateToken(authentication, accessTokenExpirationMs, new HashMap<>());
    }

    public String generateRefreshToken(Authentication authentication) {
        Map<String, String> claims = new HashMap<>();
        claims.put("tokenType", "refreshToken");
        return generateToken(authentication, refreshTokenExpirationMs, claims);
    }

    public String generateToken(Authentication authentication, long accessTokenExpirationMs, Map<String, String> claims) {
        UserDetails user = (UserDetails) authentication.getPrincipal();
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + accessTokenExpirationMs);

        return Jwts.builder()
                .header()
                .add("typ", "JWT")
                .and()
                .subject(user.getUsername())
                .claims(claims)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(getSignKey())
                .compact();


    }

    public Claims extractAllClaims(String token) {


        try{
            return Jwts.parser()
                    .verifyWith(getSignKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (JwtException | IllegalArgumentException e) {
            throw new RuntimeException("Invalid or expired JWT token", e);
        }

    }

    public String extractUsername(String token) {
        return extractAllClaims(token).getSubject();
    }

    private boolean isTokenExpired(String token) {
        return extractAllClaims(token).getExpiration().before(new Date());
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    public  boolean isRefreshTokenValid(String token){
        return "refreshToken".equals(extractAllClaims(token).get("tokenType"))&& !isTokenExpired(token);
    }

}
