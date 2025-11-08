package org.example.fenglish.util;


import io.jsonwebtoken.*;
import io.jsonwebtoken.security.SignatureException;
import io.jsonwebtoken.security.Keys;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;


@Component
public class JwtUtil {

    @Value("${jwt.secret:englishLearningSecretKey1234567890abcdefghijklmnopqrstuvwxyz1234567890abcdef}")
    private String secret;

    @Value("${jwt.expiration:86400000}") // 默认24小时
    private long expiration;


    // 把字符串密钥转为 JJWT 要求的 Key 类型（核心修正）
    private Key getSigningKey() {
        // HS512 对应 HMAC-SHA512 算法，需用 hmacShaKeyFor 生成密钥
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    // 生成JWT令牌
    public String generateToken(String userID, String userType) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration);

        return Jwts.builder()
                .setSubject(userID)
                .claim("userType", userType)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(getSigningKey(), SignatureAlgorithm.HS512)
                .compact();
    }

    // 从JWT令牌中获取用户ID
    public String getUserIDFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSigningKey()).build()
                .parseClaimsJws(token)
                .getBody();

        return claims.getSubject();
    }

    // 从JWT令牌中获取用户类型
    public String getUserTypeFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSigningKey()).build()
                .parseClaimsJws(token)
                .getBody();

        return claims.get("userType", String.class);
    }

    // 验证JWT令牌
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(token);
            return true;
        } catch (SignatureException ex) {
            // 签名无效
        } catch (MalformedJwtException ex) {
            // JWT令牌格式错误
        } catch (ExpiredJwtException ex) {
            // JWT令牌已过期
        } catch (UnsupportedJwtException ex) {
            // 不支持的JWT令牌
        } catch (IllegalArgumentException ex) {
            // JWT令牌为空
        }
        return false;
    }
}

