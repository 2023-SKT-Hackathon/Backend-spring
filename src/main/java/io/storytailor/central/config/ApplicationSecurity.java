package io.storytailor.central.config;

import io.storytailor.central.user.filter.LoginFilter;
import io.storytailor.central.user.service.LoginSVC;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.firewall.DefaultHttpFirewall;
import org.springframework.security.web.firewall.HttpFirewall;

@EnableWebSecurity
@Configuration
public class ApplicationSecurity {

  //WHITE_LIST에 적힌대로 시작하는 url은 권한없이 접근 가능
  private static final String[] AUTH_WHITELIST = {
    "/api/manager/refresh",
    "/api/login/**",
  };

  @Bean
  protected SecurityFilterChain configure(HttpSecurity http, LoginSVC loginSVC)
    throws Exception {
    return http
      .httpBasic()
      .disable()
      .cors()
      .and()
      .csrf()
      .disable()
      .authorizeHttpRequests(authorize ->
        authorize
          .shouldFilterAllDispatcherTypes(false)
          .requestMatchers(AUTH_WHITELIST)
          .permitAll()
          .anyRequest()
          .authenticated()
      )
      .sessionManagement()
      //세션인증 안할거니까 STATELESS
      .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
      .and()
      .addFilterBefore(
        new LoginFilter(loginSVC),
        UsernamePasswordAuthenticationFilter.class
      )
      .build();
  }

  //String url에 "//"값 넣을 수 있도록 허용
  @Bean
  public HttpFirewall defaultHttpFirewall() {
    return new DefaultHttpFirewall();
  }
}
