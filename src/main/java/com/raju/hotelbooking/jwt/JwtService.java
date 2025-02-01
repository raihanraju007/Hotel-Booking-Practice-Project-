package com.raju.hotelbooking.jwt;

import com.raju.hotelbooking.entity.User;
import com.raju.hotelbooking.repository.TokenRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.function.Function;

@Service
public class JwtService {

    @Autowired
    private TokenRepository tokenRepository;

    private static final String SECRET_KEY = "mySuperSecretKeyThatIsAtLeast32CharactersLong"; // Make sure it's long enough

    // get all part from token
    private Claims extractAllClaims(String token) {
        return Jwts
                .parser()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }


    private SecretKey getSigningKey() {
        byte[] keyBytes = SECRET_KEY.getBytes(); // Use plain bytes instead of decoding
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateToken(User user) {
        try {
            String jwt = Jwts
                    .builder()
                    .setSubject(user.getEmail()) // Set Email as Subject
                    .claim("role", user.getRole()) // Add user Role to Payload
                    .setIssuedAt(new Date(System.currentTimeMillis())) // Set Token Issue time
                    .setExpiration(new Date(System.currentTimeMillis() + 24 * 60 * 60 * 1000)) // Set token expire time
                    .signWith(getSigningKey(), io.jsonwebtoken.SignatureAlgorithm.HS256) // Specify HS256
                    .compact(); // Build and Compacts the token into a string

            System.out.println("Generated Token: " + jwt); //  Debug Output
            return jwt;
        } catch (Exception e) {
            System.out.println("Error Generating Token: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }


    public String extractUserName(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public boolean isValid(String token, UserDetails user) {
        String userName = extractUserName(token);
        boolean validToken = tokenRepository
                .findByToken(token)
                .map(t -> !t.isLogout())
                .orElse(false);
        return (userName.equals(user.getUsername()) && !isTokenExpired(token) && validToken);
    }

    // Extract a specific claim from the Token Calms
    public <T> T extractClaim(String token, Function<Claims, T> resolver) {
        Claims claims = extractAllClaims(token);
        return resolver.apply(claims);
    }

    // get User Role from token
    public String extractUserRole(String token) {
        return extractClaim(token, claims -> claims.get("role", String.class));
    }


}
