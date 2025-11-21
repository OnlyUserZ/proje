package com.birgundegelecek.proje;

import java.security.Key;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;

@Component 
public class JwtUtil {

	private final Key key = Keys.hmacShaKeyFor("S8k2Pq9rX4zT1mB7wC6nV0gR3fH8yJ2f".getBytes());
    

    private final long ACCESS_TOKEN_VALIDITY = 1000 * 60 * 15; 
    private final long REFRESH_TOKEN_VALIDITY = 1000L * 60 * 60 * 24 * 7; 

    public String generateAccessToken(UserDetails user) {
   
        List<String> roles = user.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        return Jwts.builder()
                .setSubject(user.getUsername()) 
                .claim("roles", roles) 
                .setIssuedAt(new Date()) 
                .setExpiration(new Date(System.currentTimeMillis() + ACCESS_TOKEN_VALIDITY)) 
                .signWith(key, SignatureAlgorithm.HS256) 
                .compact();
    }

    public String generateRefreshToken(UserDetails user, String tokenId) {
        
        return Jwts.builder()
                .setSubject(user.getUsername())
                .setId(tokenId) 
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + REFRESH_TOKEN_VALIDITY))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public Claims extractAllClaims(String token) {
       
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public String extractUsername(String token) {
       
        return extractAllClaims(token).getSubject();
    }

    public String extractTokenId(String token) {
       
        return extractAllClaims(token).getId();
    }

    public boolean isTokenExpired(String token) {
        
        return extractAllClaims(token).getExpiration().before(new Date());
    }

    public boolean isAccessTokenValid(String token, UserDetails user) {
        
        String username = extractUsername(token);
        return (username.equals(user.getUsername()) && !isTokenExpired(token));
    }
}
