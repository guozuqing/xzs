package com.mindskip.xzs.configuration.spring.security;

import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.rememberme.TokenBasedRememberMeServices;

import javax.servlet.http.HttpServletRequest;


/**
 * @version 3.5.0
 * @description: 记住我，Cookie
 * Copyright (C), 2020-2026, 天空领域培训发展部
 * @date 2021/12/25 9:45
 */
public class RestTokenBasedRememberMeServices extends TokenBasedRememberMeServices {
    public RestTokenBasedRememberMeServices(String key, UserDetailsService userDetailsService) {
        super(key, userDetailsService);
    }

    @Override
    protected boolean rememberMeRequested(HttpServletRequest request, String parameter) {
        Object remember = request.getAttribute(DEFAULT_PARAMETER);
        if (remember == null) {
            return true;
        }
        return (boolean) remember;
    }

}
