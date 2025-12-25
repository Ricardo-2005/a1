package com.example.demo.common;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class AuthInterceptor implements HandlerInterceptor {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public boolean preHandle(@NonNull HttpServletRequest request,
                             @NonNull HttpServletResponse response,
                             @NonNull Object handler) throws IOException {
        String method = request.getMethod();
        if ("OPTIONS".equalsIgnoreCase(method)) {
            return true;
        }
        String path = request.getRequestURI();
        if (path.startsWith("/api/auth")) {
            return true;
        }
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute(SessionKeys.ROLE_LEVEL) == null) {
            writeError(response, HttpServletResponse.SC_UNAUTHORIZED, "请先登录");
            return false;
        }
        int roleLevel = Integer.parseInt(String.valueOf(session.getAttribute(SessionKeys.ROLE_LEVEL)));
        if (roleLevel <= 3) {
            return true;
        }
        String requiredPerm = resolvePermission(path, method);
        if (requiredPerm == null || requiredPerm.isBlank()) {
            return true;
        }
        Object permObj = session.getAttribute(SessionKeys.PERMISSIONS);
        Set<String> permSet = new HashSet<>();
        if (permObj instanceof List<?> list) {
            for (Object item : list) {
                permSet.add(String.valueOf(item));
            }
        }
        if (!permSet.contains(requiredPerm)) {
            writeError(response, HttpServletResponse.SC_FORBIDDEN, "没有权限访问");
            return false;
        }
        return true;
    }

    private String resolvePermission(String path, String method) {
        if ("/api/employee/profile".equals(path)) {
            return "system:profile:view";
        }
        if (path.startsWith("/api/departments")) {
            return switch (method) {
                case "GET" -> "system:department:list";
                case "POST" -> "system:department:add";
                case "PUT" -> "system:department:edit";
                case "DELETE" -> "system:department:delete";
                default -> null;
            };
        }
        if (path.startsWith("/api/positions")) {
            return switch (method) {
                case "GET" -> "system:position:list";
                case "POST" -> "system:position:add";
                case "PUT" -> "system:position:edit";
                case "DELETE" -> "system:position:delete";
                default -> null;
            };
        }
        if (path.startsWith("/api/employees")) {
            return switch (method) {
                case "GET" -> "system:employee:list";
                case "POST" -> "system:employee:add";
                case "PUT" -> "system:employee:edit";
                case "DELETE" -> "system:employee:delete";
                default -> null;
            };
        }
        if (path.startsWith("/api/transfers")) {
            return switch (method) {
                case "GET" -> "system:transfer:list";
                case "POST" -> "system:transfer:add";
                default -> null;
            };
        }
        if (path.startsWith("/api/leaves")) {
            return switch (method) {
                case "GET" -> "system:leave:list";
                case "POST" -> "system:leave:add";
                case "PUT" -> "system:leave:restore";
                default -> null;
            };
        }
        if (path.startsWith("/api/reports")) {
            return "system:report:view";
        }
        if (path.startsWith("/api/users")) {
            return switch (method) {
                case "GET" -> "system:user:list";
                case "PUT" -> "system:user:reset";
                case "DELETE" -> "system:user:delete";
                default -> null;
            };
        }
        if (path.startsWith("/api/roles")) {
            return switch (method) {
                case "GET" -> "system:role:list";
                case "POST" -> "system:role:add";
                case "PUT" -> "system:role:edit";
                case "DELETE" -> "system:role:delete";
                default -> null;
            };
        }
        if (path.startsWith("/api/menus")) {
            return switch (method) {
                case "GET" -> "system:menu:list";
                case "POST" -> "system:menu:add";
                case "PUT" -> "system:menu:edit";
                case "DELETE" -> "system:menu:delete";
                default -> null;
            };
        }
        if (path.startsWith("/api/role-menus")) {
            return "system:perm:assign";
        }
        return null;
    }

    private void writeError(HttpServletResponse response, int status, String message) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json;charset=UTF-8");
        Result<Void> result = Result.fail(message);
        response.getWriter().write(objectMapper.writeValueAsString(result));
    }
}
