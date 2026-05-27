package com.mealprep.MealPrep.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private final JwtUtil jwtUtil;

  public JwtAuthenticationFilter(JwtUtil jwtUtil) {
    this.jwtUtil = jwtUtil;
  }

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {

    String authHeader = request.getHeader("Authorization");
    String path = request.getRequestURI();
    String method = request.getMethod();
    String contentType = request.getContentType();

    if (authHeader != null && authHeader.startsWith("Bearer ")) {
      String token = authHeader.substring(7);

      if (jwtUtil.validateToken(token)) {
        String username = jwtUtil.extractUsername(token);

        UsernamePasswordAuthenticationToken authentication =
            new UsernamePasswordAuthenticationToken(username, null, Collections.emptyList());
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        System.out.println("[JWT] AUTH OK: " + method + " " + path + " user=" + username);
      } else {
        System.out.println(
            "[JWT] INVALID TOKEN: "
                + method
                + " "
                + path
                + " token="
                + token.substring(0, Math.min(20, token.length()))
                + "...");
      }
    } else {
      System.out.println(
          "[JWT] NO AUTH: "
              + method
              + " "
              + path
              + " contentType="
              + contentType
              + " hasAuthHeader="
              + (authHeader != null));
    }

    filterChain.doFilter(request, response);
  }
}
