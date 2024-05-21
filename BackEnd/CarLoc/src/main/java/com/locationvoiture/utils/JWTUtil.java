package com.locationvoiture.utils;

import com.locationvoiture.dto.UserDto;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.security.Key;
import java.util.Date;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import static javax.crypto.Cipher.SECRET_KEY;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.io.Decoders;



@Component
public class JWTUtil {
    public String extractUsername(String token)
    {

        return extractClaim(token, Claims::getSubject);
    }
    public String generateToken(UserDetails userDetails)
    {

        return generateToken(new HashMap<>(),userDetails);
    }
    public boolean isTokenValid(String token,UserDetails userDeatails){
        final String userName=extractUsername(token);
        return (userName.equals(userDeatails.getUsername()))&& !isTokenExpired(token);

    }
    public <T> T extractClaim(String token, Function<Claims,T> claimsResolver)
    {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }
private String generateToken(Map<String,Object> extraClaims,UserDetails userDetails){
        return Jwts.builder().setClaims(extraClaims).setSubject(userDetails.getUsername()).
                setIssuedAt(new Date(System.currentTimeMillis())).
                setExpiration(new Date(System.currentTimeMillis() + 100 * 60 *24))
                .signWith(getSigningKey(),SignatureAlgorithm.HS256).compact();
}
public String generateRefreshToken(Map<String,Object>extraClaims,UserDetails userDetails){
    return Jwts.builder().setClaims(extraClaims).setSubject(userDetails.getUsername()).
            setIssuedAt(new Date(System.currentTimeMillis())).
            setExpiration(new Date(System.currentTimeMillis() + 604800000))
            .signWith(getSigningKey(),SignatureAlgorithm.HS256).compact();
}

    public Date extractExpiration(String token)
    {
        return extractClaim(token, Claims::getExpiration);
    }



    private Boolean isTokenExpired(String token)
    {
        return extractExpiration(token).before(new Date());
    }
    private Claims extractAllClaims(String token){
        return Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(token).getBody();
    }

   private Key getSigningKey(){
        byte[] keyBytes = Decoders.BASE64.decode("413F4428472B62555368566D");
        return Keys.hmacShaKeyFor(keyBytes);
   }
}
