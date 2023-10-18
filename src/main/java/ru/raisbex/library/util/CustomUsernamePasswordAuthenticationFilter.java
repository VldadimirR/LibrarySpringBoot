package ru.raisbex.library.util;

import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.servlet.http.HttpServletRequest;



public class CustomUsernamePasswordAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    public CustomUsernamePasswordAuthenticationFilter() {
        setRequiresAuthenticationRequestMatcher(new AntPathRequestMatcher("/process_login", "POST"));
        setAuthenticationFailureHandler(new SimpleUrlAuthenticationFailureHandler("/auth/login?error"));
    }

    @Override
    protected String obtainUsername(HttpServletRequest request) {
        // Здесь вы можете настроить, как извлекать логин из запроса, например, из параметра "login".
        return request.getParameter("login");
    }


}
