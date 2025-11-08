package org.example.fenglish.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.fenglish.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;
import java.util.Enumeration;

@Component
public class JwtInterceptor implements HandlerInterceptor {
    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
                             Object handler) throws Exception {
        // 放行OPTIONS请求
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }
        
        // 放行登录和注册接口
        String requestURI = request.getRequestURI();
        if (requestURI.contains("/api/user/ordinary/login") ||
                requestURI.contains("/api/user/admin/login") ||
                requestURI.contains("/api/user/ordinary/register") ||
                requestURI.contains("/api/user/admin/register")) {
            return true;
        }

        logRequestHeaders(request);

        String authHeader = getAuthorizationHeader(request);
        System.out.println(String.format("请求URL: %s", request.getRequestURL()));
        System.out.println(String.format("请求方法: %s", request.getMethod()));
        // 可选：补充客户端IP（原日志未加，按需添加）
        System.out.println(String.format("客户端IP: %s", request.getRemoteAddr()));

        // 检查Authorization头是否存在
        if (authHeader == null || authHeader.trim().isEmpty()) {
            System.out.println("未提供有效的Authorization头");
            sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "未提供有效的Authorization头");
            return false;
        }

        // 检查Bearer格式
        if (!authHeader.startsWith("Bearer ")) {
            System.out.println(String.format("Authorization格式错误: %s", authHeader));
            sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "Authorization格式错误，应为'Bearer <token>'");
            return false;
        }
        // 验证JWT令牌
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7).trim();
            if (jwtUtil.validateToken(token)) {
                // 将用户ID和用户类型存入请求属性中，供后续使用
                String userID = jwtUtil.getUserIDFromToken(token);
                String userType = jwtUtil.getUserTypeFromToken(token);
                request.setAttribute("userID", userID);
                request.setAttribute("userType", userType);
                return true;
            }
        }

        // 令牌无效，返回未授权错误
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write("{\"success\":false,\"message\":\"未授权访问，请先登录\"}");
        return false;
    }

    private String getAuthorizationHeader(HttpServletRequest request) {
        String[] possibleHeaders = {"Authorization", "authorization", "AUTHORIZATION"};

        for (String header : possibleHeaders) {
            String value = request.getHeader(header);
            if (value != null && !value.trim().isEmpty()) {
                System.out.println(String.format("从Header '%s' 获取到值: %s", header, value));
                return value;
            }
        }

        return null;
    }

    private void logRequestHeaders(HttpServletRequest request) {
        Enumeration<String> headerNames = request.getHeaderNames();
        System.out.println("=== 请求头信息 ===");
        while (headerNames != null && headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            String headerValue = request.getHeader(headerName);
            System.out.println(String.format("'%s': %s", headerName, headerValue));
        }
        System.out.println("=== 结束请求头信息 ===");
    }

    private void sendErrorResponse(HttpServletResponse response, int status, String message) throws IOException, IOException {
        response.setStatus(status);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write("{\"success\": false, \"message\": \"" + message + "\"}");
        response.getWriter().flush();
    }
}
