package com.gzlg.dorm.common.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * JWT 生成与解析（HS256）。
 */
@Component
public class JwtUtil {

    private final JwtProperties properties;

    private SecretKey key;

    public JwtUtil(JwtProperties properties) {
        this.properties = properties;
    }

    @PostConstruct
    void init() {
        this.key = Keys.hmacShaKeyFor(properties.getSecret().getBytes(StandardCharsets.UTF_8));
    }

    /** 生成 token：subject=username，claim=role，含签发/过期时间 */
    public String generateToken(String username, String role) {
        Date now = new Date();
        long expireMs = properties.getExpireMinutes() * 60_000L;
        return Jwts.builder()
                .subject(username)
                .claim("role", role)
                .issuedAt(now)
                .expiration(new Date(now.getTime() + expireMs))
                .signWith(key)
                .compact();
    }

    /**
     * 解析 token；无效/过期抛 JwtException。
     */
    public Claims parseToken(String token) {
        try {
            return Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();
        } catch (Exception e) {
            throw new JwtException("token 无效或已过期", e);
        }
    }
}