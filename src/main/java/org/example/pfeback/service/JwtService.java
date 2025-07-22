package org.example.pfeback.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.example.pfeback.model.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.function.Function;


@Service
public class JwtService {
    @Value("${secret-key}")
    private String SECRET_KEY ;

    @Value("${access-token-expiration}")
    private long accessTokenExpire;

    @Value("${refresh-token-expiration}")
    private long refreshTokenExpire;



    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }


    public boolean isValid(String token, UserDetails user) {
        String username = extractUsername(token);
        return (username.equals(user.getUsername())) && !isTokenExpired(token);
    }


    public Boolean isValidRefreshToken(String token, User user) {
        String username = extractUsername(token);
        return (username.equals(user.getUsername())) && !isTokenExpired(token);
    }





    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }


    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }




    public<T> T extractClaim(String token, Function<Claims,T> claimsResolver){
        Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);


    }



    private Claims extractAllClaims(String token){
        return Jwts
                .parser()
                .verifyWith(getSigninKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }


    public String generateAccessToken(User user) {
        return generateToken(user,accessTokenExpire);
    }


    public String generateRefreshToken(User user) {
        return generateToken(user,refreshTokenExpire);
    }





    private String generateToken(User user,long expireTime) {

        String token = Jwts
                .builder()
                .subject(user.getUsername())
                .claim("role", user.getRole())
                .claim("id", user.getId())
                .claim("firstname", user.getFirstname())
                .claim("lastname", user.getLastname())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis()+ expireTime))
                .signWith(getSigninKey())
                .compact();

        return token;
    }


    private SecretKey getSigninKey() {
        byte[] keyBytes = Decoders.BASE64URL.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }




}
