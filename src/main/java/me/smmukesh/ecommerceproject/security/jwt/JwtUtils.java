package me.smmukesh.ecommerceproject.security.jwt;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
@RequiredArgsConstructor
public class JwtUtils {

    private final static Logger logger = LoggerFactory.getLogger(JwtUtils.class);

    @Value("${jwt.token.expiration-time}")
    private long jwtExpiration;

    @Value("${jwt.signature}")
    private String secretKey;

    //1. Getting JWT from Header.
    public String getJwtFromHeader(HttpServletRequest request){
        String token = request.getHeader("Authorization");
        if(token != null && token.startsWith("Bearer")){
            return token.substring(7);
        }else {
            return null;
        }
    }

    //2. Generating Token from username.
    public String generateTokenFromUsername(UserDetails userDetails){
        String username = userDetails.getUsername();
        return Jwts.builder()
                .subject(username)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis()+jwtExpiration))
                .signWith(getSecretKey())
                .compact();
    }

    //3. Getting username from jwt token.
    public String getUsernameFromJwtToken(String token){
        return Jwts.parser()
                .verifyWith(getSecretKey())
                .build().parseSignedClaims(token)
                .getPayload().getSubject();
    }

    //4. Generate signature key.
    public SecretKey getSecretKey(){
        return Keys.hmacShaKeyFor(
                Decoders.BASE64.decode(secretKey)
        );
    }

    //5. Validate Jwt token.
    public boolean validateJwtToken(String token){
        try{
            System.out.println("Validate");
            Jwts.parser()
                    .verifyWith(getSecretKey())
                    .build()
                    .parseSignedClaims(token);
            return true;
        }catch (MalformedJwtException exception){
            logger.error("Invalid Jwt Token : {}",exception.getMessage());
        }catch (ExpiredJwtException exception) {
            logger.error("Token Expired : {}", exception.getMessage());
        }catch (UnsupportedJwtException exception){
            logger.error("JWT Token is unsupported : {}",exception.getMessage());
        }catch (IllegalArgumentException exception){
            logger.error("JWT Claims String is empty : {}",exception.getMessage());
        }
        return false;
    }
}
