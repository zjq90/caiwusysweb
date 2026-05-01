package com.caiwu.config;

import com.caiwu.common.Result;
import com.caiwu.utils.JwtUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
@Component
public class JwtInterceptor implements HandlerInterceptor {

    @Resource
    private JwtUtils jwtUtils;

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if ("OPTIONS".equals(request.getMethod())) {
            return true;
        }

        String token = request.getHeader(jwtUtils.getHeader());
        log.debug("请求路径: {}, Token: {}", request.getRequestURI(), token);

        if (!StringUtils.hasText(token)) {
            writeErrorResponse(response, Result.unauthorized("未登录，请先登录"));
            return false;
        }

        if (token.startsWith(jwtUtils.getPrefix())) {
            token = token.substring(jwtUtils.getPrefix().length());
        }

        if (!jwtUtils.validateToken(token)) {
            writeErrorResponse(response, Result.unauthorized("Token无效或已过期"));
            return false;
        }

        Long userId = jwtUtils.getUserIdFromToken(token);
        String username = jwtUtils.getUsernameFromToken(token);
        request.setAttribute("userId", userId);
        request.setAttribute("username", username);

        log.debug("用户 {} 已通过认证, userId: {}", username, userId);
        return true;
    }

    private void writeErrorResponse(HttpServletResponse response, Result<?> result) throws Exception {
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().write(objectMapper.writeValueAsString(result));
    }
}
