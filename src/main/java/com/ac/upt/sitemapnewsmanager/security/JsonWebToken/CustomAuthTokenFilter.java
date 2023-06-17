package com.ac.upt.sitemapnewsmanager.security.JsonWebToken;

import com.ac.upt.sitemapnewsmanager.services.UserDetailsServiceImplementation;
import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

public class CustomAuthTokenFilter extends OncePerRequestFilter {

  @Autowired private UserDetailsServiceImplementation userDetailsServiceImplementation;

  @Autowired private CustomJwtUtils customJwtUtils;
  private static final Logger logger = LoggerFactory.getLogger(CustomAuthTokenFilter.class);

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {
    try {
      String jwt = parseJwt(request);
      if (jwt != null && customJwtUtils.validateJwtToken(jwt)) {
        String username = customJwtUtils.getUsernameFromJwtToken(jwt);
        UserDetails userDetails = userDetailsServiceImplementation.loadUserByUsername(username);

        UsernamePasswordAuthenticationToken authentication =
            new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());

        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authentication);
      }
    } catch (Exception e) {
      logger.error("Cannot set user authentication: {}", e.getMessage());
    }
    filterChain.doFilter(request, response);
  }

  private String parseJwt(HttpServletRequest request) {
    return customJwtUtils.getJwtFromCookies(request);
  }
}