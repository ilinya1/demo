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
                String path = request.getRequestURI().substring(request.getContextPath().length());
                if (!hasPermission(role, path, request.getMethod())) {
                    UserContext.clear();
                    return forbidden(response);
                }
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

    private boolean forbidden(HttpServletResponse response) throws Exception {
        // HTTP 200 + 业务码 403，使前端 request 拦截器按 code!=0 弹提示（不触发登出）
        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.getWriter().write(objectMapper.writeValueAsString(Result.fail(ResultCode.FORBIDDEN)));
        return false;
    }

    /**
     * 角色门禁：管理员放行一切；学生仅能访问学生端/通用端点，管理端接口返回 403。
     */
    private boolean hasPermission(String role, String path, String method) {
        if ("ADMIN".equals(role)) {
            return true;
        }
        if (role == null) {
            return false;
        }
        return studentAllowed(path, method);
    }

    private boolean studentAllowed(String path, String method) {
        // 通用 / 学生端端点：登录即可
        if (path.startsWith("/auth/")) return true;
        if (path.startsWith("/student/")) return true;
        if (path.equals("/profile")) return true;
        // 字典：只读放开给学生（供学生选择类型/原因），增删改限管理员
        if (path.startsWith("/daily/repair-types") || path.startsWith("/daily/checkout-reasons")) {
            return "GET".equals(method);
        }
        // 报修：学生可提交(POST)/查看列表与详情(GET)、不可处理(PUT 处理限管理员)
        if (path.startsWith("/daily/repair")) {
            return !"PUT".equals(method);
        }
        // 退宿申请：学生可提交/查询本人/撤销；审核(/{id}/audit)限管理员
        if (path.startsWith("/checkout-applications")) {
            return !(path.contains("/audit") && "POST".equals(method));
        }
        // 直接退宿 / 卫生登记：限管理员；卫生 GET 学生可读
        if (path.startsWith("/checkout/direct")) return false;
        if (path.startsWith("/daily/hygiene")) return "GET".equals(method);
        // 其余（学生/班级/学院/楼栋/房间/入住记录/统计/仪表盘/系统设置等）仅管理员
        return false;
    }
}