package com.gzlg.dorm.common.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gzlg.dorm.common.result.Result;
import com.gzlg.dorm.common.result.ResultCode;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.nio.charset.StandardCharsets;

/**
 * 登录鉴权拦截器：校验 Authorization 头中的 Bearer JWT，通过后写入 UserContext。
 * 未携带/无效/过期 → 返回 HTTP 401 + {code:401,msg}（前端据此登出并跳登录页）。
 */
@Component
public class JwtInterceptor implements HandlerInterceptor {

    private static final Logger log = LoggerFactory.getLogger(JwtInterceptor.class);

    private final JwtUtil jwtUtil;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public JwtInterceptor(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 放行 CORS 预检
        if (HttpMethod.OPTIONS.matches(request.getMethod())) {
            return true;
        }

        String auth = request.getHeader("Authorization");
        if (auth != null && auth.startsWith("Bearer ")) {
            String token = auth.substring(7);
            try {
                io.jsonwebtoken.Claims claims = jwtUtil.parseToken(token);
                String username = claims.getSubject();
                String role = claims.get("role", String.class);
                UserContext.set(new LoginUser(username, role));
                return true;
            } catch (JwtException | IllegalArgumentException e) {
                log.debug("鉴权失败: {}", e.getMessage());
                return unauthorized(response);
            }
        }
        return unauthorized(response);
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        UserContext.clear();
    }

    private boolean unauthorized(HttpServletResponse response) throws Exception {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.getWriter().write(objectMapper.writeValueAsString(Result.fail(ResultCode.UNAUTHORIZED)));
        return false;
    }
}