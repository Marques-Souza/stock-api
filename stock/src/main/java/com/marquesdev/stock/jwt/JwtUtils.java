package com.marquesdev.stock.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Slf4j
@Component
public class JwtUtils {

    public static final String JWT_BEARER = "Bearer ";
    public static final String JWT_AUTHORIZATION = "Authorization";
    public static final long EXPIRE_DAYS = 0;
    public static final long EXPIRE_HOURS = 0;
    public static final long EXPIRE_MINUTES = 30;

    private final String secretKey;

    public JwtUtils(@Value("${jwt.secret}") String secretKey){
        this.secretKey = secretKey;
    }

    private Key generateKey(){
        return Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
    }

    private Date toExpireDate(Date start){
        LocalDateTime dateTime = start.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        LocalDateTime end = dateTime.plusDays(EXPIRE_DAYS).plusHours(EXPIRE_HOURS).plusMinutes(EXPIRE_MINUTES);
        return Date.from(end.atZone(ZoneId.systemDefault()).toInstant());
    }

    public JwtToken createToken(String email, String profile){
        Date issuedAt = new Date();
        Date limit = toExpireDate(issuedAt);

        String token = Jwts.builder()
                .setHeaderParam("typ", "JWT")
                .setSubject(email)
                .setIssuedAt(issuedAt)
                .setExpiration(limit)
                .signWith(generateKey(), SignatureAlgorithm.HS256)
                .claim("profile", profile)
                .compact();

        return new JwtToken(token);
    }

   private Claims getClaimsToken(String token){

        try {
            return Jwts.parser()
                    .setSigningKey(generateKey())
                    .build()
                    .parseClaimsJws(refactorToken(token))
                    .getBody();

        }catch (JwtException ex){
            log.error(String.format("Token inválido: %s", ex.getMessage()));
        }
        return  null;
   }

   public String getEmail(String token){
        return getClaimsToken(token).getSubject();
   }

   public boolean isTokenValid(String token){
        try {
            Jwts.parser()
                    .setSigningKey(generateKey()).build()
                    .parseClaimsJws(refactorToken(token));
            return true;
        }catch (JwtException ex){
            log.error(String.format("Token inválido: %s", ex.getMessage()));
        }
        return false;
   }


   private String refactorToken(String token){
        if (token.contains(JWT_BEARER)){
            return token.substring(JWT_BEARER.length());
        }
        return token;
   }
}
