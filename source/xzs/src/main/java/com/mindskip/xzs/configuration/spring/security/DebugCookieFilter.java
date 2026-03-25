package com.mindskip.xzs.configuration.spring.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Enumeration;

@Component
@Order(1)
public class DebugCookieFilter implements Filter {
    private static final Logger logger = LoggerFactory.getLogger(DebugCookieFilter.class);

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        String uri = req.getRequestURI();
        if (uri.startsWith("/api/")) {
            logger.warn("===DEBUG=== Request: {} {}", req.getMethod(), uri);
            Cookie[] cookies = req.getCookies();
            if (cookies != null) {
                for (Cookie c : cookies) {
                    logger.warn("===DEBUG=== Cookie: {}={}", c.getName(), c.getValue().substring(0, Math.min(c.getValue().length(), 50)));
                }
            } else {
                logger.warn("===DEBUG=== No cookies in request");
            }
            HttpSession session = req.getSession(false);
            if (session != null) {
                logger.warn("===DEBUG=== Session ID: {}, Principal: {}", session.getId(),
                        req.getUserPrincipal() != null ? req.getUserPrincipal().getName() : "null");
            } else {
                logger.warn("===DEBUG=== No session");
            }
        }
        chain.doFilter(request, response);
        if (uri.startsWith("/api/")) {
            HttpServletResponse res = (HttpServletResponse) response;
            for (String headerName : res.getHeaderNames()) {
                if (headerName.equalsIgnoreCase("Set-Cookie")) {
                    for (String val : res.getHeaders(headerName)) {
                        logger.warn("===DEBUG=== Response Set-Cookie: {}", val);
                    }
                }
            }
        }
    }
}
