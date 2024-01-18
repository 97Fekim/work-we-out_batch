package com.fekim.workweout.batch.security;

import com.fekim.workweout.batch.repository.member.entity.Member;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class CustomUserAuthenticationFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        Member member = (Member) request.getSession().getAttribute("LOGIN_MEMBER");

        if (member != null) {
            SecurityContextHolder.getContext().setAuthentication(member.makeAuthentication());
        }

        System.out.println("===================custom Filter==============");
        filterChain.doFilter(request, response);

    }
}
