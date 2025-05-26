package com.iecube.iecubetutorial.util.jwt;

import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Base64;
import java.util.Date;
import java.util.Map;

@Component
@Slf4j
public class JwtUtil {
    private final String secretKey="asdfghjkl";
    private final Duration expTime = Duration.ofHours(336);

    public String createToken(Map<String, Object> claims){
        JwtBuilder jwtBuilder = Jwts.builder();
        if(claims!=null){
            jwtBuilder.setClaims(claims);
        }
        jwtBuilder.setSubject("iecube.online");
        long currentMillis = System.currentTimeMillis();
        jwtBuilder.setIssuedAt(new Date(currentMillis));
        long millis = expTime.toMillis();
        if(millis>0){
            long expMillis = currentMillis+millis;
            jwtBuilder.setExpiration(new Date(expMillis));
        }
        if(StringUtils.isNotEmpty(secretKey)){
            SignatureAlgorithm signatureAlgorithm=SignatureAlgorithm.HS256;
//            jwtBuilder.signWith(signatureAlgorithm, DatatypeConverter.parseBase64Binary(secretKey));
            jwtBuilder.signWith(signatureAlgorithm, Base64.getEncoder().encode(secretKey.getBytes()));
        }

        return jwtBuilder.compact();
    }

    /**
     * 解析token
     */
    public Claims pareToken(String token){
        try {
            Claims claims=Jwts.parser()
                    .setSigningKey(Base64.getEncoder().encode(secretKey.getBytes()))
                    .parseClaimsJws(token).getBody();
            return claims;
        } catch (ExpiredJwtException e) {
            log.error("ExpiredJwtException:{}",e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.error("UnsupportedJwtException:{}",e.getMessage());
        } catch (MalformedJwtException e) {
            log.error("MalformedJwtException:{}",e.getMessage());
        } catch (SignatureException e) {
            log.error("SignatureException:{}",e.getMessage());
        } catch (IllegalArgumentException e) {
            log.error("IllegalArgumentException:{}",e.getMessage());
        }
        return null;
    }

    /**
     * 获取用户id
     * @param token
     * @return
     */
    public Claims getClaims(String token){
        Claims claims = pareToken(token);
        if (claims!=null){
            return claims;
        }
        return null;
    }

    /**
     * 校验token是否符合要求
     */
    public boolean validateToken(String token){
        try {
            Claims claims = pareToken(token);
            Date expTime = claims.getExpiration();
            return expTime.after(new Date());
        } catch (Exception e) {
            log.error("validateToken:{}",e);
            return false;
        }

    }
}
