package com.ac.upt.sitemapnewsmanager.security;

import com.ac.upt.sitemapnewsmanager.security.JsonWebToken.CustomAuthEntryPoint;
import com.ac.upt.sitemapnewsmanager.security.JsonWebToken.CustomAuthTokenFilter;
import com.ac.upt.sitemapnewsmanager.services.UserDetailsServiceImplementation;
import java.util.Arrays;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {
  @Autowired private UserDetailsServiceImplementation userDetailsServiceImplementation;
  @Autowired private CustomAuthEntryPoint unauthorizedHandler;

  @Bean
  public CustomAuthTokenFilter authenticationJwtTokenFilter() {
    return new CustomAuthTokenFilter();
  }

  @Override
  public void configure(AuthenticationManagerBuilder authenticationManagerBuilder)
      throws Exception {
    authenticationManagerBuilder
        .userDetailsService(userDetailsServiceImplementation)
        .passwordEncoder(passwordEncoder());
  }

  @Bean
  @Override
  public AuthenticationManager authenticationManagerBean() throws Exception {
    return super.authenticationManagerBean();
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();
    configuration.setAllowedOrigins(Arrays.asList("http://localhost:4200"));
    configuration.setAllowedMethods(Arrays.asList("GET", "PUT", "POST", "DELETE"));
    configuration.setAllowedHeaders(
        Arrays.asList("content-type", "undefined", "multipart/form-data"));
    configuration.setAllowCredentials(true);
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);
    return source;
  }

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http.cors()
        .and()
        .csrf()
        .disable()
        .exceptionHandling()
        .authenticationEntryPoint(unauthorizedHandler)
        .and()
        .sessionManagement()
        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        .and()
        .authorizeRequests()
        .antMatchers("/api/auth/**", "/api/app/**")
        .permitAll()
        .anyRequest()
        .authenticated();
    http.addFilterBefore(
        authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);
  }
}
