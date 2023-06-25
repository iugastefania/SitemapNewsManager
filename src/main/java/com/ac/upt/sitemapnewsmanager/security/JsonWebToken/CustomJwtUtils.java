package com.ac.upt.sitemapnewsmanager.security.JsonWebToken;

import com.ac.upt.sitemapnewsmanager.services.UserDetailsImplementation;
import io.jsonwebtoken.*;
import java.util.Date;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;
import org.springframework.web.util.WebUtils;

@Component
public class CustomJwtUtils {
  private static final Logger logger = LoggerFactory.getLogger(CustomJwtUtils.class);

  @Value("${snm.app.jwtCookieName}")
  private String jwtCookieName;

  @Value("${snm.app.jwtSecret}")
  private String jwtSecret;

  @Value("${snm.app.jwtExpirationMs}")
  private int jwtExpirationMs;

  public String getJwtFromCookies(HttpServletRequest request) {
    Cookie cookie = WebUtils.getCookie(request, jwtCookieName);
    if (cookie != null) {
      return cookie.getValue();
    } else {
      return null;
    }
  }

  public ResponseCookie getCleanJwtCookie() {
    return ResponseCookie.from(jwtCookieName, null).path("/api").build();
  }

  public String getUsernameFromJwtToken(String token) {
    return Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody().getSubject();
  }

  public ResponseCookie generateJwtCookie(UserDetailsImplementation userPrincipal) {
    String jwt = generateTokenFromUsername(userPrincipal.getUsername());
    return ResponseCookie.from(jwtCookieName, jwt)
        .path("/api")
        .maxAge(24 * 60 * 60)
        .httpOnly(true)
        .build();
  }

  public boolean validateJwtToken(String authToken) {
    try {
      Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(authToken);
      return true;
    } catch (MalformedJwtException e) {
      logger.error("Invalid JWT token: {}", e.getMessage());
    } catch (ExpiredJwtException e) {
      logger.error("JWT token is expired: {}", e.getMessage());
    } catch (UnsupportedJwtException e) {
      logger.error("JWT token is unsupported: {}", e.getMessage());
    } catch (IllegalArgumentException e) {
      logger.error("JWT claims string is empty: {}", e.getMessage());
    }
    return false;
  }

  public String generateTokenFromUsername(String username) {
    return Jwts.builder()
        .setSubject(username)
        .setIssuedAt(new Date())
        .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
        .signWith(SignatureAlgorithm.HS512, jwtSecret)
        .compact();
  }
}
