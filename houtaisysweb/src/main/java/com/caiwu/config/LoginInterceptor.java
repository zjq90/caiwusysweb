package com.caiwu.config;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@Component
public class LoginInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HttpSession session = request.getSession();
        Object admin = session.getAttribute("admin");
        
        if (admin == null) {
            String requestType = request.getHeader("X-Requested-With");
            if ("XMLHttpRequest".equals(requestType)) {
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().write("{\"code\":401,\"message\":\"未登录，请先登录\"}");
                return false;
            }
            response.sendRedirect(request.getContextPath() + "/login");
            return false;
        }
        return true;
    }
}
