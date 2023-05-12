package com.dxvalley.nedajpaymnetbackend.security.config;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

@Service
public class JwtService {
    private static final String SECRET_KEY = "71337436773979244226452948404D635166546A576E5A723475377821412543";
    public String extractUsername(String token)throws Exception {
        try {

            return extractClaim(token, Claims::getSubject); //the subject must have to be username or email
        }catch (ExpiredJwtException ex) {
            throw new Exception("Token has expired");
        } catch (UnsupportedJwtException ex) {
            throw new Exception("Unsupported JWT token");
        } catch (MalformedJwtException ex) {
            throw new Exception("Invalid JWT token");
        } catch (Exception ex) {
            throw new Exception("Error occurred while parsing JWT token");
        }
    }
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }
    //To generate token only from userdetails
    //we can use this method latter
    public String generateToken(UserDetails userDetails){
        return generateToken(new HashMap<>(),userDetails);
    }
    public String generateToken(
            Map<String, Objects> extraClaims,
            UserDetails userDetails
    ) {
        return Jwts
                .builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername()) //hence, this get username returns an email for us in our User class.
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 24))
                .signWith(getSignInkey(), SignatureAlgorithm.HS256)
                .compact();
    }
    public boolean isTokenValid(String token, UserDetails userDetails) throws Exception{
        try {
            final String username=extractUsername(token);
            return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
        } catch (ExpiredJwtException ex) {
            throw new Exception("Token has expired");
        } catch (UnsupportedJwtException ex) {
            throw new Exception("Unsupported JWT token");
        } catch (MalformedJwtException ex) {
            throw new Exception("Invalid JWT token");
        } catch (Exception ex) {
            throw new Exception("Error occurred while parsing JWT token");
        }

    }
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }
    private Claims extractAllClaims(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getSignInkey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
    private Key getSignInkey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
