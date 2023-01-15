package qichen.code.cors;


import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static org.springframework.http.HttpMethod.OPTIONS;

@Slf4j
@Component
public class AddResponseHeaderFilter extends OncePerRequestFilter {


    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {


        if (OPTIONS.equals(((HttpServletRequest) httpServletRequest).getMethod())) {
            httpServletResponse.getWriter().println("ok");
            return;
        }

        httpServletResponse.addHeader("Access-Control-Allow-Origin",httpServletRequest.getHeader("origin"));
        httpServletResponse.addHeader("Access-Control-Allow-Credentials","true");
        httpServletResponse.addHeader("Access-Control-Allow-Headers",httpServletRequest.getHeader("Access-Control-Request-Headers"));
        httpServletResponse.addHeader("X-Frame-Options", "DENY");
        httpServletResponse.addHeader("Cache-Control", "no-cache, no-store, must-revalidate, max-age=0");
        httpServletResponse.addHeader("Cache-Control", "no-cache='set-cookie'");
        httpServletResponse.addHeader("Pragma", "no-cache");

        filterChain.doFilter(httpServletRequest, httpServletResponse);
    }




}